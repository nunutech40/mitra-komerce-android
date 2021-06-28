package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.ShoppingModel
import kotlinx.android.synthetic.main.item_row_shopping.view.*

class ShoppingItem(
        val context: Context,
        val data : ShoppingModel,
        val listener : (ShoppingModel) -> Unit): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name_tools.text = data.tool_name
            itemView.tx_price.text = data.price
            itemView.btn_edit.setOnClickListener {
                listener(data)
            }
        }
    }

    override fun getLayout(): Int  = R.layout.item_row_shopping
}