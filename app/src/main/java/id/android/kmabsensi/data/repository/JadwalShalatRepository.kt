package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.JADWAL_SHOLAT_URL
import id.android.kmabsensi.utils.getTodayDate

class JadwalShalatRepository(val apiService: ApiService) {

    fun getJadwalShalat() = apiService.getJadwalShalat(JADWAL_SHOLAT_URL+ getTodayDate())
}