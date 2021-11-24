package com.example.foodvillage.storeInfo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.databinding.ActivityStoreInfoBinding
import com.example.foodvillage.schema.StoreCategory
import com.example.foodvillage.storeInfo.adapter.StoreInfoCategoryAdapter

class StoreInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreInfoBinding
    private var categoryList = arrayListOf(
        StoreCategory("전체"),
        StoreCategory("과일/채소"),
        StoreCategory("수산/건어물")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Adapter 연결
        val certificateAdapter = StoreInfoCategoryAdapter(this, categoryList)
        binding.rvStoreInfoCategory.adapter = certificateAdapter

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStoreInfoCategory.layoutManager = layoutManager
        binding.rvStoreInfoCategory.setHasFixedSize(true)
    }
}