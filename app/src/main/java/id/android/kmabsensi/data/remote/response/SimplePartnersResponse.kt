package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class SimplePartnersResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val partners: List<SimplePartner> = listOf()
)

@Parcelize
data class SimplePartner(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("partner")
    val partner: PartnerDetail = PartnerDetail()
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
    @SerializedName("bonus")
    val bonus: String = "",
    @SerializedName("status")
    val status: Int = 0
): Parcelable