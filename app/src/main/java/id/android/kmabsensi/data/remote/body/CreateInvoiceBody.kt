package id.android.kmabsensi.data.remote.body

data class CreateInvoiceBody(
    val user_requester_id: Int,
    val user_to_id: Int,
    val title: String,
    val description: String,
    val invoice_type: Int,
    val items: List<InvoiceItem>
)

data class InvoiceItem(
    val item: String,
    val total: Int
)