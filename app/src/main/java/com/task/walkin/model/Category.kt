package com.task.walkin.model

import androidx.annotation.DrawableRes
import com.task.walkin.R

data class Category(
    val id: Int,
    val title: String,
    @DrawableRes val imageResId: Int
) {
    companion object {
        fun getDummyList(): List<Category> = listOf(
            Category(1, "Salon",       R.drawable.saloon),
            Category(2, "Retail",         R.drawable.ic_retail),
            Category(3, "Malls",      R.drawable.mall),
            Category(4, "Gym",    R.drawable.vector_smart_object_1_2),
            Category(5, "Restaurants",     R.drawable.vector_smart_object_copy_3),
            Category(6, "Grocery",      R.drawable.vector_smart_object_4),
            Category(7, "Salon",    R.drawable.saloon),
            Category(8, "Retail",  R.drawable.ic_retail),
            Category(9, "Malls",  R.drawable.mall)

        )
    }
}
