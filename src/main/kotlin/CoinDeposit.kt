object CoinDeposit {

    data class Coin(var count: Int, val date:String,val time:String)

    var coinsStored = vendingCoins()


    private const val COINS_DEPOSIT_MAX_CAPACITY = 10

    fun init() {
        FileAccess.init()
    }

    fun vendingCoins() :Array<Coin>{
        val file = FileAccess.readCoinFile()
        val coins = Array(file.size){
            val coins = file[it].split(';')
            Coin(coins[0].toInt(),coins[1],coins[2])
        }
        return coins
    }
    fun depositFull() :Boolean {
        return CoinAcceptor.COINS_ACCEPTED >= COINS_DEPOSIT_MAX_CAPACITY
    }

    fun emptyDepositRequest() :String? {
        return if(depositFull()) "MAINTENANCE REQUESTED" else null
    }
}