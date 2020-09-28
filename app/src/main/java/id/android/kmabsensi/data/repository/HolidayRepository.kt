package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.body.AddHolidayParams
import id.android.kmabsensi.data.remote.body.EditHolidayParams
import id.android.kmabsensi.data.remote.service.ApiService

class HolidayRepository(val apiService: ApiService) {

    fun getHoliday() = apiService.getHoliday()

    fun addHoliday(params: AddHolidayParams) = apiService.addHoliday(params)

    fun editHoliday(params: EditHolidayParams) = apiService.editHoliday(params)

    fun deleteHoliday(holidayId: Int) = apiService.deleteHoliday(holidayId)
}