package id.android.kmabsensi.presentation.coworking

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.AddCoworkingSpaceResponse
import id.android.kmabsensi.data.remote.response.ListCoworkingSpaceResponse
import id.android.kmabsensi.data.repository.CoworkingSpaceRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class CoworkingSpaceViewModel(val coworkingSpaceRepository: CoworkingSpaceRepository,
                              val schedulerProvider: SchedulerProvider): BaseViewModel() {

    val coworkingSpace = MutableLiveData<UiState<ListCoworkingSpaceResponse>>()
    val crudCoworkingSpaceResponse = MutableLiveData<UiState<AddCoworkingSpaceResponse>>()

    fun getCoworkingSpace(){
        coworkingSpace.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.getCoworkingSpace()
            .with(schedulerProvider)
            .subscribe({
                coworkingSpace.value = UiState.Success(it)
            },{
                onError(it)
                coworkingSpace.value = UiState.Error(it)
            }))
    }

    fun addCoworkingSpace(coworkName: String,
                          description: String,
                          lat: Double,
                          lng: Double,
                          address: String,
                          status: Int,
                          slot: Int){

        crudCoworkingSpaceResponse.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.addCoworkingSpace(coworkName, description, lat, lng, address, status, slot)
            .with(schedulerProvider)
            .subscribe({
                crudCoworkingSpaceResponse.value = UiState.Success(it)
            },{
                crudCoworkingSpaceResponse.value = UiState.Error(it)
            }))
    }

    fun editCoworkingSpace(
        id: Int,
        coworkName: String,
        description: String,
        lat: Double,
        lng: Double,
        address: String,
        status: Int,
        slot: Int
    ){
        crudCoworkingSpaceResponse.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.editCoworkingSpace(id, coworkName, description, lat, lng, address, status, slot)
            .with(schedulerProvider)
            .subscribe({
                crudCoworkingSpaceResponse.value = UiState.Success(it)
            },{
                crudCoworkingSpaceResponse.value = UiState.Error(it)
            }))
    }

    fun deleteCoworkingSpace(id: Int){
        crudCoworkingSpaceResponse.value = UiState.Loading()
        compositeDisposable.add(coworkingSpaceRepository.deleteCoworkingSpace(id)
            .with(schedulerProvider)
            .subscribe({
                crudCoworkingSpaceResponse.value = UiState.Success(it)
            },{
                crudCoworkingSpaceResponse.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}