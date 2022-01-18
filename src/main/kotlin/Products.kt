/**
 * Interface that implements the Products.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Products {
    //Variable Initialization.
    var products: Array<Product?> = emptyArray()     // Log of the Product file.
    private const val ID_INDEX = 0                  // TODO: 18/01/2022 comments
    private const val NAME_INDEX = 1                //
    private const val QUANTITY_INDEX = 2            //
    private const val PRICE_INDEX = 3               //
    private const val DELIMITER = ';'               //
    private var PRODUCTS_STATE = false              // Current State of Products(if it was already initialized).

    /**
     * Class that represents a [Product] and all its properties.
     * @property id ID of the product.
     * @property name Name of the product.
     * @property quantity Current quantity of the product.
     * @property price Price of the product.
     */
    data class Product(val id: Int, val name: String, var quantity: Int, val price: Int)


    /**
     * Function that initializes the class of the Products.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (PRODUCTS_STATE) return
        FileAccess.init()
        products = vendingProducts()
        PRODUCTS_STATE = true
    }


    /**
     * Function that interprets an array of Strings from the [FileAccess] and
     * transforms it to into an array of [Product].
     * @return Array of Products after reading the Products file.
     */
    private fun vendingProducts(): Array<Product?> {
        val file = FileAccess.readProductFile()
        val products = Array(file.size) {
            val product = file[it].split(DELIMITER)
            if (it == product[ID_INDEX].toInt())
                Product(
                    id = product[ID_INDEX].toInt(),
                    name = product[NAME_INDEX],
                    quantity = product[QUANTITY_INDEX].toInt(),
                    price = product[PRICE_INDEX].toInt()
                )
            else null
        }
        return products
    }


    /**
     * Function that saves the array of [Product] into a file for log.
     * @param array Array of Products to Store information.
     */
    fun saveProducts(array: Array<Product?>) {
        var size = 0
        for (i in array.indices)
            if (array[i] != null)
                size++

        val newArray = Array(size) { "" }
        for (i in array.indices) {
            val element = array[i]
            if (element != null) {
                val line = "${element.id};${element.name};${element.quantity};${element.price}"
                newArray[i] = line
            }
        }
        FileAccess.writeProductFile(newArray)
    }


    /**
     * Function that adds quantity to a Product.
     * @receiver Product to add [quantity].
     * @param quantity quantity to add to a [Product].
     * @return Returns the original product with the new quantity.
     */
    fun Product.changeQuantity(quantity: Int): Product {
        return this.copy(quantity = quantity)
    }


//    /**
//     * Function that removes a certain quantity to a Product.
//     * @receiver Product to take [quantity].
//     * @param quantity quantity to take to a [Product].
//     * @return Returns the original product with the new quantity.
//     */
//    fun Product.takeQuantity(quantity: Int): Product {
//        return this.copy(quantity = this.quantity - quantity)
//    }

}