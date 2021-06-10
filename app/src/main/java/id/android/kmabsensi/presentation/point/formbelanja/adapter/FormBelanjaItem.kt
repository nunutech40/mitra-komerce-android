package id.android.kmabsensi.presentation.point.formbelanja.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.point.formbelanja.FormBelanjaMainModel
import id.android.kmabsensi.presentation.point.formbelanja.FormBelanjaModel
import id.android.kmabsensi.presentation.point.penarikan.PenarikanMainModel
import id.android.kmabsensi.presentation.point.penarikan.TYPE_HEADER
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_penarikan_poin.view.*

class FormBelanjaItem(
    val context: Context,
    val data: FormBelanjaMainModel,
    val listener : (FormBelanjaMainModel) -> Unit): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            if (data.type == TYPE_HEADER){
                itemView.tx_date.visible()
                itemView.tx_date.text = data.data.date
            }else{
                itemView.tx_date.gone()
            }
            data.data.apply {
                itemView.tx_username.text = this.username
                itemView.tx_no_partner.text = "No. Partner: ${this.username}"
                itemView.tx_total_poin.text = "${this.total_poin} Poin"
                itemView.tx_status.text = "${this.status}"
                itemView.tx_status.setBackgroundResource(R.drawable.bg_semi_green_5dp)
                itemView.tx_status.setTextColor(context.resources.getColor(R.color.cl_green))
                itemView.setOnClickListener {
                    listener(data)
                }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_penarikan_poin
    }

}