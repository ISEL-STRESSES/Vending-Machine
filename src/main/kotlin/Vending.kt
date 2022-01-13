
import Time.secsToTime

object Vending {
    enum class Operation { MAINTENANCE, VENDING }

    var mode: Operation? = null
    private const val TIME_OUT = 5000L

    const val coin = 30


    fun blocksInit() {
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
    }


    fun printInitialMenu() {
        val currentTime = Time.getCurrentTime()
        if (Time.getCurrentTime() - Time.LAST_TIME >= 60000L) {

            Time.LAST_TIME = currentTime
            TUI.printText("Vending Machine ", line = 0)
            printTime(Time.LAST_TIME)
        }

    }

    fun printTime(currentTime: Long) {
        TUI.printText(currentTime.secsToTime(), line = 1)
    }

    fun run(mode: Mode) {
        if (Time.LAST_TIME != Time.getCurrentTime()) {
            printInitialMenu()
        }

        if (TUI.getKBDKey() == '#') {
            TUI.printProduct(Products.products.first())
            while (true) {
                var mode = mode
                var index = 0
                val key = TUI.getKBDKey()

                if (key =='*')
                    mode = if (mode == Mode.ARROWS) Mode.INDEX else Mode.ARROWS
                else
                    if (mode == Mode.INDEX)
                        if (key in  '0'..'9') TUI.printProduct(Products.products[key-'0'-1])
                    else {
                            val browse = TUI.browseProducts(Products.products,index)
                            index = browse.id+1
                            TUI.printProduct(browse)
                            println(browse)
                        }

            }

        }
        if (M.setMaintenance())
            return
    }

}

fun main() {
    Vending.blocksInit()
    while (true) {
        Vending.printInitialMenu()
    }
}
