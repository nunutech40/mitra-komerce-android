package id.android.kmabsensi.presentation.sdm.device.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Device
import kotlinx.android.synthetic.main.item_row_device.*

class DeviceItem(val device: Device) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textDeviceName.text = "${device.deviceType} ${device.brancd}"
            textSdmName.text = "Pemegang ${device.sdm.fullName}"
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_device
    }
}