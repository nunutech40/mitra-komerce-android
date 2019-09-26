package id.android.kmabsensi.data.remote.service

import id.android.kmabsensi.data.remote.response.*
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("username_email") username_email: String,
        @Field("password") password: String
    ): Single<LoginResponse>

    @FormUrlEncoded
    @POST("api/user/get-profile")
    fun getProfileData(
        @Header("Authorization") accessToken: String,
        @Field("user_id") userId: Int
    ): Single<ProfileResponse>

    @FormUrlEncoded
    @POST("api/user")
    fun getUser(
        @Field("role_id") roleId: Int = 0,
        @Field("user_management_id") userManagementId: Int = 0,
        @Field("office_id") officeId: Int = 0
    ): Single<UserResponse>

    @FormUrlEncoded
    @POST("api/office/add")
    fun addOffice(
        @Field("office_name") officeName: String,
        @Field("lat") lat: String,
        @Field("lng") lng: String,
        @Field("address") address: String,
        @Field("pj_user_id") pjUserId: Int
    ): Single<CrudOfficeResponse>

    @FormUrlEncoded
    @POST("api/office/edit")
    fun editOffice(
        @Field("id") id: Int,
        @Field("office_name") officeName: String,
        @Field("lat") lat: String,
        @Field("lng") lng: String,
        @Field("address") address: String,
        @Field("pj_user_id") pjUserId: Int
    ): Single<CrudOfficeResponse>

    @POST("api/office")
    fun getOffices(): Single<OfficeResponse>

    @GET("api/office/delete/{office_id}")
    fun deleteOffice(
        @Path("office_id") officeId: Int
    ): Single<CrudOfficeResponse>

}