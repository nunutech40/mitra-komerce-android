package id.android.kmabsensi.data.remote.body.komship

data class AddCartParams(
    val idPartner: Int,
    val productId: Int,
    val productName: String,
    val variantId: Int,
    val variantName: String,
    val productPrice: Int,
    val productWeight: Int,
    val qty: Int,
    val subtotal: Int
)
