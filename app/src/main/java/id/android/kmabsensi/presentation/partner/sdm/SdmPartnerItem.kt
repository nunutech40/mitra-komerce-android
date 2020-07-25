package id.android.kmabsensi.presentation.partner.sdm

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.utils.calcAgePerson
import id.android.kmabsensi.utils.capitalizeWords
import kotlinx.android.synthetic.main.item_row_sdm_partner.*
import kotlinx.android.synthetic.main.item_row_sdm_partner.view.*

interface OnSdmPartnerListener {
    fun onClicked(sdm: User)
    fun onLihatDeviceClicked(sdm: User)
}

class SdmPartnerItem(
    val sdm: User,
    val listener: OnSdmPartnerListener
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val gender = if (sdm.gender == 1) "Laki-Laki" else "Perempuan"
        val age = calcAgePerson(sdm.birth_date)

        viewHolder.apply {
            itemView.txtNameWithJob.text = "${sdm.full_name.capitalizeWords()} - ${sdm.position_name}"
            itemView.txtGenderWithAge.text = "$gender, ${sdm.birth_date}, $age tahun"

            itemView.setOnClickListener {
                listener.onClicked(sdm)
            }

            btnLihatDevices.setOnClickListener {
                listener.onLihatDeviceClicked(sdm)
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_row_sdm_partner
}