package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.TalentModel
import kotlinx.android.synthetic.main.item_row_talent.view.*

class TalentItem(
        val context: Context,
        val data: TalentModel,
        val listener : (TalentModel) -> Unit): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tx_name.text = data.name
            itemView.tx_position.text = data.position
        }
    }

    override fun getLayout(): Int = R.layout.item_row_talent
}