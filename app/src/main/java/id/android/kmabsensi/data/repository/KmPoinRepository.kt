package id.android.kmabsensi.data.repository

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.body.kmpoint.AllShoppingRequestParams
import id.android.kmabsensi.data.remote.body.kmpoint.CreateShoppingRequestParams
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.kmpoint.*
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Abdul Aziz on 30/08/20.
 */
class KmPoinRepository(val apiService: ApiService) {

    fun redeemPoin(
        userId: Int = 0,
        totalRequestRedeempoin: Int
    ): Single<BaseResponse> {
        val body = mapOf(
            "user_id" to userId,
            "total_request_redeem_kmpoin" to totalRequestRedeempoin
        )
        return apiService.redeemPoin(body)
    }

    private val detailShopping by lazy {
        MutableLiveData<UiState<DetailShoppingResponse>>()
    }

    private var listShopping : MutableLiveData<UiState<AllShoppingRequestResponse>> = MutableLiveData()

    private var createShoppingrequest : MutableLiveData<UiState<CreateShoppingRequestResponse>> = MutableLiveData()

    private var updateShoppingrequest : MutableLiveData<UiState<CreateShoppingRequestResponse>> = MutableLiveData()

    private var getDataWithdraw : MutableLiveData<UiState<GetWithdrawResponse>> = MutableLiveData()

    private var getDetailWithdraw : MutableLiveData<UiState<DetailWithdrawResponse>> = MutableLiveData()

    private var updateStatusWithdraw : MutableLiveData<UiState<UpdateWithdrawResponse>> = MutableLiveData()

    private var uploadAttachment : MutableLiveData<UiState<UploadAttachmentResponse>> = MutableLiveData()

    fun shoppingRequestDetail(
            compositeDisposable: CompositeDisposable,
            id : Int
    ): MutableLiveData<UiState<DetailShoppingResponse>> {
        detailShopping.value = UiState.Loading()
        compositeDisposable.add(
                apiService.shoppingRequestDetail(id = id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            detailShopping.value = UiState.Success(it)
                        }, {
                            detailShopping.value = UiState.Error(it)
                        })

        )
        return detailShopping
    }

    fun allShoppingRequest(
            compositeDisposable: CompositeDisposable,
            params : AllShoppingRequestParams
    ): MutableLiveData<UiState<AllShoppingRequestResponse>> {
        listShopping.value = UiState.Loading()
        compositeDisposable.add(apiService.allshoppingRequest(
                page = params.page,
                limit = params.limit,
                user_requester_id = params.user_requester_id
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    listShopping.value = UiState.Success(it)
                },{
                    listShopping.value = UiState.Error(it)
                })
        )
        return listShopping
    }

    fun createShoppingRequest(
        compositeDisposable: CompositeDisposable,
        body : CreateShoppingRequestParams
    ) : MutableLiveData<UiState<CreateShoppingRequestResponse>>{
        createShoppingrequest.value = UiState.Loading()
        compositeDisposable.add(
            apiService.createShoppingRequest(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    createShoppingrequest.value = UiState.Success(it)
                }, {
                    createShoppingrequest.value = UiState.Error(it)
                })
        )
        return createShoppingrequest
    }

    fun updateShoppingRequest(
            compositeDisposable: CompositeDisposable,
            id: Int,
            body: UpdateShoppingRequestParams
    ): MutableLiveData<UiState<CreateShoppingRequestResponse>>{
        var items = arrayListOf<Map<String, Any?>>()

        /**
         * convert array to json
         */

        body.items.forEach {
            val items2 = mapOf(
                    "id" to it.id,
                    "name" to it.name,
                    "description" to it.description,
                    "total" to it.total,
                    "will_delete" to it.will_delete
            )
            items.add(items2)
        }
        val participant_user_ids = ArrayList<Int>()
        body.participant_user_ids.forEach {
            participant_user_ids.add(it)
        }

        val body2 = mapOf(
                "notes" to body.notes,
                "status" to body.status,
                "items" to items,
                "participant_user_ids" to participant_user_ids
        )
        updateShoppingrequest.value = UiState.Loading()
        compositeDisposable.add(
                apiService.updateShoppingRequest(id, body2)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            updateShoppingrequest.value = UiState.Success(it)
                        },{
                            updateShoppingrequest.value = UiState.Error(it)
                        })
        )
        return updateShoppingrequest
    }

    fun getDataWithdraw(
            compositeDisposable: CompositeDisposable,
            page : Int,
            limit : Int
    ) :  MutableLiveData<UiState<GetWithdrawResponse>>{
        getDataWithdraw.value = UiState.Loading()
        compositeDisposable.add(
                apiService.getDataWithdraw(page = page, limit = limit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            getDataWithdraw.value = UiState.Success(it)
                        },{
                            getDataWithdraw.value = UiState.Error(it)
                        })
        )
        return getDataWithdraw
    }

    fun getDetailWithdraw(
            compositeDisposable: CompositeDisposable,
            id : Int
    ): MutableLiveData<UiState<DetailWithdrawResponse>>{
        getDetailWithdraw.value = UiState.Loading()
        compositeDisposable.add(
                apiService.getDetailWithdraw(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            getDetailWithdraw.value = UiState.Success(it)
                        },{
                            getDetailWithdraw.value = UiState.Error(it)
                        })
        )
        return getDetailWithdraw
    }

    fun updateStatusWithdraw(
            compositeDisposable: CompositeDisposable,
            id : Int,
            status : String
    ) : MutableLiveData<UiState<UpdateWithdrawResponse>>{
        updateStatusWithdraw.value = UiState.Loading()
        val body = mapOf(
                "status" to status
        )
        compositeDisposable.add(
                apiService.updateStatusWithdraw(id, body)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            updateStatusWithdraw.value = UiState.Success(it)
                        },{
                            updateStatusWithdraw.value = UiState.Error(it)
                        })
        )
        return updateStatusWithdraw
    }

    fun uploadAttachment(
            schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable,
            reference_id: RequestBody,
            attachment_type: RequestBody,
            attachment_file: MultipartBody.Part?
    ): MutableLiveData<UiState<UploadAttachmentResponse>>{
        uploadAttachment.value = UiState.Loading()
        compositeDisposable.add(
                apiService.uploadAttachment(
                        reference_id,
                        attachment_type,
                        attachment_file!!)
                        .with(schedulerProvider)
                        .subscribe({
                            uploadAttachment.value = UiState.Success(it)
                        },{
                            uploadAttachment.value = UiState.Error(it)
                        })
        )
        return uploadAttachment
    }
//    fun requestWithdraw(
//        compositeDisposable: CompositeDisposable,
//        params : RequestWithdrawParams
//    ) : MutableLiveData<UiState<UpdateWithdrawResponse>>{
//        requestWithdraw.value = UiState.Loading()
//        compositeDisposable.add(
//            apiService.requestWithdraw(
//                user_id = params.user_id,
//                transaction_type = params.transaction_type,
//                nominal = params.nominal,
//                bank_name = params.bank_name,
//                bank_no = params.bank_no,
//                bank_owner_name = params.bank_owner_name,
//                notes = params.notes
//            )
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({
//                    requestWithdraw.value = UiState.Success(it)
//                },{
//                    requestWithdraw.value = UiState.Error(it)
//                })
//        )
//        return requestWithdraw
//    }
}