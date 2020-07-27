package id.android.kmabsensi.presentation.sdm.productknowledge

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.ProductKnowledge
import kotlinx.android.synthetic.main.item_row_product_knowledge.view.*

class ProductKΩnowledgeItem(
    val productKnowledge: ProductKnowledge,
    val listener: (ProductKnowledge) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.txtProductKnowledge.text = productKnowledge.title
            itemView.setOnClickListener {
                listener(productKnowledge)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_product_knowledge
    }
}