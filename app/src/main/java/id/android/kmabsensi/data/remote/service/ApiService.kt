package id.android.kmabsensi.data.remote.service

import id.android.kmabsensi.data.remote.response.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("username_email") username_email: String,
        @Field("password") password: String
    ): Single<LoginResponse>

    @FormUrlEncoded
    @POST("api/reset_password")
    fun resetPassword(
        @Field("id") userId: String,
        @Field("password") password: String
    ) : Single<BaseResponse>

    @FormUrlEncoded
    @POST("api/user/get-profile")
    fun getProfileData(
        @Header("Authorization") accessToken: String,
        @Field("user_id") userId: Int
    ): Single<UserResponse>

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


    @Multipart
    @POST("api/user/add")
    fun addSdm(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") password_confirmation: RequestBody,
        @Part("role_id") role_id: RequestBody,
        @Part("full_name") full_name: RequestBody,
        @Part("npk") npk: RequestBody,
        @Part("division_id") divisionId: RequestBody,
        @Part("office_id") officeId: RequestBody,
        @Part("position_id") positionId: RequestBody,
        @Part("no_partner") noPartner: RequestBody,
        @Part("origin_village") originVillage: RequestBody,
        @Part("no_hp") no_hp: RequestBody,
        @Part("address") address: RequestBody,
        @Part("birth_date") birth_date: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("user_management_id") user_management_id: RequestBody,
        @Part photo_profile_url: MultipartBody.Part?
    ): Single<SingleUserResponse>

    @Multipart
    @POST("api/user/edit")
    fun editSdm(
        @Part("id") id: RequestBody,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("role_id") role_id: RequestBody,
        @Part("full_name") full_name: RequestBody,
        @Part("division_id") divisionId: RequestBody,
        @Part("office_id") officeId: RequestBody,
        @Part("position_id") positionId: RequestBody,
        @Part("no_partner") noPartner: RequestBody,
        @Part("origin_village") originVillage: RequestBody,
        @Part("no_hp") no_hp: RequestBody,
        @Part("address") address: RequestBody,
        @Part("birth_date") birth_date: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("user_management_id") user_management_id: RequestBody,
        @Part photo_profile_url: MultipartBody.Part?
    ): Single<SingleUserResponse>

    @GET("api/user/delete/{user_id}")
    fun deleteSdm(
        @Path("user_id") userId: Int
    ): Single<SingleUserResponse>

    @GET("api/dashboard")
    fun getDashboardInfo(
        @Query("user_id") userId : Int
    ) : Single<DashboardResponse>
}