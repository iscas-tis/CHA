// See README.md for license details.

package testCHA.GCD

import chisel3._
import chisel3.util.Decoupled
import chiseltest.formal._
import chiseltest.formal.chaAnno._

class GcdInputBundle(val w: Int) extends Bundle {
  val value1 = UInt(w.W)
  val value2 = UInt(w.W)
}

class GcdOutputBundle(override val w: Int) extends GcdInputBundle(w) {
  val gcd    = UInt(w.W)
}

/**
  * Compute Gcd using subtraction method.
  * Subtracts the smaller from the larger until register y is zero.
  * value input register x is then the Gcd.
  * Unless first input is zero then the Gcd is y.
  * Can handle stalls on the producer or consumer side
  */
class DecoupledGcd(val bitWidth: Int) extends Module {
  val input = IO(Flipped(Decoupled(new GcdInputBundle(bitWidth))))
  val output = IO(Decoupled(new GcdOutputBundle(bitWidth)))

  val xInitial    = Reg(UInt(bitWidth.W))
  val yInitial    = Reg(UInt(bitWidth.W))
  val x           = Reg(UInt(bitWidth.W))
  val y           = Reg(UInt(bitWidth.W))
  val busy        = RegInit(false.B)
  val resultValid = RegInit(false.B)

  input.ready := ! busy
  output.valid := resultValid
  output.bits := DontCare

  // val cycle = RegInit(0.U(32.W))
  // cycle := cycle + 1.U
  
  // printf("%d xi  %d yi  %d c  %d x  %d y  %d busy  %d valid  out\n",
  //  xInitial, yInitial, cycle, x, y, busy.asUInt, resultValid.asUInt)

  when(busy)  {
    when(x >= y) {
      x := x - y
    }.otherwise {
      y := y - x
    }
    when(x === 0.U || y === 0.U) {
      when(x === 0.U) {
        output.bits.gcd := y
      }.otherwise {
        output.bits.gcd := x
      }

      output.bits.value1 := xInitial
      output.bits.value2 := yInitial
      resultValid := true.B
      busy := false.B

      when(resultValid) {
        resultValid := false.B
      }
    }
  }.otherwise {
    when(input.valid) {
      val bundle = input.deq()
      x := bundle.value1
      y := bundle.value2
      xInitial := bundle.value1
      yInitial := bundle.value2
      busy := true.B
    }
  }
  // val nBusy = !busy
  // val temp = output.valid
  // chaAssert(this,"busy |-> ##[1:15] nBusy") 
  // chaAssert(this,"busy |-> ##[1:16] nBusy") 
}

// class DecoupledGcd_4() extends Module
// {
//   val input = IO(Flipped(Decoupled(new GcdInputBundle(4))))
//   val output = IO(Decoupled(new GcdOutputBundle(4)))
//   val s1 = Module(new DecoupledGcd(4))
//   output.bits.value1 := s1.output.bits.value1
//   input.ready := s1.input.ready
//   output.bits.gcd := s1.output.bits.gcd
//   output.valid := s1.output.valid
//   s1.input.valid := input.valid
//   output.bits.value2 :=  s1.output.bits.value2
//   s1.output.ready := output.ready
//   s1.input.bits.value2 := input.bits.value2
//   s1.input.bits.value1 := input.bits.value1
//   // s1.input := input
//   // input
// }