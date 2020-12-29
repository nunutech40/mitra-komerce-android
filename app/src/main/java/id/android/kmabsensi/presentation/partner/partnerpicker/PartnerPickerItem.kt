package id.android.kmabsensi.presentation.partner.partnerpicker

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.utils.capitalizeWords
import kotlinx.android.synthetic.main.item_row_simple_partners.view.*

class PartnerPickerItem(val partner: Partner,
                        val listener: (Partner) -> Unit): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textPartner.text = "${partner.partnerDetail.noPartner} - ${partner.fullName.capitalizeWords()}"

            itemView.setOnClickListener {
                listener(partner)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_simple_partners
    }

}