package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class ListDeviceResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val devices: List<Device> = listOf()
)

data class Device(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("device_type")
    val deviceType: String = "",
    @SerializedName("brancd")
    val brancd: String = "",
    @SerializedName("spesification")
    val spesification: String = "",
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("partner")
    val partner: Partner = Partner(),
    @SerializedName("sdm")
    val sdm: Sdm = Sdm(),
    @SerializedName("attachments")
    val attachments: List<Attachment> = listOf(),
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
){
    data class Partner(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("no_partner")
        val noPartner: String = "",
        @SerializedName("username")
        val username: String = "",
        @SerializedName("full_name")
        val fullName: String = "",
        @SerializedName("email")
        val email: String = ""
    )

    data class Sdm(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("username")
        val username: String = "",
        @SerializedName("full_name")
        val fullName: String = "",
        @SerializedName("email")
        val email: String = ""
    )

    data class Attachment(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("reference_id")
        val referenceId: String = "",
        @SerializedName("attachment_path")
        val attachmentPath: String = "",
        @SerializedName("attachment_url")
        val attachmentUrl: String = "",
        @SerializedName("attachment_notes")
        val attachmentNotes: String = "",
        @SerializedName("attachment_type")
        val attachmentType: String = "",
        @SerializedName("created_at")
        val createdAt: String = "",
        @SerializedName("updated_at")
        val updatedAt: String = ""
    )
}



