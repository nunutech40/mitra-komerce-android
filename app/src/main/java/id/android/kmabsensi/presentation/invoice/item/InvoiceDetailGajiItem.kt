package id.android.kmabsensi.presentation.invoice.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailGaji
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_invoice_detail_gaji.view.*

class InvoiceDetailGajiItem(val invoiceDetailGaji: InvoiceDetailGaji): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textLeaderName.text = invoiceDetailGaji.item
            invoiceDetailGaji.bankAccount?.let {
                itemView.textBankAccountName.text = "${it[0].bankName} | ${it[0].bankOwnerName}"
                itemView.textBankAccountNumber.text = it[0].bankNo
            } ?: run {
                itemView.textBankAccountName.text = "Informasi akun bank SDM belum tersedia"
                itemView.textBankAccountNumber.text = "-"
            }

            itemView.textGajiSdm.text = convertRp(invoiceDetailGaji.total.toDouble())
            invoiceDetailGaji.description?.let {
                itemView.textDescription.visible()
                itemView.textDescription.text = invoiceDetailGaji.description
            }


        }
    }

    override fun getLayout() = R.layout.item_row_invoice_detail_gaji
}