package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class ListCsPerformanceResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<CsPerformance> = listOf()
)

data class CsPerformance(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("total_leads")
    val totalLeads: Int = 0,
    @SerializedName("total_transaction")
    val totalTransaction: Int = 0,
    @SerializedName("total_order")
    val totalOrder: Int = 0,
    @SerializedName("conversion_rate")
    val conversionRate: Double = 0.0,
    @SerializedName("order_rate")
    val orderRate: Double = 0.0,
    @SerializedName("notes")
    val notes: Any? = null,
    @SerializedName("cs")
    val cs: Cs? = null,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
)

data class Cs(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("position_name")
    val positionName: String = ""
)