package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.service.ApiService

class ProductKnowledgeRepository(val apiService: ApiService) {

    fun getListProductKnowledge(noPartner: Int) =
        apiService.getListProductKnowledge(mapOf("no_partner" to noPartner))

}