package caravan.bus.tilelink
import caravan.bus.common.{AddressMap, BusDecoder, DeviceAdapter, Switch1toN, DummyMemController}
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.stage.ChiselStage
import chisel3.util.{Cat, Decoupled, MuxLookup}
import chisel3.util.experimental.loadMemoryFromFile


class Harness/*(programFile: Option[String])*/(implicit val config: TilelinkConfig) extends Module {
  val io = IO(new Bundle {
    val valid = Input(Bool())
    val addrReq = Input(UInt(config.a.W))
    val dataReq = Input(UInt((config.w * 8).W))
    val byteLane = Input(UInt(config.w.W))
    val isWrite = Input(Bool())

    val validResp = Output(Bool())
    val dataResp = Output(UInt(32.W))
  })

  implicit val request = new TLRequest()    
  implicit val response = new TLResponse()

  val tlHost = Module(new TilelinkHost())
  val tlSlave = Module(new TilelinkDevice())
  val memCtrl = Module(new DummyMemController())

  tlHost.io.rspOut.ready := true.B  // IP always ready to accept data from wb host

  tlHost.io.tlMasterTransmitter <> tlSlave.io.tlMasterReceiver
  tlSlave.io.tlSlaveTransmitter <> tlHost.io.tlSlaveReceiver

  tlHost.io.reqIn.valid := Mux(tlHost.io.reqIn.ready, io.valid, false.B)
  tlHost.io.reqIn.bits.addrRequest := io.addrReq
  tlHost.io.reqIn.bits.dataRequest := io.dataReq
  tlHost.io.reqIn.bits.activeByteLane := io.byteLane
  tlHost.io.reqIn.bits.isWrite := io.isWrite



  tlSlave.io.reqOut <> memCtrl.io.req
  tlSlave.io.rspIn <> memCtrl.io.rsp

  io.dataResp := tlHost.io.rspOut.bits.dataResponse
  io.validResp := tlHost.io.rspOut.valid

}


