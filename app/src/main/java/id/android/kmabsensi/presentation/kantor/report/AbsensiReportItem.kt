package id.android.kmabsensi.presentation.kantor.report

import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.utils.loadImageFromUrl
import kotlinx.android.synthetic.main.item_row_report_absensi.*
import kotlinx.android.synthetic.main.item_row_report_absensi.view.*

class AbsensiReportItem(val presence: Presence) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            presence.user?.let { user ->
                itemView.textNama.text = user.full_name
                itemView.img_checkin.loadImageFromUrl(user.photo_profile_url.toString())

            }

            itemView.img_checkin.loadImageFromUrl(presence.checkIn_photo_url)
            itemView.img_checkin.setOnClickListener { view ->
                StfalconImageViewer.Builder<String>(
                    itemView.context,
                    listOf(presence.checkIn_photo_url)
                ) { view, image ->
                    Glide.with(itemView.context)
                        .load(image).into(view)
                }.show()
            }

            itemView.txtCheckIn.text = presence.check_in_datetime.split(" ")[1].substring(0, 5)
            presence.checkout_date_time?.let {
                itemView.txtCheckOut.text = it.split(" ")[1].substring(0, 5)
            }

            presence.user?.let {
                txtOfficeName.text = it.office_name
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_report_absensi
}