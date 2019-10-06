package id.android.kmabsensi.utils

import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT = "yyyy-MM-dd"
const val DATE_FORMAT2 = "dd MMMM yyyy"

fun getTodayDate(): String {
    val cal = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

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
