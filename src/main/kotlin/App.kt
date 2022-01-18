
/**
 * Class that implements all the common modules in the Vending Machine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object App {
    //Variable Initialization.
    private var APP_STATE = false   //Current State of App(if it was already initialized).


    /**
     * Function that initializes the class App.
     * If it was already initialized exists the function.
     */
    fun allBlocksInit() {
        if (APP_STATE) return
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
        TUI.init()
        M.init()
        CoinAcceptor.init()
        Dispenser.init()
        FileAccess.init()
        Time.init()
        Products.init()
        CoinDeposit.init()
        APP_STATE = true
    }
}