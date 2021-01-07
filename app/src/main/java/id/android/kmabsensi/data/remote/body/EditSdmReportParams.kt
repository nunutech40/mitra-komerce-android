package id.android.kmabsensi.data.remote.body

data class EditSdmReportParams(
    val id: Int,
    val user_id: Int,
    val no_partner: String,
    val date: String,
    val total_leads: Int,
    val total_transaction: Int,
    val total_order: Int,
    val conversion_rate: Double,
    val order_rate: Double,
    var notes: String = ""
)