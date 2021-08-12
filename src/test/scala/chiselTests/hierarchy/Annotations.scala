package chiselTests.hierarchy

import _root_.firrtl.annotations._
import chisel3.experimental.{annotate, BaseModule}
import chisel3.{Instance, Data}

object Annotations {
  case class MarkAnnotation(target: IsMember, tag: String) extends SingleTargetAnnotation[IsMember] {
    def duplicate(n: IsMember): Annotation = this.copy(target = n)
  }
  case class MarkChiselInstanceAnnotation[B <: BaseModule](d: Instance[B], tag: String, isAbsolute: Boolean) extends chisel3.experimental.ChiselAnnotation {
    def toFirrtl = MarkAnnotation(d.toTarget, tag)//if(isAbsolute) MarkAnnotation(d.toAbsoluteTarget, tag) else MarkAnnotation(d.toTarget, tag)
  }
  case class MarkChiselAnnotation(d: Data, tag: String, isAbsolute: Boolean) extends chisel3.experimental.ChiselAnnotation {
    def toFirrtl = if(isAbsolute) MarkAnnotation(d.toAbsoluteTarget, tag) else MarkAnnotation(d.toTarget, tag)
  }
  def mark(d: Data, tag: String): Unit = annotate(MarkChiselAnnotation(d, tag, false))
  def mark[B <: BaseModule](d: Instance[B], tag: String): Unit = annotate(MarkChiselInstanceAnnotation(d, tag, false))
  def amark(d: Data, tag: String): Unit = annotate(MarkChiselAnnotation(d, tag, true))
  def amark[B <: BaseModule](d: Instance[B], tag: String): Unit = annotate(MarkChiselInstanceAnnotation(d, tag, true))
}
