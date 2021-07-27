// See LICENSE for license details.

package chiselTests.experimental

import chisel3._
import chisel3.experimental.dataview._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.stage.ChiselStage
import chiselTests.ChiselFlatSpec

object DataViewTargetSpec {
  import firrtl.annotations._
  private case class DummyAnno(target: ReferenceTarget, id: Int) extends SingleTargetAnnotation[ReferenceTarget] {
    override def duplicate(n: ReferenceTarget) = this.copy(target = n)
  }
  private def mark(d: Data, id: Int) = annotate(new ChiselAnnotation {
    override def toFirrtl: Annotation = DummyAnno(d.toTarget, id)
  })
  private def markAbs(d: Data, id: Int) = annotate(new ChiselAnnotation {
    override def toFirrtl: Annotation = DummyAnno(d.toAbsoluteTarget, id)
  })
}

class DataViewTargetSpec extends ChiselFlatSpec {
  import DataViewTargetSpec._
  private val checks: Seq[Data => String] = Seq(
    _.toTarget.toString,
    _.toAbsoluteTarget.toString,
    _.instanceName,
    _.pathName,
    _.parentPathName,
    _.parentModName,
  )

  var i = 0L

  // Check helpers
  private def checkAll(impl: Data, refs: String*): Unit = {
    refs.size should be (checks.size)
    for ((check, value) <- checks.zip(refs)) {
      i += 1
      println(s"[$i] ${check(impl)} should be $value")
      check(impl) should be (value)
    }
  }
  private def checkSameAs(impl: Data, refs: Data*): Unit =
    for (ref <- refs) {
      checkAll(impl, checks.map(_(ref)):_*)
    }

  behavior of "DataView Naming"

  it should "support views of Elements" in {
    class MyChild extends Module {
      val out = IO(Output(UInt(8.W)))
      val insideView = out.viewAs(UInt())
      out := 0.U
    }
    class MyParent extends Module {
      val out = IO(Output(UInt(8.W)))
      val inst = Module(new MyChild)
      out := inst.out
    }
    val m = elaborateAndGetModule(new MyParent)
    val outsideView = m.inst.out.viewAs(UInt())
    checkSameAs(m.inst.out, m.inst.insideView, outsideView)
  }

  it should "support 1:1 mappings of Aggregates and their children" in {
    class MyBundle extends Bundle {
      val foo = UInt(8.W)
      val bars = Vec(2, UInt(8.W))
    }
    implicit val dv = DataView[MyBundle, Vec[UInt]](_.foo -> _(0), _.bars(0) -> _(1), _.bars(1) -> _(2))
    class MyChild extends Module {
      val out = IO(Output(new MyBundle))
      val outView = out.viewAs(Vec(3, UInt())) // Note different type
      val outFooView = out.foo.viewAs(UInt())
      val outBarsView = out.bars.viewAs(Vec(2, UInt(8.W)))
      val outBars0View = out.bars(0).viewAs(UInt())
      out := 0.U.asTypeOf(new MyBundle)
    }
    class MyParent extends Module {
      val out = IO(Output(new MyBundle))
      val inst = Module(new MyChild)
      out := inst.out
    }
    val m = elaborateAndGetModule(new MyParent)
    val outView = m.inst.out.viewAs(Vec(3, UInt())) // Note different type
    val outFooView = m.inst.out.foo.viewAs(UInt())
    val outBarsView = m.inst.out.bars.viewAs(Vec(2, UInt(8.W)))
    val outBars0View = m.inst.out.bars(0).viewAs(UInt())

    checkSameAs(m.inst.out, m.inst.outView, outView)
    checkSameAs(m.inst.out.foo, m.inst.outFooView, m.inst.outView(0), outFooView, outView(0))
    checkSameAs(m.inst.out.bars, m.inst.outBarsView, outBarsView)
    checkSameAs(m.inst.out.bars(0), m.inst.outBars0View, outBars0View, m.inst.outView(1), outView(1),
      m.inst.outBarsView(0), outBarsView(0))
  }

  // Ideally this would work 1:1 but that requires changing the binding
  it should "support annotation renaming of Aggregate children of Aggregate views" in {
    class MyBundle extends Bundle {
      val foo = Vec(2, UInt(8.W))
    }
    class MyChild extends Module {
      val out = IO(Output(new MyBundle))
      val outView = out.viewAs(new MyBundle)
      mark(out.foo, 0)
      mark(outView.foo, 1)
      markAbs(out.foo, 2)
      markAbs(outView, 3)
      out := 0.U.asTypeOf(new MyBundle)
    }
    class MyParent extends Module {
      val out = IO(Output(new MyBundle))
      val inst = Module(new MyChild)
      out := inst.out
    }
    val (_, annos) = getFirrtlAndAnnos(new MyParent)
    val pairs = annos.collect { case DummyAnno(t, idx) => (idx, t) }.sortBy(_._1)
    pairs.foreach(println)
  }
}
