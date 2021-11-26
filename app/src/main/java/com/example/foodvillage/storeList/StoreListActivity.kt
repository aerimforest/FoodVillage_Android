package com.example.foodvillage.storeList

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.DBMarketMapActivity
import com.example.foodvillage.R
import com.example.foodvillage.databinding.ActivityStoreListBinding
import com.example.foodvillage.storeInfo.ui.StoreInfoActivity
import com.github.channguyen.rsv.RangeSliderView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_category.view.*
import java.lang.Double.max
import java.util.ArrayList
import kotlin.math.round

class StoreListActivity : AppCompatActivity() {

    private var mBinding: ActivityStoreListBinding? = null
    private val binding get() = mBinding!!
    var filteredcategoryIdx = 0
    var categoryIdx = 0
    var mStoreAdapter: StoreAdapter? = null
    var distVal = 3.0

    var mDatabase = FirebaseDatabase.getInstance()
    var uid = FirebaseAuth.getInstance().uid
    var DbRefUser = mDatabase.getReference("users/$uid")
    val DbRefCategory = mDatabase.getReference("categories/")
    val DbRefStore = mDatabase.getReference("stores/")
    val DbRefReview = mDatabase.getReference("reviews/")
    val DbRefProduct = mDatabase.getReference("products/")

    var categoryHashMap: ArrayList<HashMap<String, Any>>? = null
    var storeHashMap: HashMap<String, HashMap<String, Any>>? = null
    var reviewHashMap: HashMap<String, HashMap<String, Any>>? = null
    var productHashMap: HashMap<String, HashMap<String, Any>>? = null
    var categoryStoreList: List<String>? = null
    var storeList = ArrayList<StoreInfo>()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        // 전체이므로 카테고리값=0(0~8까지 있음)
        if (intent.hasExtra("filteredcategoryIdx")) {
            categoryIdx = intent.getIntExtra("filteredcategoryIdx", 0)
            Log.d("필터 적용_목록", categoryIdx.toString())
            if (intent.hasExtra("distVal")) {
                distVal = intent.getDoubleExtra("distVal", 3.0)
                Log.d("필터 적용_목록", distVal.toString())
            } else {
                Log.d("필터 적용_목록_노전달", distVal.toString())
                distVal = 3.0

            }
        } else {
            Log.d("필터 적용_목록_노전달", categoryIdx.toString())
            categoryIdx = 0

        }
        binding.btnMap.setOnClickListener {
            val mapintent = Intent(this@StoreListActivity, DBMarketMapActivity::class.java)
            mapintent.putExtra("filteredcategoryIdx", filteredcategoryIdx)
            mapintent.putExtra("distVal", distVal)
            Log.d("필터 보내기_목록", filteredcategoryIdx.toString())
            startActivity(mapintent)
            this@StoreListActivity.onDestroy()
        }

        DbRefUser.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {

                val userHashMap = it.value as HashMap<String, HashMap<String, Any>>
                binding.tvHomeLocation.text = userHashMap.get("address").toString()
            }

        DbRefCategory.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                Log.d("상점", it.value.toString())
                categoryHashMap = it.value as ArrayList<HashMap<String, Any>>
                Log.d("필터", "안쪽에서 카테고리: $categoryIdx")
                categoryStoreList =
                    categoryHashMap!![categoryIdx].get("storeNames") as List<String>

