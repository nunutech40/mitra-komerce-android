package id.android.kmabsensi.presentation.role

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.body.AssignReleasePositionParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.MenuRoleResponse
import id.android.kmabsensi.data.repository.RoleRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class RoleViewModel(
    private val roleRepository: RoleRepository,
    private val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val menuRoles by lazy {
        MutableLiveData<UiState<MenuRoleResponse>>()
    }

    val assignReleaseResult by lazy {
        MutableLiveData<UiState<BaseResponse>>()
    }

    fun getMenuRole(){
        menuRoles.value = UiState.Loading()
        compositeDisposable.add(roleRepository.getMenuRole()
            .with(schedulerProvider)
            .subscribe({
                menuRoles.value = UiState.Success(it)
            },{
                menuRoles.value = UiState.Error(it)
            }))
    }

    fun assignPosition(params: AssignReleasePositionParams){
        assignReleaseResult.value = UiState.Loading()
        compositeDisposable.add(roleRepository.assignPosition(params)
            .with(schedulerProvider)
            .subscribe({
                assignReleaseResult.value = UiState.Success(it)
            },{
                assignReleaseResult.value = UiState.Error(it)
            }))
    }

    fun releasePosition(params: AssignReleasePositionParams){
        assignReleaseResult.value = UiState.Loading()
        compositeDisposable.add(roleRepository.releasePosition(params)
            .with(schedulerProvider)
            .subscribe({
                assignReleaseResult.value = UiState.Success(it)
            },{
                assignReleaseResult.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}