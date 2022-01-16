

object Products {

    data class Product(val id: Int, val name: String, var quantity: Int, val price: Int)

    var products :Array<Product> = emptyArray()

    fun init() {
        FileAccess.init()
        products = vendingProducts()

    }

    private fun vendingProducts() :Array<Product> {
        val file = FileAccess.readProductFile()
        val products = Array(file.size){
            val product = file[it].split(';')
            Product(product[0].toInt(),product[1],product[2].toInt(),product[3].toInt())
        }
        return products
    }

    fun saveProducts(array: Array<Product>) {
        val newArray = Array(array.size){""}
        for (i in array.indices) {
            val line = "${array[i].id};${array[i].name};${array[i].quantity};${array[i].price}"
            newArray[i] = line
        }

        FileAccess.writeProductFile(newArray)
    }

    fun Product.addQuantity(quantity: Int): Product {
        return this.copy(quantity = this.quantity + quantity)
    }

}