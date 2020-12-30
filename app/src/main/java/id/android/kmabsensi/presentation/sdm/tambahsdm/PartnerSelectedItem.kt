package id.android.kmabsensi.presentation.sdm.tambahsdm

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_chip_partner.*
import kotlinx.android.synthetic.main.item_row_chip_partner.view.*

class PartnerSelectedItem(val partner: Partner, val isShowClose: Boolean = true, val listener: (Partner) -> Unit): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtPartner.text = "${partner.partnerDetail.noPartner} - ${partner.fullName}"

            btnClose.setOnClickListener {
                listener(partner)
            }

            if (isShowClose){
                btnClose.visible()
            } else {
                btnClose.gone()
            }
        }
    }

    override fun getLayout() = R.layout.item_row_chip_partner

}