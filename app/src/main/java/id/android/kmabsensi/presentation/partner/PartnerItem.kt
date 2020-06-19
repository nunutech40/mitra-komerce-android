package id.android.kmabsensi.presentation.partner

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import kotlinx.android.synthetic.main.item_row_partner.view.*

class PartnerItem(val partner: Partner,
                  val listener: (Partner) -> Unit): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textPartnerNumber.text = "NO. PARTNER ${partner.noPartner}"
            itemView.textPartnerName.text = partner.fullName
            itemView.textPartnerCategory.text = partner.partnerDetail.partnerCategoryName
            itemView.textTotalSdm.text = partner.totalSdmAssigned.toString()

            itemView.setOnClickListener {
                listener(partner)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_partner

}