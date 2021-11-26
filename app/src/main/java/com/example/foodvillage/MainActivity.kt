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
//                        val aroundFragment = AroundFragment()
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.main_screen_panel, aroundFragment).commit()

                        val intent=Intent(this@MainActivity, DBMarketMapActivity::class.java)
                        startActivity(intent)

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
//        var storeName="24시 홈마트"
//        var DbRefStore = mDatabase.getReference("stores/"+storeName)
//        var store= Store(storeName, 37.534025, 126.994824, "서울 용산구 보광로 127 유영빌딩 지하1층" , listOf("과일/채소","고기/계란","생활용품"))
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
//


//        val mmDatabase = FirebaseDatabase.getInstance()
//        // 마트 생성
//        var storeName="24시 홈마트"
//        var DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        var store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.534025
//        store.currentLongitude=126.994824
//        store.address="서울 용산구 보광로 127 유영빌딩 지하1층"
//        store.categoryNames= listOf("과일/채소", "고기/계란", "생활용품")
//        store.storeImg="home_mart"
//        store.grade=3.9
//        store.reviewCnt = 10
//        store.productCnt = 5
//        store.phoneNumber="02-123-6432"
//        store.time="00:00 - 24:00"
//        store.dayOff="연중무휴"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        val mmDatabase = FirebaseDatabase.getInstance()
//        // 마트 생성
//        var storeName="GS더프레시 종로평창점"
//        var DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        var store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.606757
//        store.currentLongitude=126.968939
//        store.address="서울 종로구 평창동 158-1"
//        store.categoryNames= listOf("수산/건어물","밥/면/소스/캔")
//        store.storeImg="gs_mart"
//        store.grade= 4.15
//        store.reviewCnt = 11
//        store.productCnt = 13
//        store.phoneNumber="02-231-9123"
//        store.time="10:00-22:30"
//        store.dayOff="수요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }


//        val mmDatabase = FirebaseDatabase.getInstance()
//        // 마트 생성
//        var storeName="농협하나로마트 사직지점"
//        var DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        var store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.574364
//        store.currentLongitude=126.968858
//        store.address="서울 종로구 사직동 9"
//        store.categoryNames= listOf("간식/음","고기/계")
//        store.storeImg="hanaro_mart"
//        store.grade= 4.1
//        store.reviewCnt = 15
//        store.productCnt = 5
//        store.phoneNumber="02-812-3812"
//        store.time="09:00 - 22:00"
//        store.dayOff="연중무휴"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        val mmDatabase = FirebaseDatabase.getInstance()
//        // 마트 생성
//        var storeName="롯데슈퍼 잠원점"
//        var DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        var store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.508589
//        store.currentLongitude=127.012004
//        store.address="서울 서초구 신반포로 257 신반포 11차 상가"
//        store.categoryNames= listOf("수산/건어물", "생활용품", "과일/채소")
//        store.storeImg="lotte_super"
//        store.grade= 3.5
//        store.reviewCnt = 41
//        store.productCnt = 11
//        store.phoneNumber="02-121-3213"
//        store.time="09:00 - 22:00"
//        store.dayOff="셋째주 수요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        val mmDatabase = FirebaseDatabase.getInstance()
//        // 마트 생성
//        var storeName="월드마트 남현점"
//        var DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        var store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.47501
//        store.currentLongitude=126.976717
//        store.address="서울 관악구 남현4길 1"
//        store.categoryNames= listOf("반찬/간편식", "과일/채소")
//        store.storeImg="world_mart"
//        store.grade= 4.1
//        store.reviewCnt = 19
//        store.productCnt = 7
//        store.phoneNumber="02-121-3213"
//        store.time="00:00 - 24:00"
//        store.dayOff="연중무휴"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//
//        storeName="예림마트2"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.497
//        store.currentLongitude=127.029
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("과일/채소","간식/음료", "고기/계란", "밥/면/소스/캔")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.7
//        store.phoneNumber="02-111-2222"
//        store.time="12:00-22:00"
//        store.dayOff="매주 월요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트3"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.497
//        store.currentLongitude=127.029
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("과일/채소","간식/음료", "고기/계란", "밥/면/소스/캔")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.7
//        store.phoneNumber="02-111-2222"
//        store.time="12:00-22:00"
//        store.dayOff="매주 월요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="나연마트1"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.495
//        store.currentLongitude=127.028
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("생활용품", "고기/계란")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.7
//        store.phoneNumber="02-111-2222"
//        store.time="11:00-18:00"
//        store.dayOff="연중무휴"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="나연마트2"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.450
//        store.currentLongitude=127.030
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("건강/다이어트", "생활용품")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.9
//        store.phoneNumber="02-111-2222"
//        store.time="10:00-22:00"
//        store.dayOff="매주 월요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="유진마트1"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.490
//        store.currentLongitude=127.025
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("과일/채소","간식/음료", "밥/면/소스/캔")
//        store.storeImg="nayeon_mart1"
//        store.grade=4.3
//        store.phoneNumber="02-111-2222"
//        store.time="12:00-20:00"
//        store.dayOff="연중무휴"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="유진마트2"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.452
//        store.currentLongitude=127.035
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("간식/음료", "밥/면/소스/캔")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.7
//        store.phoneNumber="02-111-2222"
//        store.time="09:00-20:00"
//        store.dayOff="매주 수요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        storeName="소연마트3"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.495
//        store.currentLongitude=127.03
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames= listOf("반찬/간편식", "건강/다이어트", "밥/면/소스/캔")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.7
//        store.phoneNumber="02-111-2222"
//        store.time="09:00-24:00"
//        store.dayOff="매주 화요일, 수요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="소연 정육점"
//        DbRefStore = mmDatabase.getReference("stores/"+storeName)
//        store= Store()
//        store.storeName=storeName
//        store.currentLatitude=37.494
//        store.currentLongitude=127.026
//        store.address="서울특별시 서초구 서초동 강남대로 373"
//        store.categoryNames=listOf("고기/계란")
//        store.storeImg="nayeon_mart1"
//        store.grade=3.7
//        store.phoneNumber="02-111-2222"
//        store.time="09:00-18:00"
//        store.dayOff="매주 수요일"
//        DbRefStore.setValue(store)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//



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

                ////////////////////////////////////////////////////////
                //애뮬레이터 안 되면 여기부터 주석처리
