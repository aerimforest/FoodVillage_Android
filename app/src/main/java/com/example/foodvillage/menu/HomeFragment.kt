package com.example.foodvillage.menu

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodvillage.R
import com.example.foodvillage.StoreInfoData
import com.example.foodvillage.ViewPagerAdapter
import com.example.foodvillage.databinding.FragmentHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_today_sale.view.*

class HomeFragment : Fragment() {

    private var talktalkList = arrayListOf(
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500)
    )

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

        binding.rcvHomeTodayPrice.adapter = TodayPriceAdapter()
        binding.rcvHomeTodayPrice.layoutManager = LinearLayoutManager(context)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvHomeTodayPrice.layoutManager = layoutManager
        binding.rcvHomeTodayPrice.setHasFixedSize(true)

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

    inner class TodayPriceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_today_sale, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = (holder as ViewHolder).itemView
            viewHolder.tv_product_name?.text = talktalkList[position].productName
            viewHolder.tv_store_name?.text = talktalkList[position].storeName
            viewHolder.tv_distance.text = talktalkList[position].distance.toString()
            viewHolder.tv_discount_rate?.text = talktalkList[position].discountRate.toString()
            viewHolder.tv_fixed_price?.text = talktalkList[position].fixedPrice.toString()
            viewHolder.tv_discounted_price?.text = talktalkList[position].discountedPrice.toString()
            viewHolder.tv_fixed_price.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            viewHolder.tv_fixed_price_won.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            // recyclerview item click listener
        }

        override fun getItemCount(): Int {
            return talktalkList.size
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