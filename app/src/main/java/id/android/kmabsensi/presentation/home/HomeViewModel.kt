package id.android.kmabsensi.presentation.home

import com.google.gson.Gson
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseViewModel

class HomeViewModel(private val preferencesHelper: PreferencesHelper) : BaseViewModel() {

    fun getUserData() : User {
        return Gson().fromJson<User>(preferencesHelper.getString(PreferencesHelper.PROFILE_KEY), User::class.java)
    }

    fun clearPref(){
        preferencesHelper.clear()
    }

    override fun onError(error: Throwable) {

    }
}