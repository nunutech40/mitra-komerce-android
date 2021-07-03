package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestParticipant
import kotlinx.android.synthetic.main.item_row_talent.view.*

class TalentItem(
        val context: Context,
        val data: ShoppingRequestParticipant
        ): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            data.user.let {
                itemView.tx_name.text = it.fullName
                itemView.tx_position.text = it.positionName
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_talent
}