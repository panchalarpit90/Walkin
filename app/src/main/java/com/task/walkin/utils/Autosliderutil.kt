package com.task.walkin.utils

import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AutoSliderUtil {

    private var job: Job? = null
    private const val DELAY_MS = 3000L

    fun startAutoSlider(viewPager: ViewPager2, getSize: () -> Int) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(DELAY_MS)
                val size = getSize()
                if (size == 0) continue
                val next = (viewPager.currentItem + 1) % size
                viewPager.setCurrentItem(next, true)
            }
        }
    }

    fun stopAutoSlider() {
        job?.cancel()
        job = null
    }
}