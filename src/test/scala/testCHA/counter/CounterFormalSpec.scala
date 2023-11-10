package counter

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chisel3._

class CounterFormalSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  // println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new CounterProp1(4)))
  behavior of "CounterProp"
  it should "pass" in {
    // verify(new Counter(4), Seq(BoundedCheck(100), BtormcEngineAnnotation))
    println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new Counter(4)))
    //verify(new GCD(), Seq(BoundedCheck(20), BtormcEngineAnnotation) )
    //verify(new CounterProp1(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
    //verify(new CounterProp2(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  }
}
