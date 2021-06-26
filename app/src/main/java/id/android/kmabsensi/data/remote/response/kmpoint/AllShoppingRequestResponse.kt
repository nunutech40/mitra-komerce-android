package id.android.kmabsensi.data.remote.response.kmpoint


import com.google.gson.annotations.SerializedName

data class AllShoppingRequestResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("success")
    val success: Boolean?
){
    data class Data(
            @SerializedName("data")
            val `data`: List<DataListShopping>?,
            @SerializedName("total")
            val total: Int?
    ){
        data class DataListShopping(
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
                    val positionName: String?
            )

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
            )
        }
    }
}