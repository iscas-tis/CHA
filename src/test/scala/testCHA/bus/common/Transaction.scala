package caravan.bus.common
import chisel3._

/**
 * This abstract class provides a template for other protocols to implement the transaction wires.
 * This is used as a template for e.g when the core wants to communicate with the memory or with the peripheral registers.
 * It will set these signals up in order to talk to the Host adapter of the relevant bus protocol
 */
abstract class AbstrRequest extends Bundle {
  val addrRequest: UInt
  val dataRequest: UInt
  val activeByteLane: UInt
  val isWrite: Bool
}

abstract class AbstrResponse extends Bundle {
  val dataResponse: UInt
  val error: Bool
}

/** The BusHost and BusDevice bundle classes
 * are common classes that each protocol's
 * Master and Slave bundles will extend (beneficial for type parameterization) */
class BusHost extends Bundle
class BusDevice extends Bundle

/** The HostAdapter and DeviceAdapter is a class from which each host/device adapter
 * of a specific bus protocol will extend (beneficial for switch) */
abstract class DeviceAdapter extends Module
abstract class HostAdapter extends Module

// created a trait so that each specific bus protocol
// can extend from it (beneficial for type paremterization)
trait BusConfig