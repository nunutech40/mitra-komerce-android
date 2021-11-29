package id.android.kmabsensi.utils.komboard

import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

class BackIntentService: IntentService("BackIntentService") {

    init {
        instance = this
    }
    companion object{
        private lateinit var instance: BackIntentService
        private lateinit var packet: PackageManager
        private lateinit var launch: Intent
        var isRuning = false

        fun stopService(){
            Log.d("stoppingService", "stopService: ")
            isRuning = false
            instance.stopSelf()
        }
    }
    override fun onHandleIntent(p0: Intent?) {
        isRuning = true
        val uri: Uri = Uri.parse("market://details?id=id.android.kmabsensi")
        val intent = Intent(applicationContext.getPackageManager().getLaunchIntentForPackage("id.android.kmabsensi"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        Log.d("runingIntentService", "running... ")

    }
}