import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

object FileAccess {
    private const val COIN_LOG = "CoinDeposit.txt"
    private const val PRODUCTS_LOG = "Products.txt"

    fun init() {

    }

    fun readerProductFile(): Array<String> {
        return readFile(PRODUCTS_LOG)
    }


    fun readCoinFile(): Array<String> {
        return readFile(COIN_LOG)

    }

    fun writeProductFile(array: Array<Products.Product>) {
        val writer = createWriter(PRODUCTS_LOG)
        array.forEach {
            val line = "${it.id};${it.name};${it.quantity};${it.price}"
            writer.println(line)
            println(line)
        }
        writer.close()
    }

    fun writeCoinLog(array: Array<CoinDeposit.Coin>, date: String, time: String, coin: Int) {
        val writer = createWriter(COIN_LOG)
        if (array.size > 1)
            array.forEach {
                val coins = "${it.count};${it.date};${it.time}"
                writer.println(coins)
                println(coins)
            }

        val lastLine = "${coin};$date;$time"
        writer.println(lastLine)
        println(lastLine)

        writer.close()
    }

    private fun readFile(fileName: String) : Array<String>{
        val reader = createReader(fileName)
        var line: String?
        var lines = emptyArray<String>()
        while (reader.readLine().also { line = it } != null) {
            lines += line ?: break
        }
        return lines
    }

    private fun createReader(fileName: String): BufferedReader {
        return BufferedReader(FileReader(fileName))
    }

    private fun createWriter(fileName: String): PrintWriter {
        return PrintWriter(fileName)
    }

}