package caravan.bus.wishbone
import caravan.bus.common.DeviceAdapter
import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util.Decoupled

class WishboneDevice(implicit val config: WishboneConfig) extends DeviceAdapter {
  val io = IO(new Bundle {
    val wbSlaveTransmitter = Decoupled(new WishboneSlave())
    val wbMasterReceiver = Flipped(Decoupled(new WishboneMaster()))
    val reqOut = Decoupled(new WBRequest())
    val rspIn = Flipped(Decoupled(new WBResponse()))
  })

  /** fire is a handy function indicating whenever the master sends a valid request */
  def fire: Bool = io.wbMasterReceiver.valid && io.wbMasterReceiver.bits.cyc && io.wbMasterReceiver.bits.stb
  val ack = WireInit(false.B)
  /** FIXME: Assuming wishbone slave is always ready to accept master req */
  io.wbMasterReceiver.ready := true.B
  dontTouch(io.wbMasterReceiver.ready)
  dontTouch(io.wbSlaveTransmitter.ready)
  /** FIXME: Assuming wishbone slave is always ready to accept ip response data */
  io.rspIn.ready := true.B

  when(fire) {
    when(!io.wbMasterReceiver.bits.we) {
      // READ CYCLE
      val addr = io.wbMasterReceiver.bits.adr
      val activeByteLane = io.wbMasterReceiver.bits.sel
      /** FIXME: Assuming ip is always ready to accept wishbone slave's request */
      io.reqOut.valid := true.B
      io.reqOut.bits.addrRequest := addr
      io.reqOut.bits.dataRequest := DontCare
      io.reqOut.bits.activeByteLane := activeByteLane
      io.reqOut.bits.isWrite := false.B
      when(io.rspIn.valid && !io.rspIn.bits.error) {
        /** FIXME: Assuming wishbone master is always ready to accept slave's data response */
        io.wbSlaveTransmitter.valid := true.B
        ack := true.B
        io.wbSlaveTransmitter.bits.err := false.B
        io.wbSlaveTransmitter.bits.dat := io.rspIn.bits.dataResponse
      } .elsewhen(io.rspIn.valid && io.rspIn.bits.error) {
        io.wbSlaveTransmitter.valid := true.B
        ack := false.B
        io.wbSlaveTransmitter.bits.err := true.B
        io.wbSlaveTransmitter.bits.dat := io.rspIn.bits.dataResponse
      } .otherwise {
        io.wbSlaveTransmitter.valid := false.B
        ack := false.B
        io.wbSlaveTransmitter.bits.err := false.B
        io.wbSlaveTransmitter.bits.dat := DontCare
      }
    } .otherwise {
      // WRITE CYCLE
      io.reqOut.valid := true.B
      io.reqOut.bits.addrRequest := io.wbMasterReceiver.bits.adr
      io.reqOut.bits.dataRequest := io.wbMasterReceiver.bits.dat
      io.reqOut.bits.activeByteLane := io.wbMasterReceiver.bits.sel
      io.reqOut.bits.isWrite := io.wbMasterReceiver.bits.we
      when(io.rspIn.valid && !io.rspIn.bits.error) {
        io.wbSlaveTransmitter.valid := true.B
        ack := true.B
        io.wbSlaveTransmitter.bits.err := false.B
        io.wbSlaveTransmitter.bits.dat := DontCare
      } .elsewhen(io.rspIn.valid && io.rspIn.bits.error) {
        io.wbSlaveTransmitter.valid := true.B
        ack := false.B
        io.wbSlaveTransmitter.bits.err := true.B
        io.wbSlaveTransmitter.bits.dat := DontCare
      } .otherwise {
        io.wbSlaveTransmitter.valid := false.B
        ack := false.B
        io.wbSlaveTransmitter.bits.err := false.B
        io.wbSlaveTransmitter.bits.err := false.B
        io.wbSlaveTransmitter.bits.dat := DontCare
      }

    }
  } .otherwise {
    // No valid bus request from host
    io.reqOut.valid := false.B
    io.reqOut.bits.addrRequest := DontCare
    io.reqOut.bits.dataRequest := DontCare
    io.reqOut.bits.activeByteLane := DontCare
    io.reqOut.bits.isWrite := DontCare

    io.wbSlaveTransmitter.valid := false.B
    ack := false.B
    io.wbSlaveTransmitter.bits.err := false.B
    io.wbSlaveTransmitter.bits.dat := DontCare
  }

  io.wbSlaveTransmitter.bits.ack := ack
  /**
   * Rule 3.35: In standard mode, the cycle terminating signals ack_o, err_o and rty_o must be generated
   * in response to the logical AND of cyc_i and stb_i.
   *
   * Other signals besides these two maybe included in the generation of terminating signals.
   */

  /**
   * Rule 3.45: If device supports err_o or rty_o signals then it should not assert more than one of the
   * following signals at any given time: ack_o, err_o, rty_o
   */

  /**
   * Rule: 3.50: Device interfaces MUST be designed so that the ack_o, err_o and rty_o signals are asserted
   * and negated in response to the assertion and negation of stb_i
   */

  /**
   * Rule 3.65: The device must qualify the dat_miso signal with ack_o, err_o or rty_o
   */
}

object WishboneDevice extends App {
  implicit val config = WishboneConfig(addressWidth = 10, dataWidth = 32)
  println((new ChiselStage).emitVerilog(new WishboneDevice()))
}