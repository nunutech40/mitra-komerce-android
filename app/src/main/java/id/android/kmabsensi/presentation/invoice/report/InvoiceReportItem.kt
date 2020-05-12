package id.android.kmabsensi.presentation.invoice.report

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.convertRp
import kotlinx.android.synthetic.main.item_row_invoice_report.view.*

class InvoiceReportItem(val reportNote: String,
                        val totalInvoice: Int,
                        val sumOfInvoice: Int): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.labelKeterangan.text = reportNote
            itemView.textTotalInvoice.text = "${convertRp(totalInvoice.toDouble())} | $sumOfInvoice Invoice"
        }
    }

    override fun getLayout() = R.layout.item_row_invoice_report
}