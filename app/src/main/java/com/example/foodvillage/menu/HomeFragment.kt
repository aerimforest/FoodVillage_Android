package com.example.foodvillage.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.foodvillage.R
import com.example.foodvillage.ViewPagerAdapter
import com.example.foodvillage.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private var myHandler = MyHandler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerHome.adapter = ViewPagerAdapter(getBannerItemList())
        binding.viewPagerHome.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerHome.setCurrentItem(currentPosition, false)
        binding.dotsIndicatorHome.setViewPager2(binding.viewPagerHome)

        binding.viewPagerHome.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart()
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                        ViewPager2.SCROLL_STATE_SETTLING -> {
                        }
                    }
                }
            })
        }
    }

    private fun autoScrollStart() {
        myHandler.removeMessages(0)
        myHandler.sendEmptyMessageDelayed(0, 1500)
    }

    private fun autoScrollStop() {
        myHandler.removeMessages(0)
    }

    @SuppressLint("HandlerLeak")
    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == 0) {
                binding.viewPagerHome.setCurrentItem(++currentPosition, true)
                autoScrollStart()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart()
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    private fun getBannerItemList(): ArrayList<Int> {
        return arrayListOf(
            R.drawable.delete_sky,
            R.drawable.delete_light,
            R.drawable.delete_jeju,
            R.drawable.delete_flower
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}