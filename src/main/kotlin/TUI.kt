/**
 * TODO
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    //£$€₿♫
    private const val LINE_SIZE = 64
    private const val TIME_OUT = 1000L
    private var TUI_STATE = false //

    enum class Position { CENTER, LEFT, RIGHT }

    /**
     * Function that...TODO
     */
    fun init() {
        if (TUI_STATE) return
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        //...
        TUI_STATE = true
    }

    /**
     * function that prints the information of a [product] on the LCD, if it doesn't have any quantity
     * available prints "Product not available".
     * @param product Product to print information about.
     */
    fun printProduct(product: Products.Product) {
        clearLCD()

        if (product.quantity <= 0) {
            printText("Product ${product.id}", Position.CENTER, 0)
            printText("not available", Position.CENTER, 1)
            println(product)
        }

        printText(product.name, Position.CENTER, 0)
        printText(product.id.toString().padStart(2, '0'), Position.LEFT, 1)
        printText("#" + product.quantity.toString().padStart(2, '0'), Position.CENTER, 1)
        printText(product.price.toString().padStart(2, '0'), Position.RIGHT, 1)
    }


    /**
     * Function that prints the process of the sale.
     * @param product
     * @param price
     */
    fun printSell(product: Products.Product, price: Int) {
        clearLCD()
        printText(product.name, Position.CENTER, 0)
        printText(price.toString(), Position.CENTER, 1)
    }


    /**
     * Function that...TODO
     */
    fun printCancel(coins: Int) {
        clearLCD()
        printText("Vending Aborted", Position.CENTER, 0)
        if (coins > 0)
            printText("Return $coins", Position.CENTER, 1)
    }


    /**
     * Function that prints the confirmation of Shutdown on the LCD.
     */
    fun printShutdown() {
        printText("Shutdown", Position.CENTER, 0)
        printText("5-Yes", line = 1)
        printText("other-No", Position.RIGHT, 1)
    }


    /**
     * Function that...TODO
     */
    fun printText(text: String, position: Position = Position.LEFT, line: Int) {
        val textSize = text.length
        val middle = LCD.COLUMNS / 2 - textSize / 2
        when (position) {
            Position.CENTER -> {
                LCD.cursor(line, if (textSize % 2 == 0) middle else middle - 1)
                LCD.write(text, false)
            }
            Position.RIGHT -> { //TODO("MAIS BONITA")
                LCD.cursor(line, LCD.COLUMNS - textSize)
                LCD.write(text, false)
            }
            else -> {
                LCD.cursor(line, 0)
                LCD.write(text, false)
            }
        }
    }

    /**
     * Function that...TODO
     */
    private fun clearLine(line: Int) {
        repeat(LINE_SIZE) {
            LCD.cursor(line, it)
            LCD.write(" ", false)
        }
    }

    /**
     * Function that cleans the hole LCD.
     */
    fun clearLCD() {
        clearLine(0)
        clearLine(1)
    }

    /**
     * Function that...TODO
     */
    fun getInt(timeout: Long): Int {
        var value = 0
        val intToInsert = KBD.waitKey(timeout)

        if (intToInsert in (KBD.keys.filter { it != '#' && it != '*' }))
            value = intToInsert.toString().toInt()

        val newInt = KBD.waitKey(timeout)
        if (newInt in (KBD.keys.filter { it != '#' && it != '*' }))
            value = (value shl 1) or newInt.toString().toInt()
        return value
    }

    /**
     * Function that...TODO
     */
    fun getKBDKey(timeOut: Long = TIME_OUT): Char {
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
    TUI.printText("darta-cao", line = 0)
    TUI.printText("Julieta", TUI.Position.CENTER, 1)
    TUI.printText("BZ", line = 0)

}