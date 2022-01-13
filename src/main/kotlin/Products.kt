

object Products {

    data class Product(val id: Int, val name: String, var quantity: Int, val price: Int)

    var products = vendingProducts()

    fun init() {
        FileAccess.init()

    }

    private fun vendingProducts() :Array<Product> {
        val file = FileAccess.readerProductFile()
        val products = Array(file.size){
            val product = file[it].split(';')
            Product(product[0].toInt(),product[1],product[2].toInt(),product[3].toInt())
        }
        return products
    }

    fun saveProducts(array: Array<Product>) {
        FileAccess.writeProductFile(array)
    }

    fun Product.addQuantity(quantity: Int): Products.Product {
        return this.copy(quantity = this.quantity + quantity)
    }

}