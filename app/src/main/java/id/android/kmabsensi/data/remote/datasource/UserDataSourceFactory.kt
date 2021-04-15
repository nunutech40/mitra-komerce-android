package id.android.kmabsensi.data.remote.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.disposables.CompositeDisposable

class UserDataSourceFactory(
    private var apiService: ApiService,
    private var compositeDisposable: CompositeDisposable,
    private val roleId: Int = 0,
    private val userManagementId: Int = 0,
    private val leaderId: Int = 0
) : DataSource.Factory<Int, User>() {

    val userLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, User> {
        var userDataSource = UserDataSource(apiService, compositeDisposable, roleId, userManagementId, leaderId)
        userLiveData.postValue(userDataSource)
        return userDataSource
    }
}