/**
 * Interface that implements the Products.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Products {

    data class Product(val id: Int, val name: String, var quantity: Int, val price: Int)

    var products: Array<Product> = emptyArray()


    /**
     * Function that initializes the class of the File Access.
     * If it was already initialized exists the function.
     */
    fun init() {
        FileAccess.init()
        products = vendingProducts()

    }


    /**
     * Function that...TODO
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
     * Function that...TODO
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
     * Function that...TODO
     */
    fun Product.addQuantity(quantity: Int): Product {
        return this.copy(quantity = this.quantity + quantity)
    }


    /**
     * Function that...TODO
     */
    fun Product.takeQuantity(quantity: Int): Product {
        return this.copy(quantity = this.quantity - quantity)
    }

}