enum class OPERATING_MODE {MAINTENANCE, DISPENSE}

fun Products.Product.addQuantity(quantity: Int): Products.Product {
    return this.copy(quantity = quantity)
}

fun printMaintenance() {
    TUI.printText("Maintenance Mode", TUI.Position.RIGHT,0)
    TUI.printText()
}

fun printStatus(){
    TUI.printText("Vending Machine", TUI.Position.RIGHT,0)
}