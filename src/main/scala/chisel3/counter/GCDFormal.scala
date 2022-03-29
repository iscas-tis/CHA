package counter

import chisel3._
import chisel3.experimental._

import chisel3._
import chisel3.util._

class BlackBoxSwap extends BlackBox with HasBlackBoxInline  {
//class BlackBoxRealSwap extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    //val clk = Input(Clock())
      //val reset = Input(Bool())
    val out2 = Output(UInt(16.W))
    val out1 = Output(UInt(16.W))
    val in2 = Input(UInt(16.W))
    val in1 = Input(UInt(16.W))
  })


  //setResource("/real_swap.v")

  setInline("BlackBoxSwap.v",
    s"""
       |module BlackBoxSwap (
       |  input  [15:0] in1,
       |  input  [15:0] in2,
       |  output [15:0] out1,
       |  output [15:0] out2
       |);
       |
       |assign out1 = in2;
       |assign out2 = in1;
       |
       |endmodule
    """.stripMargin)

}

/**
  * Compute GCD using subtraction method.
  * Subtracts the smaller from the larger until register y is zero.
  * value in register x is then the GCD
  */
class GCD extends Module {
  val io = IO(new Bundle {
    val value1        = Input(UInt(16.W))
    val value2        = Input(UInt(16.W))
    val loadingValues = Input(Bool())
    val outputGCD     = Output(UInt(16.W))
    val outputValid   = Output(Bool())
  })

  val x  = Reg(UInt())
  val y  = Reg(UInt())

  val swap = Module(new BlackBoxSwap)

  when(x > y) { x := x - y }
    .otherwise { y := y - x }

  when(io.loadingValues) {
    //x := io.value1
    //y := io.value2
    swap.io.in1 := io.value1
    swap.io.in2 := io.value2

    x := swap.io.out1
    y := swap.io.out2
  }

  io.outputGCD := x
  io.outputValid := y === 0.U
}

object GCDFormal extends App {
  //val a = 1 
  //val seq1 = TSequence(Seq(AtmProp(true.asBool),TimeOp(2,3)))
  (new chisel3.stage.ChiselStage).emitVerilog(new GCD, Array("--target-dir", "build"))
  //(new chisel3.stage.ChiselStage).emitVerilog(new Counter(5), Array("--target-dir", "build"))
  //(new chisel3.stage.ChiselStage).emitFirrtl(new CounterProp(5), Array("--target-dir", "build"))
}