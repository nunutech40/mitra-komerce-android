package id.android.kmabsensi.data.pref

import android.content.Context

class PrefData(val context: Context) {
        companion object{
            val PREF_DATA = "KEYBOARD"
            val KEYBOARD_TYPE = "TYPE"
            val DESTINATION = "DESTINATION"
            val ORIGIN = "ORIGIN"
            val DESTINATION_TYPE = "DESTINATION_TYPE"
            val ORIGIN_TYPE = "ORIGIN_TYPE"
            val ORIGINDISTRICT = "ORIGIN_DISTRICT"
            val DESTINATIONDISTRICT = "DESTINATION_DISTRICT"
        }
        private var PreferenceData =context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE)
        fun clear(){
            PreferenceData.edit().clear().apply()
        }
        fun saveString(key: String, value: String){
            val editor = PreferenceData.edit()
            editor.putString(key, value)
            editor.apply()
        }
        fun getString(key: String): String{
            return PreferenceData.getString(key, "") ?: ""
        }
        fun saveInt(key: String, value: Int){
            val editor = PreferenceData.edit()
            editor.putInt(key, value)
            editor.apply()
        }
        fun getInt(key: String, value: Int):Int{
            return PreferenceData.getInt(key, 0)
        }
        fun saveBoolean(key: String, value: Boolean){
            val editor = PreferenceData.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }
        fun getBoolean(key: String): Boolean{
            return PreferenceData.getBoolean(key, true)
        }
}