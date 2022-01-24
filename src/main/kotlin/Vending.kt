//Imports needed for Time.
import AppTime.secsToTime
import isel.leic.utils.Time

/**
 * Class that implements the Vending Routine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object Vending {
    //Variable initialization.
    private const val TIME_OUT = 5000L          // Time to wait for a key.
    private const val SELL_TIME_OUT = 1000L     // TODO: 23/01/2022
    private const val TANKS_WAIT_TIME = 500L    // Tanks wait time.
    private const val SECS_IN_A_MINUTE = 60000L // TODO: 24/01/2022
    private const val CONFIRMATION_KEY = '#'    // TODO: 24/01/2022
    var force = true                            // Flag that indicates if is needed to print again the initial menu.
    private var VENDING_STATE = false           // Current State of Vending(if it was already initialized).


    /**
     * Function that initializes the class of the Vending.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (VENDING_STATE) return
        App.allBlocksInit()
        VENDING_STATE = true
    }

    /**
     * Function that print the initial menu of the Vending Machine.
     * @param update Flag that forces the print of the menu.
     */
    fun printInitialMenu(update: Boolean = false) {
        val currentTime = AppTime.getCurrentTime()
        if ((currentTime - AppTime.LAST_TIME >= SECS_IN_A_MINUTE) || update) {
            AppTime.LAST_TIME = currentTime
            TUI.printVendingMenu(AppTime.LAST_TIME.secsToTime())
        }

    }

    /**
     * Function that has the routine of the Vending Mode.
     * @param mode Mode needed for the [App.pickProduct].
     * @return Returns requests if there is any
     */
    fun run(mode: Mode): String? {
        printInitialMenu(force)

        var pickedProduct: Products.Product? = null

        if (TUI.getKBDKey(TIME_OUT) == CONFIRMATION_KEY)
            pickedProduct = App.pickProduct(mode, Products.products)

        if (pickedProduct != null) {
            return sellProduct(pickedProduct)

        }

        return null
    }


    /**
     * Function that has the sell product Protocol.
     * @param selectedProduct Selected product to sell.
     * @return Returns a [Operation.REQUESTS] if there is any.
     */
    private fun sellProduct(selectedProduct: Products.Product): String? {
        TUI.printSell(selectedProduct, selectedProduct.price)
        var coinsInserted = 0
        while (coinsInserted < selectedProduct.price) {
            if (TUI.getKBDKey(SELL_TIME_OUT) == CONFIRMATION_KEY) {
                TUI.printCancel(coinsInserted)
                CoinAcceptor.ejectCoins()
                break
            }

            if (CoinAcceptor.hasCoin()) {
                CoinAcceptor.acceptCoin()
                coinsInserted++
                TUI.printSell(selectedProduct, selectedProduct.price - coinsInserted)
            }
            if (coinsInserted == selectedProduct.price) {
                TUI.printCollect(selectedProduct)
                CoinAcceptor.collectCoins()
                Dispenser.dispense(selectedProduct.id)
                Products.products[selectedProduct.id]!!.quantity--
                CoinDeposit.COINS += coinsInserted
                break
            }
            TUI.printTanks()
            Time.sleep(TANKS_WAIT_TIME)
        }

        val depositRequest = CoinDeposit.depositRequest()
        if (depositRequest != null) {
            TUI.printOutOfService(depositRequest)
            return depositRequest
        }
        return null
    }

}


/**
 * Main function for testing the class.
 */
fun main() {
    App.allBlocksInit()
    val mode = Mode.INDEX
    while (true) {
        Vending.run(mode)
    }
}
