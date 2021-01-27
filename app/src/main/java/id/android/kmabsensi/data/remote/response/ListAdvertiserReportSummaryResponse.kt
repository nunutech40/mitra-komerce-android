package id.android.kmabsensi.data.remote.response


import com.google.gson.annotations.SerializedName

data class ListAdvertiserReportSummaryResponse(
    @SerializedName("code")
    var code: Int = 0,
    @SerializedName("data")
    var data: AdvertiserReportSummary,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false
)


data class AdvertiserReportSummary(
    @SerializedName("ad_cost")
    var adCost: Int = 0,
    @SerializedName("cpr")
    var cpr: Int = 0,
    @SerializedName("total_view")
    var totalView: Int = 0,
    @SerializedName("ctr_link")
    var ctrLink: String = "",
    @SerializedName("total_visitor")
    var totalVisitor: Int = 0,
    @SerializedName("ratio_lp")
    var ratioLp: String = "",
    @SerializedName("total_leads_cs")
    var totalLeadsCs: Int = 0,
    @SerializedName("total_contact_click")
    var totalContactClick: Int = 0,
    @SerializedName("total_ad_click")
    var totalAdClick: Int = 0
)


