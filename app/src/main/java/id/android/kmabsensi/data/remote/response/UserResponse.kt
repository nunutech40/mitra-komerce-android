package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserResponse(
    val code: Int,
    val `data`: List<User>,
    val message: String
):Parcelable

@Parcelize
data class User(
    val address: String,
    val birth_date: String,
    val created_at: String,
    val division_id: Int,
    val division_name: String,
    val email: String,
    val full_name: String,
    val gender: Int,
    val id: Int,
    val no_hp: String,
    val no_partner: String,
    val npk: String,
    val office_id: Int,
    val office_name: String,
    val origin_village: String,
    val photo_profile_url: String?,
    val position_id: Int,
    val position_name: String,
    val role_id: Int,
    val role_name: String?,
    val updated_at: String,
    val user_management_id: Int,
    val username: String,
    val kmpoin: Int
) : Parcelable