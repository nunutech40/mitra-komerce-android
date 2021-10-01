package id.android.kmabsensi.utils

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType
import okhttp3.RequestBody
import id.android.kmabsensi.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun ImageView.loadImageFromUrl(url: String){
    if (url.isEmpty()) return

    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions().placeholder(R.drawable.placeholder).centerCrop())
        .into(this)
}

fun ImageView.loadCircleImage(url: String){
    if (url.isEmpty()) return

    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_user)
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.loadCircleImageSdm(url: String){
    if (url.isEmpty()) return

    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_user))
        .into(this)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invis(){
    this.visibility = View.INVISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}

fun View.isVisib() = this.visibility

fun convertRp(number: Double) = String.format("Rp %,.0f", number).replace(",".toRegex(), ".")
fun convertRp(number: BigDecimal) = String.format("Rp %,.0f", number).replace(",".toRegex(), ".")
fun convertRpWithoutSpace(number: Double) = String.format("Rp%,.0f", number).replace(",".toRegex(), ".")

fun TextView.spannableQuestionEvaluation(question: String, notes: String){
    val spannable = SpannableString("$question $notes")
    spannable.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this.context, R.color._929292)),
        question.length, spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannable
}

fun String.createRequestBodyText() : RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun File.createRequestBody() : RequestBody {
    return this.asRequestBody("image/*".toMediaTypeOrNull())
}

fun File.createRequestBodyFile() : RequestBody {
    return this.asRequestBody("*/*".toMediaTypeOrNull())
}

fun Double.rounTwoDigitDecimal(): Double {
    val filterNumber = this.toString().replace(",", ".").toDouble()
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("##.##", symbols);
    val res = df.format(filterNumber).toDouble()
    return res
}

fun showDialogNotYetCheckout(context: Context) {
    val dialog = MaterialDialog(context).show {
        cornerRadius(16f)
        customView(
            R.layout.dialog_retake_foto,
            scrollable = false,
            horizontalPadding = true,
            noVerticalPadding = true
        )
    }
    val customView = dialog.getCustomView()
    val btn_retake = customView.findViewById<Button>(R.id.btn_retake)
    val txtKeterangan = customView.findViewById<TextView>(R.id.tv_detection)

    txtKeterangan.text = context.getString(R.string.ket_belum_waktu_pulang)
    btn_retake.text = context.getString(R.string.ok)

    btn_retake.setOnClickListener {
        dialog.dismiss()
    }
}


fun showDialogWarningConfirm(context: Context, listener: onWarningClicked) {
    val dialog = MaterialDialog(context).show {
        cornerRadius(16f)
        customView(
            R.layout.dialog_warning_confirm,
            scrollable = false,
            horizontalPadding = true,
            noVerticalPadding = true
        )
    }
    val customView = dialog.getCustomView()
    val btnCancle = customView.findViewById<Button>(R.id.btn_cancle)
    val btnYa = customView.findViewById<TextView>(R.id.btn_ya)

    btnCancle.setOnClickListener {
        dialog.dismiss()
    }
    btnYa.setOnClickListener {
        listener.onCLick()
        dialog.dismiss()
    }
}

interface onWarningClicked{
    fun onCLick()
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)



