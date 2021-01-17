package id.android.kmabsensi.data.repository

import id.android.kmabsensi.data.db.dao.CityDao
import id.android.kmabsensi.data.db.dao.ProvinceDao
import id.android.kmabsensi.data.db.entity.City
import id.android.kmabsensi.data.db.entity.Province
import id.android.kmabsensi.data.remote.response.ListAreaResponse
import id.android.kmabsensi.data.remote.service.ApiService
import io.reactivex.Observable
import io.reactivex.Single

class AreaRepository(val apiService: ApiService,
                     private val provinceDao: ProvinceDao,
                     private val cityDao: CityDao){

    fun getDataArea() : Observable<ListAreaResponse>{
        return apiService.getDataArea()
            .doOnNext {
                /*save to local*/
                provinceDao.insert(it.data.provinces)
                cityDao.insert(it.data.cities)
            }
    }

    fun getProvince(): Single<List<Province>>{
        return provinceDao.findAll()
    }

    fun getCityByKodeWilayah(code: String): Single<List<City>>{
        return cityDao.findByKodeWilayah(code)
    }

}