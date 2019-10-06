package id.android.kmabsensi.data.repository

import io.reactivex.Single
import id.android.kmabsensi.data.db.dao.TeamDao
import id.android.kmabsensi.domain.Team
import id.android.kmabsensi.utils.mapToListDomain

class TeamRepositoryImpl(val teamService: TeamService,
                         val teamDao: TeamDao) : TeamRepository{

    override fun getTeams(league: String): Single<List<Team>> {
        return teamService.getAllTeams(league)
            .map { mapToListDomain(it.teams) }
    }
}