package id.android.kmabsensi.presentation.permission

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Permission
import id.android.kmabsensi.utils.getDateStringFormatted
import id.android.kmabsensi.utils.getDateStringFormatted2
import kotlinx.android.synthetic.main.item_row_permission.view.*
import kotlinx.android.synthetic.main.item_row_riwayat_absensi.*
import java.text.SimpleDateFormat

class PermissionItem(val permission: Permission,
                     val listener: (Permission) -> Unit) : Item(){

    override fun bind(viewHolder: ViewHolder, position: Int) {9
        viewHolder.apply {

            //2019-10-09
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateFrom = dateFormat.parse(permission.date_from)
            val dateTo = dateFormat.parse(permission.date_to)
//            txtDate.text = getDateStringFormatted(date)

            itemView.txtDateFrom.text = getDateStringFormatted2(dateFrom)
            itemView.txtDateTo.text = getDateStringFormatted2(dateTo)
            itemView.txtAlasanTidakHadir.text = when (permission.permission_type) {
                1 -> "Izin"
                2 -> "Sakit"
                else -> "Cuti"
            }

            when(permission.status){
                0 -> {
                    itemView.txtStatus.text = "REQUESTED"
                    itemView.txtStatus.setBackgroundResource(R.drawable.bg_status_requested)
                }
                2 -> {
                    itemView.txtStatus.text = "DISETUJUI"
                    itemView.txtStatus.setBackgroundResource(R.drawable.bg_status_approved)
                }
                3 -> {
                    itemView.txtStatus.text = "DITOLAK"
                    itemView.txtStatus.setBackgroundResource(R.drawable.bg_status_rejected)
                }
            }

            permission.user?.let {
                itemView.txtNamaPemohon.text = it.full_name
            }

            itemView.setOnClickListener {
                listener(permission)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_permission

}