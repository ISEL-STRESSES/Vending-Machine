enum class Mode {ARROWS, INDEX}

/**
 * Runs the Vending machine and its modes.
 */
fun main() {
    //initializes all the app lower blocks
    Vending.blocksInit()
    Vending.printInitialMenu()
    Vending.mode = Vending.Operation.VENDING
    var mode = Mode.INDEX

    while (true) {
        if (M.setMaintenance()) {
            Vending.mode = Vending.Operation.MAINTENANCE
            Maintenance.runMaintenance(mode)
        }
        else Vending.run(mode)

    }
}
