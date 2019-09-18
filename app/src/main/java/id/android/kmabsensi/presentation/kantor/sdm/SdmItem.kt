package id.android.kmabsensi.presentation.kantor.sdm

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.item_row_list_sdm.view.*

class SdmItem(val name: String) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtNamaSdm.text = name
    }

    override fun getLayout(): Int = R.layout.item_row_list_sdm
}