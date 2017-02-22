// See LICENSE for license details.

package chiselTests

import org.scalatest._
import org.scalatest.prop._

import chisel3._
import chisel3.testers.BasicTester
import chisel3.util._

class PassModuleIO extends Bundle {
  val in = Input(UInt(8.W))
  val out = Output(UInt(8.W))
}

class PassModule extends Module {
  val io = IO(new PassModuleIO)
  io.out := io.in
}

class PassModuleChecker extends Module {
  val io = IO(Flipped(new PassModuleIO))
  io.in := 123.U // flipped from PassModule
  assert(io.out === 123.U)
}

class VecOfModuleTester extends BasicTester {
  val n = 4
  val (cycle, done) = Counter(true.B, n + 1)

  val mods = Vec.fill(4)(Module(new PassModule).io)
  // Default connections
  mods.foreach(_.in := 0.U)

  val c = Module(new PassModuleChecker)

  mods(cycle) <> c.io

  printf(p"@$cycle: $mods\n${c.io}\n")
  when (done) { stop() }  
}

class TestSpec extends ChiselFlatSpec {
  "Vec of modules" should "work" in {
    assertTesterPasses(new VecOfModuleTester)
  }
}

object TestTop extends App {
  println(chisel3.Driver.emit(() => new VecOfModuleTester))
}
