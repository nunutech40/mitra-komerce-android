package id.android.kmabsensi.presentation.sdm.productknowledge.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.ProductKnowledge
import kotlinx.android.synthetic.main.item_row_link.view.*

class LinkItem(
    val link: ProductKnowledge.AttachmentLink,
    val listener: (ProductKnowledge.AttachmentLink) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtLink.text = link.attachmentLink
            itemView.btnTelusuri.setOnClickListener {
                listener(link)
            }
            itemView.setOnClickListener {
                listener(link)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_link
    }
}