//Import needed for adding some delay to the test function.
import isel.leic.utils.Time

/**
 * Interface used for implementing the Coin Deposit.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object CoinDeposit {
    // Variable Initialization.
    var COINS = 0                                           // Number of coins in the Vending Machine.
    var COINS_LOG = emptyArray<Coin>()                      // Log of the Coin Deposit.
    private const val FILE_DELIMITER = ';'                  // Char delimiter in a String of the File.
    private const val EMPTY_STRING = ""                     // Empty string for initializing the File to save the coins.
    private const val QUANTITY_INDEX = 0                    // Index of the ID field in the file String.
    private const val DATE_INDEX = 1                        // Index of the name field in the file String.
    private const val TIME_INDEX = 2                        // Index of the quantity field in the file String.
    private const val CURRENT_LOG = 1                       // Size adder for accounting for the new log of Coins.
    private const val MINIMUM_SIZE = 1                      // Minimum size that the File needs to have for storing previous logs.
    private const val INCREMENT = 10                        // Increment for each sale
    private var DEPOSIT_MAX_CAPACITY = INCREMENT // Max capacity of the Coin Deposit.
    private const val DEPOSIT_REQUEST = "CoinDeposit Full"  // Deposit Full Request.
    private var COIN_DEPOSIT_STATE = false                  // Current State of Coin Deposit(if it was already initialized).

    /**
     * Class that represents a [Coin].
     * @property quantity current number of coins in the Vending Machine.
     * @property date date to write in the Coin Deposit file for Log.
     * @property time time to write in the Coin Deposit file for Log.
     */
    data class Coin(var quantity: Int, val date: String, val time: String)

    /**
     * Function that initializes the class Coin Deposit.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (COIN_DEPOSIT_STATE) return
        FileAccess.init()
        COINS_LOG = vendingCoins()
        COINS = vendingCoins().last().quantity
        DEPOSIT_MAX_CAPACITY = COINS + INCREMENT
        COIN_DEPOSIT_STATE = true

    }

    /**
     * Function that interprets an array of Strings from the [FileAccess] and
     * transforms it to into an array of [Coin].
     * @return Array of coins after reading the Coin Deposit file.
     */
    private fun vendingCoins(): Array<Coin> {
        val file = FileAccess.readCoinFile()
        val coins = Array(file.size) {
            val coins = file[it].split(FILE_DELIMITER)
            Coin(coins[QUANTITY_INDEX].toInt(), coins[DATE_INDEX], coins[TIME_INDEX])
        }
        return coins
    }

    /**
     * Function that saves the array of [Coin] into a file for log.
     * @param array Array of Coins to Store information.
     */
    fun saveCoins(array: Array<Coin>) {
        val newArray = Array(array.size + CURRENT_LOG) { EMPTY_STRING }
        if (array.size > MINIMUM_SIZE)
            for (i in array.indices) {
                val coin = "${array[i].quantity}$FILE_DELIMITER${array[i].date}$FILE_DELIMITER${array[i].time}"
                newArray[i] = coin
            }

        val currentLog = "${COINS}$FILE_DELIMITER${AppTime.getDate()}$FILE_DELIMITER${AppTime.getTime()}"
        newArray[newArray.lastIndex] = currentLog
        FileAccess.writeCoinFile(newArray)
    }

    /**
     * Function that checks if the Coin Deposit is full.
     * @return A boolean that represents if the [CoinDeposit] is full, false if it isn't true if it is.
     */
    private fun depositFull(): Boolean {
        return COINS >= DEPOSIT_MAX_CAPACITY
    }

    /**
     * Function that sends a request for maintenance because the [CoinDeposit] is full.
     * @return Returns a String in that has request.
     */
    fun depositRequest(): String? {
        return if (depositFull()) DEPOSIT_REQUEST else null
    }
}

/**
 * Main function for testing the class.
 */
fun main() {
    CoinDeposit.init()
    CoinDeposit.COINS_LOG.forEach(::println)
    val waitTime = 1000L
    var request: String?
    while (CoinDeposit.depositRequest().also { request = it } == null) {
        CoinDeposit.COINS++
        println(CoinDeposit.COINS)
        Time.sleep(waitTime)
    }

    println(request)
    CoinDeposit.saveCoins(CoinDeposit.COINS_LOG)
}
