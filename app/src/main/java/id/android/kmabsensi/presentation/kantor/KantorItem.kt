package id.android.kmabsensi.presentation.kantor

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.item_row_kantor.view.*

class KantorItem(val kantor: Kantor) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtKantor.text = kantor.namaKantor
            itemView.txtKelolaKantor.text = "Kelola Data Kantor ${kantor.namaKantor}"
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_kantor
    }
}