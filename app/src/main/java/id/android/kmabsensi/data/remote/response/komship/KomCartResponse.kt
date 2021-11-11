package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KomCartResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<CartItem>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class CartItem(

	@field:SerializedName("partner_id")
	val partnerId: Int? = null,

	@field:SerializedName("cart_id")
	val cartId: Int? = null,

	@field:SerializedName("variant_id")
	val variantId: Int? = null,

	@field:SerializedName("product_weight")
	val productWeight: Int? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("product_image")
	val productImage: String? = null,

	@field:SerializedName("variant_name")
	val variantName: String? = null,

	@field:SerializedName("product_price")
	val productPrice: Int? = null,

	@field:SerializedName("product_name")
	val productName: String? = null
) : Parcelable
