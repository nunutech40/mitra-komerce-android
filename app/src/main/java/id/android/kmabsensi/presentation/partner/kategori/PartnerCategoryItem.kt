package id.android.kmabsensi.presentation.partner.kategori

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.PartnerCategory
import kotlinx.android.synthetic.main.item_row_kategori_partner.view.*

class PartnerCategoryItem(val partnerCategory: PartnerCategory,
                          val listener: (PartnerCategory) -> Unit): Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textCategoryName.text = partnerCategory.partnerCategoryName

        viewHolder.itemView.setOnClickListener {
            listener(partnerCategory)
        }
    }

    override fun getLayout() = R.layout.item_row_kategori_partner

}