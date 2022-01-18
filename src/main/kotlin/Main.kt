enum class Mode { ARROWS, INDEX }

enum class Operation { MAINTENANCE, VENDING, REQUESTS }

/**
 * Runs the Vending machine and its modes.
 */
fun main() {
    //initializes all the app lower blocks
    App.allBlocksInit()
    Vending.printInitialMenu()
    var mode = Operation.VENDING
    val mode2 = Mode.INDEX

    while (true) {
        if (M.setMaintenance()) {
            mode = Operation.MAINTENANCE
            Maintenance.runMaintenance(mode2)
        } else {
            val request = Vending.run(mode2)
            if (request != null) {
                mode = Operation.REQUESTS

                //TODO("NOT YET IMPLEMENTED")
            }
        }

    }
}
