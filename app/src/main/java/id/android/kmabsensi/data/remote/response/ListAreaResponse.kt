package id.android.kmabsensi.data.remote.response

import com.google.gson.annotations.SerializedName

class ListAreaResponse(
    val status: Boolean,
    val code: Int,
    val data:
)

data class DataAreaResponse(
    val provinces:
)

data class Province(
    @SerializedName("KODE_WILAYAH")
    val kodeWilayah: String,
    @SerializedName("")
)

