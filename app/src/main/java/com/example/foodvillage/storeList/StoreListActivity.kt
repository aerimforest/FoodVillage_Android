package com.example.foodvillage.storeList

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.renderscript.RenderScript
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.DBMarketMapActivity
import com.example.foodvillage.R
import com.example.foodvillage.databinding.ActivityStoreListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.lang.Double.max
import java.util.ArrayList
import kotlin.math.round

class StoreListActivity : AppCompatActivity() {

    private var mBinding: ActivityStoreListBinding? = null
    private val binding get() = mBinding!!
    var filteredcategoryIdx=0
    var categoryIdx=0
    var mStoreAdapter:StoreAdapter?=null


    var mDatabase = FirebaseDatabase.getInstance()
    var uid = FirebaseAuth.getInstance().uid
    var DbRefUser = mDatabase.getReference("users/" + uid)
    val DbRefCategory=mDatabase.getReference("categories/")
    val DbRefStore = mDatabase.getReference("stores/")
    val DbRefReview = mDatabase.getReference("reviews/")
    val DbRefProduct = mDatabase.getReference("products/")

    var categoryHashMap: ArrayList<HashMap<String, Any>>?=null
    var storeHashMap: HashMap<String, HashMap<String, Any>>?=null
    var reviewHashMap: HashMap<String, HashMap<String, Any>>?=null
    var productHashMap: HashMap<String, HashMap<String, Any>>?=null
    var categoryStoreList: List<String>?=null
    var storeList=ArrayList<StoreInfo>()


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {

        mBinding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        // 전체이므로 카테고리값=0(0~8까지 있음)
        if (intent.hasExtra("filteredcategoryIdx")) {
            categoryIdx = intent.getIntExtra("filteredcategoryIdx", 0)
            Log.d("필터 적용_목록", categoryIdx.toString())
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
            Log.d("필터 적용_목록_노전달", categoryIdx.toString())
            categoryIdx=0

        }
        binding.btnMap.setOnClickListener{
            var mapintent = Intent(this@StoreListActivity, DBMarketMapActivity::class.java)
            mapintent.putExtra("filteredcategoryIdx", filteredcategoryIdx)
            Log.d("필터 보내기_목록", filteredcategoryIdx.toString())
            startActivity(mapintent)
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
                Log.d("필터", "안쪽에서 카테고리: "+categoryIdx)
                categoryStoreList = categoryHashMap!![categoryIdx]?.get("storeNames") as List<String>

                DbRefStore.get()
                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                    .addOnSuccessListener {
                        storeHashMap = it.value as HashMap<String, HashMap<String, Any>>

                        DbRefReview.get()
                            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                            .addOnSuccessListener {
                                reviewHashMap = it.value as HashMap<String, HashMap<String, Any>>

                                DbRefProduct.get()
                                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                                    .addOnSuccessListener {
                                        productHashMap = it.value as HashMap<String, HashMap<String, Any>>

                                        Log.d("필터", "맞니 안 맞니..: " + (categoryHashMap != null).toString() + (storeHashMap != null).toString() + (reviewHashMap != null).toString() + (productHashMap != null).toString() + (categoryStoreList != null).toString())
                                        if (categoryHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null){
                                            Log.d("상점명 리스트", categoryStoreList.toString())
                                            for (i in 0 until (categoryStoreList?.size!!)){
                                                // 상점명
                                                val storeName = categoryStoreList!![i]

                                                // 상점의 카테고리
                                                val categories = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                                                    ?.get("categoryNames") as ArrayList<String>

                                                // 상점 위치별 사용자와의 거리
                                                val distanceHashMap = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                                                    ?.get("distance") as HashMap<String, Any>
                                                val distance=distanceHashMap.get(uid) as Double

                                                // 상점의 리뷰 개수
                                                val storereviewHashMap=reviewHashMap!!.get(storeName)
                                                var reviewTotal= storereviewHashMap?.size
                                                if (reviewTotal==null) reviewTotal=0

                                                // 상점의 프로덕트 개수
                                                // 상점의 최대 할인율
                                                var prodNumTotal=0
                                                var salePercentMax:Double=0.0
                                                val productList = ArrayList<String>(productHashMap!!.keys)
                                                for (i in 0 until productList!!.size){
                                                    val storeproductHashMap=productHashMap!!.get(productList[i])

                                                    if (storeproductHashMap?.get("storeName") == storeName) {
                                                        prodNumTotal+=1
                                                        salePercentMax=max(salePercentMax, storeproductHashMap?.get("discountRate") as Double)
                                                    }

                                                }

                                                // Log.d("상점 정보들", ""+storeName+", "+distance.toString()+", "+reviewTotal.toString()+", "+prodNumTotal.toString()+", "+categories+", "+(round(salePercentMax*100)).toString()+"%")
                                                // 추가
                                                storeList?.add(StoreInfo(R.drawable.subway, storeName, distance.toString()+"km", reviewTotal.toString(), prodNumTotal.toString(), categories, (round(salePercentMax*100)).toString()+"%"))
                                                // 기본적으로 할인율 순
                                                storeList.sortByDescending { it.salePercentMax}
                                            }
                                            Log.d("필터", "스토어리스트가 생겼니: "+(storeList!=null).toString()+": "+storeList[0].storeName+", "+storeList[1].storeName)

                                            if(storeList!=null) {
                                                binding.rvStore.layoutManager =
                                                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                                binding.rvStore!!.setHasFixedSize(true)
                                                mStoreAdapter=StoreAdapter(storeList!!)
                                                binding.rvStore!!.adapter = mStoreAdapter

                                                binding.btnAll.setOnClickListener{
                                                    binding.btnAll.isSelected
                                                    filteredcategoryIdx=0
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }

                                                binding.btnFruitVegi.setOnClickListener{
                                                    binding.btnFruitVegi.isSelected
                                                    filteredcategoryIdx=1
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)

                                                }
                                                binding.btnMeat.setOnClickListener{
                                                    binding.btnMeat.isSelected
                                                    filteredcategoryIdx=2
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnSeafood.setOnClickListener{
                                                    binding.btnSeafood.isSelected
                                                    filteredcategoryIdx=3
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnSideDish.setOnClickListener{
                                                    binding.btnSideDish.isSelected
                                                    filteredcategoryIdx=4
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnSnack.setOnClickListener{
                                                    binding.btnSnack.isSelected
                                                    filteredcategoryIdx=5
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnRiceAndNoodle.setOnClickListener{
                                                    binding.btnRiceAndNoodle.isSelected
                                                    filteredcategoryIdx=6
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnHealthy.setOnClickListener{
                                                    binding.btnHealthy.isSelected
                                                    filteredcategoryIdx=7
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }
                                                binding.btnLife.setOnClickListener{
                                                    binding.btnLife.isSelected
                                                    filteredcategoryIdx=8
                                                    storeList=categoryFiltering(filteredcategoryIdx)
                                                    mStoreAdapter!!.datasetChanged(storeList)
                                                }


                                                // 정렬 기준 설정 bottomsheet 띄우기
                                                val btnPriority = binding.btnPriority
                                                btnPriority.setOnClickListener {
                                                    // val bottomsheet = Bottomsheet_filterPriority()
                                                    // bottomsheet.show(supportFragmentManager, bottomsheet.tag)
                                                    val dialogPriority = BottomSheetDialog(this@StoreListActivity)
                                                    dialogPriority.setContentView(R.layout.fragment_bottomsheet_priority_filter)
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_distance)
                                                        ?.setOnClickListener{
                                                            storeList=PriorityFiltering(filteredcategoryIdx, 0)
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_sale)
                                                        ?.setOnClickListener{
                                                            storeList=PriorityFiltering(filteredcategoryIdx, 1)
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_review)
                                                        ?.setOnClickListener{
                                                            storeList=PriorityFiltering(filteredcategoryIdx, 2)
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.findViewById<Button>(R.id.btn_priority_product)
                                                        ?.setOnClickListener{
                                                            storeList=PriorityFiltering(filteredcategoryIdx, 3)
                                                            mStoreAdapter!!.datasetChanged(storeList)
                                                            dialogPriority.dismiss()
                                                        }
                                                    dialogPriority.show()
                                                }
                                            }

                                            // 거리 범위 설정 bottomsheet 띄우기
                                            val btnDistance = binding.btnFilterDistance
                                            btnDistance.setOnClickListener{
                                                val bottomsheet = Bottomsheet_filterDistance()
                                                bottomsheet.show(supportFragmentManager, bottomsheet.tag)
                                            }

                                        }
                                    }

                            }

                    }

            }


    }

    fun categoryFiltering(filteredcategoryIdx:Int): ArrayList<StoreInfo> {
        categoryStoreList = categoryHashMap!![filteredcategoryIdx]?.get("storeNames") as List<String>
        Log.d("상점명 리스트_filtered", categoryStoreList.toString())
        storeList=ArrayList<StoreInfo>()

        for (i in 0 until (categoryStoreList?.size!!)){
            // 상점명
            val storeName = categoryStoreList!![i]

            // 상점의 카테고리
            val categories = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                ?.get("categoryNames") as ArrayList<String>

            // 상점 위치별 사용자와의 거리
            val distanceHashMap = storeHashMap!!.get((categoryStoreList!! as ArrayList<String>)[i])
                ?.get("distance") as HashMap<String, Any>
            val distance=distanceHashMap.get(uid) as Double

            // 상점의 리뷰 개수
            val storereviewHashMap=reviewHashMap!!.get(storeName)
            var reviewTotal= storereviewHashMap?.size
            if (reviewTotal==null) reviewTotal=0

            // 상점의 프로덕트 개수
            // 상점의 최대 할인율
            var prodNumTotal=0
            var salePercentMax:Double=0.0
            val productList = ArrayList<String>(productHashMap!!.keys)
            for (i in 0 until productList!!.size){
                val storeproductHashMap=productHashMap!!.get(productList[i])

                if (storeproductHashMap?.get("storeName") == storeName) {
                    prodNumTotal+=1
                    salePercentMax=max(salePercentMax, storeproductHashMap?.get("discountRate") as Double)
                }

            }

            // Log.d("상점 정보들", ""+storeName+", "+distance.toString()+", "+reviewTotal.toString()+", "+prodNumTotal.toString()+", "+categories+", "+(round(salePercentMax*100)).toString()+"%")
            // 추가
            storeList?.add(StoreInfo(R.drawable.subway, storeName, distance.toString()+"km", reviewTotal.toString(), prodNumTotal.toString(), categories, (round(salePercentMax*100)).toString()+"%"))
        }
        // 기본 할인율 순
        storeList.sortByDescending { it.salePercentMax}
        return storeList
    }

    fun PriorityFiltering(filteredcategoryIdx:Int, priority:Int): ArrayList<StoreInfo> {
        storeList=categoryFiltering(filteredcategoryIdx)
        when (priority){
            0->{
                storeList.sortBy { it.distance}
            }
            1->{
                storeList.sortByDescending { it.salePercentMax}
            }
            2->{
                storeList.sortByDescending { it.reviewTotal}
            }
            3->{
                storeList.sortByDescending { it.prodNumTotal}
            }
            else->{

            }
        }

        return storeList
    }




}