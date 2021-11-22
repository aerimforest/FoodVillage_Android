package com.example.foodvillage.storeList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodvillage.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import com.example.foodvillage.databinding.FragmentBottomsheetDistanceBinding


class Bottomsheet_filterDistance : BottomSheetDialogFragment(){

    private var mbinding: FragmentBottomsheetDistanceBinding? = null
    private val binding get() = mbinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mbinding = FragmentBottomsheetDistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // slider에 따라 textview 값 바꾸기
        val rangeSlider = binding.rsDistance
        val tv_km = binding.tvKm
//
//        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
//            override fun onStartTrackingTouch(slider: RangeSlider) {
//                val values = rangeSlider.values
//                Log.i("SliderNewValue To", values.toString())
//            }
//
//            override fun onStopTrackingTouch(slider: RangeSlider) {
//                val values = rangeSlider.values
//                Log.i("SliderNewValue To", values.toString())
//                tv_km.setText("${values}km")
//            }
//        })


    }

}