package id.android.kmabsensi.presentation.partner.device.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R

class PartnerDeviceItem(val listener: () -> Unit): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.setOnClickListener {
                listener()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_partner_device
    }
}