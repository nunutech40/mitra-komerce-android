package id.android.kmabsensi.presentation.sdm.laporan.advertiser

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.AdvertiserReport
import id.android.kmabsensi.utils.convertRpWithoutSpace
import id.android.kmabsensi.utils.getDateStringFormatted2
import id.android.kmabsensi.utils.parseStringDate
import kotlinx.android.synthetic.main.item_row_list_laporan_advertiser.view.*

interface OnAdvertiserReportListener {
    fun onEditClicked(report: AdvertiserReport)
    fun onDeleteClicked(report: AdvertiserReport)
}

class ListLaporanItem(
    val report: AdvertiserReport,
    val listener: OnAdvertiserReportListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val date = getDateStringFormatted2(parseStringDate(report.date))
            val adCost = convertRpWithoutSpace(report.adCost.toDouble())
            val cpr = convertRpWithoutSpace(report.cpr.toDouble())
            itemView.txtLaporan.text = "${date} - ${adCost} - ${cpr}/lead"

            itemView.btnEdit.setOnClickListener {
                listener.onEditClicked(report)
            }

            itemView.btnDelete.setOnClickListener {
                listener.onDeleteClicked(report)
            }

        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_list_laporan_advertiser
    }
}