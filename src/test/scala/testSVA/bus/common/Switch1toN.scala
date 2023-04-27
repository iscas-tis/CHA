package caravan.bus.common
import caravan.bus.wishbone.{WBDevice, WBHost, WishboneConfig, WishboneMaster, WishboneSlave}
import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util.{Decoupled, log2Ceil}

class Switch1toN[A <: BusHost, B <: BusDevice](mb: A, sb: B, N: Int) extends Module {
  val io = IO(new Bundle {
    val hostIn = Flipped(Decoupled(mb))
    val hostOut = Decoupled(sb)
    val devOut = Vec(N+1, Decoupled(mb))  // creating 1 extra to connect error device
    val devIn = Flipped(Vec(N+1, Decoupled(sb)))  // creating 1 extra to connect error device
    val devSel = Input(UInt(log2Ceil(N + 1).W))
  })

  /** FIXME: assuming the socket is always ready to accept data from the bus host */
  io.hostIn.ready := true.B
  /** FIXME: assuming the socket is always ready to accept data from all the devices */
  io.devIn.map(b => b.ready := true.B)

  /** sending valid to error device only when host sends a valid req and the decoder cannot match
   * any address with the address map and sends a devSel for error device */
  io.devOut(N).valid := io.hostIn.valid && (io.devSel === N.asUInt)
  /** connecting the response with the error device by default
   * this would be overridden below if devSel matches with any devices */
  io.hostOut.valid := io.devIn(N).valid
  io.hostOut.bits <> io.devIn(N).bits

  /** connecting the bits from the host to all the devices
   * but connecting the valid to only that device which is selected by the decoder */
  io.devOut.map(dev => dev.bits <> io.hostIn.bits)
  for (i <- 0 until N) {
    io.devOut(i).valid := io.hostIn.valid && (io.devSel === i.asUInt)
  }

  /** if the devSel matches, then wire the host out with that device's signals
   * else, keep them connected with the error responder device. */
  for (id <- 0 until N) {
    when(io.devSel === id.asUInt) {
      io.hostOut.bits <> io.devIn(id).bits
      io.hostOut.valid := io.devIn(id).valid
    }
  }


}


object Switch1toNDriver extends App {
  implicit val config = WishboneConfig(addressWidth = 32, dataWidth = 32)
  println((new ChiselStage).emitVerilog(new Switch1toN[WBHost, WBDevice](new WishboneMaster(), new WishboneSlave(), 3)))
}
