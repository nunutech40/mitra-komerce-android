package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader.items

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestParticipant
import kotlinx.android.synthetic.main.item_row_talent.view.*

class TalentItems(
        val talent : ShoppingRequestParticipant.User
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name.text = talent.fullName
            itemView.tx_position.text = talent.positionName
        }
    }

    override fun getLayout(): Int  = R.layout.item_row_talent
}