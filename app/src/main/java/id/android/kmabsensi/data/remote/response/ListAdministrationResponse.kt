package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class ListAdministrationResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val administrations: List<Administration> = listOf()
)

@Parcelize
data class Administration(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("leader")
    val leader: Leader = Leader(),
    @SerializedName("attachments")
    val attachments: List<Attachment> = listOf(),
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
):Parcelable{

    @Parcelize
    data class Leader(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("position_name")
        val positionName: String = ""
    ): Parcelable

    @Parcelize
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
    ): Parcelable
}

