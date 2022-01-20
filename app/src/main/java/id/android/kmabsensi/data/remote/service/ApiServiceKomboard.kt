package id.android.kmabsensi.data.remote.service

import androidx.room.RawQuery
import id.android.kmabsensi.data.remote.body.komboard.BulkResiParams
import id.android.kmabsensi.data.remote.body.komboard.CostOngkirParams
import id.android.kmabsensi.data.remote.response.komboard.*
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceKomboard {
    @GET("api/v1/track")
    fun getResi(
        @Header("Authorization") token: String,
        @Query("search")search:String
    )
    : Call<CekResiRespons>

    @GET("province")
    fun getProvince(
        @Query("key") key:String
    ): Call<ProvinceRespons>

    @GET("city")
    fun getCity(
        @Query("key") key:String,
        @Query("province") province:Int
    ):Call<CityRespons>

    @GET("subdistrict")
    fun getSubdistrict(
        @Query("key") key:String,
        @Query("province") province:Int,
        @Query("city") city: Int
    ): Call<SubDistrictRespons>

    @POST("cost")
    fun costOngkir(
        @Header("key") key: String,
        @Body body: CostOngkirParams?
    ): Call<OngkirRespons>

    @GET("api/v1/bulk-check-awb")
    fun getBulkResi(
        @Header("Authorization") token: String,
        @Query("data") data: String
    ): Call<BulkResiRespons>
}