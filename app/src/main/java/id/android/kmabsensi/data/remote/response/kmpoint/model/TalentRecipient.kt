package id.android.kmabsensi.data.remote.response.kmpoint.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TalentRecipient(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("km_poin_withdrawal_request_id")
        val kmPoinWithdrawalRequestId: Int,
        @SerializedName("nominal")
        val nominal: Int,
        @SerializedName("talent")
        val talent: Talent,
        @SerializedName("talent_id")
        val talentId: Int,
        @SerializedName("updated_at")
        val updatedAt: String
): Parcelable {

    @Parcelize
    data class Talent(
            @SerializedName("id")
            val id: Int,
            @SerializedName("user")
            val user: UserRecipient,
            @SerializedName("user_id")
            val userId: Int
    ): Parcelable {

        @Parcelize
        data class UserRecipient(
                @SerializedName("email")
                val email: String,
                @SerializedName("full_name")
                val fullName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("photo_profile_url")
                val photoProfileUrl: String,
                @SerializedName("position_name")
                val positionName: String,
                @SerializedName("role_id")
                val roleId: Int
        ): Parcelable
    }

}
