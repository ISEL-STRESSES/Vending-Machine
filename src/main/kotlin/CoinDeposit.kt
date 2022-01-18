
/**
 * Interface used for implementing the Coin Deposit.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object CoinDeposit {
    //Variable Initialization.
    var COINS: Int = 0                                  //Number of coins in the Vending Machine.
    var COINS_LOG :Array<Coin> = emptyArray()           //Log of the Coin Deposit.
    private const val COINS_DEPOSIT_MAX_CAPACITY = 10   //Max capacity of the Coin Deposit.
    private var COIN_DEPOSIT_STATE = false              //Current State of Coin Deposit(if it was already initialized).

    /**
     * Class that represents a [Coin].
     * @property count current number of coins in the Vending Machine.
     * @property date date to write in the Coin Deposit file for Log.
     * @property time time to write in the Coin Deposit file for Log.
     */
    data class Coin(var count: Int, val date: String, val time: String)

    /**
     * Function that initializes the class Coin Deposit.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (COIN_DEPOSIT_STATE) return
        FileAccess.init()
        COINS = vendingCoins().last().count
        COIN_DEPOSIT_STATE = true

    }

    /**
     * Function that interprets an array of Strings form the [FileAccess] and
     * transforms it to into an array of [Coin].
     * @return Array of coins after reading the Coin Deposit file.
     */
    private fun vendingCoins(): Array<Coin> {
        val file = FileAccess.readCoinFile()
        val coins = Array(file.size) {
            val coins = file[it].split(';')
            Coin(coins[0].toInt(), coins[1], coins[2])
        }
        return coins
    }


    /**
     * Function that saves the array of [Coin] into a file for log.
     * @param array Array of Coins to Store information.
     */
    fun saveCoins(array: Array<Coin>) {
        val newArray = Array(array.size + 1) { "" }
        if (array.size > 1)
            for (i in array.indices) {
                val coin = "${array[i].count};${array[i].date}${array[i].time}"
                newArray[i] = coin
            }

        val currentLog = "${COINS};${Time.getDate()};${Time.getTime()}"
        newArray[newArray.lastIndex] = currentLog
        FileAccess.writeCoinFile(newArray)
    }


    /**
     * Function that checks if the Coin Deposit is full.
     * @return A boolean that represents if the [CoinDeposit] is full, false if it isn't true if it is.
     */
    private fun depositFull(): Boolean {
        return COINS >= COINS_DEPOSIT_MAX_CAPACITY
    }


    /**
     * Function that sends a request for maintenance because the [CoinDeposit] is full.
     * @return Returns a String in that has request.
     */
    fun emptyDepositRequest(): String? {
        return if (depositFull()) "CoinDeposit Full" else null
    }
}