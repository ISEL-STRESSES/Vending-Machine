import isel.leic.utils.Time

/**
 * Interface used to communicate with the LCD through a Serial or Parallel protocol.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object LCD {

    //Variable initialization
    private const val LINES = 2                         //Number of lines available for data printing in display.
    private const val COLUMNS = 16                      //Number of columns available for data printing in display.
    private const val WRITE_MASK = 0x0F                 //Mask needed for sending data to the display.
    private const val REGISTER_SELECT = true            //Resister Select value in boolean.
    private const val REGISTER_SELECT_BIT = 0x10        //Bit of Register Select in the byte.
    private const val ENABLE_SIGNAL_BIT = 0x20          //Bit of Enable in the byte.
    private const val SERIAL_MODE = true                //Serial mode Selector, default value true.
    private const val LINE_CELLS = 0x40                 //Max address of cells in a line.
    private const val SET_CGRAM_ADDRESS = 0x80          //Sets CGRAM Address.
    private var LCD_STATE = false                       //Current State of LCD(if it was already initialized).

    //Initialization sequence for LCD.
    private const val DATA_INIT = 0x3                  //First 3 messages of data to send.
    private const val FIRST_WAIT_TIME = 16L            //First wait time.
    private const val SECOND_WAIT_TIME = 5L            //Second wait time.
    private const val LAST_WAIT_TIME = 10L             //Last wait time (we needed to be more than 5.48ms).
    private const val DISPLAY_ON = 0x0F                //Display on.
    private const val DISPLAY_OFF = 0x08               //Display off.
    private const val ENTRY_MODE_SET = 0x06            //Entry mode set.
    private const val DISPLAY_CLEAR = 0x01             //Clears the display.
    private const val LINES_AND_FONT = 0x28            //Specify the number of display lines and character font.
    private const val SET_FOUR_BIT_INTERFACE = 0x2     //Sets the interface to 4 bit length.

    /**
     * Function that implements a Serial communication protocol.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
    private fun writeNibbleSerial(rs: Boolean, data: Int) {
        val realData = (data shl 1)
        //adding RS bit to data.
        if (rs) SerialEmitter.send(SerialEmitter.Destination.LCD,realData or 1 )
        else SerialEmitter.send(SerialEmitter.Destination.LCD, realData or 0)

    }

    /**
     * Function that implements a Parallel communication protocol.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
    private fun writeNibbleParallel(rs: Boolean, data: Int){
        if(rs) HAL.setBits(REGISTER_SELECT_BIT)
        else HAL.clrBits(REGISTER_SELECT_BIT)
        HAL.setBits(ENABLE_SIGNAL_BIT)
        HAL.writeBits(WRITE_MASK,data)
        HAL.clrBits(ENABLE_SIGNAL_BIT)
    }

    /**
     * Function that sends a nibble of dada for writing or commands in the LCD.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
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
        writeNibble(rs, data)            //write low
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
        //check if LCD interface was already initialized.
        if (LCD_STATE) return
        HAL.init()
        SerialEmitter.init()

        // 8 bit data interface ------------
            //first message.
        Time.sleep(FIRST_WAIT_TIME)
        writeNibble(!REGISTER_SELECT, DATA_INIT)
            //second message.
        Time.sleep(SECOND_WAIT_TIME)
        writeNibble(!REGISTER_SELECT, DATA_INIT)
            //third message.
        writeNibble(!REGISTER_SELECT, DATA_INIT)
            //From this moment BF (busy flag) can be read.

        writeNibble(!REGISTER_SELECT, SET_FOUR_BIT_INTERFACE)
            //From this moment the Interface is set to four bits.

        // 4 bit data interface ------------
        writeCMD(LINES_AND_FONT)
        writeCMD(DISPLAY_OFF)
        writeCMD(DISPLAY_CLEAR)
        writeCMD(ENTRY_MODE_SET)
        writeCMD(DISPLAY_ON)
        //1.52ms (return home) + 37 microseconds * 80 (total cells) // worst case (for the others instructions +1ms)~5.48
        Time.sleep(LAST_WAIT_TIME)
        LCD_STATE = true

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
        for (i in text){
            //writes letter by letter with time gap for aesthetics.
            Time.sleep(125)
            write(i)
        }
    }

    /**
     * Function that sends a command for positioning the cursor to [line] and [column] coordinates.
     *
     * [line]: 0 until [LINES];
     *
     * [column]: 0 until [COLUMNS].
     *
     * @param line Line to set cursor.
     * @param column Column to set cursor.
     */
    fun cursor(line: Int, column: Int) {
        var cursor = column
        if (line == 1)
            cursor += LINE_CELLS
        //Command to place the cursor in the right place.
        writeCMD(SET_CGRAM_ADDRESS or cursor)
    }

    /**
     * Function that sends a command for cleaning the cursor (places implicitly the cursor in position(0, 0)).
     */
    fun clear() {
        writeCMD(DISPLAY_CLEAR)
    }

}

/**
 * Main function for testing the class.
 */
fun main () {

    HAL.init()
    SerialEmitter.init()
    LCD.init()
    LCD.cursor(1, 2)
    LCD.write("Hello Word!!")
    Time.sleep(2000)
    LCD.clear()
    //Writes part of the phrase as intended (in both lines).
    LCD.write("Let's go people,lets us see the character limits in this Liquid Cristal Display.")
}