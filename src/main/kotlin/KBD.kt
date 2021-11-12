import isel.leic.utils.Time


object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.

    private const val READ_MASK = 0x0F
    private const val DVAL_MASK = 0x10
    private const val ACK = 0x01
    private const val NONE = 0;

    private val keys = charArrayOf('1', '4', '7', '*', '2', '5', '8', '0', '3', '6','9','#')

    // Inicia a classe
    fun init() {
        HAL.clrBits(ACK)
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {

        if (HAL.isBit(DVAL_MASK)) {

            val keyToCheck = HAL.readBits(READ_MASK)
            HAL.setBits(ACK)

            while(HAL.isBit(DVAL_MASK));
            HAL.clrBits(ACK)

            return keys[keyToCheck]
        }
        else return NONE.toChar()

    }

    // Retorna quando a tecla for premida ou NONE após decorrido ‘timeout’ milisegundos.
    fun waitKey(timeout: Long): Char {
        //time reference
        val timeSince1970 = Time.getTimeInMillis()   // tempo desde 1970 em milisegumdos
        //timeout value
        val time = timeout + timeSince1970

        while (time > Time.getTimeInMillis()) {
            val key = getKey()
            if(key != NONE.toChar())
                return key
        }
        //timeout
        return NONE.toChar()
    }

}

fun main() {
    //criar uma main de testes para a class
    HAL.init()
    KBD.init()
    while (true) {
        println(KBD.getKey())
        Time.sleep(1000)
        println(KBD.waitKey(100))
    }
}