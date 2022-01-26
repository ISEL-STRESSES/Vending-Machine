//Imports todo
import Products.changeQuantity
import TUI.toInteger
import kotlin.system.exitProcess

/**
 * Class that implements the Maintenance routine.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Maintenance {
    //Variable initialization.
    private const val WAIT_TIME = 5000L             //Default wait time for getting an KBD key.
    private const val NORMAL_EXIT_CODE = 0          // Normal exit code when closing the application.
    private const val CONFIRMATION_SHUTDOWN = '5'   // Confirmation key for closing the application.
    private const val DISPENSE_TEST = '1'           // Dispense test selector key.
    private const val UPDATE_PRODUCT = '2'          // Update product selector key.
    private const val REMOVE_PRODUCT = '3'          // Remove product selector key.
    private const val SHUTDOWN = '4'                // Shutdown selector key.
    private const val MAINTENANCE_MONEY = '*'       // Money for testing the dispense mode.
    private var FIRST = true                        // Variable for checking the first print of Maintenance menu.
    private var UPDATE = false                      // Variable to forcibly print the Maintenance menu.
    private var MAINTENANCE_STATE = false           //Current State of Maintenance(if it was already initialized).

    //Array of available Options.
    private val OPTIONS = arrayOf("1-Dispense Test", "2-Update Prod.", "3-Remove Prod.", "4-Shutdown")

    /**
     * Function that initializes the class of the Maintenance.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (MAINTENANCE_STATE) return
        App.appLowerBlocksInit()
        MAINTENANCE_STATE = true
    }

    /**
     * Function that saves the products and its properties as well as the number of coins introduced
     * during functioning.
     * @param array Array of Products to save records.
     * @param coins Array of Coins to save records.
     */
    private fun printSystemOut(array: Array<Products.Product?>, coins: Array<CoinDeposit.Coin>) {
        TUI.printShutdown()
        val key = TUI.getKBDKey(WAIT_TIME)
        if (key == CONFIRMATION_SHUTDOWN) {
            CoinDeposit.saveCoins(coins)
            Products.saveProducts(array)
            exitProcess(NORMAL_EXIT_CODE)
        }
        UPDATE = true
    }

    /**
     * Function that prints maintenance menu.
     */
    private fun printMaintenance(update: Boolean, first: Boolean) {
        TUI.printMaintenanceMenu(OPTIONS, update, first)
        FIRST = false
    }

    /**
     * Function that has the routine of the Maintenance Mode.
     * @param mode Mode needed for the [dispenseTest].
     */
    fun run(mode: App.Mode) {
        printMaintenance(UPDATE, FIRST)
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
    private fun updateProduct(mode: App.Mode) {
        val product = App.pickProduct(mode, Products.products, App.Operation.MAINTENANCE) ?: return
        var key: Int
        while (TUI.getInt(WAIT_TIME).also { key = it } != TUI.NONE.toInteger()) {
            TUI.printProductName(product)
            TUI.printUpdateQuantity(product)
            // TODO: 25/01/2022
            Products.products[product.id] = product.changeQuantity(key)
        }
        UPDATE = true
    }

    /**
     * Function that removes a product form visualization.
     * @param mode Current mode of selection [App.Mode.INDEX] or [App.Mode.ARROWS].
     */
    private fun removeProduct(mode: App.Mode) {
        val product = App.pickProduct(mode, Products.products) ?: return
        Products.products[product.id] = null
        UPDATE = true
    }

    /**
     * Function that tests the dispensing of a product.
     * @param mode Current mode of selection [App.Mode.INDEX] or [App.Mode.ARROWS].
     */
    private fun dispenseTest(mode: App.Mode) {
        val product = App.pickProduct(mode, Products.products)
        if (product == null ) {
            UPDATE = true
            return
        }
        TUI.printMaintenanceSell(product)
        while (true) {
            if (TUI.getKBDKey(WAIT_TIME) == MAINTENANCE_MONEY)
                break
        }
        TUI.printCollect(product)
        Dispenser.dispense(product.id)
        UPDATE = true
    }

}

/**
 * Main function for testing the class.
 */
fun main() {
    Maintenance.init()
    val mode = App.Mode.INDEX
    while (true)
        Maintenance.run(mode)
    // TODO: 26/01/2022 HAS AN UNDOCUMENTED FIXTURE
}