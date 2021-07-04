package id.android.kmabsensi.data.remote.body.kmpoint

data class CreateShoppingRequestParams(
    val items: List<Item>,
    val notes: String,
    val participant_user_ids: List<Int>,
    val partner_id: Int,
    val user_requester_id: Int
)

data class Item(
        val description: String,
        val name: String,
        val total: Int
)