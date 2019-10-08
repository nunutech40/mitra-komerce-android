package id.android.kmabsensi.presentation.kantor.report

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.item_row_absensi.view.*

class AbsensiReportItem(val presence: Presence) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textNama.text = presence.user.full_name
            itemView.txtCheckIn.text = presence.check_in_datetime.split(" ")[1].substring(0,5)
            itemView.txtCheckOut.text = presence.checkout_date_time.split(" ")[1].substring(0,5)

            itemView.imgProfile.loadCircleImage(presence.user.photo_profile_url ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg")
        }
    }

    override fun getLayout(): Int = R.layout.item_row_absensi
}