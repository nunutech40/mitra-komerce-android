package id.android.kmabsensi.data.remote.body.komboard

data class CostOngkirParams(
    val origin: Int,
    val originType: String,
    val destination: Int,
    val destinationType: String,
    val weight: Int,
    val courier: String
)