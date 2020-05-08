package id.android.kmabsensi.presentation.invoice.item

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailGaji
import id.android.kmabsensi.utils.convertRp
import kotlinx.android.synthetic.main.item_row_upload_bukti_pembayaran.view.*

class UploadBuktiPembayaranItem(val data: InvoiceDetailGaji) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textLeaderName.text = data.item
            itemView.textTagihan.text = convertRp(data.total.toDouble())

            Glide.with(itemView.context)
                .load(data.paymentAttachmentUrl)
                .apply(RequestOptions().placeholder(R.drawable.image_placeholder).centerCrop())
                .into(itemView.imageBuktiPembayaran)

        }
    }

    override fun getLayout() = R.layout.item_row_upload_bukti_pembayaran
}