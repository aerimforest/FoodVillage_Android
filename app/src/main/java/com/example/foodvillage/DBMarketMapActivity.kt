package com.example.foodvillage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.location.Address
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.databinding.ActivityDbMarketMapBinding
import com.example.foodvillage.menu.HomeFragment
import com.example.foodvillage.schema.Product
import com.example.foodvillage.schema.Store
import com.example.foodvillage.storeList.StoreAdapter
import com.example.foodvillage.storeList.StoreInfo
import com.example.foodvillage.storeList.StoreListActivity
import com.github.channguyen.rsv.RangeSliderView
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_fmi_market.*
import kotlinx.android.synthetic.main.item_today_sale.view.*
import net.daum.mf.map.api.*
import net.daum.mf.map.api.CameraUpdateFactory.newMapPointAndDiameter
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class DBMarketMapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener {
    // 뷰 바인딩
    private var mBinding: ActivityDbMarketMapBinding? = null
    private val binding get() = mBinding!!

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private var productList = arrayListOf<Product>()

    var filteredcategoryIdx = 0
    var categoryIdx = 0
    var distVal = 3.0

    private var mapView: MapView? = null

    private val eventListener = MarkerEventListener(this)   // 마커 클릭 이벤트 리스너

    // 위치 추적을 위한 변수들
    val TAG: String = "로그"
    private var mFusedLocationProviderClient: FusedLocationProviderClient? =
        null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    var curr_lat: Double? = null
    var curr_lon: Double? = null
    var userName: String? = null

    var selected_marker_lat: Double? = null
    var selected_marker_lon: Double? = null

    var AddressData: String = ""

    var mDatabase = FirebaseDatabase.getInstance()
    var uid = FirebaseAuth.getInstance().uid
    var DbRefUser = mDatabase.getReference("users/" + uid)
    val DbRefStore = mDatabase.getReference("stores/")
    val DbRefReview = mDatabase.getReference("reviews/")
    val DbRefProduct = mDatabase.getReference("products/")
    val DbRefCategory = mDatabase.getReference("categories/")

    var storeHashMap: HashMap<String, HashMap<String, Any>>? = null
    var storeNameList: List<String>? = null
    var reviewHashMap: HashMap<String, HashMap<String, Any>>? = null
    var productHashMap: HashMap<String, HashMap<String, Any>>? = null
    var categoryStoreList: List<String>? = null
    var storeList = ArrayList<Store>()
    var categoryHashMap: ArrayList<HashMap<String, Any>>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db_market_map)
        // 바인딩
        mBinding = ActivityDbMarketMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 맵
        // 임포트 잘 해줘야함... mf들어간걸로!
        mapView = MapView(this)
        binding.clKakaoMapView3.addView(mapView)

        mapView!!.setCurrentLocationEventListener(this)
        mapView!!.setPOIItemEventListener(eventListener)
        mapView!!.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록

        // 현재 위치
        // LocationRequest() deprecated 되서 아래 방식으로 LocationRequest 객체 생성
        // mLocationRequest = LocationRequest() is deprecated
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000 // 업데이트 간격 단위(밀리초)
            //fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            //maxWaitTime= 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        if (intent.hasExtra("filteredcategoryIdx")) {
            categoryIdx = intent.getIntExtra("filteredcategoryIdx", 0)
            Log.d("필터 적용_지도", categoryIdx.toString())
            if (intent.hasExtra("distVal")) {
                distVal = intent.getDoubleExtra("distVal", 3.0)
                Log.d("필터 적용_목록", distVal.toString())
            } else {
                Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
                Log.d("필터 적용_목록_노전달", distVal.toString())
                distVal = 3.0

            }
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
            categoryIdx = 0
            Log.d("필터 적용_지도_노전달", categoryIdx.toString())
        }
        binding.btnList.setOnClickListener {
            var listintent = Intent(this@DBMarketMapActivity, StoreListActivity::class.java)
            listintent.putExtra("filteredcategoryIdx", filteredcategoryIdx)
            listintent.putExtra("distVal", distVal)

            Log.d("필터 보내기_지도", filteredcategoryIdx.toString())
            startActivity(listintent)
            this@DBMarketMapActivity.onDestroy()
        }

        // 유저 정보 위치 디비에서 받아오기
        DbRefUser.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
                curr_lat = t_hashMap.get("currentLatitude") as Double
                curr_lon = t_hashMap.get("currentLongitude") as Double
                AddressData = t_hashMap.get("address") as String
                userName = t_hashMap.get("name")!! as String

                // 저장된 위치 마커 찍기
                var mymarker = MapPOIItem()
                mymarker.itemName = "저장된 내 위치"   // 마커 이름
                mymarker.mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat!!, curr_lon!!)
                mymarker.markerType = MapPOIItem.MarkerType.BluePin
                mymarker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                mymarker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(mymarker)

                binding.tvHomeLocation.setText(AddressData)

                // 저장된 위치로 중심 이동
                var mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat!!, curr_lon!!)
                mapView?.setMapCenterPoint(mapPoint, true)
                mapView?.setZoomLevel(2, true)

                Log.d("유저", "위, 경도: " + curr_lat + ", " + curr_lon)
            }

        DbRefCategory.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                Log.d("상점", it.value.toString())
                categoryHashMap = it.value as ArrayList<HashMap<String, Any>>
                categoryStoreList =
                    categoryHashMap!![categoryIdx]?.get("storeNames") as List<String>

                DbRefStore.get()
                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                    .addOnSuccessListener {
                        storeHashMap = it.value as HashMap<String, HashMap<String, Any>>
                        storeNameList = ArrayList<String>(storeHashMap!!.keys)
                        DbRefReview.get()
                            .addOnFailureListener { e ->
                                Log.d(
                                    ContentValues.TAG,
                                    e.localizedMessage
                                )
                            }
                            .addOnSuccessListener {
                                reviewHashMap = it.value as HashMap<String, HashMap<String, Any>>
                                DbRefProduct.get()
                                    .addOnFailureListener { e ->
                                        Log.d(
                                            ContentValues.TAG,
                                            e.localizedMessage
                                        )
                                    }
                                    .addOnSuccessListener {
                                        productHashMap =
                                            it.value as HashMap<String, HashMap<String, Any>>
                                        if (productHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null) {
                                            Log.d("상점명 리스트", categoryStoreList.toString())
                                            for (i in 0 until (categoryStoreList?.size!!)) {
                                                // 상점명
                                                val storeName = categoryStoreList!![i]
                                                // 상점 정보
                                                Log.d(
                                                    "상점명",
                                                    storeHashMap!!.get(storeName).toString()
                                                )
                                                val filteredstorehash =
                                                    storeHashMap!!.get(storeName)
                                                val StoreIndi = Store()
                                                StoreIndi.address =
                                                    filteredstorehash?.get("address") as String?
                                                StoreIndi.categoryNames =
                                                    filteredstorehash?.get("categoryNames") as List<String>?
                                                StoreIndi.distance =
                                                    filteredstorehash?.get("distance") as HashMap<String, Double>?
                                                StoreIndi.currentLongitude =
                                                    filteredstorehash?.get("currentLongitude") as Double?
                                                StoreIndi.currentLatitude =
                                                    filteredstorehash?.get("currentLatitude") as Double?
                                                StoreIndi.storeName =
                                                    filteredstorehash?.get("storeName") as String?
                                                StoreIndi.storeImg =
                                                    filteredstorehash?.get("storeImg") as String?
                                                if (StoreIndi.distance?.get(uid)!! <= distVal) {
                                                    storeList?.add(StoreIndi)
                                                }

                                            }
                                            markersShow(storeList)
                                        }
                                    }
                            }
                    }
            }

        // 위치 추척 시작
        if (checkPermissionForLocation(this)) {
            // 현위치 트래킹 - 이건 주소 설정할 때 해서 최초로 받는거
            mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving)
            updateLocation()
        }
        // 길찾기 하는 방법!! 중요함!!!
