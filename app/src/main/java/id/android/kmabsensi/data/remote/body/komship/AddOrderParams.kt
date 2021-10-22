package id.android.kmabsensi.data.remote.body.komship

data class AddOrderParams(
    val date: String,
    val tariff_code: String,
    val subdistrict_name: String,
    val district_name:String,
    val city_name: String,
    val is_komship: Int,
    val customer_id: Int? = null,
    val customer_name: String,
    val customer_phone: String? = null,
    val detail_address: String? = null,
    val shipping: String,
    val shipping_type: String,
    val payment_method: String,
    val bank: String?,
    val bank_account_name: String?,
    val bank_account_no: String?,
    val subtotal: Int,
    val grandtotal: Int,
    val shipping_cost: Int,
    val service_fee: Int,
    val discount: Int,
    val shipping_cashback: Int,
    val net_profit: Int,
    val cart: List<Int>
)
