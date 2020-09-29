package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class DashboardResponse(
    val code: Int,
    val `data`: Dashboard,
    val message: String,
    val status: Boolean
)

@Parcelize
data class Dashboard(
    val total_cssr: Int,
    val total_failed_present: Int,
    val total_holiday: Int,
    val total_not_present: Int,
    val total_not_yet_present: Int,
    val total_permission: Int,
    val total_present: Int,
    val total_sick: Int,
    val total_user: Int,
    val user_kmpoin: Int,
    val total_partner: Int,
    val partner_province_statistic: List<PartnerProvinceStatistic> = mutableListOf(),
    val partner_category_statistic: List<PartnerCategoryStatistic> = mutableListOf(),
    val work_config: List<WorkConfig> = listOf(),
    val holidays: List<Holiday> = listOf()
): Parcelable

@Parcelize
data class PartnerProvinceStatistic(
    @SerializedName("province_code")
    val provinceCode: String = "",
    @SerializedName("province_name")
    val provinceName: String = "",
    @SerializedName("total")
    val total: String = ""
):Parcelable

@Parcelize
data class PartnerCategoryStatistic(
    @SerializedName("partner_category_id")
    val partnerCategoryId: Int = 0,
    @SerializedName("partner_category_name")
    val partnerCategoryName: String = "",
    @SerializedName("total")
    val total: String = ""
):Parcelable