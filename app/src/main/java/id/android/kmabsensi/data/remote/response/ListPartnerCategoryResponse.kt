package id.android.kmabsensi.data.remote.response

import com.google.gson.annotations.SerializedName

data class ListPartnerCategoryResponse(
    val code: Int = 0,
    @SerializedName("data")
    val categories: List<PartnerCategory> = mutableListOf(),
    val message: String = "",
    val status: Boolean
)

data class PartnerCategory(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("partner_category_name")
    val partnerCategoryName: String = ""
)