package id.android.kmabsensi.presentation.sdm.shift

import android.content.Context
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.utils.capitalizeWords
import id.android.kmabsensi.utils.loadCircleImageSdm
import kotlinx.android.synthetic.main.item_row_shift_sdm.view.*

interface OnSdmShiftListener {
    fun onShiftPagiSelected(position: Int, user: User)
    fun onShiftSiangSelected(position: Int, user: User)
}

class SdmShiftItem(
    val context: Context,
    val user: User,
    val listener: OnSdmShiftListener
): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtNamaSdm.text = user.full_name.capitalizeWords()
            itemView.txtPekerjaan.text = user.position_name
            itemView.imageView13.loadCircleImageSdm(user.photo_profile_url.toString())

            user.sdm_config?.let {config ->
                if (config.shiftMode == SdmShiftActivity.SHIFT_SIANG){
                    itemView.btnSHiftPagi.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_empty))
                    itemView.btnSHiftSiange.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_green_border))
                } else if (config.shiftMode == SdmShiftActivity.SHIFT_PAGI){
                    itemView.btnSHiftPagi.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_green_border))
                    itemView.btnSHiftSiange.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_empty))
                } else {
                    itemView.btnSHiftPagi.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_green_border))
                    itemView.btnSHiftSiange.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_circle_empty))
                }

                itemView.btnSHiftPagi.setOnClickListener {
                    listener.onShiftPagiSelected(adapterPosition, user)
                }

                itemView.btnSHiftSiange.setOnClickListener {
                    listener.onShiftSiangSelected(adapterPosition, user)
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_shift_sdm

}