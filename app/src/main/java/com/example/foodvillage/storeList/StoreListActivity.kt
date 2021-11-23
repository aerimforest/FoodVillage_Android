package com.example.foodvillage.storeList

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.DBMarketMapActivity
import com.example.foodvillage.R
import com.example.foodvillage.databinding.ActivityStoreListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.lang.Double.max
import java.util.ArrayList
import kotlin.math.round

class StoreListActivity : AppCompatActivity() {

    private var mBinding: ActivityStoreListBinding? = null
    private val binding get() = mBinding!!

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



        binding.btnMap.setOnClickListener{
            intent= Intent(this@StoreListActivity, DBMarketMapActivity::class.java)
            startActivity(intent)
        }

        DbRefUser.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {

                val userHashMap = it.value as HashMap<String, HashMap<String, Any>>
                binding.tvHomeLocation.text = userHashMap.get("address").toString()
            }


        // 전체이므로 카테고리값=0(0~8까지 있음)
        var categoryIdx=0

        DbRefCategory.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                Log.d("상점", it.value.toString())
                categoryHashMap = it.value as ArrayList<HashMap<String, Any>>
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

                                        if (categoryHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null)
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

                                        }

                                        if(storeList!=null) {
                                            binding.rvStore.layoutManager =
                                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                            binding.rvStore!!.setHasFixedSize(true)
                                            var mStoreAdapter=StoreAdapter(storeList!!)
                                            binding.rvStore!!.adapter = mStoreAdapter


                                            binding.btnAll.setOnClickListener{
                                                binding.btnAll.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=0
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            // 얘만 작업중!
                                            binding.btnFruitVegi.setOnClickListener{
                                                binding.btnFruitVegi.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=1
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
                                                mStoreAdapter.datasetChanged(storeList)

                                            }
                                            binding.btnMeat.setOnClickListener{
                                                binding.btnMeat.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=2
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            binding.btnSeafood.setOnClickListener{
                                                binding.btnSeafood.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=3
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            binding.btnSideDish.setOnClickListener{
                                                binding.btnSideDish.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=4
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            binding.btnSnack.setOnClickListener{
                                                binding.btnSnack.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=6
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            binding.btnRiceAndNoodle.setOnClickListener{
                                                binding.btnRiceAndNoodle.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=6
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            binding.btnHealthy.setOnClickListener{
                                                binding.btnHealthy.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=7
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }
                                            binding.btnLife.setOnClickListener{
                                                binding.btnLife.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=8
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
                                                mStoreAdapter.datasetChanged(storeList)
                                            }



                                            // 정렬 기준 설정 bottomsheet 띄우기
                                            val btnPriority = binding.btnPriority
                                            btnPriority.setOnClickListener {
                                                val bottomsheet = Bottomsheet_filterPriority()
                                                bottomsheet.show(supportFragmentManager, bottomsheet.tag)
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
    @SuppressLint("ResourceAsColor")
    fun categoryFilteringInit(categoryIdx:Int){

        DbRefCategory.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                Log.d("상점", it.value.toString())
                categoryHashMap = it.value as ArrayList<HashMap<String, Any>>
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

                                        if (categoryHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null)
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

                                        }

                                        if(storeList!=null) {
                                            binding.rvStore.layoutManager =
                                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                            binding.rvStore!!.setHasFixedSize(true)
                                            var mStoreAdapter=StoreAdapter(storeList!!)
                                            binding.rvStore!!.adapter = mStoreAdapter


                                            binding.btnAll.setOnClickListener{
                                                val filteredcategoryIdx=0
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            // 얘만 작업중!
                                            binding.btnFruitVegi.setOnClickListener{
                                                binding.btnFruitVegi.setBackgroundColor(R.color.main_green)
                                                val filteredcategoryIdx=1
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                Log.d("상점명 리스트 변경", filteredStoreList[0].storeName)
                                                mStoreAdapter.datasetChanged(filteredStoreList)

                                            }
                                            binding.btnMeat.setOnClickListener{
                                                val filteredcategoryIdx=2
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            binding.btnSeafood.setOnClickListener{
                                                val filteredcategoryIdx=3
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            binding.btnSideDish.setOnClickListener{
                                                val filteredcategoryIdx=4
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            binding.btnSnack.setOnClickListener{
                                                val filteredcategoryIdx=6
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            binding.btnRiceAndNoodle.setOnClickListener{
                                                val filteredcategoryIdx=6
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            binding.btnHealthy.setOnClickListener{
                                                val filteredcategoryIdx=7
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }
                                            binding.btnLife.setOnClickListener{
                                                val filteredcategoryIdx=8
                                                val filteredStoreList=categoryFiltering(filteredcategoryIdx)
                                                mStoreAdapter.datasetChanged(filteredStoreList)
                                            }






                                            // 정렬 기준 설정 bottomsheet 띄우기
                                            val btnPriority = binding.btnPriority
                                            btnPriority.setOnClickListener {
                                                val bottomsheet = Bottomsheet_filterPriority()
                                                bottomsheet.show(supportFragmentManager, bottomsheet.tag)
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

    fun categoryFiltering(categoryIdx:Int): ArrayList<StoreInfo> {
        DbRefCategory.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                categoryHashMap = it.value as ArrayList<HashMap<String, Any>>
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

                                        if (categoryHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null)
                                            Log.d("상점명 리스트_categoryFiltering()", categoryStoreList.toString())
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


                                    }

                            }

                    }

            }

        Log.d("상점명 리스트_categoryFiltering()_return", storeList[0].storeName.toString())
        return storeList
    }


}