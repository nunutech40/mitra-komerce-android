package id.android.kmabsensi.presentation.partner

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.utils.capitalizeWords
import kotlinx.android.synthetic.main.item_row_partner.*
import kotlinx.android.synthetic.main.item_row_partner.view.*
import kotlinx.android.synthetic.main.item_row_partner.view.btnTotalSdm

interface OnParterItemClicked{
    fun onPartnerClicked(partner: Partner)
    fun onBtnTotalSdmClicked(noPartner: String, partnerName: String)
}


class PartnerItem(val partner: Partner,
                  val listener: OnParterItemClicked): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textPartnerNumber.text = "NO. PARTNER ${partner.noPartner}"
            itemView.textPartnerName.text = partner.fullName.capitalizeWords()
            itemView.textPartnerCategory.text = partner.partnerDetail.partnerCategoryName
            itemView.textTotalSdm.text = "${partner.totalSdmAssigned} SDM, ${partner.totalDevice} Devices"

            itemView.setOnClickListener {
                listener.onPartnerClicked(partner)
            }

            btnTotalSdm.setOnClickListener {
                listener.onBtnTotalSdmClicked(partner.noPartner, partner.fullName)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_partner

}