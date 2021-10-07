package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.body.komship.OrderByPartnerParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.*
import id.android.kmabsensi.data.remote.service.ApiServiceKomship
import io.reactivex.Single
import okhttp3.RequestBody

class KomShipRepository(val apiService: ApiServiceKomship) {

    fun getPartner() : Single<KomPartnerResponse>
    = apiService.getPartnerKomship()

    fun getProductByPartner(id : Int) : Single<KomProductByPartnerResponse>
    = apiService.getProductByPartnerKomship(id)

    fun getCart(): Single<KomCartResponse>
    = apiService.getDataCart()

    fun deleteCart(listId : ArrayList<Int>): Single<BaseResponse>{
        val listReq = mapOf<String, RequestBody>()
//        listId.forEach()
//        val listMap = listId.map {
//            "cart_id" to it
//        }.toMap()
        return apiService.deleteCart(listId)
    }

    fun updateQtyCart(cart_id: Int, qty : Int) :Single<BaseResponse>
    = apiService.updateQtyCart(cart_id, qty)

    fun addCart(data : AddCartParams): Single<BaseResponse>{
        val body = mapOf(
            "product_id" to data.productId,
            "product_name" to data.productName,
            "variant_id" to data.variantId,
            "variant_name" to data.variantName,
            "product_price" to data.productPrice,
            "qty" to data.qty,
            "subtotal" to data.subtotal
        )
        return apiService.addCart(body)
    }

    fun getOrderByPartner(
        idPartner: Int,
        params: OrderByPartnerParams
    ): Single<KomOrderByPartnerResponse>
    = apiService.getOrderByPartner(idPartner, params.page, params.startDate, params.lastDate, params.paymentMethode, params.orderStatus)

    fun getDestination(
        page: Int? = null,
        search: String
    ): Single<KomDestinationResponse>
    = apiService.getDestination(page, search)

    fun getCalculate(
        discount: Int? = null,
        shipping: String,
        tariffCode: String,
        paymentMethode: String,
        partnerId: Int
    ): Single<KomCalculateResponse>
    = apiService.getCalculate(discount, shipping, tariffCode, paymentMethode, partnerId)

    fun addOrder(
        idPartner: Int,
        p: AddOrderParams
    ): Single<BaseResponse>{

        val list = p.cart.map {
            "cart" to it
        }.toMap()

        val body = mapOf(
            "date" to p.date,
            "tariff_code" to p.tariff_code,
            "subdistrict_name" to p.subdistrict_name,
            "district_name" to p.district_name,
            "city_name" to p.city_name,
            "is_komship" to p.is_komship,
            "customer_id" to p.customer_id,
            "customer_name" to p.customer_phone,
            "customer_phone" to p.customer_phone,
            "detail_address" to p.detail_address,
            "shipping" to p.shipping,
            "shipping_type" to p.shipping_type,
            "payment_method" to p.payment_method,
            "bank" to p.bank,
            "bank_account_name" to p.bank_account_name,
            "bank_account_no" to p.bank_account_no,
            "subtotal" to p.subtotal,
            "grandtotal" to p.grandtotal,
            "shipping_cost" to p.shipping_cost,
            "service_fee" to p.service_fee,
            "discount" to p.discount,
            "shipping_cashback" to p.shipping_cashback,
            "net_profit" to p.net_profit,
            "cart" to list
        )
        return apiService.addOrder(idPartner, body)
    }

    fun getCustomer(search: String? = null): Single<KomCustomerResponse>
    = apiService.getCustomer(search)

    fun getBank(): Single<KomBankResponse>
    = apiService.getBank()
}