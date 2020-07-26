package id.android.kmabsensi.presentation.partner.administratif

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Administration
import id.android.kmabsensi.data.remote.response.Device
import kotlinx.android.synthetic.main.item_row_administratif.*
import kotlinx.android.synthetic.main.item_row_administratif.view.*

interface OnAdministrationListener {
    fun onDeleteClicked(id: Int)
    fun onEditClicked(administration: Administration)
    fun onDetailClicked(administration: Administration)
}

class AdministratifItem(
    val administration: Administration,
    val listener: OnAdministrationListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtTitleWithLeader.text =
                "${administration.title} - ${administration.leader.positionName}"

            btnDelete.setOnClickListener {
                listener.onDeleteClicked(administration.id)
            }

            btnEdit.setOnClickListener {
                listener.onEditClicked(administration)
            }

            itemView.setOnClickListener {
                listener.onDetailClicked(administration)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_administratif
    }
}