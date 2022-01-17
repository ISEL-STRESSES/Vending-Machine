import isel.leic.utils.Time
import kotlin.system.exitProcess


/**
 * TODO
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object Maintenance {
    private val OPTIONS = arrayOf("1-Dispense Test ", "2-Update Prod.", "3-Remove Prod.", "4-Shutdown")
    private const val WAIT_TIME = 500L
    fun init() {
        Vending.blocksInit()
    }

    /**
     * Function that saves the products and its properties as well as the number of coins introduced during functioning.
     * TODO
     */
    private fun printSystemOut(array: Array<Products.Product>, coins: Array<CoinDeposit.Coin>) {
        TUI.printShutdown()
        val key = TUI.getKBDKey(WAIT_TIME)
        if (key == '5') {
            CoinDeposit.saveCoins(coins)
            Products.saveProducts(array)
            exitProcess(0)
        } else return
    }


    /**
     * Function that...TODO
     */
    private fun toggleThroughOptions() {
        var index = 0
        TUI.printText(OPTIONS[index++], line = 1)
        Time.sleep(WAIT_TIME)
    }


    /**
     * Function that...TODO
     */
    private fun printMaintenance() {
        TUI.clearLCD()
        TUI.printText("Maintenance Mode", TUI.Position.RIGHT, 0)
        toggleThroughOptions()

    }


    /**
     * Function that...TODO
     */
    fun runMaintenance(mode: Mode) {
        printMaintenance()
        when (TUI.getKBDKey(WAIT_TIME)) {
            '1' -> {
                dispenseTest(mode)
            }
            '2' -> {
                updateProduct()
            }
            '3' -> {
                removeProduct()
            }
            '4' -> {
                printSystemOut(Products.products, CoinDeposit.COINS_LOG)
            }
        }
    }


    /**
     * Function that...TODO
     */
    private fun updateProduct() {
        TODO("Not yet implemented")
    }


    /**
     * Function that...TODO
     */
    private fun removeProduct() {
        TODO("Not yet implemented")
    }


    /**
     * Function that...TODO
     */
    private fun dispenseTest(mode: Mode) {
        TODO("Not yet implemented")
    }


}
