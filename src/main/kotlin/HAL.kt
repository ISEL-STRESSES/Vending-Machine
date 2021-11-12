import isel.leic.UsbPort

// Virtualiza o acesso ao sistema UsbPort
object  HAL {

    //Variable initiation
    private const val ZERO_MASK = 0x00  //Mask full of Zero
    private var outputValue = 0x00      //Initial value of output

    // Inicia a classe
    fun init(){
        outputValue = ZERO_MASK
        UsbPort.out(outputValue)
    }


    // Retorna true se o bit tiver o valor lógico ‘1’
    fun isBit(mask: Int) = readBits(mask) == mask


    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int) :Int{
        UsbPort.`in`()
       return UsbPort.`in`().inv() and mask
    }

    // Escreve nos bits representados por mask o valor de value
    fun writeBits(mask: Int, value:Int) {
        outputValue = outputValue and mask.inv()
        outputValue = outputValue or (mask and value)
        UsbPort.out(outputValue.inv())

    }

    // Coloca os bits representados por mask no valor lógico ‘1’
    fun setBits(mask: Int) {
        outputValue = (outputValue or mask)
        UsbPort.out(outputValue.inv())
    }


    // Coloca os bits representados por mask no valor lógico ‘0’
    fun clrBits(mask: Int){
        outputValue = (outputValue and mask.inv())
        UsbPort.out(outputValue.inv())
    }

}

fun main() {
    // main de teste a class HAL
    HAL.init()
    //5
    val input = HAL.readBits(0x0f)
    println(input)
    println("USBPortIn -> ${HAL.readBits(0x0f)} ")
    println("if bit 2 to the power 0 is true -> ${HAL.isBit(0x01)}")    //DONE
    println("if bit 2 to the power 1 is false -> ${HAL.isBit(0x02)}")   //DONE
    println("if bit 2 to the power 2 is true -> ${HAL.isBit(0x04)}")    //DONE
    println("if bit 2 to the power 3 is false -> ${HAL.isBit(0x08)}")   //DONE

    println("Expected -> 18, real ->${HAL.writeBits(0x3c,0x18)}") //DONE

    println("Expected -> FF, real ->${HAL.clrBits(0x3c)}")                 //DONE

    println("Expected -> 18, real ->${HAL.setBits(0x18)}")                      //DONE

}