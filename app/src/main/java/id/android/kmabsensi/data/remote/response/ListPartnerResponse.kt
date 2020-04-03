package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class ListPartnerResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val partners: List<Partner> = listOf()
)

@Parcelize
data class Partner(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("role_name")
    val roleName: String = "",
    @SerializedName("username")
    val username: String = "",
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("no_hp")
    val noHp: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("join_date")
    val joinDate: String = "",
    @SerializedName("martial_status")
    val martialStatus: Int = 0,
    @SerializedName("photo_profile_url")
    val photoProfileUrl: String? = null,
    @SerializedName("birth_date")
    val birthDate: String = "",
    @SerializedName("gender")
    val gender: Int = 0,
    @SerializedName("bank_accounts")
    val bankAccounts: List<BankAccount> = listOf(),
    @SerializedName("partner_detail")
    val partnerDetail: PartnerDetail = PartnerDetail(),
    @SerializedName("total_sdm_assigned")
    val totalSdmAssigned: Int = 0
): Parcelable

@Parcelize
data class BankAccount(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("bank_name")
    val bankName: String = "",
    @SerializedName("bank_no")
    val bankNo: String = "",
    @SerializedName("bank_owner_name")
    val bankOwnerName: String = ""
): Parcelable

@Parcelize
data class PartnerDetail(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("partner_category_id")
    val partnerCategoryId: Int = 0,
    @SerializedName("partner_category_name")
    val partnerCategoryName: String = "",
    @SerializedName("province_code")
    val provinceCode: String = "",
    @SerializedName("province_name")
    val provinceName: String = "",
    @SerializedName("city_code")
    val cityCode: String = "",
    @SerializedName("city_name")
    val cityName: String = "",
    @SerializedName("status")
    val status: Int = 0
): Parcelable