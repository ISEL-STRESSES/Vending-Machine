
/**
 *
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {
    private const val REFILL ='#'
    private const val OPTION_CONSULTA_algo = '*'
    private var TUI_STATE = false //

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

    fun showProduct() {

    }
    //repositions the cursor because limit of characters
    fun repositionCursor() {

    }

    fun printText() {

    }

    fun clearLine() {

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