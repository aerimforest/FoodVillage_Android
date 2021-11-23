package com.example.foodvillage.storeList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R
import com.example.foodvillage.databinding.FragmentStorelistBinding
import com.google.android.material.slider.RangeSlider

class StoreListFragment : Fragment() {

    private val storeList = arrayListOf(
        StoreInfo(R.drawable.subway, "써브웨이 대학로점", "100m", "10", "5", "건강/다이어트", "43%"),
        StoreInfo(R.drawable.subway, "써브웨이 명동점", "1.6km", "30", "9", "건강/다이어트", "35%"),
        StoreInfo(R.drawable.subway, "써브웨이 충무로점", "3.2km", "50", "8", "건강/다이어트", "43%"),
        StoreInfo(R.drawable.subway, "써브웨이 충무로2점", "3.2km", "100+", "8", "건강/다이어트", "43%"),
        StoreInfo(R.drawable.subway, "써브웨이 대학로점", "4.5km", "15", "5", "건강/다이어트", "3%"),
        StoreInfo(R.drawable.subway, "써브웨이 명동점", "1.6km", "24", "9", "건강/다이어트", "35%"),
        StoreInfo(R.drawable.subway, "써브웨이 충무로점", "3.2km", "75", "8", "건강/다이어트", "12%"),
        StoreInfo(R.drawable.subway, "써브웨이 충무로2점", "3.2km", "1", "8", "건강/다이어트", "5%")
    )

    private var mBinding: FragmentStorelistBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentStorelistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvStore.setHasFixedSize(true)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvStore.layoutManager = layoutManager
        binding.rvStore.adapter = StoreAdapter(storeList)

        // 정렬 기준 설정 bottomsheet 띄우기
        val btnPriority = binding.btnPriority
        btnPriority.setOnClickListener{
            val bottomsheet = Bottomsheet_filterPriority()

            bottomsheet.show(parentFragmentManager, bottomsheet.tag)
        }

        // 거리 범위 설정 bottomsheet 띄우기
        val btnDistance = binding.btnFilterDistance
        btnDistance.setOnClickListener{
            val bottomsheet = Bottomsheet_filterDistance()
            bottomsheet.show(parentFragmentManager, bottomsheet.tag)
        }
    }
}