package id.android.kmabsensi.data.remote.response


import com.google.gson.annotations.SerializedName

data class SingleSdmShiftConfigResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val data: SdmConfig,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("status")
    val status: Boolean = false
)




