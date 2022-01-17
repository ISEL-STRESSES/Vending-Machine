/**
 * Interface that controls the state of the dispenser mechanism.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object Dispenser {
    private var DISPENSER_SATE = false      //Current State of Dispenser(if it was already initialized).

    /**
     * Initializes the class (in this particular case is not needed).
     */
    fun init() {
        if (DISPENSER_SATE) return
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
    HAL.init()
    SerialEmitter.init()
    Dispenser.init()

    Dispenser.dispense(9)

    Dispenser.dispense(7)
}