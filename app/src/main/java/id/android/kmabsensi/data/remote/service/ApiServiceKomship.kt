package id.android.kmabsensi.data.remote.service

import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.*
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

    @GET("api/v1/order/{id_partner}")
    fun getOrderByPartner(
        @Path("id_partner")idPartner : Int,
        @Query("page")page : Int,
        @Query("start_date")startDate : String,
        @Query("end_date")endDate : String,
        @Query("payment_methode")paymentMethode : String,
        @Query("order_status")orderStatus : Int? = null
    )
    : Single<KomListOrderByPartnerResponse>

    @GET("api/v1/destination")
    fun getDestination(
        @Query("page") page: Int? = 1,
        @Query("search") search: String
    )
    : Single<KomDestinationResponse>

    @GET("api/v1/calculate")
    fun getCalculate(
        @Query("discount")discount : Int? = 0,
        @Query("shipping")shipping : String,
        @Query("tariff_code")tariffCode : String,
        @Query("payment_method")paymentMethod : String,
        @Query("partner_id")partner_id : Int,
        @Query("cart")cart : List<Int>? = null
    )
    : Single<KomCalculateResponse>

    @JvmSuppressWildcards
    @POST("api/v1/order/{id_partner}/store")
    fun addOrder(
        @Path("id_partner")idPartner : Int,
        @Body body: Map<String, Any?>
    )
    :Single<KomAddOrderResponse>

    @GET("api/v1/customer")
    fun getCustomer(
        @Query("search")search : String? = null
    )
    :Single<KomCustomerResponse>

    @GET("api/v1/bank-account")
    fun getBank()
    : Single<KomBankResponse>

    @JvmSuppressWildcards
    @HTTP(method = "DELETE", path = "api/v1/cart/delete", hasBody = true)
    fun deleteCart(
        @Body body: Map<String, Any?>
    )
    : Single<BaseResponse>

    @GET("api/v1/order/{id_partner}/detail/{id_order}")
    fun getDetailOrder(
        @Path("id_partner") idPartner: Int,
        @Path("id_order") idOrder: Int
    )
    : Single<KomOrderDetailResponse>

    @DELETE("api/v1/order/{id_partner}/delete/{id_order}")
    fun deleteOder(
        @Path("id_partner") idPartner: Int,
        @Path("id_order") idOrder: Int
    )
    : Single<BaseResponse>
}