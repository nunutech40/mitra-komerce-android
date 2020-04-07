package id.android.kmabsensi.presentation.invoice

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.android.kmabsensi.R

class ActiveInvoiceItem(): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.item_row_active_invoice
}