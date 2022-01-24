/**
 * Interface that controls the state of the dispenser mechanism.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object Dispenser {
    //Variable initialization.
    private var DISPENSER_SATE = false      //Current State of Dispenser(if it was already initialized).

    /**
     * Initializes the class (in this particular case is not needed).
     */
    fun init() {
        if (DISPENSER_SATE) return
        SerialEmitter.init()
        DISPENSER_SATE = true
    }

    /**
     * Sends a command to dispense a product.
     * @param productId Product id to dispense.
     */
    fun dispense(productId: Int) {
        SerialEmitter.send(SerialEmitter.Destination.DISPENSER, productId)
    }

}

/**
 * Main function for testing the Dispenser interface.
 */
fun main() {
    SerialEmitter.init()
    Dispenser.init()
    val firstProductId = 9
    val secondProductId = 7

    Dispenser.dispense(firstProductId)

    Dispenser.dispense(secondProductId)
}
