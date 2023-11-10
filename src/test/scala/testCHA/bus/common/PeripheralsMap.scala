package caravan.bus.common
import chisel3._
import chisel3.ChiselEnum

object Peripherals extends ChiselEnum {
  val GPIO = Value(0.U)
  val DCCM = Value(1.U)
}
