package id.android.kmabsensi.data.remote.datasource.presentasiuser

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.android.kmabsensi.data.remote.response.Presence
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.disposables.CompositeDisposable

class PrsentasiDataSourceFactory(
        private var apiService: ApiService,
        private var compositeDisposable: CompositeDisposable,
        private var body: PresentasiBody
): DataSource.Factory<Int, Presence>() {
    val presentasiLiveData = MutableLiveData<PresentasiDataSource>()
    override fun create(): DataSource<Int, Presence> {
        var prsentasiDataSource = PresentasiDataSource(apiService, compositeDisposable, body)
        presentasiLiveData.postValue(prsentasiDataSource)
        return prsentasiDataSource
    }
}