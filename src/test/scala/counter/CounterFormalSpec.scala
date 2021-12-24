package counter

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chisel3._

class CounterFormalSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  behavior of "CounterProp"
  it should "pass" in {
    verify(new CounterProp(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  }
}
