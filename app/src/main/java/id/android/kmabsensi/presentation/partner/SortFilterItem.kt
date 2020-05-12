package id.android.kmabsensi.presentation.partner

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.item_row_sort_filter.view.*

class SortFilterItem(val content: String,
                     val listener: (String) -> Unit): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textContent.text = content
        viewHolder.itemView.setOnClickListener {
            listener(content)
        }
    }

    override fun getLayout() = R.layout.item_row_sort_filter

}