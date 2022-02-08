//import needed for checking time
import isel.leic.utils.Time

/**
 * Interface that interprets a key pressed in the Hardware (Matrix Keyboard).
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object KBD {

    //Variable initialization
    private const val READ_MASK = 0x0F          // Mask to read a key.
    private const val DVAL_MASK = 0x10          // Mask to check if a key is valid.
    private const val ACK = 0x80                // Acknowledge to send if a key is received.
    private val INVALID_KEY_RANGE = (1..0)      // Initial range for keys, must be overwritten.
    private var KEY_INTERVAL = INVALID_KEY_RANGE// Index interval of a valid key in [keys].
    const val NONE = 0.toChar()                 // Value that represents a non-existent key.
    private var KBD_STATE = false               // Current State of KBD(if it was already initialized).
    // keys that we can expect to read from the matrix keyboard (iterated by columns).
    private val keys = charArrayOf('1', '4', '7', '*', '2', '5', '8', '0', '3', '6', '9', '#', NONE, NONE, NONE, NONE)

    /**
     * Initializes the class and clears the ACK in case it is set to One.
     */
    fun init() {
        if (KBD_STATE) return
        HAL.init()
        HAL.clrBits(ACK)
        KEY_INTERVAL = getValidInterval(keys)
        KBD_STATE = true
    }

    /**
     * Function that finds the first invalid key form a Char Array.
     * @param keys CharArray of keys from a Matrix Keyboard.
     * @return Returns the range of valid keys.
     */
    private fun getValidInterval(keys: CharArray): IntRange {
        var lastIndex = 0
        for (i in keys.indices) {
            if (keys[i] == NONE)
                lastIndex = i - 1
        }
        return (0..lastIndex)
    }

    /**
     * Function that translates the code of a pressed key to char if pressed immediately.
     * pressed or [NONE] if it isn't.
     * @return The translated key or [NONE] if it isn't pressed.
     */
    private fun getKey(): Char {
        //checks for the validation flag form the Hardware.
        if (!HAL.isBit(DVAL_MASK))
            return NONE

        val keyToCheck = HAL.readBits(READ_MASK)
        HAL.setBits(ACK)

        while (HAL.isBit(DVAL_MASK));
        HAL.clrBits(ACK)

        if (keyToCheck !in KEY_INTERVAL)
            return NONE

        return keys[keyToCheck]
    }

    /**
     * Function that waits a certain [timeout] for a key.
     * @param timeout Time to wait in milliseconds.
     * @return The key or [NONE] if during [timeout] any was pressed.
     */
    fun waitKey(timeout: Long): Char {
        //time reference, time since January 1st 1970 in milliseconds
        val timeSince1970 = Time.getTimeInMillis()

        //time that we can wait for receiving a valid key
        val waitTime = timeout + timeSince1970
        while (waitTime > Time.getTimeInMillis()) {
            val key = getKey()
            if (key != NONE)
                return key
        }
        //timeout
        return NONE
    }
}

/**
 * Main function for testing the class.
 */
fun main() {
    KBD.init()

    val timeout = 1000L
    //We press 2 two random keys (2, 4)
    while (true) {
        println(KBD.waitKey(timeout))    // 2 				// TESTED
        println(KBD.waitKey(timeout))    // 4 				// TESTED
        println(KBD.waitKey(timeout))    // NONE            // TESTED
    }
}
