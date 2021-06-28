package id.android.kmabsensi.presentation.kmpoint.formbelanja.adapter

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.kmpoint.formbelanja.FormBelanjaMainModel
import id.android.kmabsensi.presentation.kmpoint.penarikan.TYPE_HEADER
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_penarikan_poin.view.*

class FormBelanjaItem(
    val context: Context,
    val data: FormBelanjaMainModel,
    val listener : (FormBelanjaMainModel) -> Unit): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            if (data.type == TYPE_HEADER){
                itemView.tx_date.visible()
                itemView.tx_date.text = data.data.createdAt
            }else{
                itemView.tx_date.gone()
            }
            data.data.apply {
                itemView.tx_no_transaksi.gone()
                itemView.tx_username.text = this.partner?.fullName ?: "-"
                itemView.tx_no_partner.text = "No. Partner: ${this.partner?.noPartner}"
                itemView.tx_total_poin.text = "${this.total} Poin"
                var status = this.status
                itemView.tx_status.apply {
                    text = when(status){
                        "requested" -> "Diajukan"
                        "completed" -> "Disetujui"
                        "rejected" -> "Ditolak"
                        "canceled" -> "Dibatalkan"
                        else -> "-"
                    }
                    setTextColor(
                        when(status){
                            "requested" -> resources.getColor(R.color.cl_yellow)
                            "completed" -> resources.getColor(R.color.cl_green)
                            "rejected" -> resources.getColor(R.color.cl_orange)
                            "canceled" -> resources.getColor(R.color.cl_orange)
                            else -> resources.getColor(R.color.cl_yellow)
                        }
                    )
                    setBackgroundColor(
                            when(status){
                                "requested" -> resources.getColor(R.color.cl_semi_yellow)
                                "completed" -> resources.getColor(R.color.cl_semi_green)
                                "rejected" -> resources.getColor(R.color.cl_semi_orange)
                                "canceled" -> resources.getColor(R.color.cl_semi_orange)
                                else -> resources.getColor(R.color.cl_semi_yellow)
                            }
                    )
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

}