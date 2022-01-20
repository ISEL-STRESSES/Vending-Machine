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
    fun pickProduct(mode: Mode, products: Array<Products.Product?>, operation: Operation = Operation.VENDING): Products.Product? {

        var currentMode = mode
        var key: Char

        var product = products.first { it != null && (it.quantity > 0 || operation != Operation.VENDING) }
        if (product == null) {
            TUI.printOutOfService("Unavailable Prod")
            ERROR = true
            return null
        }

        TUI.printProduct(product)
        var index = product.id
        var intKey = 0

        var another = false
        var first = true
        var clear = false
        while (TUI.getKBDKey(TIME_OUT).also { key = it } != TUI.NONE) {
            println(key)

            if (key in KEY_INTERVAL && another) {
                intKey = intKey * 10 + key.toInteger()
                another = false
                clear = true
                println(intKey)
            }

            if (key in KEY_INTERVAL && first) {
                intKey = key.toInteger()
                first = false
                println(intKey)
                another = true
            }

            when (key) {
                '*' -> currentMode = currentMode.switchMode()
                '#' -> return product
                in KEY_INTERVAL -> {
                    if (currentMode == Mode.INDEX) {
                        index = key.toInteger()
                        product = products[index]
                        if (first) another = true else first = true

                    } else {
                        product = products.browseProducts(index, key, operation)
                        index = product.id
                    }

                }
            }
            if (clear) {
                intKey = 0
                clear = false
            }
            if (product != null && (product.quantity > 0 || operation != Operation.VENDING) )
                TUI.printProduct(product,currentMode)
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
    private fun Array<Products.Product?>.browseProducts(currentIndex: Int = 0, key: Char, operation: Operation): Products.Product {
        return when (key.toInteger()) {
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
            for (i in currentIndex + 1..lastIndex) {
                current = get(i)
                if (current != null && (current.quantity > 0 || operation != Operation.VENDING))
                    return current
            }

        for (i in 0 until currentIndex) {
            current = get(i)
            if (current != null && (current.quantity > 0 || operation != Operation.VENDING))
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
        if (currentIndex > 0)
            for (i in currentIndex - 1 downTo 0) {
                current = get(i)
                if (current != null && (current.quantity > 0 || operation != Operation.VENDING))
                    return current
            }
        for (i in lastIndex downTo currentIndex + 1) {
            current = get(i)
            if (current != null && (current.quantity > 0 || operation != Operation.VENDING))
                return current
        }
        return current!!
    }

}

