package counter

import chisel3._
import chiseltest.formal._
import chiseltest.formal.TTSeq._
import scala.language.implicitConversions
import chisel3.experimental.{ChiselAnnotation,annotate}
import firrtl.annotations.{Annotation, ReferenceTarget, SingleTargetAnnotation, Target}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

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
          case TimeOp(lc,hc) => Seq(TimeOpAnno(lc,hc)) 
          case Implication() => Seq(ImplicationAnno())
          case NotOp() => Seq(NotAnno())
          case Leftbraket() => Seq(LeftbraketAnno())
          case Rightbraket() => Seq(RightbraketAnno())
          case FinallOp() => Seq(FinallAnno())
          case GlobalOp() => Seq(GlobalAnno())
          case NextOp() => Seq(NextAnno())
          case RepetOp() => Seq(RepetAnno())
          } 

        new SVAAnno(svaanotation)
      }
    })
  }
  //countReg(0)
  //implicit def uint2Atm(signal:UInt): AtmProp = new AtmProp(signal)    
  printlnTSeq(F (###(2, 3) ###(2, 3) ap(countReg(0)) ###(2, 3) ap(countReg(0)) ) |-> ap(countReg(1)) |-> ap(countReg(0)) )
  //makeSVAAnno(Seq(FinalOp(), Leftbraket(), Leftbraket(), NextOp(), GlobalOp(), AtmProp(countReg(0)), ))
  makeSVAAnno(Seq(NotOp(), Leftbraket(), AtmProp(countReg(0)), RepetOp(), Implication(), FinallOp(), AtmProp(countReg(0)), Rightbraket()))
  io.dout := countReg
  assert(countReg === past((countReg + 1.U)(width - 1, 0),1))
}

object Macros {

  // write macros here
  def getName(x: Any): String = macro impl

  def impl(c: Context)(x: c.Tree): c.Tree = {
    import c.universe._
    val p = x match {
      case Select(_, TermName(s)) => s
      case _ => ""
    }
    q"$p"
  }
}