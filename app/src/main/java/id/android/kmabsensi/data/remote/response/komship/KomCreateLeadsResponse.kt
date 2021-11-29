package id.android.kmabsensi.data.remote.response.komship

data class KomCreateLeadsResponse(
    val status: String,
    val code: Int,
    val message: String,
    val `data`: DataLead
)
data class DataLead(
    val total_leads: Int
)