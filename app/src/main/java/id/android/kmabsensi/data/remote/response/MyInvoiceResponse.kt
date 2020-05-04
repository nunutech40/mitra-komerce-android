package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class MyInvoiceResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val invoices: List<Invoice> = listOf()
)

data class Invoice(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_requester")
    val userRequester: UserRequester = UserRequester(),
    @SerializedName("user_to")
    val userTo: UserTo = UserTo(),
    @SerializedName("invoice_type")
    val invoiceType: Int = 0,
    @SerializedName("invoice_period")
    val invoicePeriod: String = "",
    @SerializedName("invoice_xendit_id")
    val invoiceXenditId: String? = null,
    @SerializedName("invoice_xendit_url")
    val invoiceXenditUrl: String? = null,
    @SerializedName("invoice_xendit_expiry_date")
    val invoiceXenditExpiryDate: Any? = null,
    @SerializedName("expiry_date")
    val expiryDate: String? = null,
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
    @SerializedName("payment_attachment_url")
    val paymentAttachmentUrl: Any? = null,
    @SerializedName("is_using_payment_gateway")
    val isUsingPaymentGateway: Int = 0,
    @SerializedName("is_paid")
    val isPaid: Boolean = false,
    @SerializedName("is_lock")
    val isLock: Boolean = false,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
)

data class UserRequester(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = ""
)

data class UserTo(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = ""
)