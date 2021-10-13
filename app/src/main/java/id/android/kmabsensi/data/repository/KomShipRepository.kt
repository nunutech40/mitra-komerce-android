package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.body.komship.AddOrderParams
import id.android.kmabsensi.data.remote.body.komship.DeleteParams
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

    fun updateQtyCart(cart_id: Int, qty : Int) :Single<BaseResponse>
    = apiService.updateQtyCart(cart_id, qty)

    fun addCart(data : AddCartParams): Single<BaseResponse>{
        val body = mapOf(
            "partner_id" to data.idPartner,
            "product_id" to data.productId,
            "product_name" to data.productName,
            "variant_id" to data.variantId,
            "variant_name" to data.variantName,
            "product_price" to data.productPrice,
            "product_weight" to data.productWeight,
            "qty" to data.qty,
            "subtotal" to data.subtotal
        )
        return apiService.addCart(body)
    }

    fun getOrderByPartner(
        idPartner: Int,
        params: OrderByPartnerParams
    ): Single<KomListOrderByPartnerResponse>
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
        partnerId: Int,
        cartId: List<Int>? = null
    ): Single<KomCalculateResponse>
    = apiService.getCalculate(discount, shipping, tariffCode, paymentMethode, partnerId, cartId)

    fun addOrder(
        idPartner: Int,
        p: AddOrderParams
    ): Single<KomAddOrderResponse>{
        val body = mapOf(
            "date" to p.date,
            "tariff_code" to p.tariff_code,
            "subdistrict_name" to p.subdistrict_name,
            "district_name" to p.district_name,
            "city_name" to p.city_name,
            "is_komship" to p.is_komship,
            "customer_id" to p.customer_id,
            "customer_name" to p.customer_name,
            "customer_phone" to p.customer_phone,
            "detail_address" to p.detail_address,
            "shipping" to p.shipping,
            "shipping_type" to p.shipping_type,
            "payment_method" to p.payment_method,
            "bank" to p.bank,
            "bank_account_name" to p.bank_account_name,
            "bank_account_no" to p.bank_account_no.toString(),
            "subtotal" to p.subtotal,
            "grandtotal" to p.grandtotal,
            "shipping_cost" to p.shipping_cost,
            "service_fee" to p.service_fee,
            "discount" to p.discount,
            "shipping_cashback" to p.shipping_cashback,
            "net_profit" to p.net_profit,
            "cart" to p.cart
        )
        return apiService.addOrder(idPartner, body)
    }

    fun getCustomer(search: String? = null): Single<KomCustomerResponse>
    = apiService.getCustomer(search)

    fun getBank(): Single<KomBankResponse>
    = apiService.getBank()

    fun deleteCart(
        p:List<Int>
    ): Single<BaseResponse>{

        val body = mapOf(
            "cart_id" to p
        )

        return apiService.deleteCart(body)
    }

    fun getDetailOrder(
        idPartner: Int,
        idOrder: Int
    ): Single<KomOrderDetailResponse>
    = apiService.getDetailOrder(idPartner, idOrder)

    fun deleteOrder(
        idPartner: Int,
        idOrder: Int
    ): Single<BaseResponse>
    = apiService.deleteOder(idPartner, idOrder)
}