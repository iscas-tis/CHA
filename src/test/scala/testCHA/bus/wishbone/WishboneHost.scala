package caravan.bus.wishbone
import caravan.bus.common.HostAdapter
import chisel3._
import chisel3.reflect.DataMirror
import chisel3.stage.ChiselStage
import chisel3.util.{Decoupled, Enum, MuxCase}


// Support only for Single READ/WRITE cycles for now
class WishboneHost(implicit val config: WishboneConfig) extends HostAdapter {
  val io = IO(new Bundle {
    val wbMasterTransmitter = Decoupled(new WishboneMaster())
    val wbSlaveReceiver  = Flipped(Decoupled(new WishboneSlave()))
    val reqIn = Flipped(Decoupled(new WBRequest()))
    val rspOut = Decoupled(new WBResponse())
  })

  def fire: Bool = io.reqIn.valid && io.wbMasterTransmitter.ready
  /**
   * Since valid indicates a valid request, the stb signal from wishbone
   * also indicates the same. So stb and valid are connected together.
   */
  io.wbMasterTransmitter.valid := io.wbMasterTransmitter.bits.stb

  /** FIXME: Assuming Master is always ready to accept data from Slave */
  io.wbSlaveReceiver.ready := true.B
  dontTouch(io.wbMasterTransmitter.ready)
  dontTouch(io.wbSlaveReceiver.ready)

  when(reset.asBool === true.B) {
    /**
     * Rule 3.20: Following signals must be negated when reset is asserted:
     * stb_o
     * cyc_o
     * all other signals are in an undefined state
     */
    io.wbMasterTransmitter.bits.getElements.filter(w => DataMirror.directionOf(w) == ActualDirection.Output).map(_ := 0.U)
  }
  val startWBTransaction = RegInit(false.B)
  // registers used to provide the response to the ip.
  val dataReg = RegInit(0.U(config.dataWidth.W))
  val respReg = RegInit(false.B)
  val errReg = RegInit(false.B)
  // new changes added here
  val stbReg = RegInit(false.B)
  val cycReg = RegInit(false.B)
  val weReg = RegInit(false.B)
  val datReg = RegInit(0.U)
  val adrReg = RegInit(0.U)
  val selReg = RegInit(0.U)
  // new changes ended here

  // state machine to conform to the wishbone protocol of negating stb and cyc when data latched
  val idle :: latch_data :: Nil = Enum(2)
  val stateReg = RegInit(idle)

  /** used to pass ready signal to the ip
   * is ready by default
   * de-asserts ready when a valid request is made and slave accepts it (fire)
   * re-asserts ready when the response data from slave is being latched to start another req. */
  val readyReg = RegInit(true.B)
  when(fire) {
    readyReg := false.B
  }
  when(stateReg === latch_data) {
    readyReg := true.B
  }

  if(!config.waitState) {
    /**
     * If host does not produce wait states then stb_o and cyc_o may be assigned the same signal.
     */

    // master is only ready to accept req when any prev req not pending
    io.reqIn.ready := readyReg
    when(io.reqIn.bits.isWrite === false.B && readyReg === true.B && io.reqIn.valid) {
      startWBTransaction := true.B
      stbReg := true.B
      cycReg := true.B
      weReg := io.reqIn.bits.isWrite
      adrReg := io.reqIn.bits.addrRequest
      datReg := 0.U
      selReg := io.reqIn.bits.activeByteLane
    } .elsewhen(io.reqIn.bits.isWrite === true.B && readyReg === true.B && io.reqIn.valid) {
      startWBTransaction := true.B
      stbReg := true.B
      cycReg := true.B
      weReg := io.reqIn.bits.isWrite
      adrReg := io.reqIn.bits.addrRequest
      datReg := io.reqIn.bits.dataRequest
      selReg := io.reqIn.bits.activeByteLane
    }

    io.wbMasterTransmitter.bits.stb := stbReg
    io.wbMasterTransmitter.bits.cyc := cycReg
    io.wbMasterTransmitter.bits.we := weReg
    io.wbMasterTransmitter.bits.adr := adrReg
    io.wbMasterTransmitter.bits.dat := datReg
    io.wbMasterTransmitter.bits.sel := selReg

    when(!startWBTransaction) {
      io.wbMasterTransmitter.bits.getElements.filter(w => DataMirror.directionOf(w) == ActualDirection.Output).map(_ := 0.U)
    }

    when(io.wbSlaveReceiver.bits.ack && !io.wbSlaveReceiver.bits.err) {
      dataReg := io.wbSlaveReceiver.bits.dat
      respReg := true.B
      errReg := false.B
      // making the register false when ack received so that in the next cycle stb, cyc and other signals get low
      startWBTransaction := false.B
    } .elsewhen(io.wbSlaveReceiver.bits.err && !io.wbSlaveReceiver.bits.ack) {
      dataReg := io.wbSlaveReceiver.bits.dat
      respReg := true.B
      errReg := true.B
      startWBTransaction := false.B
    }

    when(stateReg === idle) {
      stateReg := Mux(io.wbSlaveReceiver.bits.ack || io.wbSlaveReceiver.bits.err, latch_data, idle)
    } .elsewhen(stateReg === latch_data) {
      respReg := false.B
      stateReg := idle
    }

    /** FIXME: not using the ready signal from the IP to send valid data
     * assuming IP is always ready to accept data from the bus */
    io.rspOut.valid := respReg
    io.rspOut.bits.dataResponse := dataReg
    io.rspOut.bits.error := errReg
  }





  /**
   * Host initiates the transfer cycle by asserting cyc_o. When cyc_o is negated, all other
   * host signals are invalid.
   *
   * Device interface only respond to other device signals only when cyc_i is asserted.
   */

  /**
   * Rule 3.25: Host interfaces MUST assert cyc_o for the duration of SINGLE READ/WRITE, BLOCK and RMW cycles.
   * cyc_o must be asserted in the same rising edge that qualifies the assertion of stb_o
   * cyc_o must be negated in the same rising edge that qualifies the negation of stb_o
   */

  /**
   * Host asserts stb_o when it is ready to transfer data.
   * stb_o remains asserted until the device asserts one of its cycle termination signals:
   * ack_i
   * err_i
   * rty_i
   *
   * if any of the above signals are asserted then the stb_o is negated.
   */

  /**
   * Rule 3.60: Host interfaces must qualify the following signals with stb_o:
   * adr_o
   * dat_mosi
   * sel_o
   * we_o
   * tagn_o
   */


}

object WishboneHostDriver extends App {
  implicit val config = WishboneConfig(addressWidth = 32, dataWidth = 32)
  println((new ChiselStage).emitVerilog(new WishboneHost()))
}