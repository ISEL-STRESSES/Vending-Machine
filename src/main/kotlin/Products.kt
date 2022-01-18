/**
 * Interface that implements the Products.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Products {
    //Variable Initialization.
    var products: Array<Product> = emptyArray()     //Log of the Product file.
    private var PRODUCTS_STATE = false              //Current State of Products(if it was already initialized).

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
    private fun vendingProducts(): Array<Product> {
        val file = FileAccess.readProductFile()
        val products = Array(file.size) {
            val product = file[it].split(';')
            Product(product[0].toInt(), product[1], product[2].toInt(), product[3].toInt())
        }
        return products
    }


    /**
     * Function that saves the array of [Product] into a file for log.
     * @param array Array of Products to Store information.
     */
    fun saveProducts(array: Array<Product>) {
        val newArray = Array(array.size) { "" }
        for (i in array.indices) {
            val line = "${array[i].id};${array[i].name};${array[i].quantity};${array[i].price}"
            newArray[i] = line
        }

        FileAccess.writeProductFile(newArray)
    }


    /**
     * Function that adds quantity to a Product.
     * @receiver Product to add [quantity].
     * @param quantity quantity to add to a [Product].
     * @return Returns the original product with the new quantity.
     */
    fun Product.addQuantity(quantity: Int): Product {
        return this.copy(quantity = this.quantity + quantity)
    }


    /**
     * Function that removes a certain quantity to a Product.
     * @receiver Product to take [quantity].
     * @param quantity quantity to take to a [Product].
     * @return Returns the original product with the new quantity.
     */
    fun Product.takeQuantity(quantity: Int): Product {
        return this.copy(quantity = this.quantity - quantity)
    }

}