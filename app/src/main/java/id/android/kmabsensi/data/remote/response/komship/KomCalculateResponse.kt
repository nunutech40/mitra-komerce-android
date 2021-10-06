package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KomCalculateResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<CalculateItem>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class CalculateItem(

	@field:SerializedName("shipping_cost")
	val shippingCost: Double? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("shipping_type")
	val shippingType: String? = null,

	@field:SerializedName("service_fee")
	val serviceFee: Double? = null,

	@field:SerializedName("grandtotal")
	val grandtotal: Int? = null,

	@field:SerializedName("shipping_cashback")
	val shippingCashback: Int? = null,

	@field:SerializedName("discount")
	val discount: String? = null,

	@field:SerializedName("net_profit")
	val netProfit: Int? = null
) : Parcelable
