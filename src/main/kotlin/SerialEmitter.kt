/**
 * Interface that implements communication over a Serial Protocol.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado
 */
object SerialEmitter {

    /**
     * Enumerate that sets the destination for data in hardware.
     */
    enum class Destination { DISPENSER, LCD }

    private const val SCLK = 0x02                   //Mask to write the SCLK value in the USBPort.
    private const val SDX = 0x01                    //Mask to write the SDX value in the USBPort.
    private const val BUSY = 0x40                   //Mask to read the Busy flag given by the Serial Control (hardware).
    private const val ONE_MASK = 0x01               //Mask to check parity.
    private var SERIAL_EMITTER_STATE = false        //Current State of Serial Emitter(if it was already initialized).

    /**
     * Function that initializes the Serial Emitter protocol.
     */
    fun init() {
        //if Serial Emitter was already initialized skips it.
        if (SERIAL_EMITTER_STATE) return
        HAL.init()
        HAL.setBits(SDX)
        HAL.clrBits(SCLK)
        SERIAL_EMITTER_STATE = true
    }

    /**
     * Function that sends data over a Serial protocol.
     * @param address Address of hardware component.
     * @param data Data to send over serial protocol.
     */
    fun send(address: Destination, data: Int) {
        //waits until chanel is available.
        while (isBusy());

        //translates destination to bit (LCD[1] or dispenser[0]).
        val destination = if (address == Destination.DISPENSER) 0 else 1
        //data to be sent.
        var dataToSend = data
        //variable that counts the number of 1's for parity.
        var count1s = 0
        //data size depends on hardware component (LCD[5] or dispenser[4]).
        var maxSize = if (address == Destination.DISPENSER) 4 else 5

        //initialization sequence.
        HAL.clrBits(SCLK)
        HAL.setBits(SDX)
        HAL.clrBits(SDX)

        //sending destination.
        HAL.writeBits(SDX, destination)
        if (destination and ONE_MASK == 1)
            count1s++

        //sending data and checks for parity.
        while (maxSize > 0) {
            HAL.setBits(SCLK)
            HAL.writeBits(SDX, dataToSend)

            if (dataToSend and ONE_MASK == 1)
                count1s++
            dataToSend = dataToSend shr 1
            HAL.clrBits(SCLK)
            maxSize--
        }

        //parity bit (last bit in data).
        HAL.setBits(SCLK)
        if (count1s % 2 == 0) HAL.setBits(SDX)
        else HAL.clrBits(SDX)
        HAL.clrBits(SCLK)

        //Termination sequence
        HAL.setBits(SCLK)
        HAL.setBits(SDX)
        HAL.clrBits(SCLK)
    }

    /**
     * Function that checks if Serial Chanel is busy.
     * @return Returns true if busy.
     */
    private fun isBusy() = HAL.isBit(BUSY)

}

/**
 * Main function for testing.
 */
fun main() {
    HAL.init()
    LCD.init()
    SerialEmitter.init()

    SerialEmitter.send(SerialEmitter.Destination.LCD, 2)

    SerialEmitter.send(SerialEmitter.Destination.DISPENSER, 5)
}