//        binding.btnDbMarketMapActivityFindway.setOnClickListener{
//            var intent= Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("kakaomap://route?sp=" + curr_lat + "," + curr_lon + "&ep=" + selected_marker_lat + "," + selected_marker_lon + "&by=FOOT")
//            )
//            startActivity(intent)
//        }

        binding.btnDbMarketMapActivityFloating.setOnClickListener {
            var mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat!!, curr_lon!!)
            mapView?.setMapCenterPoint(mapPoint, true)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff)
        mapView!!.setShowCurrentLocationMarker(false)
//        intent= Intent(this@DBMarketMapActivity, MainActivity::class.java)
//        startActivity(intent)

//        val homeFragment = HomeFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.main_screen_panel, homeFragment).commit()

    }

    protected fun updateLocation() {
        Log.d(TAG, "updateLocation()")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "updateLocation() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d(TAG, "updateLocation() 위치 권한이 하나라도 존재하는 경우")

        DbRefUser.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
                curr_lat = t_hashMap.get("currentLatitude") as Double
                curr_lon = t_hashMap.get("currentLongitude") as Double
                AddressData = t_hashMap.get("address") as String

                //binding.tvDbmarketmapactivityMylocation.setText(AddressData)
            }


        if (storeList != null) {

            binding.btnAll.setOnClickListener {
                binding.btnAll.isSelected
                filteredcategoryIdx = 0
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)

                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }

            binding.btnFruitVegi.setOnClickListener {
                binding.btnFruitVegi.isSelected
                filteredcategoryIdx = 1
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnMeat.setOnClickListener {
                binding.btnMeat.isSelected
                filteredcategoryIdx = 2
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnSeafood.setOnClickListener {
                binding.btnSeafood.isSelected
                filteredcategoryIdx = 3
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnSideDish.setOnClickListener {
                binding.btnSideDish.isSelected
                filteredcategoryIdx = 4
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnSnack.setOnClickListener {
                binding.btnSnack.isSelected
                filteredcategoryIdx = 5
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnRiceAndNoodle.setOnClickListener {
                binding.btnRiceAndNoodle.isSelected
                filteredcategoryIdx = 6
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnHealthy.setOnClickListener {
                binding.btnHealthy.isSelected
                filteredcategoryIdx = 7
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
            binding.btnLife.setOnClickListener {
                binding.btnLife.isSelected
                filteredcategoryIdx = 8
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
                mapView?.zoomOut(true)
            }
        }
        // 거리 범위 설정 bottomsheet 띄우기
        val btnDistance = binding.btnFilterDistance
        btnDistance?.setOnClickListener {

            val kmPriority = BottomSheetDialog(this@DBMarketMapActivity)
            kmPriority.setContentView(R.layout.fragment_bottomsheet_distance)

            val tv_km = kmPriority.findViewById<TextView>(R.id.tv_km)
            tv_km?.text = "" + distVal + "km"

            kmPriority.findViewById<RangeSliderView>(R.id.rs_distance)
                ?.setOnSlideListener { index ->
                    Log.d("반경 선택", "" + index + "km")
                    tv_km?.text = "" + index + "km"
                    storeList = KmFiltering(filteredcategoryIdx!!, index)
                    markersShow(storeList)
                    btnDistance.text = "" + index + "km 이내"
                    distVal = index.toDouble()
                }
            tv_km?.text = "기본 3km"
            kmPriority.show()
        }


    }

    fun KmFiltering(filteredcategoryIdx: Int, priority: Int): ArrayList<Store> {
        storeList = categoryFiltering(filteredcategoryIdx)
        when (priority) {
            1 -> {
                storeList = storeList.filter { s ->
                    s.distance?.get(uid)?.toDouble()!! < 1.0
                } as ArrayList<Store>
            }
            2 -> {
                storeList = storeList.filter { s ->
                    s.distance?.get(uid)?.toDouble()!! < 2.0
                } as ArrayList<Store>
            }
            3 -> {
                storeList = storeList.filter { s ->
                    s.distance?.get(uid)?.toDouble()!! < 3.0
                } as ArrayList<Store>
            }
            4 -> {
                storeList = storeList.filter { s ->
                    s.distance?.get(uid)?.toDouble()!! < 4.0
                } as ArrayList<Store>
            }
            else -> {
            }
        }
        storeList.sortBy { it.distance?.get(uid) }
        return storeList
    }

    fun markersShow(storeList: ArrayList<Store>) {
        mapView!!.removeAllPOIItems()
        mapView!!.removeAllPolylines()

        mapView!!.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록

        var marker = MapPOIItem()
        for (i in 0 until storeList.size) {

            val storeName = storeList[i].storeName as String
            val currentLatitude = storeList[i].currentLatitude as Double
            val currentLongitude = storeList[i].currentLongitude as Double
            val address = storeList[i].address as String
            val categories = storeList[i].categoryNames as List<String>
            val markerdistVal = storeList[i].distance?.get(uid)!!

            if (distVal >= markerdistVal) {
                marker = MapPOIItem()
                marker.itemName = storeName
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                    currentLatitude,
                    currentLongitude
                )

                when (categories[0]) {
                    "과일/채소" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_tomato
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_tomato_selected
                    }
                    "고기/계란" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_meat
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_meat_selected
                    }
                    "수산/건어물" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_fish
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_fish_selected
                    }
                    "반찬/간편식" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_banchan
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_banchan_selected
                    }
                    "간식/음료" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_choco
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_choco_selected
                    }
                    "밥/면/소스/캔" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_bap
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_bap_selected
                    }
                    "건강/다이어트" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_lettuce
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_lettuce_selected
                    }
                    "생활용품" -> {
                        marker.markerType = MapPOIItem.MarkerType.CustomImage
                        marker.customImageResourceId = R.drawable.marker_pan
                        marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        marker.customSelectedImageResourceId = R.drawable.marker_pan_selected
                    }
                    else -> {
                        marker.markerType = MapPOIItem.MarkerType.BluePin
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    }
                }
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)
            }
            mapView?.fitMapViewAreaToShowAllPOIItems()
            mapView?.zoomOut(true)
        }
    }

    fun categoryFiltering(filteredcategoryIdx: Int): ArrayList<Store> {
        categoryStoreList =
            categoryHashMap!![filteredcategoryIdx]?.get("storeNames") as List<String>
        Log.d("상점명 리스트_filtered", categoryStoreList.toString())
        storeList = ArrayList<Store>()

        for (i in 0 until (categoryStoreList?.size!!)) {
            // 상점명
            val storeName = categoryStoreList!![i]
            // 상점 정보
            val filteredstorehash = storeHashMap!!.get(storeName)
            val StoreIndi = Store()
            StoreIndi.address = filteredstorehash?.get("address") as String?
            StoreIndi.categoryNames = filteredstorehash?.get("categoryNames") as List<String>?
            StoreIndi.distance = filteredstorehash?.get("distance") as HashMap<String, Double>?
            StoreIndi.currentLongitude = filteredstorehash?.get("currentLongitude") as Double?
            StoreIndi.currentLatitude = filteredstorehash?.get("currentLatitude") as Double?
            StoreIndi.storeName = filteredstorehash?.get("storeName") as String?
            StoreIndi.storeImg = filteredstorehash?.get("storeImg") as String?

            // Log.d("상점 정보들", ""+storeName+", "+distance.toString()+", "+reviewTotal.toString()+", "+prodNumTotal.toString()+", "+categories+", "+(round(salePercentMax*100)).toString()+"%")
            // 추가
            storeList?.add(StoreIndi)
        }

        return storeList
    }


    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult()")
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged()")
        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")
