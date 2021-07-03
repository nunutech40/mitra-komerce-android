package id.android.kmabsensi.data.remote.response.kmpoint

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.android.kmabsensi.data.remote.response.kmpoint.model.DataAttachments
import id.android.kmabsensi.data.remote.response.kmpoint.model.TalentRecipient
import id.android.kmabsensi.data.remote.response.kmpoint.model.UserWithdrawal
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateWithdrawResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val success: Boolean
) : Parcelable {
    @Parcelize
    data class Data(
            @SerializedName("id")
            val id: Int,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("transaction_type")
            val transactionType: String,
            @SerializedName("nominal")
            val nominal: Int,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("bank_no")
            val bankNo: String,
            @SerializedName("bank_owner_name")
            val bankOwnerName: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("notes")
            val notes: String,
            @SerializedName("user")
            val user: UserWithdrawal,
            @SerializedName("talent_recipients")
            val talentRecipients: List<TalentRecipient>,
            @SerializedName("attachments")
            val attachments: List<DataAttachments>,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
    ) : Parcelable
}