                DbRefStore.get()
                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                    .addOnSuccessListener {
                        storeHashMap = it.value as HashMap<String, HashMap<String, Any>>

                        DbRefReview.get()
                            .addOnFailureListener { e ->
                                Log.d(
                                    ContentValues.TAG,
                                    e.localizedMessage
                                )
                            }
                            .addOnSuccessListener {
                                reviewHashMap = it.value as HashMap<String, HashMap<String, Any>>

                                DbRefProduct.get()
                                    .addOnFailureListener { e ->
                                        Log.d(
                                            ContentValues.TAG,
                                            e.localizedMessage
                                        )
                                    }
                                    .addOnSuccessListener {
                                        productHashMap =
                                            it.value as HashMap<String, HashMap<String, Any>>

                                        Log.d(
                                            "필터",
                                            "맞니 안 맞니..: " + (categoryHashMap != null).toString() + (storeHashMap != null).toString() + (reviewHashMap != null).toString() + (productHashMap != null).toString() + (categoryStoreList != null).toString()
                                        )
                                        if (categoryHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null) {
                                            Log.d("상점명 리스트", categoryStoreList.toString())
                                            for (i in 0 until (categoryStoreList?.size!!)) {
                                                // 상점명
                                                val storeName = categoryStoreList!![i]

                                                // 상점의 카테고리
                                                val categories =
                                                    storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                                                        ?.get("categoryNames") as ArrayList<String>

                                                // 상점 위치별 사용자와의 거리
                                                val distanceHashMap =
                                                    storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                                                        ?.get("distance") as HashMap<String, Any>
                                                val distance = distanceHashMap[uid] as Double

                                                val storeImage =
                                                    storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                                                        ?.get("storeImg") as String
                                                // 상점의 리뷰 개수
                                                val storereviewHashMap =
                                                    reviewHashMap!!.get(storeName)
                                                var reviewTotal = storereviewHashMap?.size
                                                if (reviewTotal == null) reviewTotal = 0

                                                // 상점의 프로덕트 개수
                                                // 상점의 최대 할인율
                                                var prodNumTotal = 0
                                                var salePercentMax: Double = 0.0
                                                val productList =
                                                    ArrayList<String>(productHashMap!!.keys)
                                                for (i in 0 until productList.size) {
                                                    val storeproductHashMap =
                                                        productHashMap!![productList[i]]

                                                    if (storeproductHashMap?.get("storeName") == storeName) {
                                                        prodNumTotal += 1
                                                        salePercentMax = max(
                                                            salePercentMax,
                                                            storeproductHashMap["discountRate"] as Double
                                                        )
                                                    }

                                                }

                                                // Log.d("상점 정보들", ""+storeName+", "+distance.toString()+", "+reviewTotal.toString()+", "+prodNumTotal.toString()+", "+categories+", "+(round(salePercentMax*100)).toString()+"%")
                                                // 추가
                                                storeList.add(
                                                    StoreInfo(
                                                        storeImage,
                                                        storeName,
                                                        distance.toString() + "km",
                                                        reviewTotal.toString(),
                                                        prodNumTotal.toString(),
                                                        categories,
                                                        (round(salePercentMax * 100)).toString() + "%"
                                                    )
                                                )

                                                // 기본적으로 가까운 순(3km 기본)
                                                storeList = storeList.filter { s ->
                                                    s.distance?.substring(
                                                        0,
                                                        3
                                                    )?.toDouble()!! < distVal
                                                } as ArrayList<StoreInfo>
                                                storeList.sortBy { it.distance }
                                            }
                                            //Log.d("필터", "스토어리스트가 생겼니: "+(storeList!=null).toString()+": "+storeList[0].storeName+", "+storeList[1].storeName)

                                            if (storeList.size != 0) {
                                                binding.rvStore.layoutManager =
                                                    LinearLayoutManager(
                                                        this,
                                                        LinearLayoutManager.VERTICAL,
                                                        false
                                                    )
                                                binding.rvStore.setHasFixedSize(true)
                                                mStoreAdapter = StoreAdapter(
                                                    storeList,
                                                    this.applicationContext
                                                )
                                                binding.rvStore.adapter = mStoreAdapter

                                                // 가게 리스트 클릭 이벤트
                                                mStoreAdapter!!.setItemClickListener(object :
                                                    StoreAdapter.OnItemClickListener {
                                                    override fun onClick(v: View, position: Int) {
                                                        val intent = Intent(
                                                            this@StoreListActivity,
                                                            StoreInfoActivity::class.java
                                                        )
                                                        intent.putExtra(
                                                            "storeName",
                                                            storeList[position].storeName
                                                        )
                                                        this@StoreListActivity.startActivity(intent)
                                                    }
                                                })

                                                // 전체
                                                binding.btnAll.setOnClickListener {
                                                    binding.btnAll.isSelected
                                                    filteredcategoryIdx = 0
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                    binding.btnAll.setBackgroundResource(R.drawable.background_btn_selected_green)
                                                    binding.btnAll.setTextColor(Color.WHITE)
                                                }

                                                // 과일/채소
                                                binding.btnFruitVegi.setOnClickListener {
                                                    binding.btnFruitVegi.isSelected
                                                    filteredcategoryIdx = 1
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)

                                                    binding.btnAll.setBackgroundResource(R.drawable.background_category_non_selected)
                                                    binding.btnAll.setTextColor(Color.BLACK)

                                                    binding.btnFruitVegi.setBackgroundResource(R.drawable.background_btn_selected_green)
                                                    binding.btnFruitVegi.setTextColor(Color.WHITE)
                                                }

                                                binding.btnMeat.setOnClickListener {
                                                    binding.btnMeat.isSelected
                                                    filteredcategoryIdx = 2
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnSeafood.setOnClickListener {
                                                    binding.btnSeafood.isSelected
                                                    filteredcategoryIdx = 3
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnSideDish.setOnClickListener {
                                                    binding.btnSideDish.isSelected
                                                    filteredcategoryIdx = 4
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }

                                                // 간식/음료
                                                binding.btnSnack.setOnClickListener {
                                                    binding.btnSnack.isSelected
                                                    filteredcategoryIdx = 5
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)

                                                    binding.btnFruitVegi.setBackgroundResource(R.drawable.background_category_non_selected)
                                                    binding.btnFruitVegi.setTextColor(Color.BLACK)

                                                    binding.btnSnack.setBackgroundResource(R.drawable.background_btn_selected_green)
                                                    binding.btnSnack.setTextColor(Color.WHITE)
                                                }

                                                binding.btnRiceAndNoodle.setOnClickListener {
                                                    binding.btnRiceAndNoodle.isSelected
                                                    filteredcategoryIdx = 6
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnHealthy.setOnClickListener {
                                                    binding.btnHealthy.isSelected
                                                    filteredcategoryIdx = 7
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnLife.setOnClickListener {
                                                    binding.btnLife.isSelected
                                                    filteredcategoryIdx = 8
                                                    storeList =
                                                        categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }

                                                // 정렬 기준 설정 bottomsheet 띄우기
                                                val btnPriority = binding.btnPriority
                                                btnPriority.setOnClickListener {
                                                    val dialogPriority =
                                                        BottomSheetDialog(this@StoreListActivity)
                                                    dialogPriority.setContentView(R.layout.fragment_bottomsheet_priority_filter)

                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_distance)
                                                        ?.setOnClickListener {
                                                            storeList = PriorityFiltering(
                                                                filteredcategoryIdx,
                                                                0
                                                            )
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            btnPriority.text = "가까운 순"
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_sale)
                                                        ?.setOnClickListener {
                                                            storeList = PriorityFiltering(
                                                                filteredcategoryIdx,
                                                                1
                                                            )
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            btnPriority.text = "할인율 순"
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_review)
                                                        ?.setOnClickListener {
                                                            storeList = PriorityFiltering(
                                                                filteredcategoryIdx,
                                                                2
                                                            )
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            btnPriority.text = "리뷰 많은 순"
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_product)
                                                        ?.setOnClickListener {
                                                            storeList = PriorityFiltering(
                                                                filteredcategoryIdx,
                                                                3
                                                            )
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            btnPriority.text = "상품 많은 순"
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.show()
                                                }

                                                // 거리 범위 설정 bottomsheet 띄우기
                                                val btnDistance = binding.btnFilterDistance
                                                btnDistance.setOnClickListener {

                                                    val kmPriority =
                                                        BottomSheetDialog(this@StoreListActivity)
                                                    kmPriority.setContentView(R.layout.fragment_bottomsheet_distance)

                                                    val tv_km =
                                                        kmPriority.findViewById<TextView>(R.id.tv_km)
                                                    tv_km?.text = ("" + distVal + "km")

                                                    kmPriority.findViewById<RangeSliderView>(R.id.rs_distance)
                                                        ?.setOnSlideListener { index ->
                                                            Log.d("반경 선택", "" + index + "km")
                                                            tv_km?.text = ("" + index + "km")
                                                            storeList = KmFiltering(
                                                                filteredcategoryIdx!!,
                                                                index
                                                            )
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            btnDistance.text =
                                                                ("" + index + "km 이내")
                                                            distVal = index.toDouble()
                                                        }
                                                    kmPriority.show()
                                                }
                                            }
                                        }
                                    }
                            }
                    }
            }
    }

