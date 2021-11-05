import isel.leic.UsbPort


object  HAL { // Virtualiza o acesso ao sistema UsbPort
    //Variable initiation
    private const val ZERO_MASK = 0x00  //Mask full of Zero
    private var outputValue = 0xFF      //Initial value of output

    // Inicia a classe
    fun init(){
        outputValue = ZERO_MASK.inv()
        UsbPort.out(outputValue)
    }


    // Retorna true se o bit tiver o valor lógico ‘1’
    fun isBit(mask: Int) = readBits(mask) == mask


    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int) = UsbPort.`in`().inv() and mask


    // Escreve nos bits representados por mask o valor de value
    fun writeBits(mask: Int, value:Int) {
        outputValue = outputValue and (mask and value).inv()
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
    /*
    val bin = 0b00001001
    val testIsBit = HAL.isBit(0x01)
    val testIsbitFalse = HAL.isBit(0x10)
    println("Expected = true, Current = $testIsBit")
    println("Expected = false, Current = $testIsbitFalse")
     */

}