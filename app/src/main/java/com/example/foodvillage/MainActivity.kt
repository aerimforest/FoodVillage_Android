package com.example.foodvillage

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityMainBinding
import com.example.foodvillage.menu.AroundFragment
import com.example.foodvillage.menu.DibFragment
import com.example.foodvillage.menu.HomeFragment
import com.example.foodvillage.menu.MyPageFragment
import com.example.foodvillage.schema.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.pow
import kotlin.math.round


class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    private var curr_lat:Double? = 0.0
    private var curr_lon:Double? = 0.0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom Navigation
        binding.bottomNavigation.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        val homeFragment = HomeFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, homeFragment).commit()
                    }
                    1 -> {
                        val dibFragment = DibFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, dibFragment).commit()
                    }

                    2 -> {
                        val aroundFragment = AroundFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, aroundFragment).commit()
                    }
                    3 -> {
                        val myPageFragment = MyPageFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, myPageFragment).commit()
                    }
                }
            }
        })

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_screen_panel, homeFragment)
            .commit()
        //

//        // 유저 정보 넣기
//        val mDatabase = FirebaseDatabase.getInstance()

//        val uid=FirebaseAuth.getInstance().uid
//        val DbRefUser = mDatabase.getReference("users/"+uid)
//        val user= Users(uid, "NaNa Keum",37.55649948120117, 126.94249725341797, 100, "서울시 김구 예림동 111")
//        DbRefUser.setValue(user)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        // 마트 생성
//        var storeName="예림마트1"
//        var DbRefStore = mDatabase.getReference("stores/"+storeName)
//        var store= Store(storeName, 37.556, 126.950, "서울시 김구 예림동 123" , listOf("고기/계란", "수산/건어물" ))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트2"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.543, 126.883, "서울시 김구 예림동 123" , listOf("간식/음료", "밥/면/소스/캔"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="나연마트1"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.542, 126.885, "서울시 김구 예림동 123" , listOf("과일/채소", "고기/계란", "수산/건어물"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="나연마트2"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.542, 126.882, "서울시 김구 예림동 123" , listOf("건강/다이어트", "생활용품"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="유진마트1"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.557, 126.943, "서울시 김구 예림동 123" , listOf("반찬/간편식", "간식/음료", "밥/면/소스/캔"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="유진마트2"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.555, 126.945, "서울시 김구 예림동 123" , listOf("간식/음료", "밥/면/소스/캔"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="유진마트3"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.556, 126.944, "서울시 김구 예림동 123" , listOf("고기/계란", "수산/건어물" ))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트3"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.5565, 126.9425, "서울시 김구 예림동 123" , listOf("과일/채소", "고기/계란"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="나연마트3"
//        DbRefStore = mDatabase.getReference("stores/"+storeName)
//        store= Store(storeName, 37.5533, 126.9477, "서울시 김구 예림동 123" , listOf("반찬/간편식"))
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }


//        DbRefUser.get()
//            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
//            .addOnSuccessListener {
//                var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
//                Log.d("유저", "hash.name: "+t_hashMap.get("name"))
//            }
        // 마트까지의 거리 계산
        val mDatabase = FirebaseDatabase.getInstance()

        val uid = FirebaseAuth.getInstance().uid
        val DbRefUsers = mDatabase.getReference("users/" + uid)

        DbRefUsers.get()
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "아직 위치가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show()
                Log.d("유저", "못 가져옴")
            }
            .addOnSuccessListener {
                try{
                    var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
                    Log.d("유저", "hash.name: " + t_hashMap.get("name"))

                    curr_lat = t_hashMap.get("currentLatitude") as Double
                    curr_lon = t_hashMap.get("currentLongitude") as Double


                    // user 찾으면 현 위치 설정
                    val DbRefStores = mDatabase.getReference("stores/")

                    DbRefStores.get()
                        .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                        .addOnSuccessListener {
                            var t_hashMap: HashMap<String, HashMap<String, Any>> =
                                it.value as HashMap<String, HashMap<String, Any>>

                            Log.d("파베", "Main: " + t_hashMap.toString())

                            val storeNameList: List<String> = ArrayList<String>(t_hashMap.keys)
                            for (i in 0 until storeNameList.size) {
                                val currentLatitude =
                                    t_hashMap.get(storeNameList[i])?.get("currentLatitude") as Double
                                val currentLongitude =
                                    t_hashMap.get(storeNameList[i])?.get("currentLongitude") as Double
                                val marker_distance =
                                    getDistance(curr_lat!!,
                                        curr_lon!!, currentLatitude, currentLongitude)

                                // 현재위치 주소값
                                var reverseGeoCoder = MapReverseGeoCoder(
                                    getApiKeyFromManifest(this),
                                    MapPoint.mapPointWithGeoCoord(currentLatitude, currentLongitude),
                                    object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                                        override fun onReverseGeoCoderFoundAddress(
                                            mapReverseGeoCoder: MapReverseGeoCoder,
                                            s: String
                                        ) {
                                            val AddressData = s
                                            DbRefStores.child(storeNameList[i]).child("address").setValue(AddressData)
                                                .addOnFailureListener {
                                                    e -> Log.d(ContentValues.TAG, e.localizedMessage)
                                                    Log.d("파베", "주소 왜 저장 안 됨?")
                                                }
                                                .addOnSuccessListener {}
                                        }
                                        override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {

                                        }
                                    },
                                    this
                                )
                                reverseGeoCoder.startFindingAddress()

                                try {
                                    var updateHashMap=t_hashMap.get(storeNameList[i])?.get("distance") as HashMap<String, Double>
                                    updateHashMap.put(uid!!, marker_distance!!.toDouble())

                                    DbRefStores.child(storeNameList[i]).child("distance").setValue(updateHashMap)
                                        .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                                        .addOnSuccessListener {}
                                }
                                catch (e:NullPointerException) {
                                    var updateHashMap:HashMap<String, Double> = HashMap()
                                    updateHashMap.put(uid!!, marker_distance!!.toDouble())

                                    DbRefStores.child(storeNameList[i]).child("distance").setValue(updateHashMap)
                                        .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                                        .addOnSuccessListener {}
                                }

                            }

                            //이동시간
                            // Math.round(((marker_distance.toDouble() / 1000) / 3.5) * 60 * 10 ) / 10).toString()

                        }
                }
                catch(e:java.lang.NullPointerException){
                    Toast.makeText(this@MainActivity, "사용자 정보를 등록해주세요", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, MyMapActivity::class.java)
                    startActivity(intent)
                }
            }


