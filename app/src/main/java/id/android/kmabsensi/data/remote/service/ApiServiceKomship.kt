package id.android.kmabsensi.data.remote.service

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.KomCartResponse
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.data.remote.response.komship.KomProductByPartnerResponse
import io.reactivex.Single
import retrofit2.http.*

interface ApiServiceKomship {
    /**
     * KOMSHIP
     */

    @GET("api/v1/partner")
    fun getPartnerKomship()
            : Single<KomPartnerResponse>

    @GET("api/v1/partner-product/{partner_id}")
    fun getProductByPartnerKomship(
        @Path("partner_id") partnerId: Int
    )
    : Single<KomProductByPartnerResponse>

    @GET("api/v1/cart")
    fun getDataCart()
    : Single<KomCartResponse>

    @PUT("api/v1/cart/update/{cart_id}")
    fun updateQtyCart(
        @Path("cart_id") cartId : Int,
        @Query("qty") qty : Int
    )
    : Single<BaseResponse>

    @JvmSuppressWildcards
    @POST("api/v1/cart/store")
    fun addCart(
        @Body body: Map<String, Any>
    )
    : Single<BaseResponse>

//    @DELETE("api/v1/cart/delete")
    @JvmSuppressWildcards
//    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/v1/cart/delete", hasBody = true)
    fun deleteCart(
        @Body body: ArrayList<Int>
    )
    : Single<BaseResponse>




}