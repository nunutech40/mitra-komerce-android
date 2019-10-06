package id.android.kmabsensi.presentation.sdm

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.utils.loadCircleImage
import kotlinx.android.synthetic.main.item_row_sdm.view.*

class SdmItem(val sdm: User,
              val listener: (User) -> Unit): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtNamaSdm.text = sdm.full_name
            itemView.txtPekerjaan.text = sdm.position_name
            sdm.photo_profile_url?.let {
                itemView.imageView13.loadCircleImage(it)
            }


            itemView.setOnClickListener {
                listener(sdm)
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_row_sdm
}