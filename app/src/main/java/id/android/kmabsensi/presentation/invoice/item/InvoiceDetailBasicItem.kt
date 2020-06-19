package id.android.kmabsensi.presentation.invoice.item

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_invoice.view.*

data class InvoiceDetailBasic(
    val itemName: String,
    val itemPrice: Int,
    val itemDescription: String = ""
)

class InvoiceDetailBasicItem(private val invoiceDetail: InvoiceDetailBasic) : Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textItemName.text = invoiceDetail.itemName
            itemView.textItemPrice.text = convertRp(invoiceDetail.itemPrice.toDouble())
            itemView.textItemDescription.setTrimLines(0)
            if (invoiceDetail.itemDescription.isNotBlank()){
                itemView.textItemDescription.visible()
                itemView.textItemDescription.setTrimMode(0)
                itemView.textItemDescription.setTrimLines(2)
                itemView.textItemDescription.setColorClickableText(ContextCompat.getColor(itemView.context, R.color._2196F3))
                itemView.textItemDescription.text = invoiceDetail.itemDescription
            }

        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_invoice
    }

}