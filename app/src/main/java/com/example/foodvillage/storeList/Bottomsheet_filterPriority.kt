package com.example.foodvillage.storeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.foodvillage.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Bottomsheet_filterPriority : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottomsheet_priority_filter, container, false)
        val btn_priority_distance = view.findViewById<Button>(R.id.btn_priority_distance)
        btn_priority_distance.setOnClickListener {
        }
        
        return view
    }
}