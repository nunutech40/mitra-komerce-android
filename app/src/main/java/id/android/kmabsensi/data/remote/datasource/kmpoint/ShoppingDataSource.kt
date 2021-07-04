package id.android.kmabsensi.data.remote.datasource.kmpoint

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import id.android.kmabsensi.presentation.kmpoint.penarikan.WithdrawListActivity
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.isEmpty
import id.android.kmabsensi.utils.isEmpty.ISTRUE
import id.android.kmabsensi.utils.isEmpty.ISFALSE
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

private const val FIRSTPAGE = 1
private const val LIMIT = 10
private const val TAG = "shoppingPagedList"

class ShoppingDataSource(
    private val apiService: ApiService,
    private val compositeDisposable: CompositeDisposable,
    private val user_requester_id: Int? = null,
    private val status: String? = null,
) : PageKeyedDataSource<Int, ShoppingRequestModel>(){

    val state: MutableLiveData<State> = MutableLiveData()
    val isEmpty : MutableLiveData<isEmpty> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ShoppingRequestModel>
    ) {
        updateState(State.LOADING)
        compositeDisposable.add(
            apiService.allshoppingRequestPagging(page = FIRSTPAGE, status = status, user_requester_id = user_requester_id)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d(TAG, "PagedList ke $FIRSTPAGE , next = ${it.data}")
                    if (it.success!!){
                        updateState(State.DONE)
                        isEmpty(ISTRUE)
                        if (it.data?.data!!.size <= 0) isEmpty(ISTRUE) else isEmpty(ISFALSE)
                        callback.onResult(getHeader(it.data?.data!!), null, FIRSTPAGE + 1)
                    }
                }, {
                    updateState(State.ERROR)
                    Log.d(TAG, it.message.toString())
                })
        )
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ShoppingRequestModel>
    ) {}

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ShoppingRequestModel>
    ) {
        compositeDisposable.add(
            apiService.allshoppingRequestPagging(page = params.key, status = status, user_requester_id = user_requester_id)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d(TAG, "PagedList ke ${params.key}")
                    callback.onResult(getHeader(it.data?.data!!), params.key + 1)
                }, {
                    updateState(State.ERROR)
                    Log.d(TAG, it.message.toString())
                })
        )
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
    private fun isEmpty(empty: isEmpty) {
        this.isEmpty.postValue(empty)
    }

    private fun getHeader(data: List<AllShoppingRequestResponse.Data.DataListShopping>) : List<ShoppingRequestModel>{
        var date = ""
        var listData = ArrayList<ShoppingRequestModel>()
        data.forEach {
            var type = 0
            if (!date.equals(it.createdAt!!.split(" ")[0])) {
                type = WithdrawListActivity.TYPE_HEADER
                date = it.createdAt.split(" ")[0]
            } else {
                type = WithdrawListActivity.TYPE_WITHDRAWAL
            }
            listData.add(
                    ShoppingRequestModel(
                            type,
                            it
                    )
            )
        }
        return listData
    }
}