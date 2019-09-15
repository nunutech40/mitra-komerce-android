package id.android.kmabsensi.data.repository

import io.reactivex.Single
import id.android.kmabsensi.domain.Team

interface TeamRepository {

    fun getTeams(league: String) : Single<List<Team>>

}