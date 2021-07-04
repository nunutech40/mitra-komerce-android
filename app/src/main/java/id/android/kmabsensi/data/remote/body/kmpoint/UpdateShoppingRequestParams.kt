package id.android.kmabsensi.data.remote.body.kmpoint

import com.google.gson.annotations.SerializedName

data class UpdateShoppingRequestParams(
    @SerializedName("notes")
    val notes: String = "",
    @SerializedName("items")
    val items: List<UpdateItem>,
    @SerializedName("participant_user_ids")
    val participant_user_ids: List<Int>,
    @SerializedName("status")
    val status: String
){
    data class UpdateItem(
        @SerializedName("description")
        val description: String? = "",
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("total")
        val total: Int,
        @SerializedName("will_delete")
        val will_delete: Boolean
    )
}