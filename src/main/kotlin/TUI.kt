import isel.leic.utils.Time

/**
 * Interface that implements communication between the LCD and the KBD.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    //Variable Initialization.
    private const val LINE_SIZE = 64            // Max line size in the LCD(Addresses).
    private const val INITIAL_POSITION = 0      // Initial cell position on any line.
    private const val FIRST_LINE = 0            // First Line in the LCD.
    private const val SECOND_LINE = 1           // Second Line in the LCD.
    private const val OPTIONS_LINE = SECOND_LINE// Line to print the options.
    private const val FIRST_COLUMN = 0          // First column in the LCD.
    private const val DEFAULT_NUMBER_SIZE = 2   // Default number size to write in the LCD.
    const val NONE = KBD.NONE                   // Value that represents a non-existent key.
    private var OPTIONS_INDEX = 0               // Current Index in the options Array.
    private const val FILL_CHARACTER = '0'      // Character to fill if the Number Size is less than [DEFAULT_NUMBER_SIZE].
    private const val LCD_FILL_STRING = " "     // String to fill for cleaning a line.
    private const val EMPTY_STRING = ""         // Auxiliary empty string.
    private const val ZERO_CHAR = '0'           // Char needed for getting the integer values of other chars.
    private const val QUANTITY_INDICATOR = '#'  // Quantity indicator.
    private const val PARITY_CHECK = 2          // Checker of parity.
    private const val NO_COINS = 0              // No coins to return.
    private const val INIT_QUANTITY_POSITION = 4// Initial cursor position in quantity update.
    private const val DIVIDER = 2               // Divider for writing in the LCD.
    private const val OFFSET = 1                // Offset for writing in the LCD.
    private const val EVEN = 0                  // Even checker.
    private var TUI_STATE = false               // Current State of Products(if it was already initialized).

    // New chars to add to the LCD. £$€♫
    // Bit map for the currency char. ₿
    private val BITCOIN_MAP = intArrayOf(0b01010, 0b11110, 0b01011, 0b01110, 0b01011, 0b11110, 0b01010, 0b00000)
    private const val BITCOIN_CHAR_CODE = 0     // Char code for the bitcoin char in the LCD. ₿
    private const val BITCOIN_CGRAM_POSITION = 0// Bitcoin address in the CGRAM. ₿

    // Bit map for the arrow char.
    private val ARROW_MAP = intArrayOf(0b00100, 0b01110, 0b11111, 0b00100, 0b11111, 0b01110, 0b00100, 0b00000)
    private const val ARROW_CHAR_CODE = 1       // Char code for the arrow char in the LCD.
    private const val ARROW_CGRAM_POSITION = 1  // Arrow address in the CGRAM.

    /**
     * Enumerate that Represents all the available positions in a line for writing in the LCD.
     */
    enum class Position { CENTER, LEFT, RIGHT }

    /**
     * Function that initializes the class of the TUI.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (TUI_STATE) return
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
     * @param mode Current mode of product selection, by default is [App.Mode.INDEX].
     */
    fun printProduct(product: Products.Product, mode: App.Mode = App.Mode.INDEX) {
        clearLCD()
        printText(product.name, Position.CENTER, FIRST_LINE)
        printText(
            product.id.toFilledString() + if (mode == App.Mode.ARROWS) ARROW_CHAR_CODE.toChar() else EMPTY_STRING,
            Position.LEFT,
            SECOND_LINE
        )
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
     * @param index Index of a [product] if it is null.
     */
    fun printUnavailableProduct(product: Products.Product?, index: Int) {
        clearLCD()
        printText("Product ${product?.id ?: index}", Position.CENTER, FIRST_LINE)
        printText("not available", Position.CENTER, SECOND_LINE)
    }

    /**
     * Function that prints the name of a product.
     * @param product Product to print the name.
     */
    fun printProductName(product: Products.Product) {
        printText(product.name, Position.CENTER, FIRST_LINE)
    }

    /**
     * Function that prints out of service.
     */
    fun printOutOfService() {
        clearLCD()
        printText("OUT OF SERVICE", Position.CENTER, FIRST_LINE)
    }

    /**
     * Function that prints the quantity update.
     * @param product Product to change its quantity.
     */
    fun printUpdateQuantity(product: Products.Product) {
        clearLCD()
        printText(product.name, Position.CENTER, FIRST_LINE)
        printText("Qty:??", Position.LEFT, SECOND_LINE)
        LCD.setCursor(true)
        LCD.cursor(SECOND_LINE, INIT_QUANTITY_POSITION)
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

    /**
     * Print the thank-you message in the LCD.
     */
    fun printTanks() {
        clearLCD()
        printText("Thank you", Position.CENTER, FIRST_LINE)
        printText("See you again", Position.CENTER, SECOND_LINE)
    }

    /**
     * Function that prints the Vending Machine mode in Vending mode.
     * @param currentTime Time to print in the menu.
     */
    fun printVendingMenu(currentTime: String) {
        clearLCD()
        printText("Vending Machine", Position.LEFT, FIRST_LINE)
        printText(currentTime, Position.LEFT, SECOND_LINE)
    }


    /**
     * Function that prints the Vending Machine mode in Maintenance mode.
     * @param options Array of available options.
     * @param update Flag to forcibly print all the maintenance menu.
     */
    fun printMaintenanceMenu(options: Array<String>, update:Boolean) {
        if (update) {
            clearLCD()
            printText("Maintenance Mode", Position.RIGHT, FIRST_LINE)
            OPTIONS_INDEX = 0
            toggleThroughOptions(options)
        } else {
            clearLine(OPTIONS_LINE)
            toggleThroughOptions(options)
        }
    }

    /**
     * Function that allows to toggle through Maintenance [options].
     * @param options Array of available options.
     */
    private fun toggleThroughOptions(options: Array<String>) {
        printText(options[OPTIONS_INDEX++], Position.LEFT, SECOND_LINE)
        if (OPTIONS_INDEX == options.size)
            OPTIONS_INDEX = options.indices.first
    }

    /**
     * Function that prints the collect message in the LCD.
     * @param product Product to collect.
     */
    fun printCollect(product: Products.Product) {
        clearLCD()
        printProductName(product)
        printText("Collect Product", Position.CENTER, SECOND_LINE)
    }

    /**
     * Prints the maintenance sell.
     * @param product to print information in the LCD.
     */
    fun printMaintenanceSell(product: Products.Product) {
        clearLCD()
        printText(product.name, Position.CENTER, FIRST_LINE)
        printText("*- To Print", Position.CENTER, SECOND_LINE)
    }

    /**
     * Function that prints in the LCD a Canceled Sale.
     * @param coins Coins to be returned.
     */
    fun printCancel(coins: Int) {
        clearLCD()
        printText("Vending Aborted", Position.CENTER, FIRST_LINE)
        if (coins > NO_COINS)
            printText("Return $coins", Position.CENTER, SECOND_LINE)
    }

    /**
     * Function that prints the confirmation of Shutdown on the LCD.
     * @param option Option for printing in the LCD.
     * @param position Position for printing in an LCD line(Left, Center, Right).
     */
    private fun printConfirmation(option: String, position: Position) {
        clearLCD()
        printText(option, position, FIRST_LINE)
        printText("5-Yes", Position.LEFT, SECOND_LINE)
        printText("other-No", Position.RIGHT, SECOND_LINE)
    }

    /**
     * Function that allows to print text in several Positions on the LCD.
     * @param text Text to write in the LCD.
     * @param position Position to place the cursor to write, by default is [Position.LEFT].
     * @param line Line to write the [text].
     * @param column Column to write the [text], by default is [INITIAL_POSITION].
     * @param clear If the flag is set clears the current Line.
     */
    private fun printText(text: String, position: Position, line: Int, column: Int = INITIAL_POSITION, clear: Boolean = false) {
        if (clear)
            clearLine(line)
        //if somehow we forget some space in the beginning or end.
        val cleanedText = text.trim()
        val textSize = cleanedText.length
        val middle = (LCD.COLUMNS / DIVIDER) - (textSize / DIVIDER)
        when (position) {
            Position.CENTER -> {
                LCD.cursor(line, if (textSize % PARITY_CHECK == EVEN) middle else middle - OFFSET)
                LCD.write(cleanedText)
            }
            Position.RIGHT -> {
                LCD.cursor(line, LCD.COLUMNS - textSize)
                LCD.write(cleanedText)
            }
            else -> {
                LCD.cursor(line, column)
                LCD.write(cleanedText)
            }
        }
    }

    /**
     * Function that clears a line in the LCD.
     * @param line Line to be cleared.
     */
    private fun clearLine(line: Int) {
        var fill = EMPTY_STRING
        for (i in FIRST_COLUMN until LINE_SIZE)
            fill += LCD_FILL_STRING
        LCD.cursor(line, FIRST_COLUMN)
        LCD.write(fill)
    }

    /**
     * Function that cleans the hole LCD.
     */
    private fun clearLCD() {
        LCD.clear()
    }

    /**
     * Function that converts a Char to Integer.
     * @receiver Char to convert.
     * @return Char Converted.
     */
    fun Char.toInteger(): Int {
        return this - ZERO_CHAR
    }

    /**
     * Function that gets any key pressed in the Keyboard.
     * @param timeOut Timeout to wait for a Key.
     * @return The char representation of the key.
     */
    fun getKBDKey(timeOut: Long): Char {
        return KBD.waitKey(timeOut)
    }

    /**
     * Function that prints the problem send by the App.
     * @param problem Problem to print.
     */
    fun printProblems(problem: String) {
        clearLCD()
        printText("Problems", Position.CENTER, FIRST_LINE)
        printText(problem, Position.LEFT, SECOND_LINE)
    }

    /**
     * Function that print the problem solved message.
     */
    fun problemSolved() {
        clearLCD()
        printText("Problem Solved", Position.CENTER, FIRST_LINE)
        printText("Thank you", Position.CENTER, SECOND_LINE)
    }

    /**
     * Function that print an Integer in the cell given by [column].
     * @param key Integer to print.
     * @param column Place to print the [key].
     */
    fun printInt(key: Char, column: Int) {
        printText(key.toString(),Position.LEFT, SECOND_LINE,column)
    }

    /**
     * Function that print the confirmation quest of the update product function.
     * @param product Product to print for confirmation.
     * @param intKey New quantity of a given product.
     */
    fun printUpdateConfirm(product: Products.Product, intKey: Int) {
        LCD.setCursor(false)
        clearLCD()
        printConfirmation("Update", Position.LEFT)
        printText("${product.name}=$intKey",Position.RIGHT, FIRST_LINE)
    }

    /**
     * Function that print the shutdown message in the LCD.
     */
    fun printShutdown() {
        clearLCD()
        printConfirmation("Shutdown", Position.CENTER)
    }
}

/**
 * Main function for testing the Class.
 */
fun main() {
    TUI.init()
    val product = Products.Product(1,"Jack Daniels", 2, 4)
    TUI.printProduct(product)
    Time.sleep(1000L)
    TUI.printProduct(product, App.Mode.ARROWS)

}
