package id.android.kmabsensi.presentation.kmpoint.penarikandetail.items

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.kmpoint.DetailWithdrawResponse
import id.android.kmabsensi.data.remote.response.kmpoint.model.DataAttachments
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.BuktiTransferImgModel
import id.android.kmabsensi.presentation.kmpoint.penarikandetail.BuktiTransferModel
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_bukti_transfer.view.*

class TransactionImgItem(
        val context: Context,
        val data: DataAttachments,
        val listener : (DataAttachments) -> Unit): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Log.d("TAGTAGTAG", "id: ${data.id}")
            viewHolder.apply {
                itemView.btn_remove.gone()
                Glide.with(context)
                        .load(data.attachmentUrl)
                        .placeholder(R.drawable.ic_loading_image)
                        .into(itemView.img_photo)

                itemView.setOnClickListener {
                    listener(data)
                }
            }
    }
    override fun getLayout(): Int = R.layout.item_row_bukti_transfer

}