package id.android.kmabsensi.presentation.role

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Role
import id.android.kmabsensi.utils.capitalizeWords
import kotlinx.android.synthetic.main.item_row_role.view.*

class RoleItem(
    val role: Role,
    val listener: (Role) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtRoleName.text = role.name.capitalizeWords()

            itemView.materialButton.setOnClickListener {
                listener(role)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_role
}