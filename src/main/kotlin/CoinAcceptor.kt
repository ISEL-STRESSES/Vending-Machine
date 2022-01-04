import isel.leic.utils.Time

/**
 * Interface used for communication with the Coin acceptor.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object CoinAcceptor {
    private var COIN_ACCEPTOR_STATE = false
    private const val COIN_READ_MASK = 0x20         // Mask to read if a coin was introduced.
    private const val COIN_ACCEPT_MASK = 0x10       // Mask to write the Accept Signal to the Coin Acceptor hardware component.
    private const val COIN_COLLECT_MASK = 0x20      // Mask to write the Collect Signal to the Coin Acceptor hardware component.
    private const val COIN_EJECT_MASK = 0x40        // Mask to write the Eject Signal to the Coin Acceptor hardware component.

    /**
     * Function that initializes the class of the Coin Acceptor.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (COIN_ACCEPTOR_STATE) return
        HAL.init()
        COIN_ACCEPTOR_STATE = true
    }

    /**
     * Function that checks if it was introduced a new coin.
     * @return returns a boolean that states if it was introduced a new coin.
     */
    private fun hasCoin(): Boolean {
        return HAL.isBit(COIN_READ_MASK)
    }


    /**
     * Function that informs the Coin Acceptor that the coin was aconted for.
     */
    private fun acceptCoin() {
        if (hasCoin()) HAL.setBits(COIN_ACCEPT_MASK)
        else HAL.clrBits(COIN_ACCEPT_MASK)
        HAL.clrBits(COIN_ACCEPT_MASK)
    }


    /**
     * Function that sends a command to the Coin Acceptor to release all coins.
     */
    private fun ejectCoins() {
        HAL.setBits(COIN_EJECT_MASK)
        //time needed to maintain the Eject signal.
        Time.sleep(1000)
        HAL.clrBits(COIN_EJECT_MASK)
    }


    /**
     * Function that sends a command to the coin Acceptor to store all coins.
     */
    private fun collectCoins() {
        HAL.setBits(COIN_COLLECT_MASK)
        //time needed to maintain the collect signal.
        Time.sleep(1000)
        HAL.clrBits(COIN_COLLECT_MASK)
    }

}

fun main() {

}