package id.android.kmabsensi.presentation.invoice.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Invoice
import kotlinx.android.synthetic.main.item_row_active_invoice.view.*

class ActiveInvoiceItem(
    val invoice: Invoice,
    val listener: () -> Unit
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.apply {
            itemView.textInvoiceNumber.text = "No. ${invoice.invoiceKmId}"
            itemView.textPartnerName.text = "${invoice.userTo.id} - ${invoice.userTo.fullName}"
            itemView.textInvoiceTitle.text = invoice.title

            if (invoice.invoiceType == 1){
                itemView.textInvoiceType.text = "Admin"
            } else {
                itemView.textInvoiceType.text = "Gaji SDM"
            }

            itemView.textInvoiceDate.text = invoice.createdAt
        }


        viewHolder.itemView.setOnClickListener {
            listener()
        }
    }

    override fun getLayout() = R.layout.item_row_active_invoice
}