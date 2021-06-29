package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data.ShoopingRequestParticipant
import kotlinx.android.synthetic.main.item_row_talent.view.*

class TalentItem(
        val context: Context,
        val data: ShoopingRequestParticipant
        ): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name.text = data.user!!.fullName
            itemView.tx_position.text = data.user.positionName
        }
    }

    override fun getLayout(): Int = R.layout.item_row_talent
}