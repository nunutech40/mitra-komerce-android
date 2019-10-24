package id.android.kmabsensi.presentation.jabatan

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListPositionResponse
import id.android.kmabsensi.data.repository.JabatanRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class JabatanViewModel(val jabatanRepository: JabatanRepository,
                       val schedulerProvider: SchedulerProvider) : BaseViewModel() {

    val crudResponse = MutableLiveData<UiState<BaseResponse>>()
    val positionResponse = MutableLiveData<UiState<ListPositionResponse>>()

    fun addPosition(positionName: String){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(jabatanRepository.addPosition(positionName)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            })
        )
    }

    fun getPositions(){
        positionResponse.value = UiState.Loading()
        compositeDisposable.add(jabatanRepository.getPosition()
            .with(schedulerProvider)
            .subscribe({
                positionResponse.value = UiState.Success(it)
            },{
                positionResponse.value = UiState.Error(it)
            }))
    }

    fun editPosition(id: Int, positionName: String){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(jabatanRepository.editPosition(id, positionName)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            })
        )
    }

    fun deletePosition(id: Int){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(jabatanRepository.deletePosition(id)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            })
        )
    }

    override fun onError(error: Throwable) {
        Crashlytics.log(error.message)
    }
}