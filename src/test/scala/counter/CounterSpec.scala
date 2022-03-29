package counter

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import chiseltest._
import chisel3._

/*class CounterSpec extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Counter"
  it should "pass" in {
    test(new Counter(2)) { c =>
      c.io.en.poke(true.B)
      for (i <- 0 until (1 << 2)) {
        c.io.dout.expect(i.U)
        c.clock.step()
      }
      for (i <- 0 until (1 << 2)) {
        c.io.dout.expect(i.U)
        c.clock.step()
      }
      c.io.en.poke(false.B)
      for (i <- 0 until (1 << 2)) {
        c.io.dout.expect(0.U)
        c.clock.step()
      }
    }
  }
}*/
