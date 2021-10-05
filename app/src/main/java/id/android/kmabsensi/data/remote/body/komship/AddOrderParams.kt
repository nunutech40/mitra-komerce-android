package id.android.kmabsensi.data.remote.body.komship

data class AddOrderParams(
    val date: String,
    val tariff_code: String,
    val subdistrict_name: String,
    val district_name:String,
    val city_name: String,
    val is_komship: Int,
    val customer_id: Int,
    val customer_name: String,
    val customer_phone: String,
    val detail_address: String,
    val shipping: String,
    val shipping_type: String,
    val payment_method: String,
    val bank: String? = null,
    val bank_account_name: String? = null,
    val bank_account_no: Int? = null,
    val subtotal: Int,
    val grandtotal: Int,
    val shipping_cost: Int,
    val service_fee: Int,
    val discount: Int,
    val shipping_cashback: Int,
    val net_profit: Int,
    val cart: List<Int>
)