//                try{
//                    var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
//                    Log.d("유저", "hash.name: " + t_hashMap.get("name"))
//
//                    curr_lat = t_hashMap.get("currentLatitude") as Double
//                    curr_lon = t_hashMap.get("currentLongitude") as Double
//
//
//                    // user 찾으면 현 위치 설정
//                    val DbRefStores = mDatabase.getReference("stores/")
//
//                    DbRefStores.get()
//                        .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
//                        .addOnSuccessListener {
//                            var t_hashMap: HashMap<String, HashMap<String, Any>> =
//                                it.value as HashMap<String, HashMap<String, Any>>
//
//                            Log.d("파베", "Main: " + t_hashMap.toString())
//
//                            val storeNameList: List<String> = ArrayList<String>(t_hashMap.keys)
//                            for (i in 0 until storeNameList.size) {
//                                val currentLatitude =
//                                    t_hashMap.get(storeNameList[i])?.get("currentLatitude") as Double
//                                val currentLongitude =
//                                    t_hashMap.get(storeNameList[i])?.get("currentLongitude") as Double
//                                val marker_distance =
//                                    getDistance(curr_lat!!,
//                                        curr_lon!!, currentLatitude, currentLongitude)
//
//                                // 현재위치 주소값
//                                var reverseGeoCoder = MapReverseGeoCoder(
//                                    getApiKeyFromManifest(this),
//                                    MapPoint.mapPointWithGeoCoord(currentLatitude, currentLongitude),
//                                    object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
//                                        override fun onReverseGeoCoderFoundAddress(
//                                            mapReverseGeoCoder: MapReverseGeoCoder,
//                                            s: String
//                                        ) {
//                                            val AddressData = s
//                                            DbRefStores.child(storeNameList[i]).child("address").setValue(AddressData)
//                                                .addOnFailureListener {
//                                                    e -> Log.d(ContentValues.TAG, e.localizedMessage)
//                                                    Log.d("파베", "주소 왜 저장 안 됨?")
//                                                }
//                                                .addOnSuccessListener {}
//                                        }
//                                        override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
//
//                                        }
//                                    },
//                                    this
//                                )
//                                reverseGeoCoder.startFindingAddress()
//
//                                try {
//                                    var updateHashMap=t_hashMap.get(storeNameList[i])?.get("distance") as HashMap<String, Double>
//                                    updateHashMap.put(uid!!, marker_distance!!.toDouble())
//
//                                    DbRefStores.child(storeNameList[i]).child("distance").setValue(updateHashMap)
//                                        .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
//                                        .addOnSuccessListener {}
//                                }
//                                catch (e:NullPointerException) {
//                                    var updateHashMap:HashMap<String, Double> = HashMap()
//                                    updateHashMap.put(uid!!, marker_distance!!.toDouble())
//
//                                    DbRefStores.child(storeNameList[i]).child("distance").setValue(updateHashMap)
//                                        .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
//                                        .addOnSuccessListener {}
//                                }
//
//                            }
//
//                            //이동시간
//                            // Math.round(((marker_distance.toDouble() / 1000) / 3.5) * 60 * 10 ) / 10).toString()
//
//                        }
//                }
//                catch(e:java.lang.NullPointerException){
//                    Toast.makeText(this@MainActivity, "사용자 정보를 등록해주세요", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@MainActivity, MyMapActivity::class.java)
//                    startActivity(intent)
//                }
                ///////////////////////////////////////////////////////
                //애뮬레이터 안 되면 여기까지 주석처리

            }
