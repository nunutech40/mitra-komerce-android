package id.android.kmabsensi.presentation.kantor.report

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Alpha
import kotlinx.android.synthetic.main.item_row_alpha.view.*

class AlphaItem(val alpha: Alpha, val index: Int): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtSdm.text = "$index. ${alpha.fullName}"
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_alpha
    }

}