package id.android.kmabsensi.data.remote.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.UiState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

const val FIRSTPAGE = 1

class UserDataSource(
        private var apiservice: ApiService,
        private val compositeDisposable: CompositeDisposable,
        private val roleId: Int,
        private val userManagementId: Int,
        private val leaderId: Int,

): PageKeyedDataSource<Int, User>() {

    val state: MutableLiveData<State> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>) {
        updateState(State.LOADING)
        compositeDisposable.add(
                apiservice.getUserPerIndex(FIRSTPAGE,
                        roleId = roleId,
                        userManagementId = userManagementId)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            updateState(State.DONE)
                            Log.d("PagedList", "PagedList ke $FIRSTPAGE : ${it.data}")
                            if (leaderId!=0) callback.onResult(it.data.filter { it.user_management_id  == leaderId}, null, FIRSTPAGE+1)

                            else callback.onResult(it.data, null, FIRSTPAGE+1)
                        }, {
                            updateState(State.ERROR)
                            Log.d("_errorUserPagedList", it.message.toString())
                        })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        compositeDisposable.add(
                apiservice.getUserPerIndex(params.key, roleId = roleId, userManagementId = userManagementId)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            if (leaderId!=0) callback.onResult(it.data.filter { it.user_management_id  == leaderId}, params.key+1)
                            else callback.onResult(it.data, params.key+1)
                            Log.d("PagedList", "PagedList ${params.key} : ${it.data}")
                        }, {
                            Log.d("_errorUserPagedList", it.message.toString())
                        })
        )
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
}