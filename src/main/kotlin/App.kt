//Import needed for converting a key into its value.
import TUI.toInteger

/**
 * Class that implements all the common modules in the Vending Machine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object App {
    //Variable Initialization.
    const val TIME_OUT = 5000L                  // Time out for waiting for a key.
    const val FAST_TIME = 100L                  // Faster time for initializing the vending process.
    private const val KEY_UP = 2                // Key to use as up arrow.
    private const val KEY_DOWN = 8              // Key to use as down arrow.
    private const val FIRST_INDEX = 0           // First index of a collection.
    private const val INDEX_OFFSET = 1          // Offset needed for browsing through products.
    val KEY_INTERVAL = ('0'..'9')               // Interval of integer keys.
    const val CONFIRMATION_KEY = '#'            // Character that selects teh current product.
    private const val MODE_KEY = '*'            // Character that changes modes(Arrows or Index).
    const val MULTIPLIER = 10                   // Multiplier for getting 2 keys form Keyboard.
    var ERROR = false                           // Flag of for error detection.
    private var force = Vending.FORCE           // Flag for printing the initial menu again.
    private var APP_STATE = false               // Current State of App(if it was already initialized).
    var REQUEST: String? = null                 // Request send by any vending machine module.

    /**
     * Enumerate that has all the selection modes implemented for selecting a product.
     *
     */
    enum class Mode { ARROWS, INDEX }

    /**
     * Enumerate of every possible mode in the Vending Machine.
     */
    enum class Operation { MAINTENANCE, VENDING, REQUESTS }

    /**
     * Function that initializes the class App.
     * If it was already initialized exists the function.
     */
    fun appLowerBlocksInit() {
        if (APP_STATE) return
        M.init()
        CoinAcceptor.init()
        Dispenser.init()
        TUI.init()
        Products.init()
        CoinDeposit.init()
        AppTime.init()

        APP_STATE = true
    }

    /**
     * Function that runs the Vending Machine app.
     */
    fun runApp() {
        //initializes all the app lower blocks
        appLowerBlocksInit()
        val mode = Mode.INDEX
        while (true) {
            if (!M.setMaintenance() && !ERROR) {
                Vending.run(mode)
                Maintenance.UPDATE = true
            } else {
                Maintenance.run(mode, REQUEST)
                Vending.FORCE = true
            }
        }
    }

    /**
     * Function that has the pick product protocol of the Vending Machine.
     * @param mode Current mode of selection [Mode.INDEX] or [Mode.ARROWS].
     * @param products Array that has all the product of the Vending Machine.
     * @return Returns a picked Product or null if the sequence wasn't right.
     */
    fun pickProduct(mode: Mode, products: Array<Products.Product?>, operation: Operation): Products.Product? {

        var currentMode = mode
        var key: Char

        var product = products.first { it != null && it.flagDetection(operation) }
        if (product == null) {
            REQUEST = "Unavailable Prod"
            ERROR = true
            TUI.printOutOfService()
            return null
        }

        TUI.printProduct(product)
        var index = product.id
        var intKey = index

        while (TUI.getKBDKey(TIME_OUT).also { key = it } != TUI.NONE) {
            if (key in KEY_INTERVAL) {
                intKey = intKey * MULTIPLIER + key.toInteger()
            }

            if (intKey !in Products.products.indices && key in KEY_INTERVAL) {
                intKey = key.toInteger()
            }

            when (key) {
                MODE_KEY -> currentMode = currentMode.switchMode()
                CONFIRMATION_KEY -> return if (product != null && product.flagDetection(operation)) product else continue
                in KEY_INTERVAL -> {
                    if (currentMode == Mode.INDEX) {
                        index = intKey
                        product = products[index]

                    } else {
                        product = products.browseProducts(index, intKey, operation)
                        index = product.id
                    }
                }
            }

            if (product != null && product.flagDetection(operation))
                TUI.printProduct(product, currentMode)
            else TUI.printUnavailableProduct(product, index)
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
     * @receiver Array of Products.
     * @param currentIndex Current index of the product.
     * @param key Key to check if is available for [Mode.ARROWS].
     * @return Returns a product after one key pressed.
     */
    private fun Array<Products.Product?>.browseProducts(
        currentIndex: Int,
        key: Int,
        operation: Operation
    ): Products.Product {
        return when (key) {
            KEY_DOWN -> previousValid(currentIndex, operation)
            KEY_UP -> nextValid(currentIndex, operation)
            else -> get(currentIndex)!!
        }
    }

    /**
     * Function that iterates upwards the Array of Products.
     * @receiver Array of Products to iterate.
     * @param currentIndex current index on the array.
     * @return Returns the first product not null and with quantity.
     */
    private fun Array<Products.Product?>.nextValid(currentIndex: Int, operation: Operation): Products.Product {
        var current = get(currentIndex)
        if (currentIndex < lastIndex)
            for (i in currentIndex + INDEX_OFFSET..lastIndex) {
                current = get(i)
                if (current != null && current.flagDetection(operation))
                    return current
            }

        for (i in FIRST_INDEX until currentIndex - INDEX_OFFSET) {
            current = get(i)
            if (current != null && current.flagDetection(operation))
                return current
        }
        return current!!
    }


    /**
     * Function that iterates downwards the Array of Products.
     * @receiver Array of Products to iterate.
     * @param currentIndex current index on the array.
     * @return Returns the first product not null and with quantity.
     */
    private fun Array<Products.Product?>.previousValid(currentIndex: Int, operation: Operation): Products.Product {
        var current = get(currentIndex)
        if (currentIndex > FIRST_INDEX)
            for (i in currentIndex - INDEX_OFFSET downTo FIRST_INDEX) {
                current = get(i)
                if (current != null && current.flagDetection(operation))
                    return current
            }
        for (i in lastIndex downTo currentIndex + INDEX_OFFSET) {
            current = get(i)
            if (current != null && current.flagDetection(operation))
                return current
        }
        return current!!
    }

    /**
     * Function that detect flags for printing an available product.
     * @receiver Products.Product - Product for checking for quantity.
     * @param operation current operation.
     * @return Returns a boolean if any flag is true.
     */
    private fun Products.Product.flagDetection(operation: Operation): Boolean {
        return quantity > Products.MINIMUM_QUANTITY || operation != Operation.VENDING
    }

}

/**
 * Runs the Vending machine and its modes.
 */
fun main() {
    App.runApp()
}
