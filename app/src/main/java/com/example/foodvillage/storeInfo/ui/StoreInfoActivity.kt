package com.example.foodvillage.storeInfo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
    private var categoryList = arrayListOf(
        StoreCategory("전체"),
        StoreCategory("과일/채소"),
        StoreCategory("수산/건어물")
    )

    private var productList = arrayListOf(
        Product("이태리로 간 고등어", "고등어1", 0.3, 5000, listOf(), 3, "fish"),
        Product("이태리로 간 고등어", "고등어2", 0.3, 5000, listOf(), 3, "fish"),
        Product("이태리로 간 고등어", "고등어3", 0.3, 5000, listOf(), 3, "fish"),
        Product("이태리로 간 고등어", "고등어4", 0.3, 5000, listOf(), 3, "fish"),
        Product("이태리로 간 고등어", "고등어5", 0.3, 5000, listOf(), 3, "fish")
    )

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //val storeName = intent.getStringExtra("storeName")
    val storeName = "나연마트1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val storeInfoCategoryAdapter = StoreInfoCategoryAdapter(this, categoryList)
        binding.rvStoreInfoCategory.adapter = storeInfoCategoryAdapter

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStoreInfoCategory.layoutManager = layoutManager
        binding.rvStoreInfoCategory.setHasFixedSize(true)

        val storeInfoProductAdapter = StoreInfoProductAdapter(this, productList)
        binding.rcvStoreInfoProduct.adapter = storeInfoProductAdapter
        binding.rcvStoreInfoProduct.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvStoreInfoProduct.setHasFixedSize(true)

        // Todo: storeName으로 Product 테이블 접근해서 productList 업데이트

        setStoreInfo()

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

    private fun setStoreInfo() {
        val databaseStoreReference: DatabaseReference =
            firebaseDatabase.getReference(storeName)

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
    }

    private fun storeInfoClickEvent() {
        binding.svStoreInfoProduct.visibility = View.INVISIBLE
        binding.llyStoreInfoStore.visibility = View.VISIBLE
        binding.llyStoreInfoReview.visibility = View.INVISIBLE

        // Todo: 길찾기 버튼 클릭 이벤트
    }

    private fun tvReviewClickEvent() {
        binding.svStoreInfoProduct.visibility = View.INVISIBLE
        binding.llyStoreInfoStore.visibility = View.INVISIBLE
        binding.llyStoreInfoReview.visibility = View.VISIBLE
    }
}