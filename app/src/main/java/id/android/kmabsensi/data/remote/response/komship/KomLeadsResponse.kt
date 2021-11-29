package id.android.kmabsensi.data.remote.response.komship

import com.google.gson.annotations.SerializedName


data class KomLeadsResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: DataLeads? = null

)

data class DataLeads(

    @field:SerializedName("user_id")
    val user_id: Int? = null,

    @field:SerializedName("user_partner")
    val user_partner: String? = null,

    @field:SerializedName("notes")
    val notes: String? = null,

    @field:SerializedName("leads")
    val leads: List<LeadsItem>? = null,
)

data class LeadsItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("leads_time")
    val leads_time: String? = null,

    @field:SerializedName("date_leads")
    val date_leads: String? = null

)