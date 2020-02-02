package id.android.kmabsensi.services

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.splash.SplashActivity
import org.json.JSONException
import java.util.*

/**
 * Created by Abdul Aziz on 2020-01-27.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseToken"
    private lateinit var notificationManager: NotificationManager
    private val ADMIN_CHANNEL_ID = "DMSChannel"


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token: " + token)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.let {
            if (remoteMessage != null) {
                Log.i(TAG, "Message data payload: " + remoteMessage.data)
                try {
//                    val data = JSONObject(remoteMessage.data.toString())
                    val title = remoteMessage.data["title"]
                    val message = remoteMessage.data["message"]

                    //val count = data.getString("badgecount")
//                    val clickaction = remoteMessage.data["click_action"]

//                    saveDataNotification(titl!!, msg!!)
                    sendNotification2(title, message)
                } catch (e: JSONException) {
                    Log.d(TAG, "Message data payload: " + e.message)
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendNotification2(title: String?, messageBody: String?) {
        val intent: Intent = Intent(this, SplashActivity::class.java)
//        if (click_action == "NOTIFICATIONACTIVITY") {
//            intent = Intent(this, NotificationsActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        } else if (click_action == "APPDASHBOARDACTIVITY") {
//            intent = Intent(this, AppDashboard::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        } else {
//            intent = Intent(this, AppDashboard::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupNotificationChannels()
        }
        val notificationId = Random().nextInt(60000)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_km_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun setupNotificationChannels() {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        adminChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        adminChannel.setSound(defaultSoundUri, audioAttributes)
        notificationManager.createNotificationChannel(adminChannel)
    }


}