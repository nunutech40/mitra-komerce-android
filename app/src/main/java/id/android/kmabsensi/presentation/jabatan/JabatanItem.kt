package id.android.kmabsensi.presentation.jabatan

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Position
import kotlinx.android.synthetic.main.item_row_jabatan.view.*

class JabatanItem(val jabatan: Position,
                  val listener: (Position) -> Unit): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtJabatanName.text = jabatan.position_name

            itemView.setOnClickListener {
                listener(jabatan)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_jabatan
}