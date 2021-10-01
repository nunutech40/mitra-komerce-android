package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KomPartnerResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<KomPartnerItem>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class KomPartnerItem(

	@field:SerializedName("partner_id")
	val partnerId: Int? = null,

	@field:SerializedName("partner_name")
	val partnerName: String? = null
) : Parcelable
