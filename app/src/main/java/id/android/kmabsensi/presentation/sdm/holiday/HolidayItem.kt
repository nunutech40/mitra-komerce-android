package id.android.kmabsensi.presentation.sdm.holiday

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Holiday
import id.android.kmabsensi.utils.localDateFormatter
import kotlinx.android.synthetic.main.item_row_hari_libur.view.*
import org.joda.time.LocalDate

class HolidayItem(val holiday: Holiday): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtHolidayName.text = holiday.eventName
            val dateStart: LocalDate = LocalDate.parse(holiday.startDate)
            val dateEnd: LocalDate = LocalDate.parse(holiday.endDate)

            itemView.txtHolidayDate.text = if (holiday.startDate == holiday.endDate)
                localDateFormatter(dateStart)
            else
                "${localDateFormatter(dateStart, "dd MMM yyyy")} s.d ${localDateFormatter(dateEnd, "dd MMM yyyy")}"

        }
    }

    override fun getLayout() = R.layout.item_row_hari_libur
}