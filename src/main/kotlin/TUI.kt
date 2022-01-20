import isel.leic.utils.Time

/**
 * Interface that implements communication between the LCD and the KBD.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    //Variable Initialization.
    private const val LINE_SIZE = 64            //Max line size in the LCD(Addresses).
    private const val DEFAULT_TIME_OUT = 1000L  //Default Time out for waiting for a key.
    private const val INITIAL_POSITION = 0      //Initial cell position on any line.
    private const val FIRST_LINE = 0            //First Line in the LCD.
    private const val SECOND_LINE = 1           //Second Line in the LCD.
    private const val DEFAULT_NUMBER_SIZE = 2   //Default number size to write in the LCD.
    private const val FILL_CHARACTER = '0'      //Character to fill if the Number Size is less than [DEFAULT_NUMBER_SIZE].
    private const val QUANTITY_INDICATOR = '#'  //Quantity indicator.
    const val NONE = KBD.NONE                   //NONE char of KBD
    private var TUI_STATE = false               //Current State of Products(if it was already initialized).
    //new chars £$€₿♫
    private const val BITCOIN_CHAR_CODE = 0
    private const val BITCOIN_CGRAM_POSITION = 0
    private val BITCOIN_MAP = intArrayOf(
        0b01010,
        0b11110,
        0b01011,
        0b01110,
        0b01011,
        0b11110,
        0b01010,
        0b00000
    )
    private const val ARROW_CHAR_CODE = 1
    private const val ARROW_CGRAM_POSITION = 1
    private val ARROW_MAP = intArrayOf(
        0b00100,
        0b01110,
        0b11111,
        0b00100,
        0b11111,
        0b01110,
        0b00100,
        0b00000
    )
    //data class Character (val position: Int, val map:IntArray)
    /**
     * Enumerate that Represents all the valid position on the Display for writing.
     */
    enum class Position { CENTER, LEFT, RIGHT }


    /**
     * Function that initializes the class of the TUI.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (TUI_STATE) return
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        LCD.loadChar(BITCOIN_CGRAM_POSITION, BITCOIN_MAP)
        LCD.loadChar(ARROW_CGRAM_POSITION, ARROW_MAP)
        TUI_STATE = true
    }


    /**
     * Function that prints the information of a [product] on the LCD, if it doesn't have any quantity
     * available prints "Product not available".
     * @param product Product to print information about.
     */
    fun printProduct(product: Products.Product, mode: Mode = Mode.INDEX) {
        clearLCD()

        printText(product.name, Position.CENTER, FIRST_LINE)
        printText( product.id.toFilledString() + if (mode == Mode.ARROWS) ARROW_CHAR_CODE.toChar() else "", Position.LEFT, SECOND_LINE)
        printText(QUANTITY_INDICATOR + product.quantity.toFilledString(), Position.CENTER, SECOND_LINE)
        printText(BITCOIN_CHAR_CODE.toChar() + product.price.toFilledString(), Position.RIGHT, SECOND_LINE)

    }


    /**
     * Function that adds the Fill Character if it doesn't have the minimum size.
     * @receiver Integer to convert to String and Fill character if needed.
     * @return Returns a string filled if needed.
     */
    private fun Int.toFilledString(): String {
        return this.toString().padStart(DEFAULT_NUMBER_SIZE, FILL_CHARACTER)
    }


    /**
     * Function that prints an unavailable product.
     * @param product Unavailable product to Print.
     */
    fun printUnavailableProduct(product: Products.Product?, index: Int) {
        clearLCD()
        printText("Product ${product?.id ?: index}", Position.CENTER, FIRST_LINE)
        printText("not available", Position.CENTER, SECOND_LINE)
        println(product)
    }


    /**
     *
     */
    fun printOutOfService(reason: String) {
        printText("OUT OF SERVICE", Position.CENTER, FIRST_LINE)
        printText(reason, Position.CENTER, SECOND_LINE)
    }


    /**
     *
     */
    fun printUpdateQuantity(product: Products.Product) {
        clearLCD()
        printText(product.name, Position.CENTER, FIRST_LINE)
        printText("Qty:??",Position.LEFT, SECOND_LINE)
        LCD.cursor(SECOND_LINE,4)
    }


    /**
     * Function that prints the process of the sale.
     * @param product Product to print during sale.
     * @param price Price of the product.
     */
    fun printSell(product: Products.Product, price: Int) {
        clearLCD()
        printText(product.name, Position.CENTER, FIRST_LINE)
        printText(price.toString(), Position.CENTER, SECOND_LINE)
    }

    fun printTanks(){
        clearLCD()
        printText("Thank you", Position.CENTER, FIRST_LINE)
        printText("See you again", Position.CENTER, SECOND_LINE)
        Time.sleep(500L)
    }

    /**
     *
     */
    fun printMaintenanceSell(product: Products.Product){
        clearLCD()
        printText(product.name, Position.CENTER, FIRST_LINE)
        printText("*- To Print",Position.CENTER, SECOND_LINE)
    }


    /**
     * Function that prints in the LCD a Canceled Sale.
     * @param coins Coins to be returned.
     */
    fun printCancel(coins: Int) {
        clearLCD()
        printText("Vending Aborted", Position.CENTER, FIRST_LINE)
        if (coins > 0)
            printText("Return $coins", Position.CENTER, SECOND_LINE)
    }


    /**
     * Function that prints the confirmation of Shutdown on the LCD.
     */
    fun printShutdown() {
        clearLCD()
        printText("Shutdown", Position.CENTER, FIRST_LINE)
        printText("5-Yes", Position.LEFT, SECOND_LINE)
        printText("other-No", Position.RIGHT, SECOND_LINE)
    }


    /**
     * Function that allows to print text in several Positions on the LCD.
     * @param text Text to write in the LCD.
     * @param position Position to place the cursor to write, by default is [Position.LEFT].
     * @param line Line to write the [text].
     */
    fun printText(text: String, position: Position, line: Int, clear:Boolean = false) {
        if (clear)
            clearLine(line)
        //if somehow we forget some space in the beginning or end.
        val cleanedText = text.trim()
        val textSize = cleanedText.length
        val middle = (LCD.COLUMNS / 2) - (textSize / 2)
        when (position) {
            Position.CENTER -> {
                LCD.cursor(line, if (textSize % 2 == 0) middle else middle - 1)
                LCD.write(cleanedText)
            }
            Position.RIGHT -> {
                LCD.cursor(line, LCD.COLUMNS - textSize)
                LCD.write(cleanedText)
            }
            else -> {
                LCD.cursor(line, INITIAL_POSITION)
                LCD.write(cleanedText)
            }
        }
    }


    /**
     * Function that clears a line in the LCD.
     * @param line Line to be cleared.
     */
    private fun clearLine(line: Int) {
        repeat(LINE_SIZE) {
            LCD.cursor(line, it)
            LCD.write(" ")
        }
    }


    /**
     * Function that cleans the hole LCD.
     */
    fun clearLCD() {
        clearLine(FIRST_LINE)
        clearLine(SECOND_LINE)
    }


    /**
     * Function that translates an KBD key into it's integer representation, if it has one.
     * @param timeout Timeout to wait for a key.
     * @return a Key in its integer representation.
     */
    fun getInt(timeout: Long): Int {
        var value = 0
        val kbdInt = getKBDKey(timeout)

        if (kbdInt in (KBD.keys.filter { it != '#' && it != '*' }))
            value = kbdInt.toInteger()

        val newInt = KBD.waitKey(timeout)
        if (newInt in (KBD.keys.filter { it != '#' && it != '*' }))
            value = (value shl 1) or newInt.toInteger()
        return value
    }


    /**
     * Function that converts a Char to Integer.
     * @receiver Char to convert.
     * @return Char Converted.
     */
    fun Char.toInteger(): Int{
        return this - '0'
    }


    /**
     * Function that gets any key pressed in the Keyboard.
     * @param timeOut Timeout to wait for a Key, by default is [DEFAULT_TIME_OUT].
     * @return The char representation of the key.
     */
    fun getKBDKey(timeOut: Long = DEFAULT_TIME_OUT): Char {
        return KBD.waitKey(timeOut)
    }

}

/**
 * Main function for testing the Class.
 */
fun main() {
    HAL.init()
    SerialEmitter.init()
    LCD.init()
    KBD.init()
    TUI.init()
    TUI.printText("Example", TUI.Position.CENTER, 0)
    TUI.printText("Example2", TUI.Position.CENTER, 1)
    //TUI.printText("BZ", line = 0)

}