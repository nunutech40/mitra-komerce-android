package id.android.kmabsensi.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.android.kmabsensi.data.remote.datasource.userdata.UserDataSource
import id.android.kmabsensi.data.remote.datasource.userdata.UserDataSourceFactory
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.State
import id.android.kmabsensi.utils.UiState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserRepository(val apiService: ApiService) {

//    fun getProfileUser(accessToken: String, userId: Int): Single<UserResponse> {
//        return apiService.getProfileData(accessToken, userId)
//    }
    private lateinit var factory : UserDataSourceFactory
    private val filterUser: MutableLiveData<UiState<UserResponse>> = MutableLiveData()
    private val searchUser: MutableLiveData<UiState<UserResponse>> = MutableLiveData()

    fun getDataUser(compositeDisposable: CompositeDisposable,
                    roleId: Int,
                    userManagementId: Int,
                    leaderId: Int
    ): LiveData<PagedList<User>> {

        factory = UserDataSourceFactory(apiService, compositeDisposable, roleId, userManagementId, leaderId)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(100)
            .build()

        return LivePagedListBuilder(factory, config).build()
    }

    fun getState(): LiveData<State>{
        return Transformations.switchMap(factory.userLiveData,
                UserDataSource::state)
    }

    fun getProfileUser(userId: Int): Single<UserResponse> {
        return apiService.getProfileData(userId)
    }

    fun getUserByRole(roleId: Int, userManagementId: Int = 0): Single<UserResponse>{
        return apiService.getUser(roleId = roleId, userManagementId = userManagementId)
    }

    fun getUserByPartner(noPartner: Int): Single<UserResponse>{
        return apiService.getUser(noPartner = noPartner)
    }


    fun filterUser(compositeDisposable: CompositeDisposable,
                   user_management_id: Int): LiveData<UiState<UserResponse>>{
        filterUser.value = UiState.Loading()
        compositeDisposable.add(apiService.filterUser(user_management_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    filterUser.value = UiState.Success(it)
                },{
                    filterUser.value = UiState.Error(it)
                }))
        return filterUser
    }

    fun searchUser(compositeDisposable: CompositeDisposable,
                   keyword: String): LiveData<UiState<UserResponse>>{
        searchUser.value = UiState.Loading()
        compositeDisposable.add(apiService.searchUser(keyword)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                searchUser.value = UiState.Success(it)
            },{
                searchUser.value = UiState.Error(it)
            }))
        return searchUser
    }

}