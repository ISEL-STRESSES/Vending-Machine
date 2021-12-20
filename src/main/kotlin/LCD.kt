import isel.leic.utils.Time

/**
 * Interface used to communicate (write) with the LCD (8 bits interface).
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object LCD {

    //Variable initialization
    private const val LINES = 2                     //Number of lines available for data printing in display.
    private const val COLUMNS = 16                  //Number of columns available for data printing in display.
    private const val WRITE_MASK = 0x0F             //Mask needed for sending data to the display.
    private const val REGISTER_SELECT = true        //Resister Select value in boolean.
    private const val REGISTER_SELECT_BIT = 0x10    //Bit of Register Select in the byte.
    private const val ENABLE_SIGNAL_BIT = 0x20      //Bit of Enable in the byte.
    private const val SERIAL_MODE = true            //Serial mode Selector, default value -> true.

    //Initialization sequence for 4 bit rate communication.
    private const val DATA_INIT = 0x3
    private const val FIRST_WAIT_TIME = 16L
    private const val SECOND_WAIT_TIME = 5L


    /**
     * Function that sends a nibble of dada for writing or commands in the LCD.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
    private fun writeNibbleSerial(rs: Boolean, data: Int) {
        val realData = (data shl 1)
        if (rs) SerialEmitter.send(SerialEmitter.Destination.LCD,realData or 1 )
        else SerialEmitter.send(SerialEmitter.Destination.LCD, realData or 0)

    }
    private fun writeNibbleParallel(rs: Boolean, data: Int){
        if(rs) HAL.setBits(REGISTER_SELECT_BIT)
        else HAL.clrBits(REGISTER_SELECT_BIT)
        HAL.setBits(ENABLE_SIGNAL_BIT)
        HAL.writeBits(WRITE_MASK,data)
        HAL.clrBits(ENABLE_SIGNAL_BIT)
    }

    private fun writeNibble(rs: Boolean, data: Int){
        if (SERIAL_MODE) writeNibbleSerial(rs,data)
        else writeNibbleParallel(rs,data)
    }


    /**
     * Function that writes a byte of data for writing or commands in the LCD.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD.
     */
    private fun writeByte(rs: Boolean, data: Int) {
        writeNibble(rs, data shr 4) //write high
        writeNibble(rs, data) //write low
    }


    /**
     * Function that writes a command in the LCD.
     * @param data Data to be written in IR.
     */
    fun writeCMD(data: Int) {
        writeByte(!REGISTER_SELECT, data)
    }


    /**
     * Function that writes a data in the LCD.
     * @param data Data to be written in DR.
     */
    fun writeDATA(data: Int) {
        writeByte(REGISTER_SELECT, data)
    }


    /**
     * Function that initializes the LCD for communication at 4 bit rate.
     */
    fun init() {
        // 8 bit data interface ------------
            //first message
        Time.sleep(FIRST_WAIT_TIME)
        writeNibble(!REGISTER_SELECT, DATA_INIT)
            //second message
        Time.sleep(SECOND_WAIT_TIME)
        writeNibble(!REGISTER_SELECT, DATA_INIT)
            //third message
        writeNibble(!REGISTER_SELECT, DATA_INIT)
            //From this moment BF can be read
        // 4 bit data interface ------------
        val displayOn = 0x01
        val displayOf = 0x0F
        writeNibble(!REGISTER_SELECT, 0x2)
        writeCMD(0x28)
        writeCMD(0x08)
        writeCMD(displayOn)
        writeCMD(0x06)
        writeCMD(displayOf) // nome trocado com nome do on?
        Time.sleep(10) //1.52ms + 37 microseconds * 80 // worst case (for the others instructions +1ms)

    }


    /**
     * Function that writes a character in current position.
     * @param c Char to be written.
     */
    private fun write(c: Char) {
        writeDATA(c.code)
    }

    /**
     * Function that writes an entire String in current position.
     * @param text String to be written.
     */
    fun write(text: String) {
        for (i in text)
            write(i)
    }


    /**
     * Function that sends a command for positioning the cursor to [line] and [column] coordinates.
     *
     * ([line]:0 until [LINES], [column]:0 until [COLUMNS])
     * @param line Line to set cursor.
     * @param column Column to set cursor.
     */
    fun cursor(line: Int, column: Int) {
        var cursor = column
        if (line == 1)
            cursor += 0x40
        val bit7 = 0x80
        writeCMD(bit7 or cursor)
    }


    /**
     * Function that sends a command for cleaning the cursor (places implicitly the cursor in position(0, 0)).
     */
    fun clear() {
        writeCMD(0x01)
    }

}

/**
 * Main function for testing the class.
 */
fun main () {

    HAL.init()
    SerialEmitter.init()
    KBD.init()
    LCD.init()
    //LCD.cursor(1, 2)
    //LCD.write("Hello Word!!")
    //Time.sleep(2000)
    //LCD.clear()
    //LCD.write("Bora meus putos, bora lá, deixa ver o limite  de caracteres") // referencia a fixol21
}