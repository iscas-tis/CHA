package caravan.bus.tilelink

import chisel3._ 

// Module to Stall the Response one Cycle

class stallUnit extends Module {

    implicit val config = TilelinkConfig()

    val io = IO(new Bundle{
        val bundle_in = Input(new TilelinkSlave)
        val valid_in = Input(UInt(1.W))
        val bundle_out = Output(new TilelinkSlave)
        val valid_out = Output(UInt(1.W))
    })

    

    val bundle_reg = RegInit(0.U.asTypeOf(new TilelinkSlave))
    val valid_reg = RegInit(0.U(1.W))
    
    bundle_reg := io.bundle_in
    valid_reg := io.valid_in

    io.bundle_out := bundle_reg
    io.valid_out := valid_reg
}