import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

/**
 * Class that allows to read files that have information of the Products and Coins of the Vending Machine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object FileAccess {
    //Variable Inicialization.
    private const val COIN_LOG = "CoinDeposit.txt"      //Name of the Coin log file.
    private const val PRODUCTS_LOG = "Products.txt"     //Name of the file that has every product of the Vending Machine.

    /**
     * Function that initializes the class of the File Access.
     * If it was already initialized exists the function.
     */
    fun init() {

    }


    /**
     * Function that TODO
     * @return Array ...
     */
    fun readProductFile(): Array<String> {
        return readFile(PRODUCTS_LOG)
    }


    /**
     * Function that TODO
     * @return Array ...
     */
    fun readCoinFile(): Array<String> {
        return readFile(COIN_LOG)

    }


    /**
     * Function that TODO
     * @param array
     */
    fun writeProductFile(array: Array<Products.Product>) {
        val writer = createWriter(PRODUCTS_LOG)
        array.forEach {
            val line = "${it.id};${it.name};${it.quantity};${it.price}"
            writer.println(line)
            println(line)
        }
        writer.close()
    }


    /**
     * Function that writes the Coin log. TODO
     * @param array
     * @param date
     * @param time
     * @param coin
     */
    fun writeCoinLog(array: Array<CoinDeposit.Coin>, date: String, time: String, coin: Int) {
        val writer = createWriter(COIN_LOG)
        if (array.size > 1)
            array.forEach {
                val coins = "${it.count};${it.date};${it.time}"
                writer.println(coins)
                println(coins)
            }

        val currentVendingCoins = "${coin};$date;$time"
        writer.println(currentVendingCoins)
        println(currentVendingCoins)

        writer.close()
    }

    /**
     * Function that reads all the lines of a given file.
     * @param fileName Name of the file to read.
     * @return Returns an Array of Strings that represent each line.
     */
    private fun readFile(fileName: String) : Array<String>{
        val reader = createReader(fileName)
        var line: String?
        var lines = emptyArray<String>()
        while (reader.readLine().also { line = it } != null) {
            lines += line ?: break
        }
        return lines
    }


    /**
     * Function that creates a [BufferedReader] to read a file.
     * @param fileName Name of the file to read.
     * @return Returns the [BufferedReader] of [fileName].
     */
    private fun createReader(fileName: String): BufferedReader {
        return BufferedReader(FileReader(fileName))
    }


    /**
     * Function that creates a [PrintWriter] to write a file.
     * @param fileName Name to create a file.
     * @return Returns the [PrintWriter] of [fileName].
     */
    private fun createWriter(fileName: String): PrintWriter {
        return PrintWriter(fileName)
    }

}