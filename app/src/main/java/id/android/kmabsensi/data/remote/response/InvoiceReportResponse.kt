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
    val totalInvoice: Int = 0,
    @SerializedName("sum_of_invoice")
    val sumOfInvoice: Int = 0,
    @SerializedName("total_paid_invoice")
    val totalPaidInvoice: Int = 0,
    @SerializedName("sum_of_paid_invoice")
    val sumOfPaidInvoice: Int = 0,
    @SerializedName("total_unpaid_invoice")
    val totalUnpaidInvoice: Int = 0,
    @SerializedName("sum_of_unpaid_invoice")
    val sumOfUnpaidInvoice: Int = 0
)