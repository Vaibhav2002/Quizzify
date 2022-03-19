package dev.vaibhav.quizzify.util

import android.graphics.Color.argb
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.Circle
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.data.models.local.SpotLightView
import dev.vaibhav.quizzify.databinding.TargetLayoutBinding

fun Fragment.getBindingsMap(spotlightViews: List<SpotLightView>): Map<SpotLightView, TargetLayoutBinding> {
    val targetLayoutMap = mutableMapOf<SpotLightView, TargetLayoutBinding>()
    spotlightViews.forEach {
        targetLayoutMap[it] = TargetLayoutBinding.bind(
            layoutInflater.inflate(
                R.layout.target_layout,
                FrameLayout(requireContext())
            )
        )
    }
    return targetLayoutMap
}

fun Fragment.getTargets(
    spotlightViews: List<SpotLightView>,
    bindingsMap: Map<SpotLightView, TargetLayoutBinding>
) = spotlightViews.map {
    Target.Builder()
        .setAnchor(requireView().findViewById<View>(it.id))
        .setShape(Circle(100f))
        .setEffect(RippleEffect(100f, 200f, argb(30, 124, 255, 90)))
        .setOverlay(bindingsMap[it]!!.root)
        .build()
}

fun Fragment.getSpotLight(
    view: View,
    spotlightViews: List<SpotLightView>,
    onSpotLightEnd: () -> Unit
): Spotlight {
    val bindingMap = getBindingsMap(spotlightViews)
    val targets = getTargets(spotlightViews, bindingMap)
    val spotLight = Spotlight.Builder(requireActivity())
        .setTargets(targets)
        .setBackgroundColorRes(R.color.spotlightBackground)
        .setDuration(1000L)
        .setAnimation(DecelerateInterpolator(2f))
        .setOnSpotlightListener(object : OnSpotlightListener {
            override fun onStarted() = onSpotLightEnd()

            override fun onEnded() = Unit
        })
        .build()
    bindingMap.entries.forEach {
        it.value.nextButton.setOnClickListener { spotLight.next() }
        it.value.skipButton.setOnClickListener { spotLight.finish() }
        it.value.titleText.text = it.key.title
        it.value.desriptionText.text = it.key.description
    }
    return spotLight
}

fun Fragment.enableDisableButtonsWhileSpotlight(
    view: View,
    spotlightViews: List<SpotLightView>,
    isEnabled: Boolean
) {
    spotlightViews.forEach {
        view.findViewById<View>(it.id).isClickable = isEnabled
    }
}

fun Fragment.showSpotlight(
    view: View,
    spotlightViews: List<SpotLightView>,
    onSpotLightEnd: () -> Unit
): Spotlight {
    return getSpotLight(view, spotlightViews) {
        onSpotLightEnd()
    }
}