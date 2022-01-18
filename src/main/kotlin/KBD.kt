//import needed for checking time
import isel.leic.utils.Time

/**
 * Receives a key pressed in the Hardware.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object KBD {

    //Variable initialization
    private const val READ_MASK = 0x0F  //Mask to read a key.
    private const val DVAL_MASK =0x10  //Mask to check if a key is valid.
    private const val ACK = 0x80        //Acknowledge to send if a key is received.
    const val NONE = 0.toChar()         //Value that represents a non-existent key.
    private var KBD_STATE = false       //Current State of KBD(if it was already initialized).

    //keys that we can expect to read from the matrix keyboard (iterated by columns).
    val keys = charArrayOf('1', '4', '7', '*', '2', '5', '8', '0', '3', '6', '9', '#')

    /**
     * Initializes the class and clears the ACK in case it is set to One.
     */
    fun init() {
        if (KBD_STATE) return
        HAL.init()
        HAL.clrBits(ACK)
        KBD_STATE = true
    }

    /**
     * Function that translates the code of a pressed key to char if it is been immediately.
     * pressed or [NONE] if it isn't.
     * @return The translated key or [NONE] if it isn't pressed.
     */
    private fun getKey(): Char {

        return if (HAL.isBit(DVAL_MASK)) {

            val keyToCheck = HAL.readBits(READ_MASK)
            HAL.setBits(ACK)

            while (HAL.isBit(DVAL_MASK));
            HAL.clrBits(ACK)

            if (keyToCheck !in (0..12))
                return NONE

            keys[keyToCheck]

        } else NONE

    }

    /**
     * Function that waits a certain [timeout] for a key.
     * @param timeout Time to wait in milliseconds.
     * @return The key or [NONE] if during [timeout] any was pressed.
     */
    fun waitKey(timeout: Long): Char {
        //time reference
        val timeSince1970 = Time.getTimeInMillis()   //time since January 1st 1970 in milliseconds
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
    HAL.init()
    KBD.init()

    //We press 2 two random keys (2, 4)
    while (true) {
        println(KBD.waitKey(1000))

        println(KBD.waitKey(1000))
    }
}