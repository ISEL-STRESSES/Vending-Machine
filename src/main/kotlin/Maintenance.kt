import isel.leic.utils.Time

object Maintenance {
    val option = arrayOf("1-Dispense Test ","2-Update Prod.","3-Remove Prod.","4-Shutdown")
    private const val WAIT_TIME = 500L
    fun init() {
        Vending.blocksInit()
    }

    /**
     * Function that saves the products and its properties as well as the number of coins introduced during functioning.
     *
     */
    fun printsSystemOut(array: Array<Products.Product>, coins: Array<CoinDeposit.Coin>, coin:Int, loge: String){
        val data = loge.split(' ')
        FileAccess.writeCoinLog(coins,data.first(),data.last(),coin)
        FileAccess.writeProductFile(array)
    }

    fun maintenanceOptions() {
        TUI.printText("Maintenance Mode", line = 0)
        while (M.setMaintenance()) {
            togleThowoptions()
            val key = TUI.getInt(0L)
            setOptions()
        }
    }
    fun togleThowoptions(){
        var index = 0
        TUI.printText(option[index++], line = 1)
        Time.sleep(WAIT_TIME)
    }

    fun setOptions(){

    }


    fun printMaintenance() {
        TUI.clearLCD()
        TUI.printText("Maintenance Mode", TUI.Position.RIGHT, 0)
        togleThowoptions()

    }

    fun runMaintenance(mode : Mode) {
        printMaintenance()
    }


}

fun main(){
    Maintenance.maintenanceOptions()
}