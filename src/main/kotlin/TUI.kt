
/**
 *
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    //£$€₿♫
    private const val REFILL ='#'
    private const val OPTION_CONSULTA = '*'
    private const val LINE_SIZE = 64
    private var TEXT_LCD = ""
    private val TIME_OUT = 1000L
    private var TUI_STATE = false //
    enum class Position {CENTER, LEFT, RIGHT}

    /**
     *
     */
    fun init(){
        if (TUI_STATE) return
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        //...
        TUI_STATE = true
    }

    fun showProduct(array: Array<Products.Product>) {
        printText(productSelection(array, insertInt(TIME_OUT)).name,Position.CENTER, 0)
        printText(productSelection(array, insertInt(TIME_OUT)).id.toString(),Position.LEFT,1)
        printText(productSelection(array, insertInt(TIME_OUT)).quantity.toString(),Position.CENTER,1)
        printText(productSelection(array, insertInt(TIME_OUT)).price.toString(),Position.RIGHT,1)
    }


    /**
     *
     */
    fun printText(text: String, position: Position = Position.LEFT, line: Int) {
        val textSize = text.length
        when (position) {
            Position.CENTER -> {
                LCD.cursor(line, LCD.COLUMNS/2 - textSize/2)
                LCD.write(text,false)
            }
            Position.RIGHT -> { //TODO("MAIS BONITA")
                LCD.cursor(line,LCD.COLUMNS-textSize)
                LCD.write(text,false)
            }
            else -> {
                LCD.cursor(line,0)
                LCD.write(text, false)
            }
        }
    }

    /**
     *
     */
    fun clearLine() {
        repeat(LINE_SIZE) {
            LCD.write(" ", false)
        }
    }

    private fun productSelection(products: Array<Products.Product>, currentIndex :Int = 0) : Products.Product{
        var index = currentIndex
        val selector = KBD.waitKey(TIME_OUT)
        if(selector == OPTION_CONSULTA) {
            val direction = KBD.waitKey(TIME_OUT)

            return when (direction.toInt()) {
                8 -> products[--index]
                2 -> products[++index]
                else -> products[index]
            }
        }
        return products[currentIndex]
    }


    private fun insertInt(timeout: Long): Int {
        var value = 0
        val intToInsert = KBD.waitKey(timeout)

        if (intToInsert in (KBD.keys.filter { it != '#' && it != '*' }))
            value = intToInsert.toInt()

        val newInt = KBD.waitKey(timeout)
        if (newInt in (KBD.keys.filter { it != '#' && it != '*' }))
            value = (value shl 1) or newInt.toInt()
        return value
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
    TUI.printText("darta-cao",line = 0)
    TUI.printText("Julieta",TUI.Position.CENTER,1)
    TUI.printText("BZ",line = 0)

}