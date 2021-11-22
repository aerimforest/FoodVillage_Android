package com.example.foodvillage.storeList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.R
import com.example.foodvillage.databinding.ActivityStoreListBinding
import com.google.android.material.slider.RangeSlider

class StoreListActivity : AppCompatActivity() {

    private var mBinding: ActivityStoreListBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        mBinding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)

        val storeList = arrayListOf(
            StoreInfo(R.drawable.subway, "써브웨이 대학로점", "100m", "10", "5", "건강/다이어트", "43%"),
            StoreInfo(R.drawable.subway, "써브웨이 명동점", "1.6km", "30", "9", "건강/다이어트", "35%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로점", "3.2km", "50", "8", "건강/다이어트", "43%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로2점", "3.2km", "100+", "8", "건강/다이어트", "43%"),
            StoreInfo(R.drawable.subway, "써브웨이 대학로점", "4.5km", "15", "5", "건강/다이어트", "3%"),
            StoreInfo(R.drawable.subway, "써브웨이 명동점", "1.6km", "24", "9", "건강/다이어트", "35%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로점", "3.2km", "75", "8", "건강/다이어트", "12%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로2점", "3.2km", "1", "8", "건강/다이어트", "5%")
        )

        binding.rvStore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvStore.setHasFixedSize(true)
        binding.rvStore.adapter = StoreAdapter(storeList)

        // 정렬 기준 설정 bottomsheet 띄우기
        val btnPriority = binding.btnPriority
        btnPriority.setOnClickListener{
            val bottomsheet = Bottomsheet_filterPriority()
            bottomsheet.show(supportFragmentManager, bottomsheet.tag)
        }

        // 거리 범위 설정 bottomsheet 띄우기
        val btnDistance = binding.btnFilterDistance
        btnDistance.setOnClickListener{
            val bottomsheet = Bottomsheet_filterDistance()
            bottomsheet.show(supportFragmentManager, bottomsheet.tag)
        }


    }
}