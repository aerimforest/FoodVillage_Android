package com.example.foodvillage.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodvillage.DBMarketMapActivity
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.foodvillage.MarketMapActivity
import com.example.foodvillage.MyMapActivity
import com.example.foodvillage.R
import com.example.foodvillage.databinding.FragmentAroundBinding

class AroundFragment : Fragment() {

    private var _binding: FragmentAroundBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        _binding = FragmentAroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMainactivityTomymap.setOnClickListener {
            val intent = Intent(context, MyMapActivity::class.java)
            startActivity(intent)
        }
//
//        binding.btnMainactivityTomarketmap.setOnClickListener {
//            val intent = Intent(context, MarketMapActivity::class.java)
//            startActivity(intent)
//        }
        binding.btnMainactivityTodbmarketmap.setOnClickListener{
            val intent=Intent(context, DBMarketMapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}