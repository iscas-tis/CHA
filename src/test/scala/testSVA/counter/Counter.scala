package counter

import scala.reflect.runtime.{universe => ru}
import chisel3._
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.svaSeq._
import chisel3.experimental.{ChiselAnnotation,annotate,RunFirrtlTransform}
import firrtl.annotations.{Annotation, ReferenceTarget, SingleTargetAnnotation, Target,MultiTargetAnnotation}
import scala.language.reflectiveCalls

class Counter(width: Int) extends Module {
  val io = IO(new Bundle {
    //val en   = Input(Bool())
    val dout = Output(UInt(width.W))
  })
  val countReg = RegInit(0.U(width.W))
  val ccountReg = RegInit(0.U(width.W))
  countReg := countReg + 1.U
  
  val bs = countReg(0)
  val ts = countReg(3)
  // assert(countReg(0))
  svaAssert(this,"bs |-> ##[0:8] ts") 
  // svaAssert(this, "G (bs ##[1:$] ts)[*2:3] U (X (bs |-> F ts))")
  io.dout := countReg
}
