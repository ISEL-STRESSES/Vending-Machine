// Imports that allow to read and write files.
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

/**
 * Class that allows to read files that have information of the Products and Coins
 * of the Vending Machine.
 * @author Carlos Pereira, Pedro Oliveira, Filipa Machado.
 */
object FileAccess {
    //Variable Initialization.
    private const val COIN_LOG = "CoinDeposit.txt"  // Name of the Coin log file.
    private const val PRODUCTS_LOG = "Products.txt" // Name of the file that has every product of the Vending Machine.
    private var FILE_ACCESS_STATE = false           // Current State of File Access (if it was already initialized).

    /**
     * Function that initializes the class of the File Access.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (FILE_ACCESS_STATE) return
        FILE_ACCESS_STATE = true
    }

    /**
     * Function that reads the [PRODUCTS_LOG] file.
     * @return Array of strings that represent each line on the file.
     */
    fun readProductFile(): Array<String> {
        return readFile(PRODUCTS_LOG)
    }

    /**
     * Function that reads the [COIN_LOG] file.
     * @return Array of strings that represent each line on the file.
     */
    fun readCoinFile(): Array<String> {
        return readFile(COIN_LOG)
    }

    /**
     * Function that writes the Product log.
     * @param array Array of Strings that have the last information on the Products.
     */
    fun writeProductFile(array: Array<String>) {
        writeFile(array, PRODUCTS_LOG)
    }

    /**
     * Function that writes the Coin log.
     * @param array Array of Strings that have the last information on the Coins.
     */
    fun writeCoinFile(array: Array<String>) {
        writeFile(array, COIN_LOG)
    }

    /**
     * Function that writes in an output file.
     * @param array Array to write in output file.
     * @param fileName Name of the file to write.
     */
    private fun writeFile(array: Array<String>, fileName: String) {
        val writer = createWriter(fileName)
        array.forEach {
            writer.println(it)
        }
        writer.close()
    }

    /**
     * Function that reads all the lines of a given file.
     * @param fileName Name of the file to read.
     * @return Returns an Array of Strings that represent each line.
     */
    private fun readFile(fileName: String): Array<String> {
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

/**
 * Main function for testing the class.
 */
fun main() {
    FileAccess.init()
    val productFile = FileAccess.readProductFile()
    productFile.forEach(::println)
    FileAccess.writeProductFile(productFile)

    val coinFile = FileAccess.readCoinFile()
    coinFile.forEach(::println)
    FileAccess.writeCoinFile(coinFile)
}
