package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllBankResponse(
	val code: Int?,
	val data: List<DataBank>,
	val message: String?,
	val status: Boolean
) : Parcelable

@Parcelize
data class DataBank(
	val code: String? = "",
	val canNameValidate: Boolean? = false,
	val name: String? = "",
	val canDisburse: Boolean? = false
) : Parcelable
