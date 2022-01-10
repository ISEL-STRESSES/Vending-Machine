object M {

    private const val M_MASK = 0x80
    private var M_STATE = false

    fun init() {
        if (M_STATE) return
        HAL.init()
        M_STATE = true
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

    fun setMaintenance(): Boolean{
        return HAL.isBit(M_MASK)
    }

    fun maintenanceOptions() {

    }

}