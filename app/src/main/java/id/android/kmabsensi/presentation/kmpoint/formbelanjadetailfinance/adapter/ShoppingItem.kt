package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter

import android.content.Context
import android.text.Editable
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data.ShoopingRequestItem
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.ShoppingModel
import id.android.kmabsensi.utils.gone
import kotlinx.android.synthetic.main.item_row_form_belanja.view.*

class ShoppingItem(
        val context: Context,
        val data : ShoopingRequestItem): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.et_name_tools.text = data.name!!.toEditable()
            itemView.tx_price_forecasts.text = data.total.toString().toEditable()
            itemView.btn_remove.gone()
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun getLayout(): Int  = R.layout.item_row_form_belanja
}