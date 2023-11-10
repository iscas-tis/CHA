package testCHA.chiselbook.adder

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.chaAnno._
import chisel3._

class AdderProp1(bitWidth: Int) extends CarryRippleAdder(bitWidth){
  val io_a = io.a  
  val io_b = io.b
  val io_cin = io.cin
  val io_c = io.c
  val io_cout = io.cout
  

  // for (i <- 0 until bitWidth) {
  //   val ai = (io_a & (1.asUInt << i))(0) .asBool
  //   val bi = (io_b & (1.asUInt << i))(0) .asBool
  //   // chaAssert(this,"bi |-> F ai") 
  //   assert(ai)
  // }

  val ai = (io_a & (1.asUInt << 1))(0) .asBool
  val bi = (io_b & (1.asUInt << 1))(0) .asBool
  chaAssert(this,"bi |-> F ai") 

}

class AdderFormalSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new AdderProp1(4)))
  // behavior of "AdderProp1"
  // it should "pass" in {
  //   verify(new AdderProp1(4), Seq(BoundedCheck(100), BtormcEngineAnnotation))
  //   //verify(new GCD(), Seq(BoundedCheck(20), BtormcEngineAnnotation) )
  //   //verify(new CounterProp1(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  //   //verify(new CounterProp2(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  // }
}
