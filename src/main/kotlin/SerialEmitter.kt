/**
 * Interface that implements communication over a Serial Protocol.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado
 */
object SerialEmitter {

    //Variable initialization.
    private const val SCLK = 0x02                   // Mask to write the SCLK value in the USBPort.
    private const val SDX = 0x01                    // Mask to write the SDX value in the USBPort.
    private const val BUSY = 0x40                   // Mask to read the Busy flag given by the Serial Control (hardware).
    private const val ONE_MASK = 0x01               // Mask to check parity.
    private const val DISPENSER_DESTINY = 0         // Destiny bit of dispenser.
    private const val LCD_DESTINY = 1               // Destiny bit of LCD.
    private const val DISPENSER_MAX_SIZE = 4        // Message max size for dispenser.
    private const val LCD_MAX_SIZE = 5              // Message max size for LCD.
    private const val ONE_POSITION = 1              // Amount to shift data in message.
    private const val INIT_COUNT = 0                // Initial count of parity bits.
    private const val NO_SIZE = 0                   // Hole data was sent to Destiny.
    private const val PARITY_BIT = 0x01             // Parity bit to write or clear.
    private const val PARITY_CHECK = 2              // Checker of parity.
    private const val EVEN = 0                      // Even checker.
    private var SERIAL_EMITTER_STATE = false        // Current State of Serial Emitter(if it was already initialized).

    /**
     * Enumerate that sets the destination for data in hardware.
     */
    enum class Destination { DISPENSER, LCD }

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
        val destination = if (address == Destination.DISPENSER) DISPENSER_DESTINY else LCD_DESTINY
        //data to be sent.
        var dataToSend = data
        //variable that counts the number of 1's for parity.
        var count1s = INIT_COUNT
        //data size depends on hardware component (LCD[5] or dispenser[4]).
        var maxSize = if (address == Destination.DISPENSER) DISPENSER_MAX_SIZE else LCD_MAX_SIZE

        //initialization sequence.
        HAL.clrBits(SCLK)
        HAL.setBits(SDX)
        HAL.clrBits(SDX)

        //sending destination.
        HAL.writeBits(SDX, destination)
        if (destination and ONE_MASK == ONE_MASK)
            count1s++

        //sending data and checks for parity.
        while (maxSize > NO_SIZE) {
            HAL.setBits(SCLK)
            HAL.writeBits(SDX, dataToSend)

            if (dataToSend and ONE_MASK == ONE_MASK)
                count1s++
            dataToSend = dataToSend shr ONE_POSITION
            HAL.clrBits(SCLK)
            maxSize--
        }

        //parity bit (last bit in data).
        HAL.setBits(SCLK)
        if (count1s % PARITY_CHECK == EVEN)
            HAL.setBits(PARITY_BIT)
        else
            HAL.clrBits(PARITY_BIT)
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
    SerialEmitter.init()

    val data1 = 2
    val data2 = 5
    SerialEmitter.send(SerialEmitter.Destination.LCD, data1)
    SerialEmitter.send(SerialEmitter.Destination.DISPENSER, data2)
}