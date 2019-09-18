package id.android.kmabsensi.presentation.kantor.penanggungjawab

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R

class PenanggungJawabItem(val listener: () -> Unit): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.setOnClickListener {
            listener()
        }

    }

    override fun getLayout(): Int = R.layout.layout_penanggung_jawab_expanded
}