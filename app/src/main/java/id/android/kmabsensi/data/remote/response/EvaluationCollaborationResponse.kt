package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class EvaluationCollaborationResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<EvaluationCollaboration> = listOf()
)

@Parcelize
data class EvaluationCollaboration(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("user_management_id")
    val userManagementId: Int = 0,
    @SerializedName("reason_stop_collaboration")
    val reasonStopCollaboration: String = "",
    @SerializedName("improvement_suggestions")
    val improvementSuggestions: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("leader")
    val leader: Leader = Leader()
): Parcelable

@Parcelize
data class Leader(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("username")
    val username: String = "",
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = ""
): Parcelable