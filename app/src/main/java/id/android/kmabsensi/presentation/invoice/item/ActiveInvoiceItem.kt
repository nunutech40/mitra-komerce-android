package id.android.kmabsensi.presentation.invoice.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R

class ActiveInvoiceItem(val listener: () -> Unit): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            listener()
        }
    }

    override fun getLayout() = R.layout.item_row_active_invoice
}