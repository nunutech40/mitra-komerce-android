package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.AddNoteParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.ListNoteResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class NoteRepository(val apiService: ApiService){

    fun getListNote(): Single<ListNoteResponse> {
        return apiService.getListNote()
    }

    fun addNote(params: AddNoteParams): Single<BaseResponse> {
        return apiService.addNote(params)
    }

}