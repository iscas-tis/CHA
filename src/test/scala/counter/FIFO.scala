package counter

import org.scalatest.flatspec._
import chisel3._
import chisel3.util._
import chisel3.experimental.verification
import chiseltest._
import chiseltest.formal._
import chiseltest.formal.svaSeqAnno
import chiseltest.formal.svaSeq._


/*class CounterFormalSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
  behavior of "CounterProp"
  it should "pass" in {
    verify(new CounterProp1(4), Seq(BoundedCheck(100), BtormcEngineAnnotation))
    //verify(new GCD(), Seq(BoundedCheck(20), BtormcEngineAnnotation) )
    //verify(new CounterProp1(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
    //verify(new CounterProp2(4), Seq(BoundedCheck(20), BtormcEngineAnnotation))
  }
}*/
/*class BubbleFifoVerify extends AnyFlatSpec with ChiselScalatestTester with Formal {
    "BubbleFifo" should "pass" in {
    verify(new BubbleFifo(UInt(16.W), 4), Seq(BoundedCheck(20),BtormcEngineAnnotation))
  }
}*/
class DoubleBufferFifoVerify extends AnyFlatSpec with ChiselScalatestTester with Formal {
    behavior of "DoubleBubbleFifo"
    it should "pass" in {
    verify(new DoubleBufferFifo(UInt(1.W), 1), Seq(BoundedCheck(20),BtormcEngineAnnotation))
  }
}


class FifoIO [T <: Data ]( private val gen : T) extends Bundle {
    val enq = Flipped(new DecoupledIO(gen))
    val deq = new DecoupledIO(gen) 
}
abstract class Fifo[T <: Data](gen: T, depth: Int) extends Module { 
    val io = IO(new FifoIO(gen))
    assert(depth > 0, "Number of buffer elements needs to be larger than 0") 
}

class DoubleBufferFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {

  private class DoubleBuffer[T <: Data](gen: T) extends Module {
    val io = IO(new FifoIO(gen))

    val empty :: one :: two :: Nil = Enum(3)
    val stateReg = RegInit(empty)
    val dataReg = Reg(gen)
    val shadowReg = Reg(gen)

    switch(stateReg) {
      is (empty) {
        when (io.enq.valid) {
          stateReg := one
          dataReg := io.enq.bits
        }
      }
      is (one) {
        when (io.deq.ready && !io.enq.valid) {
          stateReg := empty
        }
        when (io.deq.ready && io.enq.valid) {
          stateReg := one
          dataReg := io.enq.bits
        }
        when (!io.deq.ready && io.enq.valid) {
          stateReg := two
          shadowReg := io.enq.bits
        }
      }
      is (two) {
        when (io.deq.ready) {
          dataReg := shadowReg
          stateReg := one
        }

      }
    }

    io.enq.ready := (stateReg === empty || stateReg === one)
    io.deq.valid := (stateReg === one || stateReg === two)
    io.deq.bits := dataReg
    svaSeqAnno.makeSVAAnno(this.reset,  ap((io.enq.valid & !io.deq.ready)) *(2,2) |-> ###(1,1) ap(!io.enq.ready || reset.asBool))
    /*when(past((io.enq.valid & !io.deq.ready))){
        assert(!io.enq.ready)
    }*/
  }

  private val buffers = Array.fill((depth+1)/2) { Module(new DoubleBuffer(gen)) }

  for (i <- 0 until (depth+1)/2 - 1) {
    buffers(i + 1).io.enq <> buffers(i).io.deq
  }
  io.enq <> buffers(0).io.enq
  io.deq <> buffers((depth+1)/2 - 1).io.deq
}

class BubbleFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {

  private class Buffer() extends Module {
    val io = IO(new FifoIO(gen))

    val fullReg = RegInit(false.B)
    val dataReg = Reg(gen)

    when (fullReg) {
      when (io.deq.ready) {
        fullReg := false.B
      }
    } .otherwise {
      when (io.enq.valid) {
        fullReg := true.B
        dataReg := io.enq.bits
      }
    }

    io.enq.ready := !fullReg
    io.deq.valid := fullReg
    io.deq.bits := dataReg

    /*when(!fullReg){
      verification.assert(io.enq.ready)
    }*/
  }

  private val buffers = Array.fill(depth) { Module(new Buffer()) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq <> buffers(i).io.deq
  }

  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq

  
}