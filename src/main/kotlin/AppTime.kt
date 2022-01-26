// Imports needed to access the Time lib and for formatting time
import AppTime.secsToTime
import isel.leic.utils.Time
import java.text.SimpleDateFormat

/**
 * Interface that implements a Clock to our Vending Machine.
 * @author Carlos Pereira, Pedro Poeira, Filipa Machado.
 */
object AppTime {
    //Variable Initialization.
    private const val TIME_FORMAT = "dd-MM-yyyy HH:mm"      // Time format.
    private const val MINUTE_CORRECTION = 60000             // Correction needed for getting the current time by the minute.
    const val SECS_IN_A_MINUTE = 60000L                     // Seconds in a minute.
    private const val HOUR_SIZE = 6                         // Length on the Time Format reserved for the hour.
    private const val DATE_SIZE = 11                        // Length on the Time Format reserved for the date.
    var LAST_TIME = 0L                                      // Last Time since january 1st 1970 in milliseconds.
    private val dateFormat = SimpleDateFormat(TIME_FORMAT)  // Date format.
    private var TIME_STATE = false                          // Current State of Time(if it was already initialized).

    /**
     * Function that initializes the class Time.
     * If it was already initialized exists the function.
     */
    fun init() {
        if (TIME_STATE) return
        TIME_STATE = true
    }

    /**
     * Function that gets the time by the minute.
     * @return Returns the milliseconds since january 1st 1970 by the minute.
     */
    fun getCurrentTime(): Long {
        val getTime = Time.getTimeInMillis()
        return getTime - (getTime % MINUTE_CORRECTION)
    }

    /**
     * Function that converts the seconds to the set [TIME_FORMAT].
     * @receiver Seconds in Long.
     * @return Returns a String formatted.
     */
    fun Long.secsToTime(): String = dateFormat.format(this)

    /**
     * Function that give only the current date of the Vending Machine.
     * @return Returns the current date of the Vending Machine.
     */
    fun getDate(): String {
        val time = getCurrentTime().secsToTime()
        return time.dropLast(HOUR_SIZE)
    }

    /**
     * Function that give only the current time of the Vending Machine.
     * @return Returns the current time of the Vending Machine.
     */
    fun getTime(): String {
        val time = getCurrentTime().secsToTime()
        return time.drop(DATE_SIZE)
    }
}

/**
 * Main function for testing the class.
 */
fun main() {
    AppTime.init()
    println(AppTime.getDate())
    println(AppTime.getTime())
    val secsInAMinute = 60000L
    while (true) {
        val currentTime = AppTime.getCurrentTime()
        if (currentTime - AppTime.LAST_TIME >= secsInAMinute) {
            AppTime.LAST_TIME = currentTime
            println(AppTime.LAST_TIME.secsToTime())
        }
    }
}
