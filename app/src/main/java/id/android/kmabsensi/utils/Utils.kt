package id.android.kmabsensi.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.tapadoo.alerter.Alerter
import com.wildma.idcardcamera.utils.FileUtils
import id.android.kmabsensi.R
import id.zelory.compressor.Compressor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT = "yyyy-MM-dd"
const val DATE_FORMAT2 = "dd MMMM yyyy"
const val DATE_FORMAT3 = "EEEE, dd MMM yyyy"

val APP_NAME = "KampungMarketer"
val IMG_DIRECTORY_NAME: String="ABSENSI"
val BASE_DIR = APP_NAME + File.separator
val DIR_ROOT:String= StringBuffer().append(FileUtils.getRootPath()).append(File.separator).append(BASE_DIR).toString()


fun getRoleName(roleId: Int): String = when(roleId) {
    1 -> ROLE_ADMIN
    2 -> ROLE_MANAGEMEMENT
    else -> ROLE_SDM
}

fun getTodayDate(): String {
    val cal = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    return simpleDateFormat.format(cal.time)
}

fun getTodayDateTimeDay(): String {
    val cal = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT3, Locale.getDefault())

    return simpleDateFormat.format(cal.time)
}


fun getDateString(date: Date): String{
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    return simpleDateFormat.format(date)
}

fun getDateStringFormatted(date: Date): String{
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT2, Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun createAlertError(activity: Activity, title: String, message: String, duration: Long = 5000){
    Alerter.create(activity)
        .setTitle(title)
        .setText(message)
        .setDuration(duration)
        .setBackgroundColorRes(R.color.colorAccent)
        .show()
}

fun createAlertSuccess(activity: Activity?, message: String){
    Alerter.create(activity)
        .setTitle("Berhasil")
        .setText(message)
        .setIcon(R.drawable.ic_done)
        .setBackgroundColorRes(R.color.colorSuccess)
        .show()

}

fun compressCustomerCaptureImage(context: Context, imagePath: String): String? {
    val compressedImageFile = Compressor(context)
        .setQuality(70)
        .setCompressFormat(Bitmap.CompressFormat.JPEG)
        .setDestinationDirectoryPath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).path
        )
        .compressToFile(File(imagePath))
    return compressedImageFile.path
}

fun deleteCaptureFileFromPath(path: String) {
    val fdelete = File(path)
    if (fdelete.exists()) {
        if (fdelete.delete()) {
            //LoggerUtils.error(Helpers::class.simpleName,"file delete done")
        } else {
            //LoggerUtils.error(Helpers::class.simpleName,"no file found")
        }
    }
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }
