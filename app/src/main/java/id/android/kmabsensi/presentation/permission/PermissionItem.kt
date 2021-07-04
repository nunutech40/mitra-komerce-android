package id.android.kmabsensi.presentation.permission

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Permission
import id.android.kmabsensi.utils.getDateStringFormatted
import id.android.kmabsensi.utils.getDateStringFormatted2
import kotlinx.android.synthetic.main.item_row_permission.view.*
import kotlinx.android.synthetic.main.item_row_riwayat_absensi.*
import java.text.SimpleDateFormat

class PermissionItem(val context: Context,
                     val permission: Permission,
                     val listener: (Permission) -> Unit) : Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {9
        viewHolder.apply {

            //2019-10-09
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateFrom = dateFormat.parse(permission.dateFrom)
            val dateTo = dateFormat.parse(permission.dateTo)
//            txtDate.text = getDateStringFormatted(date)

            itemView.txtDateFrom.text = getDateStringFormatted2(dateFrom)
            itemView.txtDateTo.text = getDateStringFormatted2(dateTo)
            itemView.txtAlasanTidakHadir.text = when (permission.permissionType) {
                1 -> "Izin"
                2 -> "Sakit"
                else -> "Cuti"
            }

            when(permission.status){
                0 -> {
                    itemView.txtStatus.text = "Pengajuan"
                    itemView.txtStatus.setTextColor(context.resources.getColor(R.color.cl_yellow))
                }
                2 -> {
                    itemView.txtStatus.text = "Disetujui"
                    itemView.txtStatus.setTextColor(context.resources.getColor(R.color.cl_green))
                }
                3 -> {
                    itemView.txtStatus.text = "Ditolak"
                    itemView.txtStatus.setTextColor(context.resources.getColor(R.color.cl_orange))
                }
            }

            permission.user?.let {
                itemView.txtNamaPemohon.text = it.fullName
            }

            itemView.setOnClickListener {
                listener(permission)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_permission

}