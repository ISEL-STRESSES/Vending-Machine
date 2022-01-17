/**
 * Class that detects Maintenance.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object M {
    //Variable initialization.
    private const val M_MASK = 0x80     //Mask to read the Maintenance signal.
    private var M_STATE = false         //Current State of M(if it was already initialized).


    /**
     * Function that initializes the class M.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (M_STATE) return
        HAL.init()
        M_STATE = true
    }


    /**
     * Function that checks if the M signal is active.
     * @return Returns True if the signal is active or false is inactive.
     */
    fun setMaintenance(): Boolean {
        return HAL.isBit(M_MASK)
    }

}


/**
 * Main function for testing.
 */
fun main() {
    HAL.init()
    val mask = 0x80
    val signal = HAL.readBits(mask)
    HAL.writeBits(mask, signal)
    println("$signal")
}