package id.android.kmabsensi.presentation.coworking

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.response.AddCoworkingSpaceResponse
import id.android.kmabsensi.data.repository.CoworkingSpaceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class CoworkingSpaceViewModel(val coworkingSpaceRepository: CoworkingSpaceRepository,
                              val schedulerProvider: SchedulerProvider): BaseViewModel() {

    val addCoworkingSpaceResponse = MutableLiveData<UiState<AddCoworkingSpaceResponse>>()

    fun addCoworkingSpace(coworkName: String,
                          description: String,
                          lat: Double,
                          lng: Double,
                          address: String,
                          status: Int,
                          slot: Int){

        addCoworkingSpaceResponse.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.addCoworkingSpace(coworkName, description, lat, lng, address, status, slot)
            .with(schedulerProvider)
            .subscribe({
                addCoworkingSpaceResponse.value = UiState.Success(it)
            },{
                addCoworkingSpaceResponse.value = UiState.Error(it)
            }))

    }

    override fun onError(error: Throwable) {

    }
}