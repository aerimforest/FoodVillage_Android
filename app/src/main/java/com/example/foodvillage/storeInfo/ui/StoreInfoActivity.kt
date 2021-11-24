package com.example.foodvillage.storeInfo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.databinding.ActivityStoreInfoBinding
import com.example.foodvillage.schema.Product
import com.example.foodvillage.storeInfo.adapter.StoreCategory
import com.example.foodvillage.storeInfo.adapter.StoreInfoCategoryAdapter
import com.example.foodvillage.storeInfo.adapter.StoreInfoProductAdapter

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

        val storeName = intent.getStringExtra("storeName")

        // Todo: storeName으로 Product 테이블 접근해서 productList 업데이트
    }
}