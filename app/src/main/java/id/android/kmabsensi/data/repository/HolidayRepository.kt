package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.service.ApiService

class HolidayRepository(val apiService: ApiService) {

    fun getHoliday() = apiService.getHoliday()

}