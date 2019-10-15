package id.android.kmabsensi.data.remote.response

data class ListPositionResponse(
    val code: Int,
    val `data`: List<Position>,
    val message: String,
    val status: Boolean
)

data class Position(
    val id: Int,
    val position_name: String
)

