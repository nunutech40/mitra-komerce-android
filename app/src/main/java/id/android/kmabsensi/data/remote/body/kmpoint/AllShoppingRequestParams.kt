package id.android.kmabsensi.data.remote.body.kmpoint

data class AllShoppingRequestParams(
        val page : Int? = 1,
        val limit : Int? = 10,
        val status : String? = "", // requested, approved, canceled, rejected
        val user_requester_id : Int? = null
)
