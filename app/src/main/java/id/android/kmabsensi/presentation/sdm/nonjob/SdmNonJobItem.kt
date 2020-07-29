package id.android.kmabsensi.presentation.sdm.nonjob

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.utils.capitalizeWords
import kotlinx.android.synthetic.main.item_row_sdm_non_job.*
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Years

class SdmNonJobItem(
    val user: User,
    val listener: (User) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            txtSdmNameWithPositionName.text =
                "${user.full_name.capitalizeWords()} - ${user.position_name}"
            if (user.last_date_of_pause.split("-")[0] == "0000") {
                txtDuration.text = "-"
            } else {
                val datePaused: LocalDate = LocalDate.parse(user.last_date_of_pause.split(" ")[0])
                val today = LocalDate()
                val dayDiff = Days.daysBetween(datePaused, today).days
                txtDuration.text = "Durasi $dayDiff Hari"
            }

            itemView.setOnClickListener {
                listener(user)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_sdm_non_job
    }
}