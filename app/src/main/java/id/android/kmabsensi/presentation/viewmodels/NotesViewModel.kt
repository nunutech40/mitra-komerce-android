package id.android.kmabsensi.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.remote.body.AddNoteParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListNoteResponse
import id.android.kmabsensi.data.repository.NoteRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.rx.SchedulerProvider
import id.android.kmabsensi.utils.rx.with

class NotesViewModel(
    val noteRepository: NoteRepository,
    val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    val notes by lazy { MutableLiveData<UiState<ListNoteResponse>>() }

    val crudResponse by lazy { MutableLiveData<UiState<BaseResponse>>() }

    fun getListNote(){
        notes.value = UiState.Loading()
        compositeDisposable.add(noteRepository.getListNote()
            .with(schedulerProvider)
            .subscribe({
                notes.value = UiState.Success(it)
            },{
                notes.value = UiState.Error(it)
            }))
    }

    fun addNote(params: AddNoteParams){
        crudResponse.value = UiState.Loading()
        compositeDisposable.add(noteRepository.addNote(params)
            .with(schedulerProvider)
            .subscribe({
                crudResponse.value = UiState.Success(it)
            },{
                crudResponse.value = UiState.Error(it)
            }))
    }

    override fun onError(error: Throwable) {

    }
}