//
//
//        // 리뷰 생성
//        var storeName="예림마트1"
//        var DbRefReview = mDatabase.getReference("reviews/"+storeName)
//        var Date: LocalDate = LocalDate.now()
//        var review= Review()
//        review.userId="Cg70AwEYe6bxPcI34FuGDvewFwF2"
//        review.userName="nana test"
//        review.reportDate=Date.toString()
//        review.reviewTitle="예림마트1-1 반찬 jmt"
//        review.reviewContent="장조림 너모 맛있었어요~~ 최고최고~!"
//        review.reviewImg=null
//        DbRefReview.child("Cg70AwEYe6bxPcI34FuGDvewFwF2").setValue(review)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트1"
//        DbRefReview = mDatabase.getReference("reviews/"+storeName)
//        Date= LocalDate.now()
//        review= Review()
//        review.userId="OnkBTywPZCNP7mBksXrbWl7VjUl1"
//        review.userName="옒"
//        review.reportDate=Date.toString()
//        review.reviewTitle="예림마트1-2 반찬 jmt"
//        review.reviewContent="장조림 너모 맛있었어요~~ 최고최고~!"
//        review.reviewImg=null
//        DbRefReview.child("OnkBTywPZCNP7mBksXrbWl7VjUl1").setValue(review)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="예림마트3"
//        DbRefReview = mDatabase.getReference("reviews/"+storeName)
//        Date= LocalDate.now()
//        review= Review()
//        review.userId="Cg70AwEYe6bxPcI34FuGDvewFwF2"
//        review.userName="nana test"
//        review.reportDate=Date.toString()
//        review.reviewTitle="예림마트3 반찬 jmt"
//        review.reviewContent="장조림 너모 맛있었어요~~ 최고최고~!"
//        review.reviewImg=null
//        DbRefReview.child("Cg70AwEYe6bxPcI34FuGDvewFwF2").setValue(review)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="나연마트1"
//        DbRefReview = mDatabase.getReference("reviews/"+storeName)
//        Date= LocalDate.now()
//        review= Review()
//        review.userId="Cg70AwEYe6bxPcI34FuGDvewFwF2"
//        review.userName="nana test"
//        review.reportDate=Date.toString()
//        review.reviewTitle="나연마트1 반찬 jmt"
//        review.reviewContent="장조림 너모 맛있었어요~~ 최고최고~!"
//        review.reviewImg=null
//        DbRefReview.child("Cg70AwEYe6bxPcI34FuGDvewFwF2").setValue(review)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
//        storeName="유진마트1"
//        DbRefReview = mDatabase.getReference("reviews/"+storeName)
//        Date= LocalDate.now()
//        review= Review()
//        review.userId="kDIrovjDfgTJKFCpFr0qZnezr1D2"
//        review.userName="유진"
//        review.reportDate=Date.toString()
//        review.reviewTitle="유진마트1 반찬 jmt"
//        review.reviewContent="장조림 너모 맛있었어요~~ 최고최고~!"
//        review.reviewImg=null
//        DbRefReview.child("kDIrovjDfgTJKFCpFr0qZnezr1D2").setValue(review)
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

//        var storeName="예림마트1"
//        var productName="식빵"
//        var DbRefProduct = mDatabase.getReference("products/"+"222saddsagsdghafheffh")
//        var product=Product()
//        product.productName=productName
//        product.storeName=storeName
//        product.discountRate=0.3
//        product.fixedPrice=10000
//        product.dibPeople=listOf("vRDBsjXEP8Ph18NQ0DpMgcg5Bnd2")
//        product.imgUrl="product_bread"
//        product.categoryNum=5
//        DbRefProduct.setValue(product)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

