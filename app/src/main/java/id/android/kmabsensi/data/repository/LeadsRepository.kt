package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.komship.InputNotesParams
import id.android.kmabsensi.data.remote.response.BaseResponse
import id.android.kmabsensi.data.remote.response.komship.KomLeadsResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Single

class LeadsRepository(val apiService: ApiService) {

    fun getLeadsFilter(
        idUser: Int, idPartner: Int, filterDate: String
    ): Single<KomLeadsResponse> = apiService.getLeadsFilter(idUser, idPartner, filterDate)

    fun deleteLeads(
        id: Int
    ): Single<BaseResponse> = apiService.deleteLeads(id)

    fun inputNotesLeads(
        p: InputNotesParams
    ): Single<BaseResponse> {
        val body = mapOf(
            "user_id" to p.idUser,
            "partner_id" to p.idPartner,
            "notes" to p.notes,
            "date_all_leads" to p.dateAllLeads
        )
        return apiService.inputNotesLeads(body)
    }
}