package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader.items

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import id.android.kmabsensi.utils.gone
import kotlinx.android.synthetic.main.item_row_shopping.view.*

class GoodsItems(
        val goods : DetailShoppingResponse.Data.ShoopingRequestItem
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name_tools.text = goods.name
            itemView.tx_price.text = goods.total.toString()
            itemView.btn_edit.gone()
        }
    }

    override fun getLayout(): Int  = R.layout.item_row_shopping
}