package com.example.foodvillage.menu

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodvillage.PopularStoreData
import com.example.foodvillage.R
import com.example.foodvillage.storeList.StoreInfoData
import com.example.foodvillage.ViewPagerAdapter
import com.example.foodvillage.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.item_today_popular_store.view.*
import kotlinx.android.synthetic.main.item_today_sale.view.*
import kotlinx.android.synthetic.main.item_today_sale.view.tv_store_name

class HomeFragment : Fragment() {

    private var talktalkList = arrayListOf(
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500),
        StoreInfoData("고등어", "이태리로 간 고등어", 100, 30, 5000, 3500)
    )

    private var popularStoreList = arrayListOf(
        PopularStoreData("이태리로 간 고등어", 10, 5, "고등어"),
        PopularStoreData("이태리로 간 고등어", 10, 5, "고등어"),
        PopularStoreData("이태리로 간 고등어", 10, 5, "고등어"),
        PopularStoreData("이태리로 간 고등어", 10, 5, "고등어"),
        PopularStoreData("이태리로 간 고등어", 10, 5, "고등어")
    )

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private var myHandler = MyHandler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.main_green)
        window.decorView.systemUiVisibility = 0

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcvHomeTodayPrice.adapter = TodayPriceAdapter()
//        binding.rcvHomeTodayPrice.layoutManager = LinearLayoutManager(context)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvHomeTodayPrice.layoutManager = layoutManager
        binding.rcvHomeTodayPrice.setHasFixedSize(true)

        binding.rcvHomePopularStore.adapter = PopularStoreAdapter()
        binding.rcvHomePopularStore.layoutManager = LinearLayoutManager(context)
        binding.rcvHomePopularStore.setHasFixedSize(true)

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

    inner class PopularStoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_today_popular_store, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = (holder as ViewHolder).itemView
            viewHolder.tv_popular_store_name.text = popularStoreList[position].storeName
            viewHolder.tv_travel_time.text = popularStoreList[position].travelTime.toString()
            viewHolder.tv_max_discount_rate.text = popularStoreList[position].maxDiscountRate.toString()
            viewHolder.tv_discount_product.text = popularStoreList[position].discountProduct

            // recyclerview item click listener
        }

        override fun getItemCount(): Int {
            return popularStoreList.size
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
            R.drawable.banner,
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