package id.android.kmabsensi.presentation.kantor.report

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.item_row_riwayat_absensi.*
import kotlinx.android.synthetic.main.item_row_riwayat_absensi.view.*

class AbsensiReportItem(val presence: Presence) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            presence.user?.let {
                itemView.textNama.text = it.full_name
                itemView.imgProfile.loadCircleImage(it.photo_profile_url ?: "https://cdn2.stylecraze.com/wp-content/uploads/2014/09/5-Perfect-Eyebrow-Shapes-For-Heart-Shaped-Face-1.jpg")
            } ?: run {
                itemView.textNama.text = "-"
            }

            itemView.txtCheckIn.text = presence.check_in_datetime.split(" ")[1].substring(0,5)
            presence.checkout_date_time?.let {
                itemView.txtCheckOut.text = it.split(" ")[1].substring(0,5)
            } ?: run {
                itemView.txtCheckOut.text = "-"
            }

            presence.user?.let {
                txtOfficeName.text = it.office_name
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_report_absensi
}