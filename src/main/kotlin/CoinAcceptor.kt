import isel.leic.utils.Time

/**
 * Interface used for communication with the Coin acceptor hardware component.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object CoinAcceptor {
    //Variable initialization
    private const val COIN_READ_MASK = 0x20         // Mask to read if a coin was introduced.
    private const val COIN_ACCEPT_MASK = 0x10       // Mask to write the Accept Signal to the Coin Acceptor hardware component.
    private const val COIN_COLLECT_MASK = 0x20      // Mask to write the Collect Signal to the Coin Acceptor hardware component.
    private const val COIN_EJECT_MASK = 0x40        // Mask to write the Eject Signal to the Coin Acceptor hardware component.
    private const val WAIT_TIME = 1000L             // Time to maintain Eject and Collect signal commands.
    private var COIN_ACCEPTOR_STATE = false         // Current State of Coin Acceptor class (if it was already initialized).

    //Extra------------------
    private var COINS_ACCEPTED = 0
    private const val COINS_DEPOSIT_MAX_CAPACITY = 10

    /**
     * Function that initializes the class of the Coin Acceptor.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (COIN_ACCEPTOR_STATE) return
        HAL.init()
        //preciso de limpar o porto de saida por causa dos sinais accept collect eject?
        //no init do HAL eles sao todos limpos! é suficiente?
        COIN_ACCEPTOR_STATE = true
    }

    /**
     * Function that checks if it was introduced a new coin.
     * @return returns a boolean that states if it was introduced a new coin.
     */
    fun hasCoin(): Boolean {
        return HAL.isBit(COIN_READ_MASK)
    }


    /**
     * Function that informs the Coin Acceptor that the coin was accounted for.
     */
    fun acceptCoin() {
        if (depositFull()) return

        if (hasCoin()) {
            HAL.setBits(COIN_ACCEPT_MASK)
            COINS_ACCEPTED++
        } else HAL.clrBits(COIN_ACCEPT_MASK)
        HAL.clrBits(COIN_ACCEPT_MASK)

    }


    /**
     * Function that sends a command to the Coin Acceptor to release all coins.
     */
    fun ejectCoins() {
        HAL.setBits(COIN_EJECT_MASK)
        //time needed to maintain the Eject signal.
        Time.sleep(WAIT_TIME)
        HAL.clrBits(COIN_EJECT_MASK)
    }


    /**
     * Function that sends a command to the coin Acceptor to store all coins.
     */
    fun collectCoins() {
        if(depositFull()) return
        HAL.setBits(COIN_COLLECT_MASK)
        //time needed to maintain the collect signal.
        Time.sleep(WAIT_TIME)
        HAL.clrBits(COIN_COLLECT_MASK)
    }

    //Extra(might need some care)-----------------
    fun depositFull() :Boolean {
        return COINS_ACCEPTED >= COINS_DEPOSIT_MAX_CAPACITY
    }

    fun emptyDepositRequest() :String? {
        return if(depositFull()) "MAINTENANCE REQUESTED" else null
    }

}

fun main() {
    HAL.init()
    CoinAcceptor.init()
    CoinAcceptor.ejectCoins()
    var coins = 0
    while (!CoinAcceptor.depositFull()) {
        if (CoinAcceptor.hasCoin()) {
            CoinAcceptor.acceptCoin()
            coins++
        }
        if (coins % 5 == 0)
            CoinAcceptor.collectCoins()
    }
    println(CoinAcceptor.emptyDepositRequest())

}