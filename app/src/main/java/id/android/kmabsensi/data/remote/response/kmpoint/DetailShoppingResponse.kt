package id.android.kmabsensi.data.remote.response.kmpoint


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.android.kmabsensi.data.remote.response.kmpoint.model.*
import kotlinx.android.parcel.Parcelize

data class DetailShoppingResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("status")
    val success: Boolean?
){
    @Parcelize
    data class Data(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("transaction_no")
            val transactionNo: String?,
            @SerializedName("total")
            val total: Int?,
            @SerializedName("notes")
            val notes: String?,
            @SerializedName("user_requester_id")
            val userRequesterId: Int?,
            @SerializedName("user_requester")
            val userRequester: UserRequester?,
            @SerializedName("partner_id")
            val partnerId: Int?,
            @SerializedName("partner")
            val partner: PartnerShoppingRequest?,
            @SerializedName("shopping_request_items")
            val shoopingRequestItems: List<ShoppingRequestItem>?,
            @SerializedName("shopping_request_participants")
            val shoopingRequestParticipants: List<ShoppingRequestParticipant>?,
            @SerializedName("shopping_request_attachments")
            val shoopingRequestAttachments: List<DataAttachments>?,
            @SerializedName("status")
            val status: String?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
    ): Parcelable
}