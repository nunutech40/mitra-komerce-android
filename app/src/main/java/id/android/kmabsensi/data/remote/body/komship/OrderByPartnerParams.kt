package id.android.kmabsensi.data.remote.body.komship

data class OrderByPartnerParams(
    val page: Int,
    val startDate: String,
    val lastDate: String,
    val paymentMethode: String? = null,
    val orderStatus: Int? = null
)
