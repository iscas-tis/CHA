package counter

import chisel3._
import chiseltest.formal._

class CounterProp(width: Int) extends Counter(width) {
  when(past(io.en)) {
    assert(countReg === past((countReg + 1.U)(width - 1, 0),3))
    //assert(countReg === past((countReg + 1.U)(width - 1, 0),3))
  }
}

object CounterFormal extends App {
  (new chisel3.stage.ChiselStage).emitFirrtl(new Counter(5), Array("--target-dir", "build"))
  //(new chisel3.stage.ChiselStage).emitVerilog(new Counter(5), Array("--target-dir", "build"))
  //(new chisel3.stage.ChiselStage).emitFirrtl(new CounterProp(5), Array("--target-dir", "build"))
}
