package id.android.kmabsensi.data.remote.body.kmpoint

data class RequestWithdrawParams(
    val user_id: Int,
    val transaction_type: String,
    val nominal: Int,
    val bank_name: String,
    val bank_no: String,
    val bank_owner_name: String,
    val notes: String
)