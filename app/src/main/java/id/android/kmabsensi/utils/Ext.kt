package id.android.kmabsensi.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImageFromUrl(url: String){
    if (url.isEmpty()) return

    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun ImageView.loadCircleImage(url: String){
    if (url.isEmpty()) return

    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
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