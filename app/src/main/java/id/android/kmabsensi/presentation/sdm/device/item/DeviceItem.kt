package id.android.kmabsensi.presentation.sdm.device.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Device
import kotlinx.android.synthetic.main.item_row_device.*

interface OnDeviceListener {
    fun onDeleteClicked(id: Int)
    fun onEditClicked(device: Device)
    fun onDetailClicked(device: Device)
}

class DeviceItem(
    val device: Device,
    val listener: OnDeviceListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textDeviceName.text = "${device.deviceType} ${device.brancd}"

            if (device.sdm == null){
                textSdmName.text = "Pemegang IT Support"
            } else {
                textSdmName.text = "Pemegang ${device.sdm.fullName}"
            }

            btnDelete.setOnClickListener {
                listener.onDeleteClicked(device.id)
            }

            btnEdit.setOnClickListener {
                listener.onEditClicked(device)
            }

            itemView.setOnClickListener {
                listener.onDetailClicked(device)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_device
    }
}