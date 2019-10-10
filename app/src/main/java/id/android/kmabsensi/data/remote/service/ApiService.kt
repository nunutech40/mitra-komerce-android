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
//        @Header("Authorization") accessToken: String,
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

    @FormUrlEncoded
    @POST("api/presence/check")
    fun presenceCheck(
        @Field("user_id") userId: Int,
        @Field("date") date: String
    ) : Single<PresenceCheckResponse>

    @Multipart
    @POST("api/presence/check-in")
    fun checkIn(
        @Part file: MultipartBody.Part
    ): Single<CheckinResponse>

    @POST("api/presence/check-out/{absen_id}")
    fun checkOut(
        @Path("absen_id") absenId: Int
    ): Single<CheckinResponse>

    @GET("api/presence/history/{user_id}")
    fun presenceHistory(
        @Path("user_id") userId: Int
    ): Single<PresenceHistoryResponse>

    @Multipart
    @POST("api/permission/create")
    fun createPermission(
        @Part("permission_type") permissionType: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("office_id") officeId: RequestBody,
        @Part("role_id") roleId: RequestBody,
        @Part("user_management_id") userManagementId: RequestBody,
        @Part("status") status: RequestBody,
        @Part("explanation") explanation: RequestBody,
        @Part("date_from") dateFrom: RequestBody,
        @Part("date_to") dateTo: RequestBody,
        @Part file: MultipartBody.Part
    ) : Single<BaseResponse>

    @POST("api/forget_password")
    @FormUrlEncoded
    fun forgetPassword(
        @Field("email") email: String
    ): Single<BaseResponse>

    @POST("api/permission")
    @FormUrlEncoded
    fun getListPermission(
        @Field("role_id") roleId: Int = 0,
        @Field("user_management_id") userManagementId: Int = 0,
        @Field("user_id") userId: Int = 0
    ): Single<ListPermissionResponse>

    @POST("api/permission/approve")
    @FormUrlEncoded
    fun approvePermission(
        @Field("permission_id") permissionId: Int,
        @Field("status") status: Int
    ): Single<BaseResponse>

    @POST("api/presence/report")
    @FormUrlEncoded
    fun presenceReport(
        @Field("role_id") roleId: Int,
        @Field("user_management_id") userManagementId: Int,
        @Field("office_id") officeId: Int,
        @Field("report_date") reportDate: String
    ): Single<PresenceReportResponse>


}