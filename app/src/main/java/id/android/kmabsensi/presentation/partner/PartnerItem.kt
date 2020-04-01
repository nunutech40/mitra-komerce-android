package id.android.kmabsensi.presentation.partner

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.item_row_partner.view.*

class PartnerItem(val partner: Partner): Item(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textPartnerNumber.text = "NO. PARTNER ${partner.number}"
            itemView.textPartnerName.text = partner.name
            itemView.textPartnerCategory.text = partner.category
            itemView.textTotalSdm.text = partner.totalSdm.toString()
        }
    }

    override fun getLayout() = R.layout.item_row_partner

}