    fun categoryFiltering(filteredcategoryIdx: Int): ArrayList<StoreInfo> {
        categoryStoreList =
            categoryHashMap!![filteredcategoryIdx]["storeNames"] as List<String>
        Log.d("상점명 리스트_filtered", categoryStoreList.toString())
        storeList = ArrayList<StoreInfo>()

        for (i in 0 until (categoryStoreList?.size!!)) {
            // 상점명
            val storeName = categoryStoreList!![i]

            // 상점의 카테고리
            val categories = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                ?.get("categoryNames") as ArrayList<String>

            // 상점 위치별 사용자와의 거리
            val distanceHashMap = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                ?.get("distance") as HashMap<String, Any>
            val distance = distanceHashMap.get(uid) as Double

            val storeImage = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                ?.get("storeImg") as String
            // 상점의 리뷰 개수
            val storereviewHashMap = reviewHashMap!!.get(storeName)
            var reviewTotal = storereviewHashMap?.size
            if (reviewTotal == null) reviewTotal = 0

            // 상점의 프로덕트 개수
            // 상점의 최대 할인율
            var prodNumTotal = 0
            var salePercentMax: Double = 0.0
            val productList = ArrayList<String>(productHashMap!!.keys)
            for (i in 0 until productList.size) {
                val storeproductHashMap = productHashMap!!.get(productList[i])

                if (storeproductHashMap?.get("storeName") == storeName) {
                    prodNumTotal += 1
                    salePercentMax =
                        max(salePercentMax, storeproductHashMap["discountRate"] as Double)
                }

            }

            // Log.d("상점 정보들", ""+storeName+", "+distance.toString()+", "+reviewTotal.toString()+", "+prodNumTotal.toString()+", "+categories+", "+(round(salePercentMax*100)).toString()+"%")
            // 추가
            storeList.add(
                StoreInfo(
                    storeImage,
                    storeName,
                    distance.toString() + "km",
                    reviewTotal.toString(),
                    prodNumTotal.toString(),
                    categories,
                    (round(salePercentMax * 100)).toString() + "%"
                )
            )
        }
        storeList = storeList.filter { s ->
            s.distance?.substring(0, 3)?.toDouble()!! < 3.0
        } as ArrayList<StoreInfo>
        storeList.sortBy { it.distance }
        return storeList
    }

