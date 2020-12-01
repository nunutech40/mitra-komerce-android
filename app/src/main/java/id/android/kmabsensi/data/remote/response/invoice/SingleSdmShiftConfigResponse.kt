package id.android.kmabsensi.data.remote.response.invoice


import com.google.gson.annotations.SerializedName

data class SingleSdmShiftConfigResponse(@SerializedName("code")
                                        val code: Int = 0,
                                        @SerializedName("data")
                                        val data: Data,
                                        @SerializedName("message")
                                        val message: String = "",
                                        @SerializedName("status")
                                        val status: Boolean = false)


data class Data(@SerializedName("shift_mode")
                val shiftMode: String = "",
                @SerializedName("user_id")
                val userId: Int = 0,
                @SerializedName("id")
                val id: Int = 0)


