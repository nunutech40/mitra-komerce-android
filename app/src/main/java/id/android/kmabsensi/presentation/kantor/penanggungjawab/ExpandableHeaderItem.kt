package id.android.kmabsensi.presentation.kantor.penanggungjawab

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_penanggung_jawab.view.*

class ExpandableHeaderItem(val name: String,
                           val listener: () -> Unit): Item(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    private lateinit var viewHolder: ViewHolder

    override fun bind(viewHolder: ViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            itemView.txtNamaPenanggungJawab.text = name

            itemView.setOnClickListener {
                listener()
                expandableGroup.onToggleExpanded()
                if (expandableGroup.isExpanded)
                    itemView.icon_checklist.visible()
                else
                    itemView.icon_checklist.gone()

            }
        }
    }

    fun collapse(){
        if (expandableGroup.isExpanded) {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.icon_checklist.gone()
        }
    }

    override fun getLayout(): Int = R.layout.item_row_penanggung_jawab

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener

    }
}