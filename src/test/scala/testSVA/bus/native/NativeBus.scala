package caravan.bus.native

import caravan.bus.common.{AbstrRequest, AbstrResponse}
import chisel3._

class NativeRequest(implicit val config: NativeConfig) extends AbstrRequest {
  override val addrRequest: UInt = UInt(config.addressWidth.W)
  override val dataRequest: UInt = UInt(config.dataWidth.W)
  override val activeByteLane: UInt = UInt(config.byteLane.W)
  override val isWrite: Bool = Bool()
}

class NativeResponse(implicit val config: NativeConfig) extends AbstrResponse {
  override val dataResponse: UInt = UInt(config.dataWidth.W)
  override val error: Bool = Bool()
}

