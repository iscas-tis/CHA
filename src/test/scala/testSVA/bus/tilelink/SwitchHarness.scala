package caravan.bus.tilelink
import caravan.bus.common.{AddressMap, BusDecoder, DeviceAdapter, Switch1toN, DummyMemController, Peripherals}
import chisel3._
import chisel3.ChiselEnum
import chisel3.stage.ChiselStage
import chisel3.util.{Cat, Decoupled, MuxLookup}


class SwitchHarness/*(programFile: Option[String])*/(implicit val config: TilelinkConfig) extends Module {
  val io = IO(new Bundle {
    val valid = Input(Bool())
    val addrReq = Input(UInt(config.a.W))
    val dataReq = Input(UInt((config.w * 8).W))
    val byteLane = Input(UInt(config.w.W))
    val isWrite = Input(Bool())

    val validResp = Output(Bool())
    val dataResp = Output(UInt(32.W))
    val errResp = Output(Bool())
  })

  implicit val request = new TLRequest()
  implicit val response = new TLResponse()

  val host = Module(new TilelinkHost())
  val dccmDev = Module(new TilelinkDevice())
  val gpioDev = Module(new TilelinkDevice())
  val memCtrl = Module(new DummyMemController())
  val gpioCtrl = Module(new DummyGpioController())
  val tlErr = Module(new TilelinkErr())

  val addressMap = new AddressMap
  addressMap.addDevice(Peripherals.DCCM, "h40000000".U(32.W), "h00000fff".U(32.W), dccmDev)
  addressMap.addDevice(Peripherals.GPIO, "h40001000".U(32.W), "h00000fff".U(32.W), gpioDev)
  val devices = addressMap.getDevices

  val switch = Module(new Switch1toN[TLHost, TLDevice](new TilelinkMaster(), new TilelinkSlave(), devices.size))


  host.io.rspOut.ready := true.B  // IP always ready to accept data from wb host
  host.io.reqIn.valid := Mux(host.io.reqIn.ready, io.valid, false.B)
  host.io.reqIn.bits.addrRequest := io.addrReq
  host.io.reqIn.bits.dataRequest := io.dataReq
  host.io.reqIn.bits.activeByteLane := io.byteLane
  host.io.reqIn.bits.isWrite := io.isWrite

  switch.io.hostIn <> host.io.tlMasterTransmitter
  switch.io.hostOut <> host.io.tlSlaveReceiver

  for (i <- 0 until devices.size) {
    switch.io.devIn(devices(i)._2.litValue.toInt) <> devices(i)._1.asInstanceOf[TilelinkDevice].io.tlSlaveTransmitter
    switch.io.devOut(devices(i)._2.litValue.toInt) <> devices(i)._1.asInstanceOf[TilelinkDevice].io.tlMasterReceiver
  }

  switch.io.devOut(devices.size) <> tlErr.io.tlMasterReceiver
  switch.io.devIn(devices.size) <> tlErr.io.tlSlaveTransmitter

  switch.io.devSel := BusDecoder.decode(host.io.tlMasterTransmitter.bits.a_address, addressMap)
  dccmDev.io.reqOut <> memCtrl.io.req
  dccmDev.io.rspIn <> memCtrl.io.rsp

  gpioDev.io.reqOut <> gpioCtrl.io.req
  gpioDev.io.rspIn <> gpioCtrl.io.rsp

  io.dataResp := host.io.rspOut.bits.dataResponse
  io.validResp := host.io.rspOut.valid
  io.errResp := host.io.rspOut.bits.error

}


class DummyGpioController(implicit val config: TilelinkConfig) extends Module {
  val io = IO(new Bundle {
    val req = Flipped(Decoupled(new TLRequest()))
    val rsp = Decoupled(new TLResponse())
  })

  val addr_wire = io.req.bits.addrRequest

  val err_rsp_wire = WireInit(false.B)
  val data_rsp_wire = Wire(UInt((config.w * 8).W))
  val valid_rsp_wire = WireInit(false.B)

  data_rsp_wire := DontCare

  val errReg = RegInit(false.B)
  val dataReg = RegInit(0.U((config.w * 8).W))
  val validReg = RegInit(false.B)

  object GpioRegisters extends ChiselEnum {
    val OUTPUT_EN_REG = Value(0.U)
    val WDATA_REG = Value(4.U)
    val RDATA_REG = Value(8.U)
  }

  def isRegisterFound(addr: UInt): Bool = {
    GpioRegisters.all.map(g => g.asUInt === addr).reduce((a,b) => a || b)
  }


  /** FIXME: Assuming GPIO will always have less than 64 registers available
   * that is why taking 6 bits wire for addressing */
  val offset = Wire(UInt(6.W))   // 6 bit wire

  offset := io.req.bits.addrRequest

  io.req.ready := true.B    // always ready to accept req from the bus

  val registers = RegInit(VecInit(Seq.fill(GpioRegisters.all.size)(0.U(32.W))))

  when(io.req.fire && io.req.bits.isWrite) {
    // WRITE
    valid_rsp_wire := true.B
    when(isRegisterFound(offset)) {
      // correct address for a register found
      val accessed_reg = registers(offset/4.U)
      accessed_reg := io.req.bits.dataRequest
    } .otherwise {
      // no correct address found, send an error response
      err_rsp_wire := true.B
    }
  } .elsewhen(io.req.fire && !io.req.bits.isWrite) {
    // READ
    valid_rsp_wire := true.B
    when(isRegisterFound(offset)) {
      val accessed_reg = registers(offset/4.U)
      data_rsp_wire := accessed_reg
    } .otherwise {
      err_rsp_wire := true.B
    }

  }

  validReg := valid_rsp_wire
  errReg := err_rsp_wire
  dataReg := data_rsp_wire

  io.rsp.valid := validReg
  io.rsp.bits.error := errReg
  io.rsp.bits.dataResponse := dataReg

}