package id.android.kmabsensi.data.remote.datasource.kmpoint

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingRequestModel
import io.reactivex.disposables.CompositeDisposable

class ShoppingDSFactory(
    private val apiService: ApiService,
    private val compositeDisposable: CompositeDisposable,
    private val user_requester_id: Int?,
    private val status : String?
): DataSource.Factory<Int, ShoppingRequestModel>() {

    var shoppingLivedata = MutableLiveData<ShoppingDataSource>()

    override fun create(): DataSource<Int, ShoppingRequestModel> {
        var shoppingDS = ShoppingDataSource(apiService, compositeDisposable, status = status, user_requester_id = user_requester_id)
        shoppingLivedata.postValue(shoppingDS)
        return shoppingDS
    }
}