
/**
 * Interface used for communication with the Coin acceptor.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object CoinAcceptor { // Implementa a interface com o moedeiro.
    private var COIN_ACCEPTOR_STATE = false

    // Inicia a classe
    fun init() {
        if (COIN_ACCEPTOR_STATE) return
        //...
        COIN_ACCEPTOR_STATE = true
    }

    // Retorna true se foi introduzida uma nova moeda.
    fun hasCoin(): Boolean {
        return true
    }

    // Informa o moedeiro que a moeda foi contabilizada.
    fun acceptCoin() {

    }

    // Devolve as moedas que estão no moedeiro.
    fun ejectCoins() {

    }

    // Recolhe as moedas que estão no moedeiro.
    fun collectCoins() {

    }

}