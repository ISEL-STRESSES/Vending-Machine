
object Dispenser { // Controla o estado do mecanismo de dispensa.
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {

    }
    // Envia comando para dispensar uma unidade de um produto
    fun dispense(productId: Int) {
        SerialEmitter.send(SerialEmitter.Destination.DISPENSER,productId)
    }

}