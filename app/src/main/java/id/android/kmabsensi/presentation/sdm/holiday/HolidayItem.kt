package id.android.kmabsensi.presentation.sdm.holiday

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Holiday
import kotlinx.android.synthetic.main.item_row_hari_libur.view.*

class HolidayItem(val holiday: Holiday): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtHolidayName.text = holiday.eventName
            itemView.txtHolidayDate.text = if (holiday.startDate == holiday.endDate)
                holiday.startDate
            else
                "${holiday.startDate} s.d ${holiday.endDate}"
        }
    }

    override fun getLayout() = R.layout.item_row_hari_libur
}