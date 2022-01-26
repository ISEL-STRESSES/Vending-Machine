//Import needed for checking adding some delay.
import isel.leic.utils.Time

/**
 * Interface used to communicate with the LCD through a Serial or Parallel protocol.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object LCD {

    //Variable initialization
    private const val LINES = 2                         // Number of lines available for data printing in display.
    private const val SECOND_LINE = 1                   // Second Line in the LCD.
    const val COLUMNS = 16                              // Number of columns available for data printing in display.

    //Masks and Addresses
    private const val WRITE_MASK = 0x0F                 // Mask needed for sending data to the display.
    private const val REGISTER_SELECT = true            // Resister Select value as boolean.
    private const val REGISTER_SELECT_BIT = 0x10        // Bit of Register Select in the byte.
    private const val ENABLE_SIGNAL_BIT = 0x20          // Bit of Enable in the byte.
    private const val SERIAL_MODE = true                // Serial mode Selector, default value true.
    private const val LINE_CELLS = 0x40                 // Max address of cells in a line.
    private const val SET_CGRAM_ADDRESS = 0x40          // Sets CGRAM Address.
    private const val SET_DDRAM_ADDRESS = 0x80          // Sets DDRAM Address.
    private const val AESTHETICS_TIME_INTERVAL = 125L   // Time that takes to write a char whit aesthetics.
    private const val ONE_POSITION = 1                  // Amount to shift data in message.
    private const val FOUR_POSITIONS = 4                // Amount to shift data in message(Full nibble).
    private const val INSTRUCTION_REGISTER = 1          // Address on Instruction Register in a command.
    private const val DATA_REGISTER = 0                 // Address on Data Register in a command.
    private var LCD_STATE = false                       // Current State of LCD(if it was already initialized).

    //Adding a new Char to the LCD
    private const val CHAR_LINES = 8                    // Number of lines for setting a new char in the DDRAM.
    private const val CHAR_HIGH_BITS_OFFSET = 3         // Offset for addressing the correct position in the CGRAM.
    private const val RETURN_HOME_CMD = 0x02            // Command that places the cursor in the initial position.

    //Initialization sequence for LCD.
    private const val DATA_INIT = 0x3                   // First 3 messages of data to send.
    private const val FIRST_WAIT_TIME = 16L             // First wait time.
    private const val SECOND_WAIT_TIME = 5L             // Second wait time.
    private const val CLEAR_WAIT_TIME = 10L             // Last wait time (we needed to be more than 5.48ms).
    private const val DISPLAY_ON = 0x0F                 // Display on.
    private const val DISPLAY_OFF = 0x08                // Display off.
    private const val ENTRY_MODE_SET = 0x06             // Entry mode set.
    private const val DISPLAY_CLEAR = 0x01              // Clears the display.
    private const val LINES_AND_FONT = 0x28             // Specify the number of display lines and character font.
    private const val SET_FOUR_BIT_INTERFACE = 0x02     // Sets the interface to 4 bit length.

    /**
     * Function that implements a Serial communication protocol.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
    private fun writeNibbleSerial(rs: Boolean, data: Int) {
        val realData = (data shl ONE_POSITION)
        // Adding RS bit to data.
        if (rs) SerialEmitter.send(SerialEmitter.Destination.LCD, realData or INSTRUCTION_REGISTER)
        else SerialEmitter.send(SerialEmitter.Destination.LCD, realData or DATA_REGISTER)

    }

    /**
     * Function that implements a Parallel communication protocol.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
    private fun writeNibbleParallel(rs: Boolean, data: Int) {
        if (rs) HAL.setBits(REGISTER_SELECT_BIT)
        else HAL.clrBits(REGISTER_SELECT_BIT)
        HAL.setBits(ENABLE_SIGNAL_BIT)
        HAL.writeBits(WRITE_MASK, data)
        HAL.clrBits(ENABLE_SIGNAL_BIT)
    }

    /**
     * Function that sends a nibble of dada for writing or commands in the LCD.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD (the lowest level function in the class).
     */
    private fun writeNibble(rs: Boolean, data: Int) {
        if (SERIAL_MODE) writeNibbleSerial(rs, data)
        else writeNibbleParallel(rs, data)
    }

    /**
     * Function that writes a byte of data for writing or commands in the LCD.
     * @param rs Register Select to chose if [data] is written in
     * Instruction Register (IR) or Data Register (DR).
     * @param data Data to be sent to LCD.
     */
    private fun writeByte(rs: Boolean, data: Int) {
        writeNibble(rs, data shr FOUR_POSITIONS)    // Write high
        writeNibble(rs, data)                       // Write low
    }

    /**
     * Function that writes a command in the LCD.
     * @param data Data to be written in IR.
     */
    private fun writeCMD(data: Int) {
        writeByte(!REGISTER_SELECT, data)
    }

    /**
     * Function that writes a data in the LCD.
     * @param data Data to be written in DR.
     */
    private fun writeDATA(data: Int) {
        writeByte(REGISTER_SELECT, data)
    }

    /**
     * Function that initializes the LCD for communication at 4 bit rate.
     */
    fun init() {
        // Check if LCD interface was already initialized.
        if (LCD_STATE) return
        HAL.init()
        SerialEmitter.init()

        // 8 bit data interface ------------
        // First message.
        Time.sleep(FIRST_WAIT_TIME)
        writeNibble(!REGISTER_SELECT, DATA_INIT)
        // Second message.
        Time.sleep(SECOND_WAIT_TIME)
        writeNibble(!REGISTER_SELECT, DATA_INIT)
        // Third message.
        writeNibble(!REGISTER_SELECT, DATA_INIT)
        // From this moment BF (busy flag) can be read.

        writeNibble(!REGISTER_SELECT, SET_FOUR_BIT_INTERFACE)
        // From this moment the Interface is set to four bits.

        // 4 bit data interface ------------
        writeCMD(LINES_AND_FONT)
        writeCMD(DISPLAY_OFF)
        writeCMD(DISPLAY_CLEAR)
        writeCMD(ENTRY_MODE_SET)
        writeCMD(DISPLAY_ON)
        // 1.52ms (return home) + 37 microseconds * 0x80 (total cells), worst case (for the others instructions +1ms)~5.48
        Time.sleep(CLEAR_WAIT_TIME)
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
    fun write(text: String, aesthetics: Boolean = false) {
        for (i in text) {
            //writes letter by letter with time gap for aesthetics.
            if (aesthetics) Time.sleep(AESTHETICS_TIME_INTERVAL)
            write(i)
        }
    }

    /**
     * Function that sends a command for positioning the cursor to [line] and [column]
     * coordinates.
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
        if (line == SECOND_LINE)
            cursor += LINE_CELLS
        // Command to place the cursor in the right place.
        // writeCMD(SET_CGRAM_ADDRESS or cursor)
        setDDRAMAddress(cursor)
        Time.sleep(100L)
    }

    /**
     * Function that sends a command for cleaning the cursor
     * (places implicitly the cursor in position(0, 0)).
     */
    fun clear() {
        writeCMD(DISPLAY_CLEAR)
        Time.sleep(CLEAR_WAIT_TIME)
    }

    /**
     * Function that sends a command to set the DDRAM address for the
     * [address] that is passed as a param.
     * @param address Address to set the DDRAM.
     */
    private fun setDDRAMAddress(address: Int) {
        writeCMD(SET_DDRAM_ADDRESS or address)
    }

    /**
     * Function that sends a command to set the CGRAM address for the
     * [address] that is passed as a param.
     * @param address Address to set the CGRAM.
     */
    private fun setCGRAMAddress(address: Int) {
        writeCMD(SET_CGRAM_ADDRESS or address)
    }

    /**
     * Function that allows to add custom characters to the LCD CGRAM.
     * @param position position in the CGRAM to add the new Character.
     * @param char IntArray that represents the bitmap of the character to add to the CGRAM.
     */
    fun loadChar(position: Int, char: IntArray) {
        repeat(CHAR_LINES) { // line by line
            // Shifts the current position to the 3 high bits and adds it with che current line.
            setCGRAMAddress(position.shl(CHAR_HIGH_BITS_OFFSET) or it)
            // Access to the bits of the current line of the Char.
            writeDATA(char[it])
        }
        writeCMD(RETURN_HOME_CMD) // Places the cursor in the initial position, forcing an setDDRAM command.
    }

}

/**
 * Main function for testing the class.
 */
fun main() {
    HAL.init()
    SerialEmitter.init()
    LCD.init()
    val firstLine = 0
    val line = 1
    val column = 2
    val lastVisibleColumn = 15
    val waitTime = 2000L
    val newChar = intArrayOf(0b01000, 0b01000, 0b01000, 0b11101, 0b11101, 0b11110, 0b11100, 0b00000)
    val newCharPosition = 0
    val newCharCode = 0.toChar()
    LCD.loadChar(newCharPosition, newChar)
    LCD.write("$newCharCode")
    LCD.cursor(firstLine, lastVisibleColumn)
    LCD.write("$newCharCode")
    LCD.cursor(line, column)
    LCD.write("Hello World!", true)
    Time.sleep(waitTime)
    LCD.clear()
    // Writes part of the phrase as intended (in both lines).
    LCD.write("Let's go people, lets us see the character limits in this Liquid Cristal Display.", true)
}
