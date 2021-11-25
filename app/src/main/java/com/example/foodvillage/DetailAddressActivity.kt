package com.example.foodvillage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityDetailAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailAddressActivity : AppCompatActivity(){

    private var mBinding: ActivityDetailAddressBinding? = null
    private val binding get() = mBinding!!
    var AddressData:String?=null
    var curr_lat:Double?=null
    var curr_lon:Double?=null
    var etActivityDetailAddress:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_address)

        // 바인딩
        mBinding = ActivityDetailAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        AddressData=intent.getStringExtra("주소")
        curr_lat=intent.getDoubleExtra("curr_lat", 0.0)
        curr_lon=intent.getDoubleExtra("curr_lon", 0.0)

        binding.tvDetailAddressAddress.text=AddressData

        // 뒤로가기 버튼 누르면 다시 현재주소설정 지도 페이지로
        binding.btnActivityCurrentAdressSettingDetailBack.setOnClickListener {
            val intent= Intent(this, CurrentAddressActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        // 나가기 버튼
        binding.btnActivityAddressSettingDetailCancel.setOnClickListener {
            this.finish()
        }

        // 지도에서 확인하기 버튼
        binding.clyActivityCurrentAdressSettingDetailCheckmap.setOnClickListener {

            etActivityDetailAddress= binding.etActivityDetailAddress.text.toString()
            // 주소 저장 필요
            // 현재 위치 디비에서 받아오기
            val mDatabase = FirebaseDatabase.getInstance()
            val uid= FirebaseAuth.getInstance().uid
            val DbRefUsers = mDatabase.getReference("users/" + uid)


            DbRefUsers.child("address").setValue(AddressData)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", AddressData!!)
                }
            DbRefUsers.child("currentLatitude").setValue(curr_lat)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", ""+curr_lat)
                }
            DbRefUsers.child("currentLongitude").setValue(curr_lon)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", ""+curr_lon)
                }
            DbRefUsers.child("id").setValue(uid)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", "udpate success")
                }
            DbRefUsers.child("moreaddress").setValue(etActivityDetailAddress)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", "udpate success")
                }
            Log.d("파베", ""+AddressData+", "+curr_lat+", "+curr_lon+", "+etActivityDetailAddress)


            val intent= Intent(this, CurrentAddressActivity2::class.java)
            Log.d("상세주소", etActivityDetailAddress!!)

            intent.putExtra("etActivityDetailAddress",etActivityDetailAddress)
            startActivity(intent)
            this.finish()
        }


        binding.clyActivityCurrentAddressDetailDecide.setOnClickListener {
            // 주소 저장 필요
            // 현재 위치 디비에서 받아오기
            val mDatabase = FirebaseDatabase.getInstance()
            val uid= FirebaseAuth.getInstance().uid
            val DbRefUsers = mDatabase.getReference("users/" + uid)

            etActivityDetailAddress= binding.etActivityDetailAddress.text.toString()
            DbRefUsers.child("address").setValue(AddressData)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", AddressData!!)
                }
            DbRefUsers.child("currentLatitude").setValue(curr_lat)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", ""+curr_lat)
                }
            DbRefUsers.child("currentLongitude").setValue(curr_lon)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", ""+curr_lon)
                }
            DbRefUsers.child("id").setValue(uid)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", "udpate success")
                }
            DbRefUsers.child("moreaddress").setValue(etActivityDetailAddress)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", "udpate success")
                }
            Log.d("파베", ""+AddressData+", "+curr_lat+", "+curr_lon+", "+etActivityDetailAddress)
            intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()

        }


    }

}