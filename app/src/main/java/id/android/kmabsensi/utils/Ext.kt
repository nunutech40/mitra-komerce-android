package id.android.kmabsensi.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType
import okhttp3.RequestBody
import id.android.kmabsensi.R

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