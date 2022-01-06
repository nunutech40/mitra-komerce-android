//package id.android.kmabsensi.utils.komboard
//
//import android.os.Parcelable
//import com.google.gson.annotations.SerializedName
//import id.android.kmabsensi.data.remote.response.ManagementResponse
//import kotlinx.android.parcel.Parcelize
//
//@Parcelize
//data class ResponseUserNew(
//
//	@field:SerializedName("data")
//	val data: List<DataItem?>? = null,
//
//	@field:SerializedName("message")
//	val message: String? = null,
//
//	@field:SerializedName("status")
//	val status: Boolean? = null
//) : Parcelable
//
//@Parcelize
//data class LastJob(
//
//	@field:SerializedName("rating")
//	val rating: String? = null,
//
//	@field:SerializedName("created_at")
//	val createdAt: String? = null,
//
//	@field:SerializedName("deleted_at")
//	val deletedAt: String? = null,
//
//	@field:SerializedName("office_id")
//	val officeId: Int? = null,
//
//	@field:SerializedName("partner_id")
//	val partnerId: Int? = null,
//
//	@field:SerializedName("updated_at")
//	val updatedAt: String? = null,
//
//	@field:SerializedName("partner")
//	val partner: Partner? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("position")
//	val position: Position? = null,
//
//	@field:SerializedName("team_lead_id")
//	val teamLeadId: Int? = null,
//
//	@field:SerializedName("talent_id")
//	val talentId: Int? = null,
//
//	@field:SerializedName("team_lead")
//	val teamLead: TeamLead? = null,
//
//	@field:SerializedName("position_id")
//	val positionId: Int? = null
//): Parcelable
//
//@Parcelize
//data class TeamLead(
//
//	@field:SerializedName("photo_profile_url")
//	val photoProfileUrl: Any? = null,
//
//	@field:SerializedName("no_hp")
//	val noHp: String? = null,
//
//	@field:SerializedName("gender")
//	val gender: Int? = null,
//
//	@field:SerializedName("birth_date")
//	val birthDate: String? = null,
//
//	@field:SerializedName("martial_status")
//	val martialStatus: Int? = null,
//
//	@field:SerializedName("edumo_token")
//	val edumoToken: String? = null,
//
//	@field:SerializedName("kmpoin")
//	val kmpoin: Int? = null,
//
//	@field:SerializedName("office_id")
//	val officeId: Int? = null,
//
//	@field:SerializedName("nik")
//	val nik: Any? = null,
//
//	@field:SerializedName("pin")
//	val pin: String? = null,
//
//	@field:SerializedName("role_id")
//	val roleId: Int? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("email")
//	val email: String? = null,
//
//	@field:SerializedName("user_management_id")
//	val userManagementId: Int? = null,
//
//	@field:SerializedName("address")
//	val address: String? = null,
//
//	@field:SerializedName("join_date")
//	val joinDate: String? = null,
//
//	@field:SerializedName("is_owner")
//	val isOwner: Int? = null,
//
//	@field:SerializedName("npk")
//	val npk: String? = null,
//
//	@field:SerializedName("position_name")
//	val positionName: String? = null,
//
//	@field:SerializedName("division_id")
//	val divisionId: Int? = null,
//
//	@field:SerializedName("email_verified_at")
//	val emailVerifiedAt: Any? = null,
//
//	@field:SerializedName("is_komplace")
//	val isKomplace: Int? = null,
//
//	@field:SerializedName("deleted_at")
//	val deletedAt: Any? = null,
//
//	@field:SerializedName("last_date_of_pause")
//	val lastDateOfPause: String? = null,
//
//	@field:SerializedName("new_kmpoin")
//	val newKmpoin: Int? = null,
//
//	@field:SerializedName("office_name")
//	val officeName: String? = null,
//
//	@field:SerializedName("full_name")
//	val fullName: String? = null,
//
//	@field:SerializedName("is_komship")
//	val isKomship: Int? = null,
//
//	@field:SerializedName("division_name")
//	val divisionName: String? = null,
//
//	@field:SerializedName("origin_village")
//	val originVillage: String? = null,
//
//	@field:SerializedName("is_onboarding")
//	val isOnboarding: Int? = null,
//
//	@field:SerializedName("username")
//	val username: String? = null,
//
//	@field:SerializedName("status")
//	val status: Int? = null,
//
//	@field:SerializedName("position_id")
//	val positionId: Int? = null
//)
//
//@Parcelize
//data class Position(
//
//	@field:SerializedName("position_name")
//	val positionName: String? = null,
//
//	@field:SerializedName("division_id")
//	val divisionId: Int? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null
//) : Parcelable
//
//@Parcelize
//data class Province(
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null
//): Parcelable
//
//@Parcelize
//data class District(
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("regency")
//	val regency: Regency? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null
//) : Parcelable
//
//@Parcelize
//data class Partner(
//
//	@field:SerializedName("business_logo")
//	val businessLogo: String? = null,
//
//	@field:SerializedName("partner_category_id")
//	val partnerCategoryId: Int? = null,
//
//	@field:SerializedName("partner_category_name")
//	val partnerCategoryName: String? = null,
//
//	@field:SerializedName("bonus")
//	val bonus: String? = null,
//
//	@field:SerializedName("city_code")
//	val cityCode: String? = null,
//
//	@field:SerializedName("account_status")
//	val accountStatus: String? = null,
//
//	@field:SerializedName("province_code")
//	val provinceCode: String? = null,
//
//	@field:SerializedName("no_partner")
//	val noPartner: String? = null,
//
//	@field:SerializedName("reference")
//	val reference: String? = null,
//
//	@field:SerializedName("city_name")
//	val cityName: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("business_location")
//	val businessLocation: String? = null,
//
//	@field:SerializedName("brand_name")
//	val brandName: String? = null,
//
//	@field:SerializedName("province_name")
//	val provinceName: String? = null,
//
//	@field:SerializedName("pic_name")
//	val picName: String? = null,
//
//	@field:SerializedName("off_at")
//	val offAt: String? = null,
//
//	@field:SerializedName("user_id")
//	val userId: Int? = 0,
//
//	@field:SerializedName("pic_phone")
//	val picPhone: String? = null,
//
//	@field:SerializedName("pause_at")
//	val pauseAt: String? = null,
//
//	@field:SerializedName("team_members")
//	val teamMembers: Int? = 0,
//
//	@field:SerializedName("district_id")
//	val districtId: Int? = 0,
//
//	@field:SerializedName("active_at")
//	val activeAt: String? = null,
//
//	@field:SerializedName("postal_code")
//	val postalCode: Int? = 0,
//
//	@field:SerializedName("business_type_id")
//	val businessTypeId: Int? = 0,
//
//	@field:SerializedName("status")
//	val status: Int? = null
//): Parcelable
//
//@Parcelize
//data class Talent(
//
//	@field:SerializedName("education")
//	val education: String? = null,
//
//	@field:SerializedName("regency_id")
//	val regencyId: String? = null,
//
//	@field:SerializedName("has_work_experience")
//	val hasWorkExperience: Int? = null,
//
//	@field:SerializedName("created_at")
//	val createdAt: String? = null,
//
//	@field:SerializedName("deleted_at")
//	val deletedAt: String? = null,
//
//	@field:SerializedName("hired_at")
//	val hiredAt: String? = null,
//
//	@field:SerializedName("non_job_at")
//	val nonJobAt: String? = null,
//
//	@field:SerializedName("updated_at")
//	val updatedAt: String? = null,
//
//	@field:SerializedName("user_id")
//	val userId: Int? = null,
//
//	@field:SerializedName("province_id")
//	val provinceId: String? = null,
//
//	@field:SerializedName("year_experience")
//	val yearExperience: String? = null,
//
//	@field:SerializedName("district")
//	val district: District? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("district_id")
//	val districtId: Int? = null,
//
//	@field:SerializedName("last_job")
//	val lastJob: LastJob? = null,
//
//	@field:SerializedName("status")
//	val status: String? = null
//): Parcelable
//
//@Parcelize
//data class SdmConfig(
//
//	@field:SerializedName("shift_mode")
//	val shiftMode: String? = null,
//
//	@field:SerializedName("user_id")
//	val userId: Int? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null
//): Parcelable
//
//@Parcelize
//data class BankAccountsItem(
//
//	@field:SerializedName("bank_code")
//	val bankCode: String? = null,
//
//	@field:SerializedName("bank_owner_name")
//	val bankOwnerName: String? = null,
//
//	@field:SerializedName("user_id")
//	val userId: Int? = null,
//
//	@field:SerializedName("bank_name")
//	val bankName: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("bank_no")
//	val bankNo: String? = null,
//
//	@field:SerializedName("is_default")
//	val isDefault: Int? = null
//) : Parcelable
//
//@Parcelize
//data class PartnerAssignmentsItem(
//
//	@field:SerializedName("full_name")
//	val fullName: String? = null,
//
//	@field:SerializedName("role_id")
//	val roleId: Int? = null,
//
//	@field:SerializedName("position_name")
//	val positionName: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("no_partner")
//	val noPartner: String? = null,
//
//	@field:SerializedName("email")
//	val email: String? = null
//) : Parcelable
//
//@Parcelize
//data class Regency(
//
//	@field:SerializedName("province")
//	val province: Province? = null,
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null
//) : Parcelable
//
//@Parcelize
//data class DataItem(
//
//	@field:SerializedName("photo_profile_url")
//	val photoProfileUrl: String? = null,
//
//	@field:SerializedName("no_hp")
//	val noHp: String? = null,
//
//	@field:SerializedName("gender")
//	val gender: Int? = null,
//
//	@field:SerializedName("total_device")
//	val totalDevice: Int? = null,
//
//	@field:SerializedName("birth_date")
//	val birthDate: String? = null,
//
//	@field:SerializedName("martial_status")
//	val martialStatus: Int? = null,
//
//	@field:SerializedName("no_partners")
//	val noPartners: List<String?>? = null,
//
//	@field:SerializedName("talent_rating")
//	val talentRating: Int? = null,
//
//	@field:SerializedName("no_partner")
//	val noPartner: String? = null,
//
//	@field:SerializedName("kmpoin")
//	val kmpoin: Int? = null,
//
//	@field:SerializedName("office_id")
//	val officeId: Int? = null,
//
//	@field:SerializedName("role_id")
//	val roleId: Int? = null,
//
//	@field:SerializedName("talent")
//	val talent: Talent? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("email")
//	val email: String? = null,
//
//	@field:SerializedName("user_management_id")
//	val userManagementId: Int? = null,
//
//	@field:SerializedName("address")
//	val address: String? = null,
//
//	@field:SerializedName("join_date")
//	val joinDate: String? = null,
//
//	@field:SerializedName("npk")
//	val npk: String? = null,
//
//	@field:SerializedName("position_name")
//	val positionName: String? = null,
//
//	@field:SerializedName("division_id")
//	val divisionId: Int? = null,
//
//	@field:SerializedName("sdm_config")
//	val sdmConfig: SdmConfig? = null,
//
//	@field:SerializedName("partner_assignments")
//	val partnerAssignments: List<PartnerAssignmentsItem?>? = null,
//
//	@field:SerializedName("last_date_of_pause")
//	val lastDateOfPause: String? = null,
//
//	@field:SerializedName("role_name")
//	val roleName: String? = null,
//
//	@field:SerializedName("office_name")
//	val officeName: String? = null,
//
//	@field:SerializedName("full_name")
//	val fullName: String? = null,
//
//	@field:SerializedName("management")
//	val management: ManagementResponse? = null,
//
//	@field:SerializedName("division_name")
//	val divisionName: String? = null,
//
//	@field:SerializedName("origin_village")
//	val originVillage: String? = null,
//
//	@field:SerializedName("bank_accounts")
//	val bankAccounts: List<BankAccountsItem?>? = null,
//
//	@field:SerializedName("username")
//	val username: String? = null,
//
//	@field:SerializedName("status")
//	val status: Int? = null,
//
//	@field:SerializedName("position_id")
//	val positionId: Int? = null
//) : Parcelable
