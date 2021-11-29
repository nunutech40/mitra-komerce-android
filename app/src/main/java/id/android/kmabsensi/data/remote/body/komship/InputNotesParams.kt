package id.android.kmabsensi.data.remote.body.komship

data class InputNotesParams(
    val idUser: Int,
    val idPartner: Int,
    val notes: String,
    val dateAllLeads: String
)
