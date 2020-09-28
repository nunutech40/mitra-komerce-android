package id.android.kmabsensi.presentation.invoice.report.detail

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.InvoiceReportDetail
import kotlinx.android.synthetic.main.item_invoice_report_detail.*

/**
 * Created by Abdul Aziz on 30/08/20.
 */
class InvoiceReportDetailitem(val data: InvoiceReportDetail): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvLeaderName.text = data.fullName
            tvReportValue.text = "${data.totalInvoice} dari ${data.totalPartner} partner"
        }
    }

    override fun getLayout(): Int = R.layout.item_invoice_report_detail
}