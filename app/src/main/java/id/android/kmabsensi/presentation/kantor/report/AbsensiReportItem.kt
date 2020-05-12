package id.android.kmabsensi.presentation.kantor.report

import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.utils.capitalizeWords
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.loadImageFromUrl
import kotlinx.android.synthetic.main.item_row_report_absensi.*
import kotlinx.android.synthetic.main.item_row_report_absensi.view.*

class AbsensiReportItem(val presence: Presence) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            itemView.txtCheckIn.text = presence.check_in_datetime.split(" ")[1].substring(0, 5)

            itemView.btnLihatFotoDatang.setOnClickListener {
                StfalconImageViewer.Builder<String>(
                    itemView.context,
                    listOf(presence.checkIn_photo_url)
                ) { view, image ->
                    Glide.with(itemView.context)
                        .load(image).into(view)
                }.show()
            }

            presence.checkout_date_time?.let {
                itemView.txtCheckOut.text = it.split(" ")[1].substring(0, 5)

                itemView.btnLihatFotoPulang.setOnClickListener {
                    presence.checkOut_photo_url?.let {photoUrl ->
                        StfalconImageViewer.Builder<String>(
                            itemView.context,
                            listOf(photoUrl)
                        ) { view, image ->
                            Glide.with(itemView.context)
                                .load(image).into(view)
                        }.show()
                    }

                }
            } ?: kotlin.run {
                itemView.txtCheckOut.gone()
                itemView.btnLihatFotoPulang.gone()
            }

            //2019-10-09 23:54:5
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            val date = dateFormat.parse(presenceHistory.check_in_datetime)
//            txtDate.text = getDateStringFormatted(date)

            presence.user?.let {
                itemView.txtKantor.text = it.office_name
                itemView.txtPartner.text = it.division_name
                itemView.txtName.text = it.full_name.toLowerCase().capitalizeWords()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_report_absensi
}