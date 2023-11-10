package testCHA.GCD

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.chaAnno._
import chisel3._

class DecoupledGcdProp1(bitWidth: Int) extends DecoupledGcd(bitWidth){
  val nBusy1 = !busy
  val temp = output.valid
  // val busy = !s1.input.ready
  // assert(nBusy1)
  // chaAssert(this,"busy |-> ##[1:15] nBusy") 
  chaAssert(this,"busy") 
  // chaAssume(this,"F temp")
  chaAssume(this,"temp")
  // chaAssert(this,"temp |-> F nBusy1") 
  // chaAssert(this,"busy |-> temp") 
  // chaAssert(this,"busy |-> ##[1:15] nBusy") 
  // chaAnno.makeCHAAnno(this.reset, ap(busy) |->  ###(1,15) ap(!busy))
}

class DecoupledGcdSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  // println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new DecoupledGcdProp1(4)))
  behavior of "DecoupledGcd"
  it should "pass" in {
    verify(new DecoupledGcdProp1(4), Seq(BoundedCheck(20), PonoEngineAnnotation))
  }
}