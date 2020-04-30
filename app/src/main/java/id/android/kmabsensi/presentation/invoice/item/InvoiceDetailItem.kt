package id.android.kmabsensi.presentation.invoice.item

import com.github.ajalt.timberkt.d
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.item_row_invoice_detail.*
import kotlinx.android.synthetic.main.item_row_invoice_detail.view.*

interface OnInvoiceDetailListener {
    fun onEditClicked(invoiceDetail: InvoiceDetail, position: Int)
    fun onCheckBoxClicked(invoiceDetail: InvoiceDetail, position: Int)
}

data class InvoiceDetail(
    val itemName: String,
    val itemPrice: Int,
    var selected: Boolean = false
)

class InvoiceDetailItem(private val invoiceDetail: InvoiceDetail,
                        private val listener: OnInvoiceDetailListener? = null): Item(){

    lateinit var viewHolder: ViewHolder

    override fun bind(viewHolder: ViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            itemView.textItemName.text = invoiceDetail.itemName
            itemView.textItemPrice.text = convertRp(invoiceDetail.itemPrice.toDouble())

            itemView.btnEdit.setOnClickListener {
                listener?.onEditClicked(invoiceDetail, position)
            }

            itemView.checkBoxDelete.setOnCheckedChangeListener { buttonView, isChecked ->
                invoiceDetail.selected = checkBoxDelete.isChecked
                d { invoiceDetail.selected.toString()}
                listener?.onCheckBoxClicked(invoiceDetail, position)
            }
        }
    }

    fun showDeleteSession(){
        viewHolder.itemView.checkBoxDelete.visible()
    }

    fun cancelDeleteSession(){
        viewHolder.itemView.checkBoxDelete.gone()
    }

    override fun getLayout() = R.layout.item_row_invoice_detail

}