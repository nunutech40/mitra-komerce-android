package id.android.kmabsensi.presentation.sdm

import com.github.ajalt.timberkt.d
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.utils.loadCircleImage
import id.android.kmabsensi.utils.loadCircleImageSdm
import kotlinx.android.synthetic.main.item_row_sdm.view.*

class SdmItem(val sdm: User,
              val listener: (User) -> Unit): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtNamaSdm.text = sdm.full_name
            itemView.txtPekerjaan.text = sdm.position_name
            itemView.imageView13.loadCircleImageSdm(sdm.photo_profile_url.toString())


            itemView.setOnClickListener {
                listener(sdm)
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_row_sdm
}