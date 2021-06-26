package id.android.kmabsensi.presentation.point.penarikan.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.point.penarikan.PenarikanMainModel
import id.android.kmabsensi.presentation.point.penarikan.TYPE_HEADER
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_penarikan_poin.view.*

class PenarikanItem(
    val context: Context,
    val data: PenarikanMainModel,
    val listener : (PenarikanMainModel) -> Unit): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            if (data.type == TYPE_HEADER){
                itemView.tx_date.visible()
//                itemView.tx_date.text = data.data.date
            }else{
                itemView.tx_date.gone()
            }
            data.data.apply {
                itemView.tx_username.text = this.user.fullName
                itemView.tx_no_partner.text = "No. Partner: -"
                itemView.tx_total_poin.text = "${this.nominal} Poin"
                itemView.tx_status.text = "${this.status}"
                var status = this.status
                itemView.tx_status.apply {
                    text = getTextStatus(status)
                    setTextColor(resources.getColor(getStatusTextColor(status)))
                    setBackgroundColor(resources.getColor(getStatusBackgroundColor(status)))
                }
                itemView.setOnClickListener {
                    listener(data)
                }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_penarikan_poin
    }

    fun getTextStatus(status : String) : String{
        return when (status) {
            "requested" -> "Diajukan"
            "completed" -> "Disetujui"
            "rejected" -> "Ditolak"
            "canceled" -> "Dibatalkan"
            else -> "-"
        }
    }

    fun getStatusTextColor(status : String) : Int{
        return  when (status) {
            "requested" -> R.color.cl_yellow
            "completed" -> R.color.cl_green
            "rejected" -> R.color.cl_orange
            "canceled" -> R.color.cl_orange
            else -> R.color.cl_yellow
        }
    }

    fun getStatusBackgroundColor(status : String) : Int{
        return  when (status) {
            "requested" -> R.color.cl_semi_yellow
            "completed" -> R.color.cl_semi_green
            "rejected" -> R.color.cl_semi_orange
            "canceled" -> R.color.cl_semi_orange
            else -> R.color.cl_semi_yellow
        }
    }

    fun editable(status : String): Boolean{
        return when (status) {
            "completed" -> false
            "rejected" -> false
            "canceled" -> false
            else -> true
        }
    }
}