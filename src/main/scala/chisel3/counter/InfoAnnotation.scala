package chisel3.counter


import chisel3._
import chisel3.experimental.{annotate, ChiselAnnotation, RunFirrtlTransform}
import chisel3.internal.InstanceId

import firrtl._
import firrtl.annotations.{Annotation, SingleTargetAnnotation}
import firrtl.annotations.{CircuitTarget, ModuleTarget, InstanceTarget, ReferenceTarget, Target}

case class InfoAnnotation(target: Target, info: String) extends SingleTargetAnnotation[Target] {
  def duplicate(newTarget: Target) = this.copy(target = newTarget)
}

class InfoTransform() extends Transform with DependencyAPIMigration {

  override def prerequisites = firrtl.stage.Forms.HighForm

  override def execute(state: CircuitState): CircuitState = {
    //println("Starting transform 'IdentityTransform'")

    val annotationsx = state.annotations.flatMap{
      case InfoAnnotation(a: CircuitTarget, info) =>
        println(s"  - Circuit '${a.serialize}' annotated with '$info'")
        None
      case InfoAnnotation(a: ModuleTarget, info) =>
        println(s"  - Module '${a.serialize}' annotated with '$info'")
        None
      case InfoAnnotation(a: InstanceTarget, info) =>
        println(s"  - Instance '${a.serialize}' annotated with '$info'")
        None
      case InfoAnnotation(a: ReferenceTarget, info) =>
        println(s"  - Component '${a.serialize} annotated with '$info''")
        None
      case a =>
        Some(a)
    }

    state.copy(annotations = annotationsx)
  }
}

object InfoAnnotator {
  def info(component: InstanceId, info: String): Unit = {
    annotate(new ChiselAnnotation with RunFirrtlTransform {
      def toFirrtl: Annotation = InfoAnnotation(component.toTarget, info)
      def transformClass = classOf[InfoTransform]
    })
  }
}

class ModC(widthC: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(widthC.W))
    val out = Output(UInt(widthC.W))
  })
  io.out := io.in

  InfoAnnotator.info(this, s"ModC($widthC)")

  InfoAnnotator.info(io.out, s"ModC(ignore param)")
}

import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
object AnnotationTest extends App{
    (new ChiselStage).emitFirrtl(new ModC(4), Array("--target-dir", "build"))
    //execute(Array.empty, Seq(ChiselGeneratorAnnotation(() => new ModC(4))))
}
