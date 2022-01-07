object M {

    fun init() {
        return
    }
    /**
     * Function that saves the products and its properties as well as the number of coins introduced during functioning.
     *
     */
    fun printsSystemOut(array: Array<Products.Product>, coins: Array<CoinDeposit.Coin>, loge: String){
        val data = loge.split(' ')
        FileAccess.writeCoinLog(coins,data.first(),data.last())
        FileAccess.writeProductFile(array)
    }
}