//        txtTime.text = "Updated at : " + simpleDateFormat.format(date) // 갱신된 날짜
    }

    // 위치 권한이 있는지 확인하는 메서드
    fun checkPermissionForLocation(context: Context): Boolean {
        Log.d(TAG, "checkPermissionForLocation()")
        // Android 6.0 Marshmallow 이상에서는 지리 확보(위치) 권한에 추가 런타임 권한이 필요합니다.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : O")
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : X")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                false
            }
        } else {
            true
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult()")
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 클릭")

                // 현위치 트래킹
                mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving);
                updateLocation()

            } else {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(
                    this@DBMarketMapActivity,
                    "권한이 없어 해당 기능을 실행할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCurrentLocationUpdate(p0: MapView, p1: MapPoint, p2: Float) {
        val mapPointGeo: MapPoint.GeoCoordinate = p1.getMapPointGeoCoord()
        Log.i(
            "로그",
            java.lang.String.format(
                "MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)",
                mapPointGeo.latitude,
                mapPointGeo.longitude,
                p2
            )
        )
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {

    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {

    }

    // 커스텀 말풍선 클래스
    inner class CustomBalloonAdapter(inflater: LayoutInflater) : CalloutBalloonAdapter {
        var mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            if (poiItem != null) {
                val market_dist_hash = storeHashMap!!.get(poiItem?.itemName)
                    ?.get("distance") as HashMap<String, HashMap<String, Any>>
                val market_dist = market_dist_hash.get(uid) as Double

                (mCalloutBalloon.findViewById(R.id.ball_tv_name) as TextView).text =
                    poiItem?.itemName
                (mCalloutBalloon.findViewById(R.id.ball_tv_address) as TextView).text =
                    market_dist.toString() + "km, " + round((market_dist / 3.5) * 60).toString() + "분"
                Log.d(
                    "시간",
                    "벌룬용(" + poiItem?.itemName + "): " + market_dist.toString() + ", " + round((market_dist / 3.5) * 60).toString()
                )
            }
            return mCalloutBalloon

        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            return mCalloutBalloon
        }
    }

    inner class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, marker: MapPOIItem?) {
            Log.d("마커", "onPOIItemSelected()")

            mapView!!.removeAllPolylines()

            // 라인 생성
            // 폴리 라인
            val polyline = MapPolyline()
            polyline.tag = 1000
            polyline.lineColor = Color.argb(128, 0, 0, 0) // Polyline 컬러 지정.

            if (marker != null) {
                // Polyline 좌표 지정

                val market_lat = marker.mapPoint.mapPointGeoCoord.latitude
                val market_lon = marker.mapPoint.mapPointGeoCoord.longitude

                selected_marker_lat = market_lat
                selected_marker_lon = market_lon

                Log.d("마커", ": " + market_lat + ", " + market_lon)

                polyline.addPoint(MapPoint.mapPointWithGeoCoord(curr_lat!!, curr_lon!!))
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(market_lat, market_lon))

                // Polyline 지도에 올리기.
                mapView!!.addPolyline(polyline)

                val mapPointBounds = MapPointBounds(polyline.mapPoints)
                val padding = 200 // px

                mapView!!.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))

            }
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
            // 말풍선 클릭 시 (Deprecated)
            // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            buttonType: MapPOIItem.CalloutBalloonButtonType?
        ) {

            val dialog = BottomSheetDialog(this@DBMarketMapActivity)
            dialog.setContentView(R.layout.dialog_fmi_market)

            val storeName = poiItem?.itemName

            // 상품 목록 HorizontalScrollView
            val layoutManager = LinearLayoutManager(this@DBMarketMapActivity, LinearLayoutManager.HORIZONTAL, false)
            val productListRcv = findViewById<RecyclerView>(R.id.rv_dialog_fmi_product_list)
            productListRcv.adapter = storeName?.let { ProductListDialogAdapter(it) }
            productListRcv.layoutManager = layoutManager
            productListRcv.setHasFixedSize(true)

            val selectedstoreHashMap =
                storeHashMap?.get(storeName) as HashMap<String, HashMap<String, Any>>

            val market_dist = selectedstoreHashMap.get("distance")?.get(uid) as Double
            val market_address = selectedstoreHashMap.get("address") as String

            val tv_marketmapactivity_dialog_title =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_title)
            tv_marketmapactivity_dialog_title!!.setText("${poiItem?.itemName}")

            val tv_marketmapactivity_dialog_minute =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_minute)
            tv_marketmapactivity_dialog_minute!!.setText("도보 " + round((market_dist / 3.5) * 60).toString() + "분")

            val tv_marketmapactivity_dialog_distance =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_distance)
            tv_marketmapactivity_dialog_distance!!.setText("" + market_dist + "km")

            tv_marketmapactivity_dialog_salepercent
            tv_marketmapactivity_dialog_saleproduct

            // 상점의 최대 할인율과 그 품목
            var salePercentMax = 0.0
            var salePercentMaxProduct = ""
            val productList = ArrayList<String>(productHashMap!!.keys)
            for (i in 0 until productList!!.size) {
                val storeproductHashMap =
                    productHashMap!!.get(productList[i])

                if (storeproductHashMap?.get("storeName") == storeName) {
                    if (salePercentMax < storeproductHashMap?.get("discountRate") as Double) {
                        salePercentMax = storeproductHashMap?.get("discountRate") as Double
                        salePercentMaxProduct = storeproductHashMap?.get("productName") as String
                    }
                }
            }

            val tv_marketmapactivity_dialog_salepercent =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_salepercent)
            tv_marketmapactivity_dialog_salepercent!!.setText("" + round(salePercentMax * 100) + "%")

            val tv_marketmapactivity_dialog_saleproduct =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_saleproduct)
            tv_marketmapactivity_dialog_saleproduct!!.setText(salePercentMaxProduct)

            // 더 필요한 거 있으면 StoreListActivity 참고 바람!

            dialog.show()


        }

        override fun onDraggablePOIItemMoved(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            mapPoint: MapPoint?
        ) { // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }

    inner class ProductListDialogAdapter(storeName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            databaseReference = firebaseDatabase.getReference("products")
            databaseReference.orderByChild("discountRate").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    productList.clear()

                    for (postSnapshot in dataSnapshot.children) {
                        if(postSnapshot.child("storeName").toString() == storeName) {
                            val item = postSnapshot.getValue(Product::class.java)

                            if (item != null) {
                                productList.add(0, item) // index: 0 -> 내림차순 정렬
                            }
                        }
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_today_sale, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = (holder as ViewHolder).itemView

            viewHolder.tv_product_name?.text = productList[position].productName
            viewHolder.tv_store_name?.text = productList[position].storeName
            viewHolder.tv_discount_rate?.text =
                (productList[position].discountRate?.times(100))?.toInt()
                    .toString()
            viewHolder.tv_fixed_price?.text = productList[position].fixedPrice.toString()
            viewHolder.tv_discounted_price?.text = (productList[position].fixedPrice?.times(
                productList[position].discountRate!!
            ))?.toInt().toString()

            // drawable 파일에서 이미지 검색 후 적용
            val id = resources.getIdentifier(
                productList[position].imgUrl.toString(),
                "drawable",
                packageName
            )
            viewHolder.imv_product.setImageResource(id)

            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val databaseDistanceReference: DatabaseReference =
                firebaseDatabase.getReference("stores/${productList[position].storeName}/distance/${auth.uid}")

            databaseDistanceReference.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    viewHolder.tv_distance.text = dataSnapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            viewHolder.tv_fixed_price.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            viewHolder.tv_fixed_price_won.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            // Todo: recyclerview item click listener
        }

        override fun getItemCount(): Int {
            return productList.size
        }
    }
}