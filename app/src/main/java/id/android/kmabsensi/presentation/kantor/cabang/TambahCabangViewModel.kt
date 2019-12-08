package id.android.kmabsensi.presentation.kantor.cabang

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.CrudOfficeResponse
import id.android.kmabsensi.data.remote.response.UserResponse
import id.android.kmabsensi.data.repository.OfficeRepository
import id.android.kmabsensi.data.repository.UserRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class TambahCabangViewModel(
    val userRepository: UserRepository,
    val officeRepository: OfficeRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val crudCabangState = MutableLiveData<UiState<CrudOfficeResponse>>()

    val penanggungJawabState = MutableLiveData<UiState<UserResponse>>()

    fun getUserManagement() {
        compositeDisposable.add(userRepository.getUserByRole(2)
            .with(schedulerProvider)
            .subscribe({
                penanggungJawabState.value = UiState.Success(it)
            }, {
                penanggungJawabState.value = UiState.Error(it)
            })
        )
    }

    fun addOffice(
        officeName: String,
        lat: String,
        lng: String,
        address: String,
        pjUserId: Int
    ) {
        compositeDisposable.add(officeRepository.addOffice(officeName, lat, lng, address, pjUserId)
            .with(schedulerProvider)
            .subscribe({
                crudCabangState.value = UiState.Success(it)
            },{
                crudCabangState.value = UiState.Error(it)
            }))
    }

    fun editOffice(
        id: Int,
        officeName: String,
        lat: String,
        lng: String,
        address: String,
        pjUserId: Int
    ){
        compositeDisposable.add(officeRepository.editOffice(id, officeName, lat, lng, address, pjUserId)
            .with(schedulerProvider)
            .subscribe({
                crudCabangState.value = UiState.Success(it)
            },{
                crudCabangState.value = UiState.Error(it)
            }))
    }

    fun deleteOffice(officeId: Int){
        compositeDisposable.add(officeRepository.deleteOffice(officeId)
            .with(schedulerProvider)
            .subscribe({
                crudCabangState.value = UiState.Success(it)
            },{
                crudCabangState.value = UiState.Error(it)
            }))
    }



    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}