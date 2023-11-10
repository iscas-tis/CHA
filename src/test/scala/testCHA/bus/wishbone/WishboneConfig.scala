package caravan.bus.wishbone

import caravan.bus.common.BusConfig

// no tag support
case class WishboneConfig
(
  /**
   * addressWidth: the address width in bits
   * dataWidth: the data width in bits
   * granularity: the minimal data transfer size over the bus
   * waitState: whether the host can produce wait states during the bus transfer cycle
   */
  addressWidth: Int,
  dataWidth: Int,
  granularity: Int = 8,
  waitState: Boolean = false
) extends BusConfig

