package com.task.walkin.ui.dashboard.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.task.walkin.databinding.FragmentHomeBinding
import com.task.walkin.model.BannerItem
import com.task.walkin.model.Category
import com.task.walkin.ui.dashboard.home.adapter.BannerSliderAdapter
import com.task.walkin.ui.dashboard.home.adapter.CategoryAdapter
import com.task.walkin.utils.AutoSliderUtil

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val banners = BannerItem.getDummyList()
        val sliderAdapter = BannerSliderAdapter(banners)

        binding.imageSlider.adapter = sliderAdapter
        binding.indicator.setViewPager(binding.imageSlider)

        AutoSliderUtil.startAutoSlider(binding.imageSlider) {
            banners.size
        }

        val categoryAdapter = CategoryAdapter(
            Category.getDummyList()
        ) {
            Toast.makeText(
                requireContext(),
                it.title,
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.tilesRecyclerview.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = categoryAdapter
        }

        binding.ivClose.setOnClickListener {
            binding.staySafeCard.visibility = View.GONE
        }

        return binding.root
    }
}
