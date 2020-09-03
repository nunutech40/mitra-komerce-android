package id.android.kmabsensi.data.remote.response


import com.google.gson.annotations.SerializedName

data class InvoiceReportDetailResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val data: MutableList<InvoiceReportDetail>? = null,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("status")
    val status: Boolean = false
)


data class InvoiceReportDetail(
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("position_name")
    val positionName: String = "",
    @SerializedName("total_partner")
    val totalPartner: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("total_invoice")
    val totalInvoice: Int = 0
)


