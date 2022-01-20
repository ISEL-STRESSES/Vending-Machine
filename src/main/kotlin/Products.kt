/**
 * Interface that implements the Products.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Products {
    //Variable Initialization.
    private const val MACHINE_MAX_SIZE = 16                         // Vending machine max capacity for products
    var products: Array<Product?> = arrayOfNulls(MACHINE_MAX_SIZE)  // Log of the Product file.
    private const val ID_INDEX = 0                                  // Index of the ID field in the file String.
    private const val NAME_INDEX = 1                                // Index of the name field in the file String.
    private const val QUANTITY_INDEX = 2                            // Index of the quantity field in the file String.
    private const val PRICE_INDEX = 3                               // Index of the price field in the file String.
    private const val DELIMITER = ';'                               // Delimiter of the file String.
    private var PRODUCTS_STATE = false                              // Current State of Products(if it was already initialized).

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
        var idx = 0
        val products = Array(MACHINE_MAX_SIZE) {
            val product = file[idx++].split(DELIMITER)
            if (it == product[ID_INDEX].toInt())
                Product(
                    id = product[ID_INDEX].toInt(),
                    name = product[NAME_INDEX],
                    quantity = product[QUANTITY_INDEX].toInt(),
                    price = product[PRICE_INDEX].toInt()
                )
            else {
                idx--
                null
            }
        }
        return products
    }


    /**
     * Function that saves the array of [Product] into a file for log.
     * @param array Array of Products to Store information.
     */
    fun saveProducts(array: Array<Product?>) {
        val aux = array.filterNotNull()
        val newArray = Array(aux.size){
            val element = aux[it]
            "${element.id};${element.name};${element.quantity};${element.price}"
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

}

fun main () {
    Products.init()
    Products.products.forEach(::println)
    println()
    Products.saveProducts(Products.products)
}