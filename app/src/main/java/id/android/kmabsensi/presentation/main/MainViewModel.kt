package id.android.kmabsensi.presentation.main

import androidx.lifecycle.MutableLiveData
import id.android.kmabsensi.data.repository.TeamRepository
import id.android.kmabsensi.domain.Team
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.UiState
import id.android.momakan.utils.scheduler.SchedulerProvider
import id.android.momakan.utils.scheduler.with

class MainViewModel(
    val teamRepository: TeamRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val teamState = MutableLiveData<UiState<List<Team>>>()

    fun getTeams(league: String) {
        teamState.value = UiState.Loading()
        compositeDisposable.add(teamRepository.getTeams(league)
            .with(schedulerProvider)
            .subscribe({
                teamState.value = UiState.Success(it)
            }, this::onError)
        )
    }

    override fun onError(error: Throwable) {
        teamState.value = UiState.Error(error)
    }

}