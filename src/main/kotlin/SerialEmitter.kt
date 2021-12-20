
object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.
    enum class Destination {DISPENSER, LCD}
    private const val SCLK = 0x02
    private const val SDX = 0x01
    private const val BUSY = 0x80
    private const val ONE_MASK = 0x01

    // Inicia a classe
    fun init() {
        HAL.setBits(SDX)
        HAL.clrBits(SCLK)
    }

    // Envia uma trama para o SerialReceiver identificado o destino em addr e os bits de dados em
    //‘data’.
    fun send(addr: Destination, data: Int) {
        while (isBusy());    // encquanto estiver a true fica em loop
        val destination = if (addr ==Destination.DISPENSER) 0 else 1  //saber é para o dispenser ou LCD
        var dataToSend = data //??
        var count1s = 0
        var maxSize = if (addr == Destination.DISPENSER) 4 else 5  //Saber quantos bits de dados vamos receber dependendo se é para LCD ou dispenser

        //initialization sequence
        HAL.clrBits(SCLK)
        HAL.setBits(SDX)
        HAL.clrBits(SDX)

        //sending destination
        HAL.writeBits(SDX,destination)
        if (destination and ONE_MASK == 1)
            count1s++

        while (maxSize > 0){
            HAL.setBits(SCLK)
            HAL.writeBits(SDX,dataToSend)

            if (dataToSend and ONE_MASK == 1)
                count1s++
            dataToSend = dataToSend shr 1
            HAL.clrBits(SCLK)
            maxSize--
        }

        HAL.setBits(SCLK)
        if (count1s % 2 == 0) HAL.setBits(SDX)
        else HAL.clrBits(SDX)
        HAL.clrBits(SCLK)

    }

    // Retorna true se o canal série estiver ocupado
    fun isBusy() = HAL.isBit(BUSY)

}

fun main() {

    SerialEmitter.init()
    LCD.init()
    HAL.init()
    SerialEmitter.send(SerialEmitter.Destination.LCD,2)
}