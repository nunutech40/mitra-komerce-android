package id.android.kmabsensi.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType
import okhttp3.RequestBody
import id.android.kmabsensi.R
import java.io.File

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

fun String.createRequestBodyText() : RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), this)
}

fun convertRp(number: Double) = String.format("Rp %,.0f", number).replace(",".toRegex(), ".")

fun TextView.spannableQuestionEvaluation(question: String, notes: String){
    val spannable = SpannableString("$question $notes")
    spannable.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this.context, R.color._929292)),
        question.length, spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannable
}