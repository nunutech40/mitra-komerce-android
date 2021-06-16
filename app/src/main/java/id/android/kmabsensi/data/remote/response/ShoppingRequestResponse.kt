package id.android.kmabsensi.data.remote.response


import com.google.gson.annotations.SerializedName

data class ShoppingRequestResponse(
        @SerializedName("data")
    val `data`: Data?,
        @SerializedName("message")
    val message: String?,
        @SerializedName("success")
    val success: Boolean?
){

    data class Data(
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("notes")
            val notes: Any?,
            @SerializedName("partner")
            val partner: Partner?,
            @SerializedName("partner_id")
            val partnerId: Int?,
            @SerializedName("shooping_request_attachments")
            val shoopingRequestAttachments: List<Any>?,
            @SerializedName("shooping_request_items")
            val shoopingRequestItems: List<ShoopingRequestItem>?,
            @SerializedName("shooping_request_participants")
            val shoopingRequestParticipants: List<ShoopingRequestParticipant>?,
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
    ){
        data class Partner(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("no_partner")
                val noPartner: String?,
                @SerializedName("user")
                val user: User?,
                @SerializedName("user_id")
                val userId: Int?
        )

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
        )

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
                val user: UserX?,
                @SerializedName("user_id")
                val userId: Int?
        )

        data class User(
                @SerializedName("email")
                val email: String?,
                @SerializedName("full_name")
                val fullName: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("photo_profile_url")
                val photoProfileUrl: String?,
                @SerializedName("role_id")
                val roleId: Int?
        )

        data class UserRequester(
                @SerializedName("email")
                val email: String?,
                @SerializedName("full_name")
                val fullName: String?,
                @SerializedName("id")
                val id: Int?
        )


        data class UserX(
                @SerializedName("email")
                val email: String?,
                @SerializedName("full_name")
                val fullName: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("photo_profile_url")
                val photoProfileUrl: Any?,
                @SerializedName("role_id")
                val roleId: Int?
        )
    }
}