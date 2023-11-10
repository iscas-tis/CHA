package caravan.bus.tilelink

import caravan.bus.common.BusConfig


case class TilelinkConfig
(

    /*
    
    w => Width of the data bus in bytes.                                  || DUW => width of device user bits, default 4
    a => Width of each address field in bits.                             || AW => width of address bus, default 32
    z => Width of each size field in bits.                                || SZW => size width, covers 2^(x) <= DBW; (2 bit for 4B)
    o => Number of bits needed to disambiguate per-link master sources.   || AIW => width of address source (ID) bus, default 8
    i => Number of bits needed to disambiguate per-link slave sinks.      || DIW => width of sink bits, default 1

    */

    val w: Int = 4,
    val a: Int = 32,
    val z: Int = 2,
    val o: Int = 8,
    val i: Int = 1,

) extends BusConfig

