package counter

import chisel3._

class Counter(width: Int) extends Module {
  val io = IO(new Bundle {
    val en   = Input(Bool())
    val dout = Output(UInt(width.W))
  })

  val countReg = RegInit(0.U(width.W))

  when(io.en) {
    countReg := countReg + 1.U
  }

  io.dout := countReg
}
