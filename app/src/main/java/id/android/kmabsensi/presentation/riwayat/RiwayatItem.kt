package id.android.kmabsensi.presentation.riwayat

import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.data.remote.response.PresenceHistory
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.getDateStringFormatted
import id.android.kmabsensi.utils.loadCircleImage
import id.android.kmabsensi.utils.loadImageFromUrl
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
            itemView.textNama.text = userName
            itemView.txtCheckIn.text =
                presenceHistory.check_in_datetime.split(" ")[1].substring(0, 5)

            presenceHistory.checkout_date_time?.let {
                itemView.txtCheckOut.text = it.split(" ")[1].substring(0, 5)
            }

            itemView.imgCheckin.loadImageFromUrl(presenceHistory.checkIn_photo_url)
            itemView.imgCheckin.setOnClickListener { view ->
                StfalconImageViewer.Builder<String>(
                    itemView.context,
                    listOf(presenceHistory.checkIn_photo_url)
                ) { view, image ->
                    Glide.with(itemView.context)
                        .load(image).into(view)
                }.show()
            }


            //2019-10-09 23:54:5
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = dateFormat.parse(presenceHistory.check_in_datetime)
            txtDate.text = getDateStringFormatted(date)

            presenceHistory.user?.let {
                txtOfficeName.text = it.office_name
            }

        }

    }

    override fun getLayout(): Int = R.layout.item_row_riwayat_absensi
}