//        // 리뷰 생성
//        storeName="예림마트"
//        val DbRefReview = mDatabase.getReference("reviews/"+storeName)
//        val Date: LocalDate = LocalDate.now()
//        val review= Review()
//        review.userId=user.id
//        review.userName=user.name
//        review.reportDate=Date.toString()
//        review.reviewTitle="예림마트 반찬 jmt"
//        review.reviewContent="장조림 너모 맛있었어요~~ 최고최고~!"
//        review.reviewImg=null
//        DbRefReview.setValue(review)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        // 카테고리 생성
//        var i=0
//        val catList: List<String> = listOf( "전체", "과일/채소", "고기/계란", "수산/건어물", "반찬/간편식", "간식/음료", "밥/면/소스/캔", "건강/다이어트", "생활용품")
//        val storeNamesList:List<List<String>> = listOf(listOf("나연마트1", "나연마트2"), listOf("나연마트3"),listOf("예림마트1"),listOf("나연마트2"),listOf("나연마트2"),listOf("유진마트1"),listOf("유진마트2"),listOf("예림마트2"),listOf("예림마트3"))
//        while (i<catList.size){
//            val DbRefCategory = mDatabase.getReference("categories/"+i)
//            val category=Category()
//            category.categoryNum=i
//            category.categoryName=catList[i]
//            category.storeNames=storeNamesList[i]
//            DbRefCategory.setValue(category)
//                .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//            i++
//        }

//        // store 생성
//
//        storeName="예림마트1"
//        var productName="타이어보다 싸다 고등어"
//        var DbRefProduct = mDatabase.getReference("products/"+storeName+"/"+productName)
//        var product=Product()
//        product.productName=productName
//        product.storeName=storeName
//        product.fixedPrice=100000
//        product.discountRate=0.3
//        product.productImg=null
//        product.categoryNum=3
//        DbRefProduct.setValue(product)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트1"
//        productName="타이어보다 싸다 자몽에이드"
//        DbRefProduct = mDatabase.getReference("products/"+storeName+"/"+productName)
//        product=Product()
//        product.productName=productName
//        product.storeName=storeName
//        product.fixedPrice=70000
//        product.discountRate=0.3
//        product.productImg=null
//        product.categoryNum=5
//        DbRefProduct.setValue(product)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트2"
//        productName="예림마트1보다 싸다 자몽에이드"
//        DbRefProduct = mDatabase.getReference("products/"+storeName+"/"+productName)
//        product=Product()
//        product.productName=productName
//        product.storeName=storeName
//        product.fixedPrice=50000
//        product.discountRate=0.3
//        product.productImg=null
//        product.categoryNum=5
//        DbRefProduct.setValue(product)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        // 유저 정보 빼기
//
//        val mDatabase = FirebaseDatabase.getInstance()
//
//        val uid= FirebaseAuth.getInstance().uid
//        val DbRefUser = mDatabase.getReference("users/"+uid)
//
//        DbRefUser.get()
//            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
//            .addOnSuccessListener {
//                var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
//                Log.d("유저", "hash.name: "+t_hashMap.get("name"))
//            }
    }

    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6372.8 * 1000

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2).pow(2.0) + Math.sin(dLon / 2).pow(2.0) * Math.cos(
            Math.toRadians(
                lat1
            )
        ) * Math.cos(
            Math.toRadians(lat2)
        )
        val c = 2 * Math.asin(Math.sqrt(a))
        return round((R * c))/100
    }
    private fun getApiKeyFromManifest(context: Context): String? {
        var apiKey: String? = null
        try {
            val e = context.packageName
            val ai = context
                .packageManager
                .getApplicationInfo(e, PackageManager.GET_META_DATA)
            val bundle = ai.metaData
            if (bundle != null) {
                apiKey = bundle.getString("com.kakao.sdk.AppKey")
            }
        } catch (var6: Exception) {
            Log.d(
                "meta-data",
                "Caught non-fatal exception while retrieving apiKey: $var6"
            )
        }
        return apiKey
    }

}