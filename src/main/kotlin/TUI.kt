
/**
 *
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    //£$€₿♫
    private const val REFILL ='#'
    private const val OPTION_CONSULTA = '*'
    private const val LINE_SIZE = 64
    private const val TIME_OUT = 1000L
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

    /**
     *
     */
    fun printProduct(product: Products.Product) {
        clearLCD()

        if (product.quantity <= 0) {
            printText("Product ${product.id}", Position.CENTER, 0)
            printText("not available", Position.CENTER, 1)
            println(product)
        }

        printText(product.name,Position.CENTER, 0)
        printText(product.id.toString().padStart(2,'0'),Position.LEFT,1)
        printText("#" +product.quantity.toString().padStart(2,'0'),Position.CENTER,1)
        printText(product.price.toString().padStart(2,'0'),Position.RIGHT,1)
    }


    /**
     *
     */
    fun printText(text: String, position: Position = Position.LEFT, line: Int) {
        val textSize = text.length
        val middle = LCD.COLUMNS/2 - textSize/2
        when (position) {
            Position.CENTER -> {
                LCD.cursor(line,if (textSize % 2 == 0) middle else middle /*-1*/)
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
    private fun clearLine(line:Int) {
        repeat(LINE_SIZE) {
            LCD.cursor(line,it-1)
            LCD.write(" ", false)
        }
    }

    /**
     *
     */
    fun clearLCD(){
        clearLine(0)
        clearLine(1)
    }

    /**
     * TOMAR PARA APP
     */
//    fun browseProducts(products: Array<Products.Product>, currentIndex :Int = 0) : Products.Product{
//        var index = currentIndex
//        return when (getInt(TIME_OUT)) {
//            KEY_DOWN -> if (index - 1 in products.indices) products[--index] else products[index]
//            KEY_UP -> if (index + 1 in products.indices) products[++index] else products[index]
//            else -> products[index]
//        }
//    }

    /**
     *
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
     *
     */
    fun getKBDKey(timeOut: Long = TIME_OUT):Char {
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
    TUI.printText("darta-cao",line = 0)
    TUI.printText("Julieta",TUI.Position.CENTER,1)
    TUI.printText("BZ",line = 0)

}