    fun PriorityFiltering(filteredcategoryIdx: Int, priority: Int): ArrayList<StoreInfo> {
        storeList = categoryFiltering(filteredcategoryIdx)
        when (priority) {
            0 -> {
                storeList.sortBy { it.distance }
            }
            1 -> {
                storeList.sortByDescending { it.salePercentMax }
            }
            2 -> {
                storeList.sortByDescending { it.reviewTotal }
            }
            3 -> {
                storeList.sortByDescending { it.prodNumTotal }
            }
            else -> {

            }
        }

        return storeList
    }

    fun KmFiltering(filteredcategoryIdx: Int, priority: Int): ArrayList<StoreInfo> {
        storeList = categoryFiltering(filteredcategoryIdx)
        when (priority) {
            1 -> {
                storeList = storeList.filter { s ->
                    s.distance?.substring(0, 3)?.toDouble()!! < 1.0
                } as ArrayList<StoreInfo>
            }
            2 -> {
                storeList = storeList.filter { s ->
                    s.distance?.substring(0, 3)?.toDouble()!! < 2.0
                } as ArrayList<StoreInfo>
            }
            3 -> {
                storeList = storeList.filter { s ->
                    s.distance?.substring(0, 3)?.toDouble()!! < 3.0
                } as ArrayList<StoreInfo>
            }
            4 -> {
                storeList = storeList.filter { s ->
                    s.distance?.substring(0, 3)?.toDouble()!! < 4.0
                } as ArrayList<StoreInfo>
            }
            else -> {
            }
        }

        storeList.sortBy { it.distance }
        return storeList
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}