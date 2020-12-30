package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName


@Parcelize
data class UserResponse(
    val code: Int,
    val status: Boolean,
    val `data`: List<User>,
    val message: String
) : Parcelable

@Parcelize
data class User(
    val address: String,
    val birth_date: String,
    val created_at: String?,
    val division_id: Int,
    val division_name: String,
    val email: String,
    val full_name: String,
    val gender: Int,
    val id: Int,
    val no_hp: String,
    val no_partner: String = "",
    val no_partners: List<String>,
    val partner_assignments: List<PartnerAssignment> = listOf(),
    val npk: String,
    val office_id: Int,
    val office_name: String,
    val origin_village: String,
    val photo_profile_url: String?,
    val position_id: Int,
    val position_name: String,
    val role_id: Int,
    val role_name: String?,
    val updated_at: String?,
    val user_management_id: Int,
    val username: String,
    val kmpoin: Int,
    val status: Int,
    val join_date: String,
    val martial_status: Int,
    val last_date_of_pause: String,
    val bank_accounts: List<BankAccount>? = listOf(),
    val management: ManagementResponse? = ManagementResponse(),
    var sdm_config: SdmConfig? = SdmConfig()
) : Parcelable

@Parcelize
data class PartnerAssignment(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("position_name")
    val positionName: String? = null,
    @SerializedName("no_partner")
    val noPartner: String = ""
): Parcelable

