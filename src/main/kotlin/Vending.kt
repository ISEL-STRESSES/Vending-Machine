import TUI.toInteger
import Time.secsToTime

/**
 * Class that implements the Vending Routine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object Vending {
    //Variable initialization.
    private const val TIME_OUT = 5000L          //Time to wait for a key.
    private const val KEY_UP = 2                //Key to use as up arrow.
    private const val KEY_DOWN = 8              //Key to use as down arrow.
    private var force = true                    //Flag that indicates if is needed to print again the initial menu.
    private val KEY_INTERVAL = ('0'..'9') //Interval of integer keys.
    private var VENDING_STATE = false           //Current State of Vending(if it was already initialized).


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
        val currentTime = Time.getCurrentTime()
        if ((currentTime/*Time.getCurrentTime()*/ - Time.LAST_TIME >= 60000L) || update) {

            Time.LAST_TIME = currentTime
            TUI.printText("Vending Machine ",TUI.Position.LEFT, 0)
            printTime(Time.LAST_TIME)
        }

    }


    /**
     * Function that prints the Time and Date on the LCD.
     * @param currentTime Time since 1st january 1970 in milliseconds.
     */
    private fun printTime(currentTime: Long) {
        TUI.printText(currentTime.secsToTime(), TUI.Position.LEFT,1)
    }


    /**
     * Function that has the routine of the Vending Mode.
     * @param mode Mode needed for the [pickProduct].
     * @return Returns requests if there is any
     */
    fun run(mode: Mode): String? {
        printInitialMenu(force)

        var pickedProduct: Products.Product? = null

        if (TUI.getKBDKey(TIME_OUT) == '#')
            pickedProduct = pickProduct(mode, Products.products)

        if (pickedProduct != null) {
            return sellProduct(pickedProduct)

        }

        return null
    }


    /**
     * Function that has the pick product protocol of the Vending Machine.
     * @param mode Current mode of selection [Mode.INDEX] or [Mode.ARROWS].
     * @param products Array that has all the product of the Vending Machine.
     * @return Returns a picked Product or null if the sequence wasn't right.
     */
    private fun pickProduct(mode: Mode, products: Array<Products.Product>): Products.Product? {

        var currentMode = mode
        var key: Char

        var product = products.first { it.quantity > 0 }
        TUI.printProduct(product)
        var index = product.id

        while (TUI.getKBDKey(TIME_OUT).also { key = it } != TUI.NONE) {

            println(key)
            when (key) {
                '*' -> currentMode = currentMode.switchMode()
                '#' -> if (product.quantity > 0) return product
                in KEY_INTERVAL -> {
                    if (currentMode == Mode.INDEX) {
                        product = products[key.toInteger()]
                        index = product.id
                    } else {
                        product = browseProducts(products, index, key)
                        index = product.id
                    }

                }
            }
            TUI.printProduct(product)
        }
        force = true
        return null
    }


    /**
     * Function that toggles trough modes
     * @receiver [Mode] current mode.
     * @return new mode.
     */
    private fun Mode.switchMode(): Mode = if (this == Mode.INDEX) Mode.ARROWS else Mode.INDEX


    /**
     * Function that browses troth all the products of the vending Machine.
     * @param products Array of all Products.
     * @param currentIndex Current index of the product.
     * @param key Key to check if is available for [Mode.ARROWS].
     * @return Returns a product after one key pressed.
     */
    private fun browseProducts(products: Array<Products.Product>, currentIndex: Int = 0, key: Char): Products.Product {
        var index = currentIndex
        return when (key.toInteger()) {
            KEY_DOWN -> {
                if (index - 1 in products.indices)
                    products[--index]
                else if (index == 0 )
                    products[products.lastIndex]
                else if (products[index].quantity <= 0)
                    browseProducts(products,index,key)
                else products[index]
            }//TODO("check for null products")
            KEY_UP -> {
                if (index + 1 in products.indices)
                    products[++index]
                else if (index == products.lastIndex)
                        products[0]
                else if (products[index].quantity <= 0)
                    browseProducts(products,index,key)
                else products[index]
            }
            else -> products[index]
        }
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
            if (TUI.getKBDKey(TIME_OUT) == '#') {
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
                TUI.printText("Collect Product", TUI.Position.CENTER, 1)
                CoinAcceptor.collectCoins()
                Dispenser.dispense(selectedProduct.id)
                Products.products[selectedProduct.id].quantity--
                CoinDeposit.COINS += coinsInserted
                break
            }
        }

        val depositRequest = CoinDeposit.emptyDepositRequest()
        if (depositRequest != null) {
            TUI.printText("OUT OF SERVICE", TUI.Position.CENTER, 0)
            TUI.printText(depositRequest, TUI.Position.CENTER, 1)
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
