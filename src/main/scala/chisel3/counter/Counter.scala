package counter

import chisel3._
import chiseltest.formal._
import chisel3.experimental.{ChiselAnnotation,annotate}
import firrtl.annotations.{Annotation, ReferenceTarget, SingleTargetAnnotation, Target}

class Counter(width: Int) extends Module {
  val io = IO(new Bundle {
    //val en   = Input(Bool())
    val dout = Output(UInt(width.W))
  })
  val countReg = RegInit(0.U(width.W))
  val testReg = RegInit(0.U(width.W))
  val vis_countReg = countReg(0)
  val vis_testReg = testReg(2)
  countReg := countReg + 1.U
  def makeSVAAnno(sva: Seq[TSeqElement]) = {
    annotate(new ChiselAnnotation {
      // Conversion to FIRRTL Annotation 
      override def toFirrtl: Annotation = 
      {
        val svaanotation : Seq[Seq[TSeqElementAnno]] = sva map {
          case AtmProp(ap) => Seq(AtmPropAnno(ap.toTarget))
          case TimeOp(lc,hc) => Seq(TimeOpAnno(lc,hc)) } 
        new SVAAnno(svaanotation)
      }
    })
  }
  makeSVAAnno(Seq(AtmProp(countReg(0)),TimeOp(1,2),AtmProp(testReg(0))))
  io.dout := countReg
  // assert(countReg === past((countReg + 1.U)(width - 1, 0),1))
}
