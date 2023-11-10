package testCHA.chiselbook.CombinationalTest

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.chaAnno._
import chisel3._

class CombWhen2UntestedProp() extends CombWhen2Untested(){
  
  when (coinSum >= price) {
    assert(enoughMoney)
    chaAssert(this,"enoughMoney |-> F enoughMoney")
  }
  

  // for (i <- 0 until bitWidth) {
  //   val ai = (io_a & (1.asUInt << i))(0) .asBool
  //   val bi = (io_b & (1.asUInt << i))(0) .asBool
  //   // chaAssert(this,"bi |-> F ai") 
  //   assert(ai)
  // }
   

}

class CombWhen2UntestedPropSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new CombWhen2UntestedProp()))
  // behavior of "AdderProp1"
  // it should "pass" in {
  //   verify(new AdderProp1(4), Seq(BoundedCheck(100), BtormcEngineAnnotation))
  //   //verify(new GCD(), Seq(BoundedCheck(20), BtormcEngineAnnotation) )
  //   //verify(new CounterProp1(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  //   //verify(new CounterProp2(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  // }
}
