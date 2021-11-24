package com.example.foodvillage.storeList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodvillage.databinding.FragmentBottomsheetDistanceBinding
import com.github.channguyen.rsv.RangeSliderView
import com.github.channguyen.rsv.RangeSliderView.OnSlideListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider


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

        val listener =
            OnSlideListener { index ->
                //Toast.makeText(context, "Hi index: $index", Toast.LENGTH_SHORT).show()
                Log.d("반경", ""+index+"km")
                tv_km.setText(""+index+"km")
            }
        rangeSlider.setOnSlideListener(listener)

//        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
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