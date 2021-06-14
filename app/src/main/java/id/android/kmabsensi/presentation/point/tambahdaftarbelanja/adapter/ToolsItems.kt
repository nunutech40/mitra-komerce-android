package id.android.kmabsensi.presentation.point.tambahdaftarbelanja.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.point.tambahdaftarbelanja.ToolsModel
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.invis
import kotlinx.android.synthetic.main.item_row_form_belanja.view.*

class ToolsItems(
        val context: Context,
        val data : ToolsModel,
        val listener : (ToolsModel, position: Int) -> Unit): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            if (data.id == 0){
                itemView.btn_remove.invis()
            }
            itemView.btn_remove.setOnClickListener {
                listener(data, position)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_form_belanja
}