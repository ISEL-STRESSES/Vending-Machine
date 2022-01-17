import Time.secsToTime

object Vending {
    enum class Operation { MAINTENANCE, VENDING }

    var mode: Operation? = null
    private const val TIME_OUT = 5000L
    private const val KEY_UP = 2
    private const val KEY_DOWN = 8
    private var force = true
    private val KEY_INTERVAL = ('0'..'9')


    /**
     * Initializes all the Lower Blocks.
     */
    fun blocksInit() {
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

    }


    /**
     * Function that...TODO
     */
    fun printInitialMenu(update: Boolean = false) {
        val currentTime = Time.getCurrentTime()
        if ((currentTime/*Time.getCurrentTime()*/ - Time.LAST_TIME >= 60000L) || update) {

            Time.LAST_TIME = currentTime
            TUI.printText("Vending Machine ", line = 0)
            printTime(Time.LAST_TIME)
        }

    }


    /**
     * Function that...TODO
     */
    private fun printTime(currentTime: Long) {
        TUI.printText(currentTime.secsToTime(), line = 1)
    }


    /**
     * Function that...TODO
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
     * Function that...TODO
     */
    private fun pickProduct(mode: Mode, products: Array<Products.Product>): Products.Product? {

        var currentMode = mode
        var key: Char

        var product = products.first { it.quantity > 0 }
        TUI.printProduct(product)
        var index = product.id

        while (TUI.getKBDKey(TIME_OUT).also { key = it } != KBD.NONE) {

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
     * Function that...TODO
     */
    private fun browseProducts(products: Array<Products.Product>, currentIndex: Int = 0, key: Char): Products.Product {
        var index = currentIndex
        return when (key.toInteger()) {
            KEY_DOWN -> if (index - 1 in products.indices) products[--index] else products[index]//TODO("CAN MAKE TEH LIST GO AROUND")
            KEY_UP -> if (index + 1 in products.indices) products[++index] else products[index]
            else -> products[index]
        }
    }


    /**
     * Function that...TODO
     */
    private fun Char.toInteger(): Int = this - '0'


    /**
     * Function that...TODO
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
    Vending.blocksInit()
    while (true) {
        Vending.printInitialMenu()
    }
}
