package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.komship.AddCartParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.KomCartResponse
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.data.remote.response.komship.KomProductByPartnerResponse
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
}