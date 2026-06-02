package com.task.walkin.model

import androidx.annotation.DrawableRes

data class BannerItem(
    val id: Int,
    @DrawableRes val imageResId: Int
) {
    companion object {
        fun getDummyList(): List<BannerItem> = listOf(
            BannerItem(1, com.task.walkin.R.drawable.offer),
            BannerItem(2, com.task.walkin.R.drawable.banner2),
            BannerItem(3, com.task.walkin.R.drawable.offer)
        )
    }
}