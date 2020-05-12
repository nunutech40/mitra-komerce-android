package id.android.kmabsensi.presentation.partner.partnerpicker

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import kotlinx.android.synthetic.main.item_row_simple_partners.view.*

class SimplePartnerItem(val simplePartner: SimplePartner,
                        val listener: (SimplePartner) -> Unit): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textPartner.text = "${simplePartner.noPartner} - ${simplePartner.fullName}"

            itemView.setOnClickListener {
                listener(simplePartner)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_simple_partners
    }

}