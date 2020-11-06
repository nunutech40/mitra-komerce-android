package id.android.kmabsensi.presentation.sdm.laporan

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.CsPerformance
import id.android.kmabsensi.utils.localDateFormatter
import kotlinx.android.synthetic.main.item_row_sdm_report.view.*
import org.joda.time.LocalDate

interface OnSdmReportListener {
    fun onDeleteClicked(report: CsPerformance)
    fun onEditClicked(report: CsPerformance)
}

class SdmReportItem(
    val report: CsPerformance,
    val listener: OnSdmReportListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val localDate: LocalDate = LocalDate.parse(report.date)
        viewHolder.apply {
            itemView.txtReportDate.text = "Laporan - ${localDateFormatter(localDate)}"
            itemView.txtJumlahAndRating.text =
                "Jumlah Order: ${report.totalOrder}   Rating Konversi: ${report.conversionRate}%"

            itemView.btnDelete.setOnClickListener {
                listener.onDeleteClicked(report)
            }

            itemView.btnEdit.setOnClickListener {
                listener.onEditClicked(report)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_sdm_report
}