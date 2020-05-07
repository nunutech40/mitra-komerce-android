package id.android.kmabsensi.data.remote.body

import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail

data class CreateInvoiceBody(
    val user_requester_id: Int,
    val user_to_id: Int,
    val title: String,
    val description: String,
    val invoice_type: Int,
    val invoice_period: String,
    val items: List<InvoiceItem>
)

data class InvoiceItem(
    val item: String,
    val description: String,
    val total: Int
){
    companion object {
        fun from(data: InvoiceDetail) = InvoiceItem(
            data.itemName,
            data.itemDescription,
            data.itemPrice
        )
    }
}