package id.android.kmabsensi.presentation.role

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Position
import kotlinx.android.synthetic.main.item_row_position_access.view.*


class PositionAssignItem(
    val jabatan: Position,
    val listener: (Position, Boolean) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.checkBoxAssign.isChecked = jabatan.isChecked
            itemView.txtPositionName.text = jabatan.position_name

//            itemView.checkBoxAssign.setOnCheckedChangeListener { buttonView, isChecked ->
//                jabatan.isChecked = isChecked
//                listener(jabatan, isChecked)
//            }

            itemView.view.setOnClickListener {
                itemView.checkBoxAssign.isChecked = !jabatan.isChecked
                jabatan.isChecked = itemView.checkBoxAssign.isChecked
                listener(jabatan, itemView.checkBoxAssign.isChecked)
            }

//            itemView.setOnClickListener {
//
//            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_position_access
}