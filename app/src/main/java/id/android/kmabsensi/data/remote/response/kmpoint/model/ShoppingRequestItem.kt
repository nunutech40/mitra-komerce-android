package id.android.kmabsensi.data.remote.response.kmpoint.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShoppingRequestItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("shopping_request_id")
        val shoppingRequestId: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("total")
        val total: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?
): Parcelable
