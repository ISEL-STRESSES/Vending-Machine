
object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.
    enum class Destination {DISPENSER, LCD}
    private const val SCLK = 0x02
    private const val SDX = 0x01
    private const val BUSSY = 0x80
    private const val ONE_MASK = 0x01

    // Inicia a classe
    fun init() {
        HAL.setBits(SCLK)
        HAL.clrBits(SDX)
    }

    // Envia uma trama para o SerialReceiver identificado o destino em addr e os bits de dados em
    //‘data’.
    fun send(addr: Destination, data: Int) {
        while (isBusy());
        val destination = if (addr ==Destination.DISPENSER) 1 else 0
        var data = (data shl 1) or destination
        var count1s = 0
        var maxSize = if (addr == Destination.DISPENSER) 5 else 6

        //initialization sequence
        HAL.clrBits(SCLK)
        HAL.setBits(SDX)
        HAL.clrBits(SDX)

        while (maxSize > 0){
            HAL.setBits(SCLK)
            HAL.writeBits(SDX,data)
            if (data and ONE_MASK == 1)
                count1s++
            data = data shr 1
            HAL.clrBits(SCLK)
            maxSize--
        }

        HAL.setBits(SCLK)
        if (count1s % 2 == 0) HAL.setBits(SDX)
        else HAL.clrBits(SDX)
        HAL.clrBits(SCLK)
    }

    // Retorna true se o canal série estiver ocupado
    fun isBusy() = HAL.isBit(BUSSY)

}