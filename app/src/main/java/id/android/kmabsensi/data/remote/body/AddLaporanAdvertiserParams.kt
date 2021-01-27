package id.android.kmabsensi.data.remote.body

data class AddAdvertiserReportParams(
    val user_id: Int,
    val no_partner: String,
    val date: String,
    val platform_type: Int,
    val total_view: Int,
    val total_ad_click: Int,
    val total_visitor: Int,
    val total_contact_click: Int,
    val total_leads_cs: Int,
    val ad_cost: Long,
    val ctr_link: Double,
    val ratio_lp: Double,
    val cpr: Long,
    val notes: String
)

data class EditAdvertiserReportParams(
    val id: Int,
    val user_id: Int,
    val no_partner: String,
    val date: String,
    val platform_type: Int,
    val total_view: Int,
    val total_ad_click: Int,
    val total_visitor: Int,
    val total_contact_click: Int,
    val total_leads_cs: Int,
    val ad_cost: Long,
    val ctr_link: Double,
    val ratio_lp: Double,
    val cpr: Long,
    val notes: String
)