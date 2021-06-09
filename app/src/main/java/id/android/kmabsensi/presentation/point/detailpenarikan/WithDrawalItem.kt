package id.android.kmabsensi.presentation.point.detailpenarikan

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_bukti_transfer.view.*

class WithDrawalItem(
    val context: Context,
    val data: BuktiTransferModel,
    val onRemove: onCLick,
    val listener : (BuktiTransferModel) -> Unit): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.apply {
                if (data.id == 0){
                    itemView.btn_remove.gone()
                    itemView.img_photo.setBackgroundResource(R.drawable.ic_btn_camera)
                }else{
                    itemView.btn_remove.visible()
                    itemView.img_photo.setImageBitmap(data.img)
                }
                itemView.setOnClickListener {
                    listener(data)
                }
                itemView.btn_remove.setOnClickListener {
                    onRemove.onRemove(position)
                }
            }
    }
    override fun getLayout(): Int = R.layout.item_row_bukti_transfer

    interface onCLick{
        fun onRemove(position : Int)
    }
}