package testCHA.chiselbook.fifo

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.chaAnno._
import chisel3._

class CombFifoProp[T <: Data](gen: T, depth: Int) extends CombFifo(gen: T, depth: Int){
  val nBusy1 = memFifo.io.deq.valid
  // val busy = !s1.input.ready
  // assert(nBusy1)
  // chaAssert(this,"busy |-> ##[1:15] nBusy") 
  // chaAssert(this,"busy |-> ##[1:16] nBusy1") 
  // chaAssume(this,"G busy U busy")
  chaAssert(this,"nBusy1 |-> F nBusy1") 
  // chaAssert(this,"busy |-> ##[1:15] nBusy") 
  // chaAnno.makeCHAAnno(this.reset, ap(busy) |->  ###(1,15) ap(!busy))
}

class CombFifoPropSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new CombFifoProp(UInt(16.W), 4)))
  // behavior of "DecoupledGcd"
  // it should "pass" in {
  //   verify(new CombFifoProp(UInt(16.W), 4)), Seq(KInductionCheck(50), PonoEngineAnnotation))
  // }
}