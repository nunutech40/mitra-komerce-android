package id.android.kmabsensi.presentation.kmpoint.penarikandetail.items

import android.annotation.SuppressLint
import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse
import id.android.kmabsensi.data.remote.response.kmpoint.model.ShoppingRequestParticipant
import id.android.kmabsensi.data.remote.response.kmpoint.model.TalentRecipient
import kotlinx.android.synthetic.main.item_row_talent.view.*

class TalentItems(
        val context: Context,
        val talent : TalentRecipient,
        val price: Int? = 0
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name.text = talent.talent.user.fullName
            itemView.tx_position.text = price.toString()
            itemView.tx_position.setTextColor(context.resources.getColor(R.color.cl_orange))
        }
    }

    override fun getLayout(): Int  = R.layout.item_row_talent
}