// updated dependency by log-when
package wishbone
import caravan.bus.wishbone.{Harness, WishboneConfig, WBResponse, WBRequest, WishboneHost}
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chisel3._
import org.scalatest.freespec._
import chiseltest.formal.svaAnno._

 // necessary to import


class WishboneHostProp(implicit config: WishboneConfig) extends WishboneHost()(config)
{
  val cyc_o = io.wbMasterTransmitter.bits.cyc
  val stb_o = io.wbMasterTransmitter.bits.stb 
  val ack_i = io.wbSlaveReceiver.bits.ack
  val err_i = io.wbSlaveReceiver.bits.err
  //it is allowed to get ack in the same cycle with stb
  svaAssert(this, "stb_o |-> ( stb_o U ack_i || err_i) || G stb_o" )
}

class WishboneHostTest2 extends AnyFlatSpec with ChiselScalatestTester with Formal {
  
  implicit val config = WishboneConfig(10, 32)
  behavior of "WishboneHost"
  it should "pass" in {
    verify(new WishboneHostProp(), Seq(BoundedCheck(100), BtormcEngineAnnotation))
  }
}
