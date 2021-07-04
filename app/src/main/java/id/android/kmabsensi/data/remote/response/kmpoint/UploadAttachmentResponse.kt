package id.android.kmabsensi.data.remote.response.kmpoint

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadAttachmentResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: DataAttachment,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
): Parcelable{
    @Parcelize
    data class DataAttachment(
            @SerializedName("attachment_notes")
            val attachmentNotes: String,
            @SerializedName("attachment_type")
            val attachmentType: String,
            @SerializedName("attachment_url")
            val attachmentUrl: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("reference_id")
            val referenceId: Int,
            @SerializedName("updated_at")
            val updatedAt: String
    ): Parcelable
}