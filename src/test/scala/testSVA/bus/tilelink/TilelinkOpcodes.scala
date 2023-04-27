package caravan.bus.tilelink

trait OpCodes {
    val Get = 4
    val AccessAckData = 1
    val PutFullData = 0
    val PutPartialData = 1
    val AccessAck = 0
}