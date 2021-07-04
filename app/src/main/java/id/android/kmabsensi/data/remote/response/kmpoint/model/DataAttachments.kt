package id.android.kmabsensi.data.remote.response.kmpoint.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataAttachments(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("attachment_notes")
        val attachmentNotes: String?,
        @SerializedName("attachment_url")
        val attachmentUrl: String?,
        @SerializedName("attachment_type")
        val attachmentType: String?,
        @SerializedName("reference_id")
        val referenceId: Int?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("updated_at")
        val updated_at: String?
): Parcelable
