import isel.leic.utils.Time
import kotlin.system.exitProcess


/**
 * Class that implements the Maintenance routine.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Maintenance {
    //Variable initialization.
    private var OPTIONS_INDEX = 0           //Current Index in the options Array.
    private const val WAIT_TIME = 500L      //Default wait time for getting an KBD key.
    private var MAINTENANCE_STATE = false   //Current State of Maintenance(if it was already initialized).

    //Array of available Options.
    private val OPTIONS = arrayOf("1-Dispense Test ", "2-Update Prod.", "3-Remove Prod.", "4-Shutdown")

    /**
     * Function that initializes the class of the Maintenance.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (MAINTENANCE_STATE) return
        App.allBlocksInit()
        MAINTENANCE_STATE = true
    }

    /**
     * Function that saves the products and its properties as well as the number of coins introduced during functioning.
     * @param array Array of Products to save records.
     * @param coins Array of Coins to save records.
     */
    private fun printSystemOut(array: Array<Products.Product>, coins: Array<CoinDeposit.Coin>) {
        TUI.printShutdown()
        val key = TUI.getKBDKey(WAIT_TIME)
        if (key == '5') {
            CoinDeposit.saveCoins(coins)
            Products.saveProducts(array)
            exitProcess(0)
        } else return
    }


    /**
     * Function that allows to toggle through Maintenance [OPTIONS].
     */
    private fun toggleThroughOptions() {
        TUI.printText(OPTIONS[OPTIONS_INDEX++],TUI.Position.LEFT, 1)
        Time.sleep(WAIT_TIME)
        if (OPTIONS_INDEX == OPTIONS.size)
            OPTIONS_INDEX = OPTIONS.indices.first
    }


    /**
     * Function that prints maintenance menu.
     */
    private fun printMaintenance() {
        TUI.clearLCD()
        TUI.printText("Maintenance Mode", TUI.Position.RIGHT, 0)
        toggleThroughOptions()

    }


    /**
     * Function that has the routine of the Maintenance Mode.
     * @param mode Mode needed for the [dispenseTest].
     */
    fun runMaintenance(mode: Mode) {
        printMaintenance()
        when (TUI.getKBDKey(WAIT_TIME)) {
            '1' -> {
                dispenseTest(mode)
            }
            '2' -> {
                updateProduct()
            }
            '3' -> {
                removeProduct()
            }
            '4' -> {
                printSystemOut(Products.products, CoinDeposit.COINS_LOG)
            }
        }
    }


    /**
     * Function that...TODO
     */
    private fun updateProduct() {
        TODO("Not yet implemented")
    }


    /**
     * Function that...TODO
     */
    private fun removeProduct() {
        TODO("Not yet implemented")
    }


    /**
     * Function that...TODO
     * @param mode Current mode of selection [Mode.INDEX] or [Mode.ARROWS].
     */
    private fun dispenseTest(mode: Mode) {
        TODO("Not yet implemented")
    }


}
