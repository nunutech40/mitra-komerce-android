package id.android.kmabsensi.presentation.kantor

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import kotlinx.android.synthetic.main.item_row_kantor.view.*

class KantorItem(val office: Office,
                 val listener: (Office) -> Unit) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textKantor.text = office.office_name
            itemView.txtKelolaKantor.text = "Kelola Data Kantor ${office.office_name}"

            itemView.setOnClickListener {
                listener(office)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_kantor
    }
}