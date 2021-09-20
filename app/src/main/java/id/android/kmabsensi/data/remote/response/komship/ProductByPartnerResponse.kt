package id.android.kmabsensi.data.remote.response.komship

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductByPartnerResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataProductItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class VariantItem(

	@field:SerializedName("variant_id")
	val variantId: Int? = null,

	@field:SerializedName("variant_option")
	val variantOption: List<VariantOptionItem?>? = null,

	@field:SerializedName("variant_name")
	val variantName: String? = null
) : Parcelable

@Parcelize
data class DataProductItem(

	@field:SerializedName("product_images")
	val productImages: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("variant")
	val variant: List<VariantItem?>? = null,

	@field:SerializedName("is_variant")
	val isVariant: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("product_variant")
	val productVariant: List<ProductVariantItem?>? = null
) : Parcelable

@Parcelize
data class ProductVariantItem(

	@field:SerializedName("option_parent")
	val optionParent: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("option_id")
	val optionId: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null
) : Parcelable

@Parcelize
data class VariantOptionItem(

	@field:SerializedName("option_id")
	val optionId: Int? = null,

	@field:SerializedName("option_name")
	val optionName: String? = null
) : Parcelable
