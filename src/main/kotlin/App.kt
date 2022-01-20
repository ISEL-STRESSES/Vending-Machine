import TUI.toInteger

/**
 * Class that implements all the common modules in the Vending Machine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object App {
    //Variable Initialization.
    private const val TIME_OUT = 5000L
    private const val KEY_UP = 2                //Key to use as up arrow.
    private const val KEY_DOWN = 8              //Key to use as down arrow.
    private val KEY_INTERVAL = ('0'..'9') //Interval of integer keys.
    private var ERROR = false
    private var force = Vending.force
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

    /**
     * Function that has the pick product protocol of the Vending Machine.
     * @param mode Current mode of selection [Mode.INDEX] or [Mode.ARROWS].
     * @param products Array that has all the product of the Vending Machine.
     * @return Returns a picked Product or null if the sequence wasn't right.
     */
    fun pickProduct(mode: Mode, products: Array<Products.Product?>): Products.Product? {

        var currentMode = mode
        var key: Char

        var product = products.first { it != null && it.quantity > 0 }
        if (product == null) {
            TUI.printText("OUT OF SERVICE", TUI.Position.CENTER, 0)
            TUI.printText("Unavailable Prod",TUI.Position.CENTER,1)
            ERROR = true
            return null
        }
        TUI.printProduct(product)
        var index = product.id

        while (TUI.getKBDKey(TIME_OUT).also { key = it } != TUI.NONE) {
            println(key)

            when (key) {
                '*' -> currentMode = currentMode.switchMode()
                '#' -> if (product?.quantity!! > 0) return product
                in KEY_INTERVAL -> {
                    if (currentMode == Mode.INDEX) {
                        product = products[key.toInteger()]!!
                        index = key.toInteger()/*product.id*/
                    } else {
                        product = browseProducts(products, index, key)
                        index = key.toInteger()/*product.id*/
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
    private fun browseProducts(products: Array<Products.Product?>, currentIndex: Int = 0, key: Char): Products.Product? {
        var index = currentIndex
        return when (key.toInteger()) {
            KEY_DOWN -> {
                when {
                    index - 1 in products.indices -> products[--index]
                    index == 0 -> products[products.lastIndex]
                    products[index] == null -> null
                    products[index]?.quantity!! <= 0 -> browseProducts(products,index,key)
                    else -> products[index]
                }
            }//TODO("check for null products")
            KEY_UP -> {
                when {
                    index + 1 in products.indices -> products[++index]
                    index == products.lastIndex -> products[0]
                    products[index] == null -> null
                    products[index]?.quantity!! <= 0 -> browseProducts(products,index,key)
                    else -> products[index]
                }
            }
            else -> products[index]
        }
    }


}