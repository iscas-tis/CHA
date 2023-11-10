package caravan.bus.common
// import caravan.bus.wishbone.Peripherals

import scala.collection._
import chisel3._
import chisel3.util.{MuxCase, log2Ceil}

/** creating a type inside object so that it can be easily imported
 * BusMap type provides a short way of mentioning mutable.Map[Peripheral, (UInt, UInt)] everywhere */
object BusMap {
  type BusMap = mutable.Map[Peripherals.Type, (UInt, UInt, DeviceAdapter)]
}

import BusMap._
/** This class provides the user an API to define peripherals and their address mapping */
class AddressMap {
  private val map: BusMap = mutable.Map[Peripherals.Type, (UInt, UInt, DeviceAdapter)]()
  /** addDevice provides the user to add each device to the address map */
  /** FIXME: there is no restriction on adding two peripherals with same base address.
   * logically this should never happen, however user can add two peripherals with same base addresses
   * there is no check for this and would break the code in later steps when we decode the addr of a peripheral */
  def addDevice(peripheral: Peripherals.Type, baseAddr: UInt, addrMask: UInt, devAdapter: DeviceAdapter): Unit = map += (peripheral -> (baseAddr, addrMask, devAdapter))
  /** an helper function that returns the map [Encapsulation] */
  def getMap(): BusMap = map
  def getDevices: Seq[(DeviceAdapter, Peripherals.Type)] = {
    getMap().map(f => (f._2._3, f._1)).toSeq
  }
}

/** BusDecoder provides an helpful utility to decode the address and send a device sel to the bus switch
 *  with which different peripherals are connected */
object BusDecoder {
  /** decode takes the addr from the host and an address map and figures out which peripheral's id should be sent
   *  as dev sel to the switch */
  def decode(addr: UInt, addressMap: AddressMap): UInt = {
    // contains which peripheral's addr hit
    val addr_hit = Wire(Vec(addressMap.getMap().size, Bool()))
    // contains the id of the matched peripheral
    val id = Wire(Vec(addressMap.getMap().size, UInt(log2Ceil(addressMap.getMap().size + 1).W)))

    /** looping over the map to:
     * 1) assert the appropriate wire of the peripheral with whom the received address matches
     * 2) store the id of that peripheral */
    for (i <- 0 until addressMap.getMap().size) {
      addr_hit(i) := (~addressMap.getMap().toList(i)._2._2 & addr) === addressMap.getMap().toList(i)._2._1
      id(i) := Mux((~addressMap.getMap().toList(i)._2._2 & addr) === addressMap.getMap().toList(i)._2._1,  // condition
                    addressMap.getMap().toList(i)._1.asUInt,  // if true
                    addressMap.getMap().size.U)             // if false send the id as the total size which has err module connected
    }

    /** creating cases for the MuxCase construct.
     * addr_hit zip id -> something like this:
     * ((true.B, 1.U), (false.B, 2.U))
     * returning a sequence for MuxCase
     * where if bool is true the id it contains will be forwarded
     * else by defualt size will be forwarded
     * with which the error module will be connected */

    val cases = addr_hit zip id map { case (bool, id) =>
      bool -> id
    }

    MuxCase(addressMap.getMap().size.U, cases)
  }
}
