package id.android.kmabsensi.data.remote.body.komboard

data class AddressParams(
    val id_province: Int? = null
)
data class CityOngkir(
    val id_province: Int
)
data class subDistrictOngkir(
    val id_province: Int,
    val id_city: Int
)