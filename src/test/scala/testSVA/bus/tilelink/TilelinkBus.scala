package caravan.bus.tilelink
import chisel3._
import caravan.bus.common.{AbstrRequest, AbstrResponse, BusDevice, BusHost}



class TLRequest(implicit val config: TilelinkConfig) extends AbstrRequest {
  override val addrRequest: UInt = UInt(config.a.W)
  override val dataRequest: UInt = UInt((config.w * 8).W)
  override val activeByteLane: UInt = UInt(config.w.W)
  override val isWrite: Bool = Bool()
}

class TLResponse(implicit val config: TilelinkConfig) extends AbstrResponse {
  override val dataResponse: UInt = UInt((config.w * 8).W)
  override val error: Bool = Bool()
}

// channel A -- Request Channel
class TilelinkMaster(implicit val config: TilelinkConfig) extends TLHost {
  
    val a_opcode = UInt(3.W)
    val a_param = UInt(3.W)
    val a_size = UInt(config.z.W)
    val a_source = UInt(config.o.W)
    val a_address = UInt(config.a.W)
    val a_mask = UInt(config.w.W)
    val a_corrupt = Bool()
    val a_data = UInt((config.w * 8).W)

}

// channel D -- Response Channel
class TilelinkSlave(implicit val config: TilelinkConfig) extends TLDevice {
    val d_opcode = UInt(3.W)
    val d_param = UInt(2.W)
    val d_size = UInt(config.z.W)
    val d_source = UInt(config.o.W)
    val d_sink = UInt(config.i.W)  
    val d_denied = Bool()
    val d_corrupt = Bool()
    val d_data = UInt((config.w * 8).W)
}

case class TLHost() extends BusHost
case class TLDevice() extends BusDevice



// Channel A pin details

//          Signal Name  | No of Bits           | Description                                                                                         
//          a_opcode     | [2:0]                | Request opcode (read, write, or partial write)                                                      
//          a_param      | [2:0]                | Unused/Ignored                                                                                      
//          a_size       | config.z [2:0]       | Request size (requested size is 2^a_size, thus 0 = byte, 1 = 16b, 2 = 32b, 3 = 64b, etc)            
//          a_source     | config.o [8:0]       | Request identifier of configurable width                                                            
//          a_address    | config.a [32:0]      | Request address of configurable width                                                               
//          a_mask       | config.w [4:0]       | Write strobe, one bit per byte indicating which lanes of data are valid for this write request      
//          a_corrupt    | Bool                 |  Reserved; must be 0                                                                                
//          a_data       | config.w x 8 [32:0]  | Write request data of configurable width                                                            


// Channel D pin details

//          Signal Name  | No of Bits           | Description                                                                                         
//          d_opcode     | [2:0]                | Response opcode (Ack or Data)                                                                       
//          d_param      | [2:0]                | Response parameter (unused)                                                                         
//          d_size       | config.z [2:0]       | Response data size                                                                                  
//          d_source     | config.o [8:0]       | Bouncing of request ID of configurable width                                                        
//          d_sink       | config.i [0]         | Response ID of configurable width (possibly unused)                                                 
//          d_denied     | Bool                 | The slave was unable to service the request.                                                        
//          d_corrupt    | Bool                 | Reserved; must be 0                                                                                 
//          d_data       | config.w x 8 [32:0]  | Response data of configurable width                                                                 
