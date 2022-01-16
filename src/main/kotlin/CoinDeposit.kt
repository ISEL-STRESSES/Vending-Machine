object CoinDeposit {

    data class Coin(var count: Int, val date:String,val time:String)
    var COINS :Int = 0
    var COINS_LOG = vendingCoins()


    private const val COINS_DEPOSIT_MAX_CAPACITY = 10

    fun init() {
        FileAccess.init()
        COINS = vendingCoins().last().count

    }

    private fun vendingCoins() :Array<Coin>{
        val file = FileAccess.readCoinFile()
        val coins = Array(file.size){
            val coins = file[it].split(';')
            Coin(coins[0].toInt(),coins[1],coins[2])
        }
        return coins
    }

    fun saveCoins(array: Array<Coin>) {
        val newArray = Array(array.size+1){""}
        if (array.size > 1)
            for (i in array.indices){
                val coin = "${array[i].count};${array[i].date}${array[i].time}"
                newArray[i] = coin
            }

        val currentLog = "${COINS};${Time.getDate()};${Time.getTime()}"
        newArray[newArray.lastIndex] = currentLog
        FileAccess.writeCoinLog(newArray)
    }

    private fun depositFull() :Boolean {
        return COINS >= COINS_DEPOSIT_MAX_CAPACITY
    }

    fun emptyDepositRequest() :String? {
        return if(depositFull()) "MAINTENANCE REQUESTED" else null
    }
}