package com.example.foodvillage.storeInfo.ui

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.R
import com.example.foodvillage.databinding.ActivityStoreInfoBinding
import com.example.foodvillage.schema.Product
import com.example.foodvillage.schema.Store
import com.example.foodvillage.storeInfo.adapter.StoreCategory
import com.example.foodvillage.storeInfo.adapter.StoreInfoCategoryAdapter
import com.example.foodvillage.storeInfo.adapter.StoreInfoProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StoreInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreInfoBinding
//    private var categoryList = arrayListOf(
//        StoreCategory("전체"),
//        StoreCategory("과일/채소"),
//        StoreCategory("수산/건어물")
//    )

//    private var productList = arrayListOf(
//        Product("이태리로 간 고등어", "고등어1", 0.3, 5000, listOf(), 3, "fish"),
//        Product("이태리로 간 고등어", "고등어2", 0.3, 5000, listOf(), 3, "fish"),
//        Product("이태리로 간 고등어", "고등어3", 0.3, 5000, listOf(), 3, "fish"),
//        Product("이태리로 간 고등어", "고등어4", 0.3, 5000, listOf(), 3, "fish"),
//        Product("이태리로 간 고등어", "고등어5", 0.3, 5000, listOf(), 3, "fish")
//

    private var categoryList=ArrayList<StoreCategory>()
    private var productList=ArrayList<Product>()
    var uid = FirebaseAuth.getInstance().uid


    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    val DbRefProduct: DatabaseReference = firebaseDatabase.getReference("products/")
    val DbRefStore = firebaseDatabase.getReference("stores/")
    val DbRefCategory=firebaseDatabase.getReference("categories/")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var productHashMap: HashMap<String, HashMap<String, Any>>?=null
    var storeHashMap: HashMap<String, HashMap<String, Any>>?=null
    var categoryHashMap: java.util.ArrayList<HashMap<String, Any>>?=null
    //val storeName = intent.getStringExtra("storeName")
    var storeName:String?=null// = "나연마트1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        storeName=intent.getStringExtra("storeName")
        //setStoreInfo()

        iconHeartClickEvent(storeName!!)

        DbRefProduct.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                productHashMap = it.value as HashMap<String, HashMap<String, Any>>
                val productsList = java.util.ArrayList<String>(productHashMap!!.keys)
                Log.d("상점 상세0",productsList.toString() )
                for (i in 0 until productsList!!.size){
                    val storeproductHashMap=productHashMap!!.get(productsList[i])
                    Log.d("상점 상세0.1",storeproductHashMap.toString() )

                    if (storeproductHashMap?.get("storeName") == storeName) {
                        val prod=Product()
                        prod.categoryNum= storeproductHashMap?.get("categoryNum") as Long?
                        prod.storeName= storeproductHashMap?.get("storeName") as String?
                        prod.productName= storeproductHashMap?.get("productName") as String?
                        prod.discountRate= storeproductHashMap?.get("discountRate") as Double?
                        prod.fixedPrice= storeproductHashMap?.get("fixedPrice") as Long?
                        prod.dibPeople= storeproductHashMap?.get("dibPeople") as List<String>?
                        prod.imgUrl= storeproductHashMap?.get("imgUrl") as String

                        Log.d("상점 상세0.2",prod.toString())

                        productList?.add(prod)
                        Log.d("상점 상세0.3",productList.toString() )

                    }
                }

                DbRefStore.get()
                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                    .addOnSuccessListener {
                        storeHashMap = it.value as HashMap<String, HashMap<String, Any>>
                        // 상점의 카테고리
                        val categories = storeHashMap!!.get(storeName)
                            ?.get("categoryNames") as java.util.ArrayList<String>
                        for (cat in categories){
                            categoryList?.add(StoreCategory(cat))
                        }

                        val storeInfos=storeHashMap!!.get(storeName)

                        binding.tvStoreInfoName.text = storeInfos?.get("storeName") as String?
                        binding.tvStoreInfoPhone.text = storeInfos?.get("phoneNumber") as String?
                        binding.tvStoreInfoTime2.text = storeInfos?.get("time") as String?
                        binding.tvStoreInfoTime.text = storeInfos?.get("time") as String?
                        binding.tvStoreInfoBreak.text = storeInfos?.get("dayOff") as String?
                        binding.tvStoreInfoLocation.text = storeInfos?.get("address") as String?

                        // 거리
                        val databaseDistanceReference: DatabaseReference =
                            firebaseDatabase.getReference("stores/$storeName/distance/${auth.uid}")

                        databaseDistanceReference.addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                binding.tvStoreInfoDistance.text = dataSnapshot.value.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })

                        // drawable 파일에서 이미지 검색 후 적용
                        val id = resources.getIdentifier(
                            storeInfos?.get("storeImg").toString(),
                            "drawable",
                            packageName
                        )
                        binding.imvStoreInfoImg.setImageResource(id)




                        Log.d("상점 상세1",categoryList.toString() )
                        Log.d("상점 상세2",productList.toString() )
                        val storeInfoCategoryAdapter = StoreInfoCategoryAdapter(this, categoryList!!)
                        binding.rvStoreInfoCategory.adapter = storeInfoCategoryAdapter

                        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        binding.rvStoreInfoCategory.layoutManager = layoutManager
                        binding.rvStoreInfoCategory.setHasFixedSize(true)

                        val storeInfoProductAdapter = StoreInfoProductAdapter(this, productList!!)
                        binding.rcvStoreInfoProduct.adapter = storeInfoProductAdapter
                        binding.rcvStoreInfoProduct.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rcvStoreInfoProduct.setHasFixedSize(true)




                        binding.tvStoreInfoCall.setOnClickListener {
                            val phoneNum=binding.tvStoreInfoPhone.text
                            var intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:$phoneNum")
                            if(intent.resolveActivity(packageManager) != null){
                                startActivity(intent)
                            }

                        }



                        //        if (storeName != null) {
            //            iconHeartClickEvent(storeName)
            //        }

                        binding.tvProductInfo.setOnClickListener {
                            productInfoClickEvent()
                        }

                        binding.tvStoreInfo.setOnClickListener {
                            storeInfoClickEvent()
                        }

                        binding.tvReview.setOnClickListener {
                            tvReviewClickEvent()
                        }

                    }



            }


    }

    private fun setStoreInfo() {
        val databaseStoreReference: DatabaseReference =
            firebaseDatabase.getReference("stores/"+storeName!!)

        databaseStoreReference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (postSnapshot in dataSnapshot.children) {
                    val item = postSnapshot.getValue(Store::class.java)
                    if (item != null) {
                        if (item.storeName == storeName) {
                            binding.tvStoreInfoName.text = item.storeName
                            binding.tvStoreInfoPhone.text = item.phoneNumber
                            binding.tvStoreInfoTime2.text = item.time
                            binding.tvStoreInfoTime.text = item.time
                            binding.tvStoreInfoBreak.text = item.dayOff
                            binding.tvStoreInfoLocation.text = item.address

                            // 거리
                            val databaseDistanceReference: DatabaseReference =
                                firebaseDatabase.getReference("stores/$storeName/distance/${auth.uid}")

                            databaseDistanceReference.addValueEventListener(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    binding.tvStoreInfoDistance.text = dataSnapshot.value.toString()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })

                            // drawable 파일에서 이미지 검색 후 적용
                            val id = resources.getIdentifier(
                                item.storeImg.toString(),
                                "drawable",
                                packageName
                            )
                            binding.imvStoreInfoImg.setImageResource(id)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun iconHeartClickEvent(storeName: String) {
        binding.imvStoreInfoHeart.setOnClickListener {
            binding.imvStoreInfoHeart.setImageResource(R.drawable.icon_heart_fill_white)
            databaseReference = firebaseDatabase.getReference("stores/$storeName")
            databaseReference.child("dibPeople").push().setValue(auth.uid)
        }
    }

    private fun productInfoClickEvent() {
        binding.svStoreInfoProduct.visibility = View.VISIBLE
        binding.llyStoreInfoStore.visibility = View.INVISIBLE
        binding.llyStoreInfoReview.visibility = View.INVISIBLE

        binding.tvProductInfo.setTextColor(Color.parseColor("#63B22F"))
        binding.tvStoreInfo.setTextColor(Color.parseColor("#999999"))
        binding.tvReview.setTextColor(Color.parseColor("#999999"))

        binding.viewProductInfoBar.visibility = View.VISIBLE
        binding.viewStoreInfoBar.visibility = View.INVISIBLE
        binding.viewReviewBar.visibility = View.INVISIBLE
    }

    private fun storeInfoClickEvent() {
        binding.svStoreInfoProduct.visibility = View.INVISIBLE
        binding.llyStoreInfoStore.visibility = View.VISIBLE
        binding.llyStoreInfoReview.visibility = View.INVISIBLE

        binding.tvProductInfo.setTextColor(Color.parseColor("#999999"))
        binding.tvStoreInfo.setTextColor(Color.parseColor("#63B22F"))
        binding.tvReview.setTextColor(Color.parseColor("#999999"))

        binding.viewProductInfoBar.visibility = View.INVISIBLE
        binding.viewStoreInfoBar.visibility = View.VISIBLE
        binding.viewReviewBar.visibility = View.INVISIBLE

        val aniSlideOutRight: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_left)
        val aniSlideOutLeft: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_right)

        binding.viewProductInfoBar.startAnimation(aniSlideOutRight)
        binding.viewStoreInfoBar.startAnimation(aniSlideOutLeft)

        // Todo: 길찾기 버튼 클릭 이벤트
    }

    private fun tvReviewClickEvent() {
        binding.svStoreInfoProduct.visibility = View.INVISIBLE
        binding.llyStoreInfoStore.visibility = View.INVISIBLE
        binding.llyStoreInfoReview.visibility = View.VISIBLE

        binding.tvProductInfo.setTextColor(Color.parseColor("#999999"))
        binding.tvStoreInfo.setTextColor(Color.parseColor("#999999"))
        binding.tvReview.setTextColor(Color.parseColor("#63B22F"))

        binding.viewProductInfoBar.visibility = View.INVISIBLE
        binding.viewStoreInfoBar.visibility = View.INVISIBLE
        binding.viewReviewBar.visibility = View.VISIBLE

        val aniSlideOutRight: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_left)
        val aniSlideOutLeft: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_right)

        binding.viewStoreInfoBar.startAnimation(aniSlideOutRight)
        binding.viewReviewBar.startAnimation(aniSlideOutLeft)
    }
}