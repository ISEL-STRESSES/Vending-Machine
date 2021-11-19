
//import needed for connecting with an external device through a USB Port
import isel.leic.UsbPort

/**
 * Interface used to communicate with the hardware easily.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object  HAL {

    //Variable initialization
    //value that will be manipulated for the output value.
    private var outputValue = 0x00      //Initial value of output

    /**
     * Initializes the class by writing the default [outputValue] into the hardware for it's reset.
     */
    fun init(){
        val fullMask = 0xFF
        writeBits(fullMask, outputValue)
    }


    /**
     * Function that verifies if a bit is One or Zero based on a specific [mask].
     * @param mask Mask to check if a bit is One or Zero.
     * @return The representation of that bit in form of a boolean (true or false).
     */
    fun isBit(mask: Int) :Boolean {
        require(mask.countOneBits() == 1) { "Mask can only have one set bit" }
        val readBit = readBits(mask)
        return readBit == 1
    }


    /**
     * Function that reads the Input port through the One's of [mask].
     * @param mask Mask corresponding to the bits that we can read.
     * @return The bits in the input USB Port passed by [mask].
     */
    fun readBits(mask: Int) :Int{
        val input = UsbPort.`in`().inv()
        return input and mask
    }

    /**
     * Function that writes the data contained in [value] after passing it through a [mask].
     * @param mask Mask corresponding to the bits that we can write.
     * @param value Value to write in the output USB Port.
     */
    fun writeBits(mask: Int, value:Int) {
        outputValue = outputValue and mask.inv()
        outputValue = outputValue or (mask and value)
        UsbPort.out(outputValue.inv())

    }

    /**
     * Function that sets the bits corresponding to the One's in [mask] to the logic value One.
     * @param mask Mask to write One's.
     */
    fun setBits(mask: Int) {
        outputValue = outputValue or mask
        UsbPort.out(outputValue.inv())
    }


    /**
     * Function that sets the bits corresponding to the One's in [mask] to the logic value Zero.
     * @param mask Mask to write Zero's.
     */
    fun clrBits(mask: Int){
        outputValue = (outputValue and mask.inv())
        UsbPort.out(outputValue.inv())
    }

}


/**
 * Main function for testing the class.
 */
fun main() {
    HAL.init()
    //5
    val input = HAL.readBits(0x0f)
    println(input)
    println("USBPortIn -> ${HAL.readBits(0x0f)} ")
    println("if bit 2 to the power 0 is true -> ${HAL.isBit(0x01)}")                            //DONE
    println("if bit 2 to the power 1 is false -> ${HAL.isBit(0x02)}")                           //DONE
    println("if bit 2 to the power 2 is true -> ${HAL.isBit(0x04)}")                            //DONE
    println("if bit 2 to the power 3 is false -> ${HAL.isBit(0x08)}")                           //DONE
    //Will trow an expected exception
    println("Should print \"Mask can only have one set bit\" and prints -> ${HAL.isBit(0x03)}") //DONE

    println("Expected -> 18, real ->${HAL.writeBits(0x3c,0x18)}")                          //DONE

    println("Expected -> FF, real ->${HAL.clrBits(0x3c)}")                                      //DONE

    println("Expected -> 18, real ->${HAL.setBits(0x18)}")                                            //DONE

}