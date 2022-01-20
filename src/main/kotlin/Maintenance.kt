import Products.changeQuantity
import TUI.NONE
import TUI.toInteger
import isel.leic.utils.Time
import kotlin.system.exitProcess


/**
 * Class that implements the Maintenance routine.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Maintenance {
    //Variable initialization.
    private var OPTIONS_INDEX = 0           //Current Index in the options Array.
    private const val WAIT_TIME = 5000L      //Default wait time for getting an KBD key.
    private const val TOGGLE_TIME = 1000L
    private const val CONFIRMATION_SHUTDOWN = '5'
    private const val DISPENSE_TEST = '1'
    private const val UPDATE_PRODUCT = '2'
    private const val REMOVE_PRODUCT = '3'
    private const val SHUTDOWN = '4'
    private const val MAINTENANCE_MONEY = '*'
    private var MAINTENANCE_STATE = false   //Current State of Maintenance(if it was already initialized).

    //Array of available Options.
    val OPTIONS = arrayOf("1-Dispense Test", "2-Update Prod.", "3-Remove Prod.", "4-Shutdown")

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
    private fun printSystemOut(array: Array<Products.Product?>, coins: Array<CoinDeposit.Coin>) {
        TUI.printShutdown()
        val key = TUI.getKBDKey(WAIT_TIME)
        if (key == CONFIRMATION_SHUTDOWN) {
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
        Time.sleep(TOGGLE_TIME)
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
            DISPENSE_TEST -> dispenseTest(mode)
            UPDATE_PRODUCT -> updateProduct(mode)
            REMOVE_PRODUCT -> removeProduct(mode)
            SHUTDOWN -> printSystemOut(Products.products, CoinDeposit.COINS_LOG)
        }
    }


    /**
     * Function that changes the quantity of a product in the Vending Machine.
     * @param mode Mode to browse trough products.
     */
    private fun updateProduct(mode: Mode) {
        val product = App.pickProduct(mode, Products.products, Operation.MAINTENANCE) ?: return
        var key :Int
        while (TUI.getInt(WAIT_TIME).also { key = it } != TUI.NONE.toInteger()) {
            TUI.printText(product.name,TUI.Position.CENTER,0)
            TUI.printUpdateQuantity(product)
            Products.products[product.id] = product.changeQuantity(key)
        }

    }


    /**
     * Function that removes a product form visualization.
     * @param mode Current mode of selection [Mode.INDEX] or [Mode.ARROWS].
     */
    private fun removeProduct(mode: Mode) {
        val product = App.pickProduct(mode, Products.products) ?: return
        Products.products[product.id] = null
    }


    /**
     * Function that tests the dispensing of a product.
     * @param mode Current mode of selection [Mode.INDEX] or [Mode.ARROWS].
     */
    private fun dispenseTest(mode: Mode) {
        val product = App.pickProduct(mode,Products.products) ?: return
        TUI.printMaintenanceSell(product)
        while (true) {
            if (TUI.getKBDKey(WAIT_TIME) == MAINTENANCE_MONEY)
                break
        }
        TUI.printText("Collect Product", TUI.Position.CENTER, 1)
        Dispenser.dispense(product.id)
    }


}


/**
 *
 */
fun main() {
    Maintenance.OPTIONS.forEach{ println(it.length) }
}