package counter

import chisel3._

class Counter(width: Int) extends Module {
  val io = IO(new Bundle {
    //val en   = Input(Bool())
    val dout = Output(UInt(width.W))
  })

  val countReg = RegInit(0.U(width.W))

  countReg := countReg + 1.U
  
  /*when(countReg === 4.asUInt)
  {
    println(s"countReg(0): $countReg[0],  countReg(2): ${countReg(2).asInstanceOf[Int]}")
  }*/
  /*when(io.en) {
    
  }*/

  io.dout := countReg
}
