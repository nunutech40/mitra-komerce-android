package id.android.kmabsensi.presentation.sdm.dataalfa

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Alpha
import kotlinx.android.synthetic.main.item_row_data_alfa.view.*

class AlphaItem(val alpha: Alpha): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtName.text = alpha.fullName
            itemView.txtAlfa.text = "${alpha.totalAlpha} kali"
        }
    }

    override fun getLayout() = R.layout.item_row_data_alfa
}