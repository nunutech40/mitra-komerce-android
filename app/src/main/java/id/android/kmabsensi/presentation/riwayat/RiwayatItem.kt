package id.android.kmabsensi.presentation.riwayat

import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.data.remote.response.PresenceHistory
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.item_row_riwayat_absensi.*
import kotlinx.android.synthetic.main.item_row_riwayat_absensi.view.*
import java.text.SimpleDateFormat

class RiwayatItem(
    val presenceHistory: PresenceHistory,
    val userName: String,
    val userPict: String?
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.apply {
            itemView.txtCheckIn.text =
                presenceHistory.check_in_datetime.split(" ")[1].substring(0, 5)

            itemView.btnLihatFotoDatang.setOnClickListener {
                StfalconImageViewer.Builder<String>(
                    itemView.context,
                    listOf(presenceHistory.checkIn_photo_url)
                ) { view, image ->
                    Glide.with(itemView.context)
                        .load(image).into(view)
                }.show()
            }

            presenceHistory.checkout_date_time?.let {
                itemView.txtCheckOut.text = it.split(" ")[1].substring(0, 5)

                itemView.btnLihatFotoPulang.setOnClickListener {
                    presenceHistory.checkOut_photo_url?.let {photoUrl ->
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
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = dateFormat.parse(presenceHistory.check_in_datetime)
            txtDate.text = getDateStringFormatted(date)

            presenceHistory.user?.let {
                itemView.txtKantor.text = it.office_name
                itemView.txtPartner.text = it.division_name
                itemView.txtName.text = it.full_name.toLowerCase().capitalizeWords()
            }

        }

    }

    override fun getLayout(): Int = R.layout.item_row_riwayat_absensi
}