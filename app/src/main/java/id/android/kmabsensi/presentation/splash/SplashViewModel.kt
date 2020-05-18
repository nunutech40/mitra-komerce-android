package id.android.kmabsensi.presentation.splash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.repository.SdmRepository
import id.android.kmabsensi.presentation.base.BaseViewModel
import id.android.kmabsensi.utils.rx.SchedulerProvider

/**
 * Created by Abdul Aziz on 2020-01-31.
 */
class SplashViewModel(val sdmRepository: SdmRepository,
                      val prefHelper: PreferencesHelper,
                      val schedulerProvider: SchedulerProvider
) : BaseViewModel() {


    fun saveFcmtoken(token: String) {
//        prefHelper.saveString(PreferencesHelper.FCM_TOKEN, token)
    }



    override fun onError(error: Throwable) {
        error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
    }
}