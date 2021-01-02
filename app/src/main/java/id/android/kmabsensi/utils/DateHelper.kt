package id.android.kmabsensi.utils

import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.*

object DateHelper {

    fun getTodayDate(): Date = DateTime.now().toDate()
    fun getCurrentYear(): Int = DateTime.now().year
    fun getCurrentMonth(): Int = DateTime.now().monthOfYear

    fun getFirstDateOfMonth(month: Int, year: Int) : Date {
        val lDate = LocalDate(year, month, 1)
        return lDate.dayOfMonth().withMinimumValue().toDate()
    }

    fun getLastDateOfMonth(month: Int, year: Int) : Date {
        val lDate = LocalDate(year, month, 1)
        return lDate.dayOfMonth().withMaximumValue().toDate()
    }

    fun getYesterdayDate(): Date {
        return DateTime.now().minusDays(1).toDate()
    }

    fun get7DaysAgoDate(): Date {
        return DateTime.now().minusDays(6).toDate()
    }

    fun getLastMonth() = DateTime.now().minusMonths(1).monthOfYear

}