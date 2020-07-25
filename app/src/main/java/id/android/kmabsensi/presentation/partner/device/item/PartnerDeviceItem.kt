package id.android.kmabsensi.presentation.partner.device.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Device
import kotlinx.android.synthetic.main.item_row_partner_device.view.*

class PartnerDeviceItem(
    val device: Device,
    val listener: (Device) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtDeviceName.text = "${device.deviceType} ${device.brancd}"

            itemView.setOnClickListener {
                listener(device)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_partner_device
    }
}