import isel.leic.utils.Time
import java.text.SimpleDateFormat


enum class OPERATING_MODE { MAINTENANCE, DISPENSE }

val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")

val coin = 30
fun Products.Product.addQuantity(quantity: Int): Products.Product {
    return this.copy(quantity = quantity)
}

fun printMaintenance() {
    TUI.printText("Maintenance Mode", TUI.Position.RIGHT, 0)
    //   TUI.printText()
}

fun printTime(status : OPERATING_MODE) {
    if (status != OPERATING_MODE.DISPENSE) return
    TUI.printText("Vending Machine ", line = 0)
    var getTime = Time.getTimeInMillis()
    var currentTime = formatter.format(getTime)
    TUI.printText(currentTime, line = 1)
    while (status == OPERATING_MODE.DISPENSE) {
        getTime = Time.getTimeInMillis()
        currentTime = formatter.format(getTime)
        if (getTime % 60000 == 0L)
            TUI.printText(currentTime, line = 1)
    }

}



fun main() {
    HAL.init()
    SerialEmitter.init()
    LCD.init()
    KBD.init()
    TUI.init()
    M.init()
    CoinAcceptor.init()
    Dispenser.init()
    Products.init()
    CoinDeposit.init()
    FileAccess.init()
    var mode = OPERATING_MODE.DISPENSE
    //printTime(mode)
    M.printsSystemOut(Products.products,CoinDeposit.COINS_LOG,coin,formatter.format(Time.getTimeInMillis()))
}