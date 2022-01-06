

class Products {
    data class Product(val id: Int, val name: String, var quantity: Int, val price: Int)

    val vending = arrayOf(
        Product(1, "Kit-Kat", 10, 4),
        Product(2, "Lion", 8, 3),
        Product(3, "M&M", 12, 4),
        Product(4, "Skittles", 5, 4),
        Product(5, "Oreo", 5, 5),
        Product(6, "Chips-Ahoy", 12, 5),
        Product(7, "Gomas", 4, 3),
        Product(8, "Sandes de queijo", 3, 2),
        Product(9, "Sandes de fiambre", 4, 2),
        Product(10, "Sandes mista", 3, 3),
        Product(11, "Fantastic", 7, 5),
        Product(12, "Coca-Cola", 9, 5),
        Product(13, "Pepsi", 14, 5),
        Product(14, "Agua", 6, 1),
        Product(15, "Agua", 13, 1),
        Product(16, "Compal", 7, 2)
    )

}