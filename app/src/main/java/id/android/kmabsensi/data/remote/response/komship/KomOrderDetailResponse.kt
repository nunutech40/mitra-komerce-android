package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KomOrderDetailResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: DataDetailOrder? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataDetailOrder(

	@field:SerializedName("order_no")
	val orderNo: String? = null,

	@field:SerializedName("customer_address")
	val customerAddress: String? = null,

	@field:SerializedName("product")
	val product: List<ProductDetailOrderItem>? = null,

	@field:SerializedName("shipping_cost")
	val shippingCost: Int? = null,

	@field:SerializedName("shipping_type")
	val shippingType: String? = null,

	@field:SerializedName("customer_phone")
	val customerPhone: String? = null,

	@field:SerializedName("service_fee")
	val serviceFee: Int? = null,

	@field:SerializedName("discount")
	val discount: Int? = null,

	@field:SerializedName("user_fullname")
	val userFullname: String? = null,

	@field:SerializedName("airway_bill")
	val airwayBill: String? = null,

	@field:SerializedName("order_status")
	val orderStatus: String? = null,

	@field:SerializedName("order_date")
	val orderDate: String? = null,

	@field:SerializedName("shipping")
	val shipping: String? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("is_komship")
	val isKomship: Int? = null,

	@field:SerializedName("grandtotal")
	val grandtotal: Int? = null,

	@field:SerializedName("shipping_cashback")
	val shippingCashback: Int? = null,

	@field:SerializedName("net_profit")
	val netProfit: Int? = null,

	@field:SerializedName("customer_name")
	val customerName: String? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null
) : Parcelable

@Parcelize
data class ProductDetailOrderItem(

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("product_image")
	val productImage: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("weight")
	val weight: Int? = null,

	@field:SerializedName("variant_name")
	val variantName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("product_variant_id")
	val productVariantId: Int? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("detail_order_id")
	val detailOrderId: Int? = null
) : Parcelable
