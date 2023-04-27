package testSVA.GCD

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.svaSeq._
import chisel3._

class DecoupledGcdProp1(width: Int) extends DecoupledGcd(width: Int){
  val nBusy = !busy
  // assert(busy)
  // svaAssert(this,"busy |-> ##[1:15] nBusy") 
  svaAssert(this,"busy |-> ##[1:16] nBusy") 
  // svaAssert(this,"busy |-> ##[1:15] nBusy") 
  // svaSeqAnno.makeSVAAnno(this.reset, ap(busy) |->  ###(1,15) ap(!busy))
}

class DecoupledGcdSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  // println(new (chisel3.stage.ChiselStage).emitSystemVerilog(new DecoupledGcdProp1(4)))
  behavior of "DecoupledGcd"
  it should "pass" in {
    verify(new DecoupledGcdProp1(4), Seq(KInductionCheck(50), PonoEngineAnnotation))
  }
}