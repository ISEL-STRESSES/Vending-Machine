object CoinDeposit {

    data class Coin(var count: Int, val date:String,val time:String)

    var COINS_LOG = vendingCoins()


    private const val COINS_DEPOSIT_MAX_CAPACITY = 10

    fun init() {
        FileAccess.init()
    }

    private fun vendingCoins() :Array<Coin>{
        val file = FileAccess.readCoinFile()
        val coins = Array(file.size){
            val coins = file[it].split(';')
            Coin(coins[0].toInt(),coins[1],coins[2])
        }
        return coins
    }

    private fun depositFull() :Boolean {
        return CoinAcceptor.COINS_ACCEPTED >= COINS_DEPOSIT_MAX_CAPACITY
    }

    fun emptyDepositRequest() :String? {
        return if(depositFull()) "MAINTENANCE REQUESTED" else null
    }
}