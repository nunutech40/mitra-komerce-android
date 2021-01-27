package id.android.kmabsensi.presentation.notes

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Note
import kotlinx.android.synthetic.main.item_row_note.view.*

class NoteItem(val note: Note): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtTitle.text = note.title
            itemView.txtDescription.text = note.description
        }
    }

    override fun getLayout() = R.layout.item_row_note
}