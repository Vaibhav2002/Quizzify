package dev.vaibhav.quizzify.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(url: String?) {
//    Glide.with(context).load(url).into(this)
    load(url) {
        crossfade(true)
    }
}

@BindingAdapter("setImageRes")
fun ImageView.setImageRes(image: Int) {
    setImageResource(image)
}