package dev.vaibhav.quizzify.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import dev.vaibhav.quizzify.R

@BindingAdapter("setImageUrl")
fun ImageView.setImageUrl(url: String?) {
//    Glide.with(context).load(url).into(this)
    load(url) {
        crossfade(true)
    }
}

@BindingAdapter("setProfilePic")
fun ImageView.setProfilePic(url: String?) {
    load(url) {
        crossfade(true)
        placeholder(R.drawable.blank_avatar)
        error(R.drawable.blank_avatar)
    }
}

@BindingAdapter("setImageRes")
fun ImageView.setImageRes(image: Int) {
    setImageResource(image)
}