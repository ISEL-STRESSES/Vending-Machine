
/**
 *
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    //£$€₿♫
    private const val REFILL ='#'
    private const val OPTION_CONSULTA_algo = '*'
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

    fun showProduct(array: Array<Products.Product>, direction: Int) {
        printText(productSelection(array, insertInt(TIME_OUT), direction).name,Position.CENTER, 0)
        printText(productSelection(array, insertInt(TIME_OUT), direction).id.toString(),Position.LEFT,1)
        printText(productSelection(array, insertInt(TIME_OUT), direction).quantity.toString(),Position.CENTER,1)
        printText(productSelection(array, insertInt(TIME_OUT), direction).price.toString(),Position.RIGHT,1)

    }


    /**
     *
     */
    fun printText(text: String, position: Position, line: Int) {
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

    private fun productSelection(products: Array<Products.Product>, currentIndex :Int = 0, direction: Int) : Products.Product{
        var index = currentIndex
        return when (direction) {
            8 -> products[--index]
            2 -> products[++index]
            else -> products[index]
        }
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
}