//        var storeName="예림마트1"
//        var productName="맛있는 김치"
//        var DbRefProduct = mDatabase.getReference("products/"+"444444444tgdfsgdsfd")
//        var product=Product()
//        product.productName=productName
//        product.storeName=storeName
//        product.discountRate=0.3
//        product.fixedPrice=12000
//        product.dibPeople=listOf("OnkBTywPZCNP7mBksXrbWl7VjUl1", "LsKrfBzrAPdksRyRU366ntjottX2")
//        product.imgUrl="product_kimchi"
//        product.categoryNum=4
//        DbRefProduct.setValue(product)
//            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
//
        var storeName="GS더프레시 종로평창점"
        var productName="정말 맛있는 로제파스타 소스"
        var DbRefProduct = mDatabase.getReference("products/"+"rosepastasauce")
        var product=Product()
        product.productName=productName
        product.storeName=storeName
        product.discountRate=0.4
        product.fixedPrice=5000
        product.dibPeople=listOf("OnkBTywPZCNP7mBksXrbWl7VjUl1", "LsKrfBzrAPdksRyRU366ntjottX2")
        product.imgUrl="product_pasta"
        product.categoryNum=6
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        /*
        storeName="예림마트1"
        productName="그릴드 치킨"
        DbRefProduct = mDatabase.getReference("products/"+"fgdddddddddddddfgdfgdfgfdg")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=7000
        product.discountRate=0.3
        product.imgUrl="product_grilled_chicken"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="예림마트1"
        productName="맛있는 김치"
        DbRefProduct = mDatabase.getReference("products/"+"444444444tgdfsgdsfd")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=50000
        product.discountRate=0.3
        product.imgUrl="product_kimchi"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="예림마트1"
        productName="맛있는 김치"
        DbRefProduct = mDatabase.getReference("products/"+"ggggggggggggggggggggg")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=50000
        product.discountRate=0.4
        product.imgUrl="product_kimchi"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }


        storeName="소연마트2"
        productName="냄새 좋은 두리안"
        DbRefProduct = mDatabase.getReference("products/"+"qqqqqqqqqqqqqqqqqqqqqqqq")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=50000
        product.discountRate=0.9
        product.imgUrl="product_durian"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="소연마트2"
        productName="달콤한 파파야"
        DbRefProduct = mDatabase.getReference("products/"+"eeeeeeeeeeeeeeeeeeeeeeee")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=500000
        product.discountRate=0.3
        product.imgUrl="product_papaya"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="소연마트1"
        productName="예림마트보다 싸다 김치"
        DbRefProduct = mDatabase.getReference("products/"+"ryyyyyyyyyyyyyyyyrrrrrrr")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=40000
        product.discountRate=0.3
        product.imgUrl="product_kimchi"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="나연마트1"
        productName="아프지망고"
        DbRefProduct = mDatabase.getReference("products/"+"wetsgdfgfdgsdfgdfgd")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=40000
        product.discountRate=0.3
        product.imgUrl="product_mango"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="나연마트1"
        productName="안 본 지 오렌지"
        DbRefProduct = mDatabase.getReference("products/"+"dfgsfgsfgsdfgsdfgsdfg")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=40000
        product.discountRate=0.3
        product.imgUrl="product_orange"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="나연마트1"
        productName="아임 파인 애플! 앤 유?"
        DbRefProduct = mDatabase.getReference("products/"+"fdgdhfafhdgjjgjsgfjdfg")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=40000
        product.discountRate=0.3
        product.imgUrl="product_pineapple"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }

        storeName="나연마트1"
        productName="스트로베리베리"
        DbRefProduct = mDatabase.getReference("products/"+"eesgdksgnkdjsfngjdf")
        product=Product()
        product.productName=productName
        product.storeName=storeName
        product.fixedPrice=40000
        product.discountRate=0.3
        product.imgUrl="product_strawberries"
        product.categoryNum=5
        DbRefProduct.setValue(product)
            .addOnFailureListener { e -> Log.d(TAG, e.localizedMessage) }
*/
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
        return round(R * c).toDouble() /1000
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

    //category
    /*
    나연마트1: 생활용품, 고기/계란, 수산/건어물
    나연2: 건강/다이어트, 생활용품
    나3: 건강/다이어트
    예1: 밥/면/소스/캔, 수산/건어물
    예2: 반찬/간편식, 밥/면/소스/캔
    예3: 수산/건어물, 고기/계란
    유1: 과일/채소, 간식/음료, 밥/면/소스/캔
    유2: 간식/음료, 밥/면/소스/캔
    유3: 고기/계란, 수산/건어물
     */

}