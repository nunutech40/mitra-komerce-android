package id.android.kmabsensi.presentation.permission

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Permission
import kotlinx.android.synthetic.main.item_row_permission.view.*

class PermissionItem(val permission: Permission,
                     val listener: (Permission) -> Unit) : Item(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtDate.text = "${permission.date_from} / ${permission.date_to}"
            itemView.txtExplanation.text = permission.explanation

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