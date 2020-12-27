package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class ListAdvertiserReportResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<AdvertiserReport> = listOf()
)

@Parcelize
data class AdvertiserReport(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("platform_type")
    val platformType: Int = 0,
    @SerializedName("total_view")
    val totalView: Int = 0,
    @SerializedName("total_ad_click")
    val totalAdClick: Int = 0,
    @SerializedName("total_visitor")
    val totalVisitor: Int = 0,
    @SerializedName("total_contact_click")
    val totalContactClick: Int = 0,
    @SerializedName("total_leads_cs")
    val totalLeadsCs: Int = 0,
    @SerializedName("ad_cost")
    val adCost: String = "",
    @SerializedName("ctr_link")
    val ctrLink: String = "",
    @SerializedName("ratio_lp")
    val ratioLp: String = "",
    @SerializedName("cpr")
    val cpr: String = "",
    @SerializedName("notes")
    val notes: String? = null,
    @SerializedName("advertiser")
    val advertiser: Advertiser? = null
): Parcelable

@Parcelize
data class Advertiser(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("position_name")
    val positionName: String = ""
): Parcelable