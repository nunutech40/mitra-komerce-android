package id.android.kmabsensi.data.remote.datasource.kmpoint

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.android.kmabsensi.data.remote.response.kmpoint.AllShoppingRequestResponse
import id.android.kmabsensi.data.remote.response.kmpoint.GetWithdrawResponse
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import id.android.kmabsensi.presentation.kmpoint.penarikan.WithdrawListActivity
import id.android.kmabsensi.presentation.kmpoint.penarikan.WithdrawMainModel
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.isEmpty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import id.android.kmabsensi.utils.isEmpty.ISFALSE
import id.android.kmabsensi.utils.isEmpty.ISTRUE

private const val FIRSTPAGE = 1
private const val LIMIT = 10
private const val TAG = "shoppingPagedList"

class WithdrawDataSource(
    private val apiService: ApiService,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, WithdrawMainModel>(){

    val state: MutableLiveData<State> = MutableLiveData()
    val isEmpty : MutableLiveData<isEmpty> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, WithdrawMainModel>
    ) {
        updateState(State.LOADING)
        compositeDisposable.add(
            apiService.getDataWithdraw(page = FIRSTPAGE)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d(TAG, "PagedList ke $FIRSTPAGE , next = ${it.data}")
                    if (it.success!!){
                        updateState(State.DONE)
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
        callback: LoadCallback<Int, WithdrawMainModel>
    ) {}

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, WithdrawMainModel>
    ) {
        compositeDisposable.add(
            apiService.getDataWithdraw(page = params.key)
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

    private fun getHeader(data: List<GetWithdrawResponse.DataWithDraw.DataDetailWithDraw>) : List<WithdrawMainModel>{
        var date = ""
        var listData = ArrayList<WithdrawMainModel>()
        data.forEach {
            var type = 0
            if (!date.equals(it.createdAt!!.split(" ")[0])) {
                type = WithdrawListActivity.TYPE_HEADER
                date = it.createdAt.split(" ")[0]
            } else {
                type = WithdrawListActivity.TYPE_WITHDRAWAL
            }
            listData.add(
                    WithdrawMainModel(
                            type,
                            it
                    )
            )
        }
        return listData
    }
}