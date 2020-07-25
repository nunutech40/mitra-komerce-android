package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.service.ApiService

class AttachmentRepository(val apiService: ApiService) {

    fun deleteAttachment(id: Int) = apiService.deleteAttachment(id)

}