package id.android.kmabsensi.data.remote.response.kmpoint

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GetWithdrawResponse(
    @SerializedName("data")
    val `data`: DataWithDraw,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
): Parcelable {
    @Parcelize
    data class DataWithDraw(
            @SerializedName("data")
            val `data`: List<DataDetailWithDraw>,
            @SerializedName("total")
            val total: Int
    ): Parcelable {
        @Parcelize
        data class DataDetailWithDraw(
                @SerializedName("attachments")
                val attachments: List<DataAttachments>,
                @SerializedName("bank_name")
                val bankName: String,
                @SerializedName("bank_no")
                val bankNo: String,
                @SerializedName("bank_owner_name")
                val bankOwnerName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("nominal")
                val nominal: Int,
                @SerializedName("notes")
                val notes: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("transaction_type")
                val transactionType: String,
                @SerializedName("user")
                val user: User,
                @SerializedName("user_id")
                val userId: Int
        ): Parcelable {

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

            @Parcelize
            data class User(
                    @SerializedName("address")
                    val address: String,
                    @SerializedName("bank_accounts")
                    val bankAccounts: List<BankAccount>,
                    @SerializedName("birth_date")
                    val birthDate: String,
                    @SerializedName("division_id")
                    val divisionId: Int,
                    @SerializedName("division_name")
                    val divisionName: String,
                    @SerializedName("email")
                    val email: String,
                    @SerializedName("full_name")
                    val fullName: String,
                    @SerializedName("gender")
                    val gender: Int,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("join_date")
                    val joinDate: String,
                    @SerializedName("kmpoin")
                    val kmpoin: Int,
                    @SerializedName("last_date_of_pause")
                    val lastDateOfPause: String,
                    @SerializedName("management")
                    val management: Management,
                    @SerializedName("martial_status")
                    val martialStatus: Int,
                    @SerializedName("no_hp")
                    val noHp: String,
                    @SerializedName("npk")
                    val npk: String,
                    @SerializedName("office_id")
                    val officeId: Int,
                    @SerializedName("office_name")
                    val officeName: String,
                    @SerializedName("origin_village")
                    val originVillage: String,
                    @SerializedName("photo_profile_url")
                    val photoProfileUrl: String,
                    @SerializedName("position_id")
                    val positionId: Int,
                    @SerializedName("position_name")
                    val positionName: String,
                    @SerializedName("role_id")
                    val roleId: Int,
                    @SerializedName("role_name")
                    val roleName: String,
                    @SerializedName("sdm_config")
                    val sdmConfig: String,
                    @SerializedName("status")
                    val status: Int,
                    @SerializedName("total_device")
                    val totalDevice: Int,
                    @SerializedName("user_management_id")
                    val userManagementId: Int,
                    @SerializedName("username")
                    val username: String
            ): Parcelable {

                @Parcelize
                data class Management(
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("full_name")
                        val fullName: String,
                        @SerializedName("email")
                        val email: String,
                        @SerializedName("role_id")
                        val roleId: String,
                        @SerializedName("position_name")
                        val positionName: String,
                        @SerializedName("office_name")
                        val officeName: String,
                        @SerializedName("no_hp")
                        val noNp: String,
                        @SerializedName("address")
                        val address: String
                ): Parcelable

                @Parcelize
                data class BankAccount(
                        @SerializedName("bank_name")
                        val bankName: String,
                        @SerializedName("bank_no")
                        val bankNo: String,
                        @SerializedName("bank_owner_name")
                        val bankOwnerName: String,
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("user_id")
                        val userId: Int
                ): Parcelable
            }
        }
    }
}