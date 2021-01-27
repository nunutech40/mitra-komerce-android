package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName

data class ListNoteResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<Note> = listOf()
)

data class Note(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("user")
    val user: User = User()
){
    data class User(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("full_name")
        val fullName: String = "",
        @SerializedName("email")
        val email: String = ""
    )
}


