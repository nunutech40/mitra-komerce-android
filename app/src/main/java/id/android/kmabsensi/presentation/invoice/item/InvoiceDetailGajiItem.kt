package id.android.kmabsensi.presentation.invoice.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailGaji
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_invoice_detail_gaji.view.*

class InvoiceDetailGajiItem(val invoiceDetailGaji: InvoiceDetailGaji): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textLeaderName.text = invoiceDetailGaji.sdmData?.fullName
            invoiceDetailGaji.bankAccount?.let {
                itemView.textBankAccountName.text = "${it.bankName} | ${it.bankOwnerName}"
                itemView.textBankAccountNumber.text = it.bankNo
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