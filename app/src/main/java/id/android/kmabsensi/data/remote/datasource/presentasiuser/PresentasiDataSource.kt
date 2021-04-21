package id.android.kmabsensi.data.remote.datasource.presentasiuser

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.data.remote.response.Report
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
private const val FIRSTPAGE = 1
class PresentasiDataSource(
        private var apiservice: ApiService,
        private val compositeDisposable: CompositeDisposable,
        private val body: PresentasiBody
) : PageKeyedDataSource<Int, Presence>(){
    val state: MutableLiveData<State> = MutableLiveData()
    val report: MutableLiveData<Report> = MutableLiveData()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Presence>) {
        updateState(State.LOADING)
        val body = mapOf(
                "role_id" to body.roleId,
                "user_management_id" to body.userManagementId,
                "office_id" to body.officeId,
                "no_partner" to 0,
                "start_date" to body.startDate,
                "end_date" to body.endDate
        )
        compositeDisposable.add(
                apiservice.presenceReportFilteredPaging(FIRSTPAGE, body)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.d("_errorPresentation", "${it.data.presence}")
                            updateState(State.DONE)
                            updateReport(it.data.report)
                            callback.onResult(it.data.presence, null, FIRSTPAGE +1)
                        }, {
                            updateState(State.ERROR)
                            Log.d("_errorPresentation", it.message.toString())
                        })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Presence>) {
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Presence>) {
        val body = mapOf(
                "role_id" to body.roleId,
                "user_management_id" to body.userManagementId,
                "office_id" to body.officeId,
                "no_partner" to 0,
                "start_date" to body.startDate,
                "end_date" to body.endDate
        )
        compositeDisposable.add(
                apiservice.presenceReportFilteredPaging(params.key, body)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.d("_errorPresentationnext", "${it.data}")
                            callback.onResult(it.data.presence, params.key +1)
                        }, {
                            updateState(State.ERROR)
                            Log.d("_errorPresentationnext", it.message.toString())
                        })
        )
    }

    private fun updateReport(report: Report) {
        this.report.postValue(report)
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
}