package id.android.kmabsensi.data.remote.body

data class AddNoteParams(
    val user_id : Int,
    val title: String,
    val description: String,
    val date: String = ""
)
