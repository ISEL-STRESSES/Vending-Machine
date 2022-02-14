//Imports needed for Time.
import AppTime.secsToTime
import isel.leic.utils.Time

/**
 * Class that implements the Vending Routine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object Vending {
    //Variable initialization.
    private const val CANCEL_TIME_OUT = 500L    // Time to wait for a key.
    private const val TANKS_WAIT_TIME = 1000L   // Tanks wait time.
    var FORCE = false                           // Flag that indicates if is needed to print again the initial menu.
    private var VENDING_STATE = false           // Current State of Vending(if it was already initialized).

    /**
     * Function that initializes the class of the Vending.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (VENDING_STATE) return
        App.appLowerBlocksInit()
        VENDING_STATE = true
    }

    /**
     * Function that print the initial menu of the Vending Machine.
     * @param update Flag that forces the print of the menu.
     */
    private fun printInitialMenu(update: Boolean = false) {
        val currentTime = AppTime.getCurrentTime()
        if ((currentTime - AppTime.LAST_TIME >= AppTime.SECS_IN_A_MINUTE) || update) {
            AppTime.LAST_TIME = currentTime
            TUI.printVendingMenu(AppTime.LAST_TIME.secsToTime())
        }
    }

    /**
     * Function that has the routine of the Vending Mode.
     * @param mode Mode needed for the [App.pickProduct].
     * @return Returns requests if there is any
     */
    fun run(mode: App.Mode) {
        printInitialMenu(FORCE)
        FORCE = false
        var pickedProduct: Products.Product? = null

        if (TUI.getKBDKey(App.FAST_TIME) == App.CONFIRMATION_KEY)
            pickedProduct = App.pickProduct(mode, Products.products, App.Operation.VENDING)

        if (App.ERROR)
            return

        if (pickedProduct != null) {
            sellProduct(pickedProduct)
        }
    }

    /**
     * Function that has the sell product Protocol.
     * @param selectedProduct Selected product to sell.
     * @return Returns a [App.Operation.REQUESTS] if there is any.
     */
    private fun sellProduct(selectedProduct: Products.Product) {
        TUI.printSell(selectedProduct, selectedProduct.price)
        var coinsInserted = 0

        while (coinsInserted < selectedProduct.price) {
            if (TUI.getKBDKey(CANCEL_TIME_OUT) == App.CONFIRMATION_KEY) {
                TUI.printCancel(coinsInserted)
                CoinAcceptor.ejectCoins()
                FORCE = true
                break
            }

            if (CoinAcceptor.hasCoin()) {
                CoinAcceptor.acceptCoin()
                coinsInserted++

                if (CoinDeposit.depositRequest() != null) {
                    TUI.printOutOfService()
                    App.REQUEST = CoinDeposit.depositRequest()
                    App.ERROR = true
                    return
                }
                TUI.printSell(selectedProduct, selectedProduct.price - coinsInserted)
            }

            if (coinsInserted == selectedProduct.price) {
                TUI.printCollect(selectedProduct)
                CoinAcceptor.collectCoins()
                CoinDeposit.COINS += coinsInserted
                Dispenser.dispense(selectedProduct.id)
                Products.products[selectedProduct.id]!!.quantity--
                TUI.printTanks()
                Time.sleep(TANKS_WAIT_TIME)
                FORCE = true
                break
            }
        }
    }
}


/**
 * Main function for testing the class.
 */
fun main() {
    App.appLowerBlocksInit()
    val mode = App.Mode.INDEX
    while (true) {
        Vending.run(mode)
    }
}
