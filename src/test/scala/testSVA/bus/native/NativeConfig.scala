package caravan.bus.native

import caravan.bus.common.BusConfig

case class NativeConfig
(
  addressWidth: Int,
  dataWidth: Int,
  byteLane: Int = 4
) extends BusConfig
