
/**
 *
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object TUI {

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