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
    val created_at: String? = "",
    val division_id: Int,
    val division_name: String? = "",
    val email: String,
    val full_name: String,
    val gender: Int,
    val id: Int,
    val no_hp: String,
    val no_partner: String? = "",
    val no_partners: List<String>? = listOf(),
    val partner_assignments: List<PartnerAssignment>? = listOf(),
    val npk: String? = "",
    val office_id: Int,
    val office_name: String? = "",
    val origin_village: String?,
    val photo_profile_url: String? = "",
    val position_id: Int,
    val position_name: String,
    val role_id: Int,
    val role_name: String? = "",
    val updated_at: String? = "",
    val user_management_id: Int,
    val username: String,
    val kmpoin: Int,
    val status: Int,
    val join_date: String,
    val martial_status: Int,
    val last_date_of_pause: String,
    val bank_accounts: List<BankAccountsItem>? = listOf(),
    val management: ManagementResponse? = ManagementResponse(),
    var sdm_config: SdmConfig? = SdmConfig(),
    val talent: Talent? = Talent(),
    val staff: Staff? = Staff()
) : Parcelable


@Parcelize
data class BankAccountsItem(

    @field:SerializedName("bank_code")
    val bankCode: String? = null,

    @field:SerializedName("bank_owner_name")
    val bankOwnerName: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("bank_name")
    val bankName: String? = null,

    @field:SerializedName("id")
    val id: Int? = 0,

    @field:SerializedName("bank_no")
    val bankNo: String? = null,

    @field:SerializedName("is_default")
    val isDefault: Int? = null
) : Parcelable

@Parcelize
data class Talent(

    @field:SerializedName("education")
    val education: String? = null,

    @field:SerializedName("regency_id")
    val regencyId: String? = null,

    @field:SerializedName("has_work_experience")
    val hasWorkExperience: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: String? = null,

    @field:SerializedName("hired_at")
    val hiredAt: String? = null,

    @field:SerializedName("non_job_at")
    val nonJobAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("province_id")
    val provinceId: String? = null,

    @field:SerializedName("year_experience")
    val yearExperience: String? = null,

    @field:SerializedName("district")
    val district: District? = null,

    @field:SerializedName("id")
    val id: Int = 0,

    @field:SerializedName("district_id")
    val districtId: Int = 0,

    @field:SerializedName("status")
    val status: String? = null
): Parcelable


@Parcelize
data class District(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("regency")
    val regency: Regency? = null,

    @SerializedName("id")
    val id: Int? = 0
) : Parcelable

@Parcelize
data class Regency(

    @SerializedName("province")
    val province: Province? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = 0
) : Parcelable

@Parcelize
data class Province(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = 0
): Parcelable


@Parcelize
data class PartnerAssignment(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("position_name")
    val positionName: String? = "",
    @SerializedName("no_partner")
    val noPartner: String? = ""
): Parcelable

@Parcelize
data class Staff(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("district")
    val district: District? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int = 0,

    @field:SerializedName("district_id")
    val districtId: Int = 0,

    @field:SerializedName("deleted_at")
    val deletedAt: String? = null
) : Parcelable