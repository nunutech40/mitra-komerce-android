package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class CreateInvoiceResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val invoiceData: InvoiceData = InvoiceData()
)

data class InvoiceData(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_requester_id")
    val userRequesterId: Int = 0,
    @SerializedName("user_to_id")
    val userToId: Int = 0,
    @SerializedName("invoice_type")
    val invoiceType: Int = 0,
    @SerializedName("invoice_xendit_id")
    val invoiceXenditId: Any? = null,
    @SerializedName("invoice_xendit_url")
    val invoiceXenditUrl: Any? = null,
    @SerializedName("invoice_xendit_expiry_date")
    val invoiceXenditExpiryDate: Any? = null,
    @SerializedName("expiry_date")
    val expiryDate: Any? = null,
    @SerializedName("invoice_km_id")
    val invoiceKmId: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("currency")
    val currency: String = "",
    @SerializedName("amount")
    val amount: String = "",
    @SerializedName("is_using_payment_gateway")
    val isUsingPaymentGateway: Int = 0,
    @SerializedName("is_paid")
    val isPaid: Int = 0,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
)