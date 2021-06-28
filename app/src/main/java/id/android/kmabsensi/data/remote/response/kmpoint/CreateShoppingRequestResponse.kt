package id.android.kmabsensi.data.remote.response.kmpoint


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateShoppingRequestResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("success")
    val success: Boolean?
): Parcelable {
    @Parcelize
    data class Data(
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("notes")
            val notes: String?,
            @SerializedName("partner")
            val partner: Partner?,
            @SerializedName("partner_id")
            val partnerId: Int?,
            @SerializedName("shopping_request_attachments")
            val shoopingRequestAttachments: List<DataAttachments>?,
            @SerializedName("shopping_request_items")
            val shoopingRequestItems: List<DetailShoppingResponse.Data.ShoopingRequestItem>?,
            @SerializedName("shopping_request_participants")
            val shoopingRequestParticipants: List<DetailShoppingResponse.Data.ShoopingRequestParticipant>?,
            @SerializedName("status")
            val status: String?,
            @SerializedName("total")
            val total: Int?,
            @SerializedName("transaction_no")
            val transactionNo: String?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("user_requester")
            val userRequester: UserRequester?,
            @SerializedName("user_requester_id")
            val userRequesterId: Int?
    ): Parcelable {

        @Parcelize
        data class DataAttachments(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("attachment_notes")
                val attachmentNotes: String,
                @SerializedName("attachment_url")
                val attachmentUrl: String,
                @SerializedName("attachment_type")
                val attachmentType: String,
                @SerializedName("reference_id")
                val referenceId: Int,
                @SerializedName("id")
                val id: Int,
                @SerializedName("updated_at")
                val updated_at: String
        ): Parcelable

        @Parcelize
        data class Partner(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("no_partner")
                val noPartner: String?,
                @SerializedName("full_name")
                val fullName: String?,
                @SerializedName("email")
                val email: String?,
                @SerializedName("photo_profile_url")
                val photoProfileUrl: String?
        ): Parcelable

        @Parcelize
        data class ShoopingRequestItem(
                @SerializedName("created_at")
                val createdAt: String?,
                @SerializedName("description")
                val description: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("shopping_request_id")
                val shoppingRequestId: Int?,
                @SerializedName("total")
                val total: Int?,
                @SerializedName("updated_at")
                val updatedAt: String?
        ): Parcelable

        @Parcelize
        data class ShoopingRequestParticipant(
                @SerializedName("created_at")
                val createdAt: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("shopping_request_id")
                val shoppingRequestId: Int?,
                @SerializedName("updated_at")
                val updatedAt: String?,
                @SerializedName("user")
                val user: User?,
                @SerializedName("user_id")
                val userId: Int?
        ): Parcelable

        @Parcelize
        data class UserRequester(
                @SerializedName("email")
                val email: String?,
                @SerializedName("full_name")
                val fullName: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("division_id")
                val divisionId: Int?,
                @SerializedName("division_name")
                val divisionName: String?,
                @SerializedName("position_id")
                val positionId: Int?,
                @SerializedName("position_name")
                val positionName: String?,
        ): Parcelable

        @Parcelize
        data class User(
                @SerializedName("email")
                val email: String?,
                @SerializedName("full_name")
                val fullName: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("photo_profile_url")
                val photoProfileUrl: String?,
                @SerializedName("position_name")
                val positionName: String?,
                @SerializedName("role_id")
                val roleId: Int?
        ): Parcelable
    }
}