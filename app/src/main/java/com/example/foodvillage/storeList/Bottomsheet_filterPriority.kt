package com.example.foodvillage.storeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodvillage.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Bottomsheet_filterPriority : BottomSheetDialogFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet_priority_filter,container,false)
    }

}