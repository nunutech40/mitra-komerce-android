package id.android.kmabsensi.data.remote.service

import id.android.kmabsensi.data.remote.body.*
import id.android.kmabsensi.data.remote.body.kmpoint.CreateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.*
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetailResponse
import id.android.kmabsensi.data.remote.response.invoice.MyInvoiceResponse
import id.android.kmabsensi.data.remote.response.kmpoint.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("username_email") username_email: String,
        @Field("password") password: String,
        @Field("fcm_token") fcmToken: String,
        @Field("applicationType") applicationType: Int = 1
    ): Single<LoginResponse>

    @FormUrlEncoded
    @POST("api/logout")
    fun logout(
        @Field("fcm_token") fcmToken: String
    ): Single<BaseResponse>

    @FormUrlEncoded
    @POST("api/reset_password")
    fun resetPassword(
        @Field("id") userId: String,
        @Field("password") password: String
    ): Single<BaseResponse>

    @FormUrlEncoded
    @POST("api/change_password")
    fun changePassword(
        @Field("id") userId: String,
        @Field("password_old") passwordOld: String,
        @Field("password") password: String
    ): Single<BaseResponse>

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
        @Field("office_id") officeId: Int = 0,
        @Field("position_id") positionId: Int = 0,
        @Field("no_partner") noPartner: Int = 0
    ): Single<UserResponse>

    @POST("api/user/sdmNonJob")
    fun getSdmNonJob(): Single<UserResponse>

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
        @Part("status") status: RequestBody,
        @Part photo_profile_url: MultipartBody.Part?,
        @Part("join_date") joinDate: RequestBody,
        @Part("martial_status") martialStatus: RequestBody,
        @Part("bank_name") bankName: RequestBody,
        @Part("bank_no") bankNo: RequestBody,
        @Part("bank_owner_name") bankOwnerName: RequestBody
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
        @Part("status") status: RequestBody,
        @Part photo_profile_url: MultipartBody.Part?,
        @Part("join_date") joinDate: RequestBody,
        @Part("martial_status") martialStatus: RequestBody,
        @Part("bank_account_id") bankAccountId: RequestBody,
        @Part("bank_name") bankName: RequestBody,
        @Part("bank_no") bankNo: RequestBody,
        @Part("bank_owner_name") bankOwnerName: RequestBody
    ): Single<SingleUserResponse>

    @GET("api/user/delete/{user_id}")
    fun deleteSdm(
        @Path("user_id") userId: Int
    ): Single<SingleUserResponse>

    @GET("api/dashboard")
    fun getDashboardInfo(
        @Query("user_id") userId: Int
    ): Single<DashboardResponse>

    @FormUrlEncoded
    @POST("api/presence/check")
    fun presenceCheck(
        @Field("user_id") userId: Int,
        @Field("date") date: String
    ): Single<PresenceCheckResponse>

    @Multipart
    @POST("api/presence/check-in")
    fun checkIn(
        @Part file: MultipartBody.Part,
        @Part("ontime_level") ontimeLevel: RequestBody
    ): Single<CheckinResponse>

    @Multipart
    @POST("api/presence/check-out/{absen_id}")
    fun checkOut(
        @Path("absen_id") absenId: Int,
        @Part file: MultipartBody.Part
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
//        @Part("status") status: RequestBody,
        @Part("explanation") explanation: RequestBody,
        @Part("date_from") dateFrom: RequestBody,
        @Part("date_to") dateTo: RequestBody,
        @Part attachment_leader: MultipartBody.Part,
        @Part attachment_partner: MultipartBody.Part?
    ): Single<BaseResponse>

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

    @POST("api/permission/filterd")
    @FormUrlEncoded
    fun filterListPermission(
        @Field("role_id") roleId: Int = 0,
        @Field("user_management_id") userManagementId: Int = 0,
        @Field("user_id") userId: Int = 0,
        @Field("date_from") dateFrom: String,
        @Field("date_to") dateTo: String,
        @Field("permission_type") status: Int
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

    @JvmSuppressWildcards
    @POST("api/presence/report/filtered")
    fun presenceReportFiltered(@Body body: Map<String, Any>): Single<PresenceReportResponse>

    @JvmSuppressWildcards
    @POST("api/presence/report/filtered")
    fun presenceReportFilteredPaging(
            @Query("page")page:Int,
            @Body body: Map<String, Any>
    ): Single<PresenceReportResponse>

    @POST("api/position")
    fun listPosition(): Single<ListPositionResponse>

    @POST("api/position/add")
    @FormUrlEncoded
    fun addPosition(
        @Field("position_name") positionName: String
    ): Single<BaseResponse>

    @POST("api/position/edit")
    @FormUrlEncoded
    fun editPosition(
        @Field("id") id: Int,
        @Field("position_name") positionName: String
    ): Single<BaseResponse>

    @GET("api/position/delete/{position_id}")
    fun deletePosition(
        @Path("position_id") positionId: Int
    ): Single<BaseResponse>

    @GET
    fun getJadwalShalat(@Url url: String): Single<JadwalShalatResponse>

    @POST("api/coworkingspace")
    fun getCoworkingSpace(): Single<ListCoworkingSpaceResponse>

    @JvmSuppressWildcards
    @POST("api/coworkingspace/add")
    fun addCoworkingSpace(@Body body: Map<String, Any>): Single<AddCoworkingSpaceResponse>

    @JvmSuppressWildcards
    @POST("api/coworkingspace/edit")
    fun editCoworkingSpace(@Body body: Map<String, Any>): Single<AddCoworkingSpaceResponse>

    @GET("api/coworkingspace/delete/{id}")
    fun deleteCoworkingSpace(
        @Path("id") id: Int
    ): Single<AddCoworkingSpaceResponse>

    @POST("api/coworkingspace/coworkUserData/{user_id}")
    fun getUserCoworkData(
        @Path("user_id") userId: Int
    ): Single<UserCoworkDataResponse>

    @GET("api/coworkingspace/check-in/{cowork-id}")
    fun checkinCoworkingSpace(
        @Path("cowork-id") coworkId: Int
    ): Single<BaseResponse>

    @GET("api/coworkingspace/check-out/{cowork_presence_id}")
    fun checkOutCoworkingSpace(
        @Path("cowork_presence_id") cowork_presence_id: Int
    ): Single<BaseResponse>

    @JvmSuppressWildcards
    @POST("api/presence/ticket/add")
    fun reportAbsen(
        @Body body: Map<String, Any>
    ): Single<BaseResponse>

    @POST("api/partnerCategory")
    fun getPartnerCategories(): Single<ListPartnerCategoryResponse>

    @POST("/api/partnerCategory/add")
    @FormUrlEncoded
    fun addPartnerCategory(
        @Field("partner_category_name") partnerCategory: String
    ): Single<BaseResponse>

    @POST("/api/partnerCategory/edit")
    @FormUrlEncoded
    fun editPartnerCategory(
        @Field("id") id: Int,
        @Field("partner_category_name") partnerCategory: String
    ): Single<BaseResponse>

    @GET("/api/partnerCategory/delete/{id}")
    fun deletePartnerCategory(
        @Path("id") id: Int
    ): Single<BaseResponse>

    @GET("api/masterdata/area")
    fun getDataArea(): Observable<ListAreaResponse>

    @Multipart
    @POST("api/user/partner/add")
    fun addPartner(
        @Part("no_partner") noPartner: RequestBody,
        @Part("username") username: RequestBody,
        @Part("status") status: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") passwordConfirmation: RequestBody,
        @Part("role_id") roleId: RequestBody,
        @Part("full_name") fullname: RequestBody,
        @Part("no_hp") noHp: RequestBody,
        @Part("address") address: RequestBody,
        @Part photo_profile_url: MultipartBody.Part?,
        @Part("birth_date") birth_date: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("join_date") joinDate: RequestBody,
        @Part("martial_status") martialStatus: RequestBody,
        @Part("partner_category_id") partnerCategoryId: RequestBody,
        @Part("partner_category_name") partnerCategoryName: RequestBody,
        @Part("province_code") provinceCode: RequestBody,
        @Part("province_name") provinceName: RequestBody,
        @Part("city_code") cityCode: RequestBody,
        @Part("city_name") cityName: RequestBody,
        @Part("user_management_id") userManagementId: RequestBody,
        @Part("bonus") bonus: RequestBody
    ): Single<BaseResponse>

    @GET("api/user/partner")
    fun getPartners(): Single<ListPartnerResponse>

    @GET("api/user/partner/partnerOff")
    fun getPartnerOff(): Single<ListPartnerResponse>

    @GET("api/masterdata/partner")
    fun getSimplePartners(): Single<SimplePartnersResponse>

    @GET("api/user/partner/partnerOfSdmAssigned/{user_management_id}")
    fun getPartnerByManagement(@Path("user_management_id") userManagementId: Int): Single<ListPartnerResponse>

    @Multipart
    @POST("api/user/partner/edit")
    fun editPartner(
        @Part("id") id: RequestBody,
        @Part("no_partner") noPartner: RequestBody,
        @Part("username") username: RequestBody,
        @Part("status") status: RequestBody,
        @Part("email") email: RequestBody,
        @Part("role_id") roleId: RequestBody,
        @Part("full_name") fullname: RequestBody,
        @Part("no_hp") noHp: RequestBody,
        @Part("address") address: RequestBody,
        @Part photo_profile_url: MultipartBody.Part?,
        @Part("birth_date") birth_date: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("join_date") joinDate: RequestBody,
        @Part("martial_status") martialStatus: RequestBody,
        @Part("partner_category_id") partnerCategoryId: RequestBody,
        @Part("partner_category_name") partnerCategoryName: RequestBody,
        @Part("province_code") provinceCode: RequestBody,
        @Part("province_name") provinceName: RequestBody,
        @Part("city_code") cityCode: RequestBody,
        @Part("city_name") cityName: RequestBody,
        @Part("user_management_id") userManagementId: RequestBody,
        @Part("bonus") bonus: RequestBody
    ): Single<BaseResponse>

    @GET("api/user/partner/delete/{user-id}")
    fun deletePartner(
        @Path("user-id") userId: Int
    ): Single<BaseResponse>

    @JvmSuppressWildcards
    @POST("api/invoice/myInvoice")
    fun getMyInvoice(@Body body: Map<String, Any>): Single<MyInvoiceResponse>

    @JvmSuppressWildcards
    @POST("api/invoice/myInvoice")
    fun filterMyInvoice(@Body body: Map<String, Any>): Single<MyInvoiceResponse>

    @GET("api/invoice/detail/admin/{invoice_id}")
    fun getInvoiceAdminDetail(@Path("invoice_id") invoiceId: Int): Single<InvoiceDetailResponse>

    @GET("api/invoice/detail/gaji/{invoice_id}")
    fun getInvoiceGajiDetail(@Path("invoice_id") invoiceId: Int): Single<InvoiceDetailResponse>

    @POST("api/invoice/create")
    fun createInvoice(@Body body: CreateInvoiceBody): Single<CreateInvoiceResponse>

    @POST("api/invoice/update")
    fun updateInvoice(@Body body: Map<String, Int>): Single<BaseResponse>

    @GET("api/user/partner/leaderOfSdmAssigned/{no_partner}")
    fun getSdmOfPartner(@Path("no_partner") noPartner: String): Single<SdmOfPartnerResponse>

    @GET("api/evaluation/myEvaluation/{user_id}")
    fun getMyEvaluation(@Path("user_id") userId: Int): Single<MyEvaluationResponse>

    @JvmSuppressWildcards
    @POST("api/evaluation/leaderResult")
    fun getLeaderEvaluation(@Body body: Map<String, Any>): Single<MyEvaluationResponse>

    @JvmSuppressWildcards
    @POST("api/invoice/report/summary")
    fun getInvoiceReport(@Body body: Map<String, Any>): Single<InvoiceReportResponse>

    @JvmSuppressWildcards
    @POST("api/invoice/report/summaryDetail")
    fun getInvoiceReportDetail(@Body body: Map<String, Any>): Single<InvoiceReportDetailResponse>

    @GET("api/kmconfig")
    fun getWorkConfig(): Single<WorkConfigResponse>

    @POST("api/kmconfig/update")
    fun updateWorkConfig(@Body workConfigParams: WorkConfigParams): Single<WorkConfigResponse>

    @GET("api/evaluationcollaboration")
    fun getEvaluationCollaboration(): Single<EvaluationCollaborationResponse>

    @POST("api/evaluationcollaboration/filtered")
    fun filterEvaluationCollaboration(@Body params: FilterEvaluationCollaborationParams): Single<EvaluationCollaborationResponse>

    @GET("api/menu")
    fun getMenuRole(): Single<MenuRoleResponse>

    @GET("api/menu/getMenuByPosition/{position_id}")
    fun getMenuRoleByPosition(
        @Path("position_id") positionId: Int
    ): Single<MenuRoleByPositionResponse>

    @POST("api/menu/assignPosition")
    fun assignPosition(@Body assignReleasePositionParams: AssignReleasePositionParams): Single<BaseResponse>

    @POST("api/menu/releasePosition")
    fun releasePosition(@Body assignReleasePositionParams: AssignReleasePositionParams): Single<BaseResponse>

    @GET("api/device")
    fun getListDevice(): Single<ListDeviceResponse>

    @Multipart
    @POST("api/device/store")
    fun addDevice(
        @Part("device_type") deviceTpe: RequestBody,
        @Part("brancd") brancd: RequestBody,
        @Part("spesification") spesification: RequestBody,
        @Part("no_partner") noPartner: RequestBody,
        @Part("user_sdm_id") userSdmId: RequestBody,
        @Part("device_pick_date") devicePickDate: RequestBody,
        @Part attachment_1: MultipartBody.Part?,
        @Part attachment_2: MultipartBody.Part?,
        @Part attachment_3: MultipartBody.Part?,
        @Part("device_owner_type") deviceOwnerType: RequestBody
    ): Single<BaseResponse>

    @Multipart
    @POST("api/device/edit")
    fun editDevice(
        @Part("id") id: RequestBody,
        @Part("device_type") deviceTpe: RequestBody,
        @Part("brancd") brancd: RequestBody,
        @Part("spesification") spesification: RequestBody,
        @Part("no_partner") noPartner: RequestBody,
        @Part("user_sdm_id") userSdmId: RequestBody,
        @Part("device_pick_date") devicePickDate: RequestBody,
        @Part attachment_1: MultipartBody.Part?,
        @Part attachment_2: MultipartBody.Part?,
        @Part attachment_3: MultipartBody.Part?,
        @Part("device_owner_type") deviceOwnerType: RequestBody
    ): Single<BaseResponse>


    @GET("api/device/delete/{id}")
    fun deleteDevice(
        @Path("id") id: Int
    ): Single<BaseResponse>

    @POST("api/device/filtered")
    fun filterDevice(
        @Body filterDeviceParams: FilterDeviceParams
    ): Single<ListDeviceResponse>

    @GET("api/attachment/file/delete/{id}")
    fun deleteAttachment(@Path("id") id: Int): Single<BaseResponse>

    @GET("api/administrationdata")
    fun getListAdministration(): Single<ListAdministrationResponse>

    @Multipart
    @POST("api/administrationdata/store")
    fun addAdministrationData(
        @Part("title") title: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("position_id") positionId: RequestBody,
        @Part attachment: MultipartBody.Part?
    ): Single<BaseResponse>

    @Multipart
    @POST("api/administrationdata/edit")
    fun editAdministrationData(
        @Part("id") id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("position_id") positionId: RequestBody,
        @Part attachment: MultipartBody.Part?
    ): Single<BaseResponse>

    @GET("api/administrationdata/delete/{id}")
    fun deleteAdministrationData(
        @Path("id") id: Int
    ): Single<BaseResponse>

    @POST("api/productknowledge/filtered")
    fun getListProductKnowledge(@Body body: Map<String, Int>): Single<ListProductKnowledgeResponse>

    @GET("api/productknowledge")
    fun getListProductKnowledge(): Single<ListProductKnowledgeResponse>

    @JvmSuppressWildcards
    @POST("api/kmpoin/redeempoin")
    fun redeemPoin(@Body body: Map<String, Int>): Single<BaseResponse>

    @POST("api/holiday")
    fun getHoliday(): Single<HolidayResponse>

    @POST("api/holiday/add")
    fun addHoliday(@Body body: AddHolidayParams): Single<BaseResponse>

    @GET("api/holiday/delete/{holiday_id}")
    fun deleteHoliday(
        @Path("holiday_id") holidayId: Int
    ): Single<BaseResponse>

    @POST("api/holiday/edit")
    fun editHoliday(@Body body: EditHolidayParams): Single<BaseResponse>

    @POST("api/presence/alphaAttendance")
    fun getListAlpha(@Body body: ListAlphaParams): Single<ListAlphaResponse>

    @GET("api/performance/cs")
    fun getSdmReport(): Single<ListCsPerformanceResponse>

    @POST("api/performance/cs/add")
    fun addSdmReport(@Body body: AddSdmReportParams): Single<BaseResponse>

    @POST("api/performance/cs/edit")
    fun editSdmReport(@Body body: EditSdmReportParams): Single<BaseResponse>

    @GET("api/performance/cs/delete/{id}")
    fun deleteSdmReport(
        @Path("id") id: Int
    ): Single<BaseResponse>

    @POST("api/performance/cs/filtered")
    fun filterSdmReport(@Body body: FilterSdmReportParams): Single<ListCsPerformanceResponse>

    @POST("api/performance/cs/reportSummaryFiltered")
    fun filterCsReportSummary(@Body body: FilterSdmReportParams): Single<ListCsPerformanceResponse>

    @FormUrlEncoded
    @POST("api/userconfiguration/sdmShiftConfiguration")
    fun getSdmShiftConfiguration(
        @Field("user_management_id") userManagementId: Int = 0
    ): Single<UserConfigurationResponse>

    @POST("api/userconfiguration/updateSdmShift")
    fun updateSdmShift(
        @Body body: UpdateSdmShiftConfigParam
    ): Single<SingleSdmShiftConfigResponse>

    @GET("api/performance/adv")
    fun getAdvertiserReports() : Single<ListAdvertiserReportResponse>

    @POST("api/performance/adv/add")
    fun addAdvertiserReport(
        @Body body: AddAdvertiserReportParams
    ): Single<BaseResponse>

    @GET("api/performance/adv/delete/{id}")
    fun deleteAdvertiserReport(
        @Path("id") id: Int
    ): Single<BaseResponse>


    @POST("api/performance/adv/edit")
    fun editAdvertiserReport(
        @Body body: EditAdvertiserReportParams
    ): Single<BaseResponse>

    @POST("api/performance/adv/filtered")
    fun filterAdvertiserReport(@Body body: FilterSdmReportParams) : Single<ListAdvertiserReportResponse>

    @POST("api/performance/adv/reportSummaryFiltered")
    fun filterAdvertiserReportSummary(
        @Body body: FilterSdmReportParams
    ): Single<ListAdvertiserReportSummaryResponse>

    @FormUrlEncoded
    @POST("api/user/pagination")
    fun getUserPerIndex(
        @Query("page")page:Int,
        @Field("role_id") roleId: Int = 0,
        @Field("user_management_id") userManagementId: Int = 0,
        @Field("office_id") officeId: Int = 0,
        @Field("position_id") positionId: Int = 0,
        @Field("no_partner") noPartner: Int = 0
    ): Single<UserResponse>

    @GET("api/user/getTalentLeader")
    fun filterUser(
            @Query("user_management_id") user_management_id : Int
    ): Single<UserResponse>

    @GET("api/user/searchUser")
    fun searchUser(
        @Query("keyword") keyword : String
    ): Single<UserResponse>

    @POST("api/shoppingRequest")
    fun createShoppingRequest(
        @Body body : CreateShoppingRequestParams
    ): Single<CreateShoppingRequestResponse>

    @JvmSuppressWildcards
    @PUT("api/shoppingRequest/{id}")
    fun updateShoppingRequest(
            @Path("id") id: Int,
            @Body body: Map<String, Any>
    ): Single<CreateShoppingRequestResponse>

    @GET("api/shoppingRequest")
    fun allshoppingRequest(
            @Query("page") page : Int? = 1,
            @Query("limit") limit : Int? = 10,
            @Query("user_requester_id") user_requester_id : Int?,
    ): Single<AllShoppingRequestResponse>

    @GET("api/shoppingRequest/{id}")
    fun shoppingRequestDetail(
        @Path("id") id: Int
    ): Single<DetailShoppingResponse>

    @GET("api/kmpoin/kmPoinWithdrawalRequest")
    fun getDataWithdraw(
            @Query("page") page : Int? = 1,
            @Query("limit") limit : Int? = 10,
    ): Single<GetWithdrawResponse>

    @GET("api/kmpoin/kmPoinWithdrawalRequest/{id}")
    fun getDetailWithdraw(
            @Path("id") id: Int
    ): Single<DetailWithdrawResponse>

    @JvmSuppressWildcards
    @PUT("api/kmpoin/kmPoinWithdrawalRequest/{id}")
    fun updateStatusWithdraw(
            @Path("id") id: Int,
            @Body body: Map<String, Any>
    ): Single<UpdateWithdrawResponse>

    @FormUrlEncoded
    @POST("api/kmpoin/kmPoinWithdrawalRequest")
    fun requestWithdraw(
        @Field("user_id") user_id : Int,
        @Field("transaction_type") transaction_type: String,
        @Field("nominal") nominal: Int,
        @Field("bank_name") bank_name: String,
        @Field("bank_no") bank_no: String,
        @Field("bank_owner_name") bank_owner_name: String,
        @Field("notes") notes: String
    ): Single<UpdateWithdrawResponse>

}
