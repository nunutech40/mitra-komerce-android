package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class InvoiceReportResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val invoiceReport: InvoiceReport = InvoiceReport()
)

data class InvoiceReport(
    @SerializedName("total_invoice")
    val totalInvoice: Long = 0,
    @SerializedName("sum_of_invoice")
    val sumOfInvoice: Long = 0,
    @SerializedName("total_paid_invoice")
    val totalPaidInvoice: Long = 0,
    @SerializedName("sum_of_paid_invoice")
    val sumOfPaidInvoice: Long = 0,
    @SerializedName("total_unpaid_invoice")
    val totalUnpaidInvoice: Long = 0,
    @SerializedName("sum_of_unpaid_invoice")
    val sumOfUnpaidInvoice: Long = 0
)