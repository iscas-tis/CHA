package caravan.bus.tilelink
import caravan.bus.common.HostAdapter
import chisel3._
import chisel3.experimental.DataMirror
import chisel3.stage.ChiselStage
import chisel3.util.{Decoupled, Enum, MuxLookup,MuxCase}

class TilelinkHost(implicit val config: TilelinkConfig) extends HostAdapter with OpCodes {
    val io = IO(new Bundle {
        val tlMasterTransmitter = Decoupled(new TilelinkMaster())
        val tlSlaveReceiver  = Flipped(Decoupled(new TilelinkSlave()))
        val reqIn = Flipped(Decoupled(new TLRequest()))
        val rspOut = Decoupled(new TLResponse())
    })

    def fire: Bool = io.reqIn.valid && io.tlMasterTransmitter.ready

    //FSM for indicating valid response only when the response comes.
    val idle :: latch_data :: Nil = Enum(2)
    val stateReg = RegInit(idle)
    val respReg = RegInit(false.B)
    val readyReg = RegInit(true.B)

    when(fire) {
        readyReg := false.B
    }
    when(stateReg === latch_data) {
        readyReg := true.B
    }

    io.tlSlaveReceiver.ready := true.B
    io.reqIn.ready := readyReg


    when(io.reqIn.valid){
        io.tlMasterTransmitter.bits.a_opcode := Mux(readyReg, Mux(io.reqIn.bits.isWrite, Mux(io.reqIn.bits.activeByteLane === "b1111".U, PutFullData.U, PutPartialData.U) , Get.U), 2.U)
        io.tlMasterTransmitter.bits.a_data := io.reqIn.bits.dataRequest
        io.tlMasterTransmitter.bits.a_address := io.reqIn.bits.addrRequest
        io.tlMasterTransmitter.bits.a_param := 0.U
        io.tlMasterTransmitter.bits.a_source := 2.U 
        io.tlMasterTransmitter.bits.a_size := MuxLookup(config.w.U, 2.U,Array(                    // default 32-bit
                                                                                (1.U) -> 0.U,
                                                                                (2.U) -> 1.U,
                                                                                (4.U) -> 2.U,
                                                                                (8.U) -> 3.U
                                                                            ).toIndexedSeq)
        io.tlMasterTransmitter.bits.a_mask := io.reqIn.bits.activeByteLane
        io.tlMasterTransmitter.bits.a_corrupt := false.B
        io.tlMasterTransmitter.valid := io.reqIn.valid

    } otherwise {
        io.tlMasterTransmitter.bits.a_opcode := 2.U         // 2 is used for DontCare
        io.tlMasterTransmitter.bits.a_data := DontCare
        io.tlMasterTransmitter.bits.a_address := DontCare
        io.tlMasterTransmitter.bits.a_param := DontCare
        io.tlMasterTransmitter.bits.a_source := DontCare
        io.tlMasterTransmitter.bits.a_size := DontCare
        io.tlMasterTransmitter.bits.a_mask := DontCare
        io.tlMasterTransmitter.bits.a_corrupt := DontCare
        io.tlMasterTransmitter.valid := false.B
    }

    // response is valid when either acknowledment or error is coming back.
    respReg := MuxCase(false.B,Array(
        ((io.tlSlaveReceiver.bits.d_opcode === AccessAck.U || io.tlSlaveReceiver.bits.d_opcode === AccessAckData.U) && !io.tlSlaveReceiver.bits.d_denied) -> true.B,
        (io.tlSlaveReceiver.bits.d_denied & io.tlSlaveReceiver.valid) -> true.B,
    ).toIndexedSeq)
    

    when(stateReg === idle){
        stateReg := Mux(
            (io.tlSlaveReceiver.bits.d_denied |
            (io.tlSlaveReceiver.bits.d_opcode === AccessAck.U || io.tlSlaveReceiver.bits.d_opcode === AccessAckData.U)),
            latch_data,
            idle
        )
    }.elsewhen(stateReg === latch_data){
        respReg := false.B                  // response is invalid for idle state
        stateReg := idle
    }
    


    // only valid resp is Reg'ed because data and error are coming from device after being stalled already.
    io.rspOut.bits.dataResponse := io.tlSlaveReceiver.bits.d_data  
    io.rspOut.bits.error := io.tlSlaveReceiver.bits.d_denied
    io.rspOut.valid := respReg
    




}