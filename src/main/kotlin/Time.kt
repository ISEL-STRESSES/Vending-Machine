import isel.leic.utils.Time
import java.text.SimpleDateFormat

object Time {

    private const val TIME_FORMAT = "dd-MM-yyyy HH:mm"
    var LAST_TIME = 0L
    private val dateFormat = SimpleDateFormat(TIME_FORMAT)

    fun getCurrentTime(): Long {
        val getTime = Time.getTimeInMillis()
        return getTime - (getTime % 60000)
    }

    fun Long.secsToTime(): String = dateFormat.format(this)
}