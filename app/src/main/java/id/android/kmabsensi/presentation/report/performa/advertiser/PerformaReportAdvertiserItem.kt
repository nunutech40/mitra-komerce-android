package id.android.kmabsensi.presentation.report.performa.advertiser

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.item_row_performa_advertiser.view.*


class PerformaReportAdvertiserItem(val report: PerformaAdvertiserReport): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            report.apply {
                itemView.txtIndikator.text = indikator
                itemView.txtToday.text = today
                itemView.txtYesterday.text = yesterday
                itemView.txtLast7Days.text = last7days
                itemView.txtThisMonth.text = thisMonth
                itemView.txtLastMonth.text = lastMonth
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_performa_advertiser
    }

}