package id.android.kmabsensi.data.remote.datasource.kmpoint

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import id.android.kmabsensi.presentation.kmpoint.penarikan.WithdrawMainModel
import io.reactivex.disposables.CompositeDisposable

class WithdrawDSFactory(
    private val apiService: ApiService,
    private val compositeDisposable: CompositeDisposable
): DataSource.Factory<Int, WithdrawMainModel>() {

    var withdrawLivedata = MutableLiveData<WithdrawDataSource>()

    override fun create(): DataSource<Int, WithdrawMainModel> {
        var withdrawDS = WithdrawDataSource(apiService, compositeDisposable)
        withdrawLivedata.postValue(withdrawDS)
        return withdrawDS
    }
}