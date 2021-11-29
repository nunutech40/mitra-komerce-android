package id.android.kmabsensi.data.remote.response.komship

data class LeadsCountResponse(
    val code: Int,
    val `data`: Data,
    val status: String
)
data class Data(
    val total_leads: Int
)