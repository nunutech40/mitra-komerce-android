package id.android.kmabsensi.utils

import android.app.Activity
import com.tapadoo.alerter.Alerter
import id.android.kmabsensi.R
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


const val DATE_FORMAT = "yyyy-MM-dd"
const val DATE_FORMAT2 = "dd MMMM yyyy"
const val DATE_FORMAT3 = "EEEE, dd MMM yyyy"

fun getRoleName(roleId: Int): String = when(roleId) {
    1 -> ROLE_ADMIN
    2 -> ROLE_MANAGEMEMENT
    else -> ROLE_SDM
}

fun getTodayDate(): String {
    val cal = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    return simpleDateFormat.format(cal.time)
}

fun getTodayDateTimeDay(): String {
    val cal = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT3, Locale.getDefault())

    return simpleDateFormat.format(cal.time)
}


fun getDateString(date: Date): String{
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    return simpleDateFormat.format(date)
}

fun getDateStringFormatted(date: Date): String{
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT2, Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun createAlertError(activity: Activity, title: String, message: String, duration: Long = 5000){
    Alerter.create(activity)
        .setTitle(title)
        .setText(message)
        .setDuration(duration)
        .setBackgroundColorRes(R.color.colorAccent)
        .show()
}

fun createAlertSuccess(activity: Activity?, message: String){
    Alerter.create(activity)
        .setTitle("Berhasil")
        .setText(message)
        .setIcon(R.drawable.ic_done)
        .setBackgroundColorRes(R.color.colorSuccess)
        .show()

}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }
