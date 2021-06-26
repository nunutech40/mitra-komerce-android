package id.android.kmabsensi.presentation.point.formbelanjadetailleader.items

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import kotlinx.android.synthetic.main.item_row_talent.view.*

class TalentItems(
        val talent : DetailShoppingResponse.Data.User
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name.text = talent.fullName
            itemView.tx_position.text = talent.positionName
        }
    }

    override fun getLayout(): Int  = R.layout.item_row_talent
}