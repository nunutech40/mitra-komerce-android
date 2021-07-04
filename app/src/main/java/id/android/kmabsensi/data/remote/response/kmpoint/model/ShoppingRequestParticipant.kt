package id.android.kmabsensi.data.remote.response.kmpoint.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShoppingRequestParticipant(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("shopping_request_id")
    val shoppingRequestId: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("user_id")
    val userId: Int
): Parcelable {
    @Parcelize
    data class User(
            @SerializedName("email")
            val email: String,
            @SerializedName("full_name")
            val fullName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("photo_profile_url")
            val photoProfileUrl: String?,
            @SerializedName("position_name")
            val positionName: String,
            @SerializedName("role_id")
            val roleId: Int
    ): Parcelable
}