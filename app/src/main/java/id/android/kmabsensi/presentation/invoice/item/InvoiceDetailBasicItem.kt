package id.android.kmabsensi.presentation.invoice.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.convertRp
import kotlinx.android.synthetic.main.item_row_invoice.view.*

data class InvoiceDetailBasic(
    val itemName: String,
    val itemPrice: Int
)

class InvoiceDetailBasicItem(private val invoiceDetail: InvoiceDetailBasic) : Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.textItemName.text = invoiceDetail.itemName
            itemView.textItemPrice.text = convertRp(invoiceDetail.itemPrice.toDouble())
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_invoice
    }

}