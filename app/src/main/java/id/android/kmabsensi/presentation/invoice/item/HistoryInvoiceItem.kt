package id.android.kmabsensi.presentation.invoice.item

import android.util.Log
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.Invoice
import kotlinx.android.synthetic.main.item_row_history_invoice.view.*

class HistoryInvoiceItem(
    val invoice: Invoice,
    val listener: () -> Unit
): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var clickable = true
        viewHolder.apply {
            itemView.textInvoiceNumber.text = "No. ${invoice.invoiceKmId}"
            try {
                itemView.textPartnerName.text = "${invoice.userTo.id} - ${invoice.userTo.fullName}"
            }catch (e: Exception){
                Log.d("_invoice", "desc error: ${e.message} - detail data : $invoice")
                itemView.textPartnerName.text = "Tidak ada partner"
                itemView.alpha = 0.5F
            }
            itemView.textInvoiceTitle.text = invoice.title

            if (invoice.invoiceType == 1){
                itemView.textInvoiceType.text = "Admin"
            } else {
                itemView.textInvoiceType.text = "Gaji SDM"
            }

            itemView.textInvoiceDate.text = invoice.createdAt
            when (invoice.status){
                1 -> {
                    itemView.textInvoiceStatus.text = "OPEN"
                    itemView.textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_open)
                    itemView.textInvoiceStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color._64C3F9))
                }
                2 -> {
                    itemView.textInvoiceStatus.text = "COMPLETED"
                    itemView.textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_complete)
                    itemView.textInvoiceStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color._5AB04F))
                }
                3 -> {
                    itemView.textInvoiceStatus.text = "EXPIRED"
                    itemView.textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_cancel)
                    itemView.textInvoiceStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color._DE4D4E))

                }
            }
        }

        viewHolder.itemView.setOnClickListener {
            listener()
        }
    }

    override fun getLayout() = R.layout.item_row_history_invoice

}