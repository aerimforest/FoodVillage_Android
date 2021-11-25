package com.example.foodvillage.menu

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.foodvillage.AddressSettingAcitivity
import com.example.foodvillage.MyMapActivity
import com.example.foodvillage.R
import com.example.foodvillage.storeList.StoreListActivity
import com.example.foodvillage.ViewPagerAdapter
import com.example.foodvillage.databinding.FragmentHomeBinding
import com.example.foodvillage.schema.Product
import com.example.foodvillage.schema.Store
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_today_popular_store.view.*
import kotlinx.android.synthetic.main.item_today_sale.view.imv_product
import kotlinx.android.synthetic.main.item_today_sale.view.tv_discount_rate
import kotlinx.android.synthetic.main.item_today_sale.view.tv_discounted_price
import kotlinx.android.synthetic.main.item_today_sale.view.tv_distance
import kotlinx.android.synthetic.main.item_today_sale.view.tv_fixed_price
import kotlinx.android.synthetic.main.item_today_sale.view.tv_fixed_price_won
import kotlinx.android.synthetic.main.item_today_sale.view.tv_product_name
import kotlinx.android.synthetic.main.item_today_sale.view.tv_store_name
import kotlin.math.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private var myHandler = MyHandler()

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private var databaseStoreReference: DatabaseReference = firebaseDatabase.reference
    private var todayPriceList = arrayListOf<Product>()
    private var todayStoreList = arrayListOf<Store>()

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

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 주소 설정 페이지로 이동하기
        binding.tvHomeLocation.setOnClickListener{
            val intent=Intent(context, AddressSettingAcitivity::class.java)
            startActivity(intent)
        }
        binding.rcvHomeTodayPrice.adapter = TodayPriceAdapter()
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

        binding.llyHomeAll.setOnClickListener {
            val intent = Intent(context, StoreListActivity::class.java)
            startActivity(intent)
        }
    }

    inner class TodayPriceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            databaseReference = firebaseDatabase.getReference("products")
            databaseReference.orderByChild("discountRate").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    // ArrayList 비워줌
                    todayPriceList.clear()

                    for (postSnapshot in dataSnapshot.children) {
                        val item = postSnapshot.getValue(Product::class.java)

                        if (item != null) {
                            todayPriceList.add(0, item)
                        }
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
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

            viewHolder.tv_product_name?.text = todayPriceList[position].productName
            viewHolder.tv_store_name?.text = todayPriceList[position].storeName
            viewHolder.tv_discount_rate?.text =
                (todayPriceList[position].discountRate?.times(100))?.toInt()
                    .toString()
            viewHolder.tv_fixed_price?.text = todayPriceList[position].fixedPrice.toString()
            viewHolder.tv_discounted_price?.text = (todayPriceList[position].fixedPrice?.times(
                todayPriceList[position].discountRate!!
            ))?.toInt().toString()

            // drawable 파일에서 이미지 검색 후 적용
            val id = context!!.resources.getIdentifier(
                todayPriceList[position].imgUrl.toString(),
                "drawable",
                context!!.packageName
            )
            viewHolder.imv_product.setImageResource(id)

            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val databaseDistanceReference: DatabaseReference =
                firebaseDatabase.getReference("stores/${todayPriceList[position].storeName}/distance/${auth.uid}")

            databaseDistanceReference.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    viewHolder.tv_distance.text = dataSnapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            viewHolder.tv_fixed_price.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            viewHolder.tv_fixed_price_won.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            // Todo: recyclerview item click listener
        }

        override fun getItemCount(): Int {
            return todayPriceList.size
        }
    }

    inner class PopularStoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            databaseStoreReference = firebaseDatabase.getReference("stores")
            databaseStoreReference.orderByChild("grade").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    // ArrayList 비워줌
                    todayStoreList.clear()

                    for (postSnapshot in dataSnapshot.children) {
                        val item = postSnapshot.getValue(Store::class.java)

                        if (item != null) {
                            todayStoreList.add(0, item)
                        }
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
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
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            viewHolder.tv_popular_store_name.text = todayStoreList[position].storeName

            val distance = todayStoreList[position].distance?.get(auth.uid)
            if (distance != null) {
                viewHolder.tv_travel_time.text =
                    ((((distance.toDouble() / 1000) / 3.5) * 60 * 10).roundToInt() / 10).toString()
            }

            databaseReference.orderByChild("discountRate")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        for (postSnapshot in dataSnapshot.children) {
                            if (postSnapshot.child("storeName").value == todayStoreList[position].storeName) {
                                viewHolder.tv_max_discount_rate.text =
                                    postSnapshot.child("discountRate").value.toString()
                                viewHolder.tv_discount_product.text =
                                    postSnapshot.child("productName").value.toString()
                            }
                        }
                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            // drawable 파일에서 이미지 검색 후 적용
            val id = context!!.resources.getIdentifier(
                todayStoreList[position].storeImg.toString(),
                "drawable",
                context!!.packageName
            )
            viewHolder.imv_popular_store.setImageResource(id)

            // Todo: recyclerview item click listener
        }

        override fun getItemCount(): Int {
            return todayStoreList.size
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