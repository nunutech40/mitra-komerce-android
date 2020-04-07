package id.android.kmabsensi.data.pref

import android.content.Context
import com.github.ajalt.timberkt.d

class PreferencesHelper(val context: Context){

    companion object {
        private val PREF_NAME = "kmabsensi"

        //KEY
        val ACCESS_TOKEN_KEY = "access_token"
        val FCM_TOKEN = "fcm_token"
        val PROFILE_KEY = "profile"
        val IS_LOGIN = "is_login"
    }

    private var sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun clear() {
        sharedPref.edit().clear().apply()
    }

    fun saveString(key: String, value: String) {
        d { "save $value to $key" }
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String = sharedPref.getString(key, "") ?: ""

    fun saveInt(key: String, value: Int) {
        d { "save $value to $key" }
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun saveLong(key: String, value: Long) {
        val editor = sharedPref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String): Long = sharedPref.getLong(key, 0)

    fun getInt(key: String): Int = sharedPref.getInt(key, 0)

    fun getBoolean(key: String): Boolean = sharedPref.getBoolean(key, false)

    fun saveBoolean(key: String, value: Boolean) {
        d { "save $value to $key" }
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

}