package counter

import chisel3._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.svaSeq._

class Counter(width: Int) extends Module {
  val io = IO(new Bundle {
    //val en   = Input(Bool())
    val dout = Output(UInt(width.W))
  })
  val countReg = RegInit(0.U(width.W))
  //val testReg = RegInit(0.U(width.W))
  //val vis_countReg = countReg(0)
  //val vis_testReg = testReg(2)
  countReg := countReg + 1.U
  
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0)) )
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(1,-1) )
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(1,-1) *(2,3))
  // svaSeqAnno.makeSVAAnno(this.reset,G ap(countReg(0)) ###(1,-1) *(2,3))
  // svaSeqAnno.makeSVAAnno(this.reset,G ap(countReg(0)) ###(1,-1) *(2,3) U X ap(countReg(0)) ###(1,-1) |-> F ap(countReg(2)))
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(1,-1) |-> F ap(countReg(2)))
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0)) U ap(countReg(1)) U ap(countReg(0)) )
  // assume(reset==false)
  svaSeqAnno.makeSVAAnno(this.reset, |- ap(countReg(0)) -|)
  // svaSeqAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) -| -|)
  // svaSeqAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) ###(1,-1) -| *(2,3) -| ###(2,3) ap(countReg(1)))
  // svaSeqAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) ###(1,-1) -| *(2,3) -| ###(2,3) ap(countReg(1)) U X ap(countReg(2)) *(4,5) |-> G ap(countReg(2)))
  // svaSeqAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) ###(1,-1) -| *(2,3) -| U ###(2,3) ap(countReg(1)))
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0) & !countReg(2)) |-> ###(1,4) ap(countReg(2) ) )
  // svaSeqAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) ap(countReg(1)))
  // svaSeqAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) ap(countReg(1)) |-> G X ap(!countReg(0)))
  // svaSeqAnno.makeSVAAnno(this.reset, |- ap(!countReg(0)) ###(1,-1) ap(countReg(1)) ###(2,3) *(1,-1) |->  G X ###(1,-1) ap(countReg(1)) -|)
  // svaSeqAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) ap(countReg(1)) ###(2,3) *(1,-1) |-> |- G X ###(1,-1) ap(countReg(1)) -|)
  // svaSeqAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) |- ap(countReg(1)) ###(2,3) -| *(1,-1) |-> G X ap(!countReg(0)))
  
  // svaSeqAnno.makeSVAAnno(this.reset, F |- |- ###(2, 3) ap(countReg(0)) -| *(2,3) | ap(countReg(1)) U G ap(countReg(0)) -| )
  
  // //svaSeqAnno.makeSVAAnno(this.reset, |- F  ###(2, 3) ap(countReg(0)) *(2,3) -| | ap(countReg(1))  U G ap(countReg(0)) )
  
  // svaSeqAnno.makeSVAAnno(this.reset, G |- ###(2, 3) ###(2, 3) ap(countReg(0)) && G ap(countReg(0)) -|)
  // svaSeqAnno.makeSVAAnno(this.reset, F |- |- ###(2, 3) ap(countReg(0)) -| *(2,3) | ap(countReg(1)) U G ap(countReg(0)) -| )
  // svaSeqAnno.makeSVAAnno(this.reset, ap(countReg(0))) 
  // printlnTSeq(###(2, 3) ###(2, 3) ap(countReg(0)) ###(2, 3) ap(countReg(0)) |-> ap(countReg(1)) |-> G F ap(countReg(0)) )
  // makeSVAAnno(Seq(FinalOp(), Leftbraket(), Leftbraket(), NextOp(), GlobalOp(), AtmProp(countReg(0))))
  // svaSeqAnno.makeSVAAnno(this.reset,! ap(countReg(0)) |-> F ! G ap(countReg(0)))
  // svaSeqAnno.makeSVAAnno(this.reset, |- ap(!countReg(0)) -|)
  io.dout := countReg
  // assert(countReg(0))
}
