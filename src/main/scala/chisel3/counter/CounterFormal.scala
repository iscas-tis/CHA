package counter

import chisel3._
import chiseltest.formal._

class CounterProp(width: Int) extends Counter(width) {
  when(past(io.en)) {
    //assert(countReg === past((countReg + 1.U)(width - 1, 0),3))
    //assert(TSequence(Seq(AtmProp(countReg(0)),TimeOp(2,2),Implication(),TimeOp(1,1),Implication(),TimeOp(3,3),AtmProp(countReg(3)))))
    assert(TSequence(Seq(TimeOp(1,2),AtmProp(countReg(3)))))
    //assert(TSeq(a, ##1, b)) == 
    //assert(b & past(a, 2)) 3
    //assert(a & past(b,-2)) 1
    //0 1 2
    //assert(countReg === past((countReg + 1.U)(width - 1, 0),3))
  }
}

object CounterFormal extends App {
  //val a = 1 
  //val seq1 = TSequence(Seq(AtmProp(true.),TimeOp(2,3)))
  //(new chisel3.stage.ChiselStage).emitFirrtl(new Counter(5), Array("--target-dir", "build"))
  //(new chisel3.stage.ChiselStage).emitVerilog(new Counter(5), Array("--target-dir", "build"))
  (new chisel3.stage.ChiselStage).emitFirrtl(new CounterProp(5), Array("--target-dir", "build"))
}
