//import needed for connecting external devices through a USB Port
import isel.leic.UsbPort

/**
 * Hardware Abstraction Layer, interface used to communicate with the hardware.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object HAL {
    //Variable initialization
    //value that will be manipulated for the output value.
    private var outputValue = 0x00      // Initial value of output
    private const val FULL_MASK = 0xFF  // Full mask to write in the USBPort.
    private const val MAX_SIZE = 1      // Max Size for checking if a bit is set.
    private var HAL_STATE = false       // Current State of HAL(if it was already initialized).

    /**
     * Initializes the class by writing the default [outputValue] into the hardware for it's reset.
     */
    fun init() {
        if (HAL_STATE) return
        writeBits(FULL_MASK, outputValue)
        HAL_STATE = true
    }

    /**
     * Function that verifies if a bit is One or Zero based on a specific [mask].
     * Requires that the mask can only contain one set bit or throws an Exception.
     * @param mask Mask to check if a bit is One or Zero.
     * @return The representation of that bit in form of a boolean (true or false).
     */
    fun isBit(mask: Int): Boolean {
        require(mask.countOneBits() == MAX_SIZE) { "Mask can only have one set bit" }
        return readBits(mask) == mask
    }

    /**
     * Function that reads the Input port through the One's of [mask].
     * @param mask Mask corresponding to the bits that we can read.
     * @return The bits in the input USB Port passed by [mask].
     */
    fun readBits(mask: Int): Int {
        return usbPortIn() and mask
    }

    /**
     * Function that writes the data contained in [value] after passing it through a [mask].
     * @param mask Mask corresponding to the bits that we can write.
     * @param value Value to write in the output USB Port.
     */
    fun writeBits(mask: Int, value: Int) {
        outputValue = outputValue and mask.inv()
        outputValue = outputValue or (mask and value)
        usbPortOut(outputValue)

    }

    /**
     * Function that sets the bits corresponding to the One's in [mask] to the logic value One.
     * @param mask Mask to write One's.
     */
    fun setBits(mask: Int) {
        outputValue = outputValue or mask
        usbPortOut(outputValue)
    }

    /**
     * Function that sets the bits corresponding to the One's in [mask] to the logic value Zero.
     * @param mask Mask to write Zero's.
     */
    fun clrBits(mask: Int) {
        outputValue = outputValue and mask.inv()
        usbPortOut(outputValue)
    }

    /**
     * Function that inverts a [value] for writing in the USBPort.
     * @param value Value to invert.
     */
    private fun usbPortOut(value: Int) {
        UsbPort.out(value.inv())
    }

    /**
     * Function that reads and inverts the input value of the USBPort.
     * @return Value of the USBPort In inverted.
     */
    private fun usbPortIn(): Int {
        return UsbPort.`in`().inv()
    }
}

/**
 * Main function for testing the class.
 */
fun main() {
    HAL.init()
    //variable initialization
    val lowerNibble = 0x0f
    val firstBit = 0x01
    val secondBit = 0x02
    val thirdBit = 0x04
    val fourthBit = 0x08
    val exception = 0x03
    val mask = 0x3c
    val value = 0x18

    println(HAL.readBits(lowerNibble))// key pressed -> 5                                               // DONE
    println("USBPortIn -> ${HAL.readBits(lowerNibble)} ")
    println("if bit 2 to the power 0 is set -> ${HAL.isBit(firstBit)}")                                 // DONE
    println("if bit 2 to the power 1 is not set -> ${HAL.isBit(secondBit)}")                            // DONE
    println("if bit 2 to the power 2 is set -> ${HAL.isBit(thirdBit)}")                                 // DONE
    println("if bit 2 to the power 3 is not set -> ${HAL.isBit(fourthBit)}")                            // DONE
    //Will trow an expected exception                                                                   // DONE
    println("Should print \"Mask can only have one set bit\" and prints -> ${HAL.isBit(exception)}")

    println("Expected -> $value, real ->${HAL.writeBits(mask, value)}")                                 // DONE
    println("Expected -> FF, real ->${HAL.clrBits(mask)}")                                              // DONE
    println("Expected -> $value, real ->${HAL.setBits(value)}")                                         // DONE
}
