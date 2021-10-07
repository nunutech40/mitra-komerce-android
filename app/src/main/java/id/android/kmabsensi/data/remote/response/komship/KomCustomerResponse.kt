package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KomCustomerResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<CustomerItem>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class CustomerItem(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("tariff_code")
	val tariffCode: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("customer_id")
	val customerId: Int? = null,

	@field:SerializedName("zip_code")
	val zipCode: String? = null
) : Parcelable
