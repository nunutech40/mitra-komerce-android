package id.android.kmabsensi.data.remote.response.kmpoint.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PartnerShoppingRequest(
         @SerializedName("id")
         val id: Int?,
         @SerializedName("user_id")
         val userId: Int?,
         @SerializedName("no_partner")
         val noPartner: String?,
         @SerializedName("user")
         val user: PartnerDetail?
): Parcelable{

    @Parcelize
    data class PartnerDetail(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("role_id")
            val roleId: Int?,
            @SerializedName("full_name")
            val fullName: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("position_name")
            val positionName: String?,
            @SerializedName("photo_profile_url")
            val photoProfileUrl: String?
    ): Parcelable
}