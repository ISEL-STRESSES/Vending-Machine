import isel.leic.utils.Time


object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.

    private const val READ_MASK = 0x0F
    private const val DVAL_MASK = 0x10
    private const val ACK_MASK = 0x01
    private const val ACK = 0x01
    private const val NONE = 0;

    private val keys = charArrayOf('0', '4', '7', '*', '2', '5', '8', '0', '3', '6','9','#')

    // Inicia a classe
    fun init() {

        HAL.writeBits(ACK_MASK,ACK)
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {

        if (HAL.isBit(DVAL_MASK)) {

            val keyToCheck = HAL.readBits(READ_MASK)
            HAL.writeBits(ACK_MASK,ACK)
            return keys[keyToCheck]

        } else {
            return NONE.toChar()
        }
    }

    // Retorna quando a tecla for premida ou NONE após decorrido ‘timeout’ milisegundos.
    fun waitKey(timeout: Long): Char {

        val timeSince1970 = Time.getTimeInMillis()
        val time = timeout + timeSince1970

        while (time > Time.getTimeInMillis()) {
            val key = getKey()
            if(key != NONE.toChar())
                return key
        }
        return NONE.toChar()
    }

}

fun main() {
    //criar uma main de testes para a class


}