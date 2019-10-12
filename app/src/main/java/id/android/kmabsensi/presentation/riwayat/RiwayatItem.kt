package id.android.kmabsensi.presentation.riwayat

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.data.remote.response.PresenceHistory
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.item_row_absensi.*
import kotlinx.android.synthetic.main.item_row_absensi.view.*

class RiwayatItem(val presenceHistory: PresenceHistory,
                  val userName: String,
                  val userPict: String?) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.apply {
            itemView.textNama.text = userName
            itemView.txtCheckIn.text = presenceHistory.check_in_datetime.split(" ")[1].substring(0,5)
            presenceHistory.checkOut_photo_url?.let {
                itemView.txtCheckOut.text = it.split(" ")[1].substring(0,5)
            } ?: run {
                itemView.txtCheckOut.text = "-"
            }


            imgProfile.loadCircleImage(userPict.toString())

        }

    }

    override fun getLayout(): Int = R.layout.item_row_absensi
}