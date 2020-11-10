package id.android.kmabsensi.presentation.report.performa

import com.afollestad.materialdialogs.MaterialDialog
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.CsPerformance
import kotlinx.android.synthetic.main.item_row_performa.*
import kotlinx.android.synthetic.main.item_row_performa.view.*

class PerformaItem(val performa: CsPerformance): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            performa.cs?.let {
                itemView.txtNamaSdm.text = it.fullName
            } ?: kotlin.run {
                itemView.txtNamaSdm.text = "null"
            }

            itemView.txtLeads.text = "${performa.totalLeads}"
            itemView.txtTransaksi.text = "${performa.totalTransaction}"
            itemView.txtOrder.text = "${performa.totalOrder}"
            itemView.txtRatingKonversi.text = "${performa.conversionRate}"
            itemView.txtRatingOrder.text = "${performa.orderRate}"

            itemView.txtNb.setOnClickListener {
                MaterialDialog(itemView.context).show {
                    title(text = "N.B")
                    message(text = performa.notes ?: "Tidak ada catatan")
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_row_performa
}