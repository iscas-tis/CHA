package caravan.bus.wishbone
import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util.{Decoupled, Fill}

class WishboneErr(implicit val config: WishboneConfig) extends Module {
  val io = IO(new Bundle {
    val wbSlaveTransmitter = Decoupled(new WishboneSlave())
    val wbMasterReceiver = Flipped(Decoupled(new WishboneMaster()))
  })

  def fire: Bool = io.wbMasterReceiver.valid && io.wbMasterReceiver.bits.cyc && io.wbMasterReceiver.bits.stb

  val ackReg = RegInit(false.B)
  val dataReg = RegInit(0.U)
  val errReg = RegInit(false.B)
  val validReg = RegInit(false.B)
  /** FIXME: Assuming wishbone slave is always ready to accept master req */
  io.wbMasterReceiver.ready := true.B

  when(fire) {
    // a valid request from the host. The decoder pointed to us which means there was a wrong address given by the user
    // for writes we are going to ignore them completely.
    // for reads we are going to signal an err out and send all FFFs.
    errReg := true.B
    validReg := true.B
    when(io.wbMasterReceiver.bits.we) {
      // WRITE
      dataReg := DontCare
    } .otherwise {
      // READ
      dataReg := Fill(config.dataWidth/4, "hf".U)
    }

  } .otherwise {
      // no valid request from the host
    dataReg := 0.U
    errReg := false.B
    validReg := false.B
  }

  io.wbSlaveTransmitter.valid := validReg
  io.wbSlaveTransmitter.bits.ack := ackReg
  io.wbSlaveTransmitter.bits.dat := dataReg
   io.wbSlaveTransmitter.bits.err := errReg

}

object WishboneErrDriver extends App {
  implicit val config = WishboneConfig(10, 32)
  println((new ChiselStage).emitVerilog(new WishboneErr()))
}
