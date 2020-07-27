package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class ListProductKnowledgeResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<ProductKnowledge> = listOf()
)

@Parcelize
data class ProductKnowledge(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("faq")
    val faq: String = "",
    @SerializedName("partner")
    val partner: Partner? = Partner(),
    @SerializedName("attachment_files")
    val attachmentFiles: List<AttachmentFile> = listOf(),
    @SerializedName("attachment_links")
    val attachmentLinks: List<AttachmentLink> = listOf(),
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
): Parcelable{

    @Parcelize
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
    ):Parcelable

    @Parcelize
    data class AttachmentFile(
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

    @Parcelize
    data class AttachmentLink(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("reference_id")
        val referenceId: String = "",
        @SerializedName("attachment_link")
        val attachmentLink: String = "",
        @SerializedName("attachment_notes")
        val attachmentNotes: String = "",
        @SerializedName("created_at")
        val createdAt: String = "",
        @SerializedName("updated_at")
        val updatedAt: String = ""
    ): Parcelable
}

