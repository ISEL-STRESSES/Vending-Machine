object M {
    //duvida como por o modulo m a falar unica e exclusivamente com a app.
    //neste mommento o modulo esta a falar com o File Access saltando todos os outros modulos.

    private const val M_MASK = 0x80
    private var M_STATE = false

    fun init() {
        if (M_STATE) return
        HAL.init()
        M_STATE = true
    }

    fun setMaintenance(): Boolean{
        return HAL.isBit(M_MASK)
    }

    fun maintenanceOptions() {

    }

}