package id.android.kmabsensi.data.remote.response.kmpoint


import com.google.gson.annotations.SerializedName
import id.android.kmabsensi.data.remote.response.kmpoint.model.PartnerShoppingRequest
import id.android.kmabsensi.data.remote.response.kmpoint.model.UserRequester

data class AllShoppingRequestResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("status")
    val success: Boolean?
){
    data class Data(
            @SerializedName("data")
            val `data`: List<DataListShopping>?,
            @SerializedName("total")
            val total: Int?
    ){
        data class DataListShopping(
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
                @SerializedName("status")
                val status: String?,
                @SerializedName("created_at")
                val createdAt: String?,
                @SerializedName("updated_at")
                val updatedAt: String?
        )
    }
}