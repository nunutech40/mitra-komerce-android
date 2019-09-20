package id.android.kmabsensi.presentation.sdm

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.item_row_sdm.view.*

class SdmItem(val sdm: Sdm): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtNamaSdm.text = sdm.name
            itemView.txtPekerjaan.text = sdm.job
        }
    }


    override fun getLayout(): Int = R.layout.item_row_sdm
}