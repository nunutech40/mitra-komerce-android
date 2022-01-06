package id.android.kmabsensi.data.remote.response.komship

import id.android.kmabsensi.data.remote.response.User

data class SingleUserResponse2(
    val success: Boolean,
    val `data`: User,
    val message: String
)
