package id.android.kmabsensi.data.remote.response.invoice
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.android.kmabsensi.data.remote.response.BankAccount
import kotlinx.android.parcel.Parcelize


data class InvoiceDetailResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val invoice: InvoiceDetail = InvoiceDetail()
)

@Parcelize
data class InvoiceDetail(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_requester")
    val userRequester: UserRequester = UserRequester(),
    @SerializedName("user_to")
    val userTo: UserTo = UserTo(),
    @SerializedName("invoice_type")
    val invoiceType: Int = 0,
    @SerializedName("invoice_xendit_id")
    val invoiceXenditId: String? = "",
    @SerializedName("invoice_xendit_url")
    val invoiceXenditUrl: String? = "",
    @SerializedName("invoice_xendit_expiry_date")
    val invoiceXenditExpiryDate: String? = null,
    @SerializedName("expiry_date")
    val expiryDate: String? = "",
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
    val isPaid: Boolean = false,
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("invoice_detail_admin")
    val invoiceDetailAdmin: List<InvoiceDetailAdmin>? = null,
    @SerializedName("invoice_detail_gaji")
    val invoiceDetailGaji: List<InvoiceDetailGaji>? = null,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
): Parcelable

@Parcelize
data class InvoiceDetailAdmin(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("invoice_id")
    val invoiceId: Int = 0,
    @SerializedName("item")
    val item: String = "",
    @SerializedName("total")
    val total: String = "",
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("payment_attachment_url")
    val paymentAttachmentUrl: String? = null
): Parcelable

@Parcelize
data class InvoiceDetailGaji(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("invoice_id")
    val invoiceId: Int = 0,
    @SerializedName("item")
    val item: String = "",
    @SerializedName("total")
    val total: String = "",
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("payment_attachment_url")
    var paymentAttachmentUrl: String? = null,
    @SerializedName("bank_account")
    val bankAccount: List<BankAccount>? = null
): Parcelable