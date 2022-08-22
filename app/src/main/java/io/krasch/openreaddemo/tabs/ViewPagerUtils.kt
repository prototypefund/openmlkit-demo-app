package io.krasch.openreaddemo.tabs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2


fun disableAnimations(viewPager: ViewPager2) {
    for (i in 0 until viewPager.childCount) {
        val view: View = viewPager.getChildAt(i)
        if (view is RecyclerView) {
            val animator = (view as RecyclerView).itemAnimator
            if (animator != null) {
                (animator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            break
        }
    }
}