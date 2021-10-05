package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KomOrderByPartnerResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: DataOrder? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class OrderItem(

	@field:SerializedName("order_no")
	val orderNo: String? = null,

	@field:SerializedName("product")
	val product: List<ProductItem>? = null,

	@field:SerializedName("shipping_cost")
	val shippingCost: Int? = null,

	@field:SerializedName("detail_address")
	val detailAddress: String? = null,

	@field:SerializedName("airway_bill")
	val airwayBill: String? = null,

	@field:SerializedName("order_status")
	val orderStatus: String? = null,

	@field:SerializedName("order_date")
	val orderDate: String? = null,

	@field:SerializedName("bank")
	val bank: String? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("is_komship")
	val isKomship: Int? = null,

	@field:SerializedName("customer_name")
	val customerName: String? = null,

	@field:SerializedName("grand_total")
	val grandTotal: Int? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("payment_method")
	val paymentMethod: String? = null
) : Parcelable

@Parcelize
data class LinksItem(

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("url")
	val url: String? = null
) : Parcelable

@Parcelize
data class ProductItem(

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

@Parcelize
data class DataOrder(

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("data")
	val data: List<OrderItem>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: String? = null,

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("links")
	val links: List<LinksItem?>? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
) : Parcelable
