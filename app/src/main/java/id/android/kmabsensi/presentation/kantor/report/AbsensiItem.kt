package id.android.kmabsensi.presentation.kantor.report

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.item_row_absensi.view.*
import kotlin.math.abs

class AbsensiItem(val absensi: Absensi) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtNama.text = absensi.nama
            itemView.txtCheckIn.text = absensi.checkIn
            itemView.txtCheckOut.text = absensi.checkOut

            itemView.imgProfile.loadCircleImage(absensi.imgProfile)
        }
    }

    override fun getLayout(): Int = R.layout.item_row_absensi
}