package com.example.foodvillage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.databinding.ActivityDbMarketMapBinding
import com.example.foodvillage.menu.HomeFragment
import com.example.foodvillage.schema.Store
import com.example.foodvillage.storeList.StoreAdapter
import com.example.foodvillage.storeList.StoreInfo
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_fmi_market.*
import net.daum.mf.map.api.*
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class DBMarketMapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener{
    // 뷰 바인딩
    private var mBinding: ActivityDbMarketMapBinding? = null
    private val binding get() = mBinding!!

    private var mapView: MapView?=null

    private val eventListener = MarkerEventListener(this)   // 마커 클릭 이벤트 리스너

    // 위치 추적을 위한 변수들
    val TAG: String = "로그"
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    var curr_lat:Double?=null
    var curr_lon:Double?=null
    var userName:String?=null

    var selected_marker_lat:Double?=null
    var selected_marker_lon:Double?=null

    var AddressData:String=""

    var mDatabase = FirebaseDatabase.getInstance()
    var uid = FirebaseAuth.getInstance().uid
    var DbRefUser = mDatabase.getReference("users/" + uid)
    val DbRefStore = mDatabase.getReference("stores/")
    val DbRefReview = mDatabase.getReference("reviews/")
    val DbRefProduct = mDatabase.getReference("products/")
    val DbRefCategory=mDatabase.getReference("categories/")

    var storeHashMap: HashMap<String, HashMap<String, Any>>?=null
    var storeNameList: List<String>?=null
    var reviewHashMap: HashMap<String, HashMap<String, Any>>?=null
    var productHashMap: HashMap<String, HashMap<String, Any>>?=null
    var categoryStoreList: List<String>?=null
    var storeList=ArrayList<Store>()
    var categoryHashMap: ArrayList<HashMap<String, Any>>?=null


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
                var marker = MapPOIItem()
                marker.itemName = "저장된 내 위치"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat!!, curr_lon!!)
                marker.markerType = MapPOIItem.MarkerType.BluePin
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                // 저장된 위치로 중심 이동
                var mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat!!, curr_lon!!)
                mapView?.setMapCenterPoint(mapPoint, true)
                mapView?.setZoomLevel(2, true)

                Log.d("유저", "위, 경도: " + curr_lat + ", " + curr_lon)
            }

        var categoryIdx=0

        DbRefCategory.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                Log.d("상점", it.value.toString())
                categoryHashMap = it.value as ArrayList<HashMap<String, Any>>
                categoryStoreList = categoryHashMap!![categoryIdx]?.get("storeNames") as List<String>

                DbRefStore.get()
                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                    .addOnSuccessListener {
                        storeHashMap = it.value as HashMap<String, HashMap<String, Any>>
                        storeNameList = ArrayList<String>(storeHashMap!!.keys)
                        DbRefReview.get()
                            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
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
                                        productHashMap = it.value as HashMap<String, HashMap<String, Any>>
                                        if (productHashMap != null && storeHashMap != null && reviewHashMap != null && productHashMap != null && categoryStoreList != null){
                                            Log.d("상점명 리스트", categoryStoreList.toString())
                                            for (i in 0 until (categoryStoreList?.size!!)){
                                                // 상점명
                                                val storeName = categoryStoreList!![i]
                                                // 상점 정보
                                                Log.d("상점명", storeHashMap!!.get(storeName).toString())
                                                val filteredstorehash=storeHashMap!!.get(storeName)
                                                val StoreIndi = Store()
                                                StoreIndi.address= filteredstorehash?.get("address") as String?
                                                StoreIndi.categoryNames= filteredstorehash?.get("categoryNames") as List<String>?
                                                StoreIndi.distance= filteredstorehash?.get("distance") as HashMap<String, Double>?
                                                StoreIndi.currentLongitude= filteredstorehash?.get("currentLongitude") as Double?
                                                StoreIndi.currentLatitude= filteredstorehash?.get("currentLatitude") as Double?
                                                StoreIndi.storeName= filteredstorehash?.get("storeName") as String?
                                                StoreIndi.storeImg= filteredstorehash?.get("storeImg") as String?
                                                storeList?.add(StoreIndi)
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

        binding.btnDbMarketMapActivityFloating.setOnClickListener{
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
//

    }


    protected fun updateLocation() {
        Log.d(TAG, "updateLocation()")

       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "updateLocation() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d(TAG, "updateLocation() 위치 권한이 하나라도 존재하는 경우")

        DbRefUser.get()
            .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
            .addOnSuccessListener {
                var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
                curr_lat= t_hashMap.get("currentLatitude") as Double
                curr_lon= t_hashMap.get("currentLongitude") as Double
                AddressData=t_hashMap.get("address") as String

                //binding.tvDbmarketmapactivityMylocation.setText(AddressData)
            }


        if(storeList!=null) {

            var filteredcategoryIdx = 0

            binding.btnAll.setOnClickListener {
                binding.btnAll.isSelected
                filteredcategoryIdx = 0
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }

            binding.btnFruitVegi.setOnClickListener {
                binding.btnFruitVegi.isSelected
                filteredcategoryIdx = 1
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnMeat.setOnClickListener {
                binding.btnMeat.isSelected
                filteredcategoryIdx = 2
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnSeafood.setOnClickListener {
                binding.btnSeafood.isSelected
                filteredcategoryIdx = 3
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnSideDish.setOnClickListener {
                binding.btnSideDish.isSelected
                filteredcategoryIdx = 4
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnSnack.setOnClickListener {
                binding.btnSnack.isSelected
                filteredcategoryIdx = 5
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnRiceAndNoodle.setOnClickListener {
                binding.btnRiceAndNoodle.isSelected
                filteredcategoryIdx = 6
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnHealthy.setOnClickListener {
                binding.btnHealthy.isSelected
                filteredcategoryIdx = 7
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
            binding.btnLife.setOnClickListener {
                binding.btnLife.isSelected
                filteredcategoryIdx = 8
                storeList = categoryFiltering(filteredcategoryIdx)
                markersShow(storeList)
                mapView?.fitMapViewAreaToShowAllPOIItems()
            }
        }

    }

    fun markersShow(storeList: ArrayList<Store>){
        mapView!!.removeAllPOIItems()
        mapView!!.removeAllPolylines()

        mapView!!.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록

        var marker = MapPOIItem()
        for (i in 0 until storeList.size) {

            marker = MapPOIItem()
            val storeName = storeList[i].storeName as String
            val currentLatitude = storeList[i].currentLatitude as Double
            val currentLongitude = storeList[i].currentLongitude as Double
            val address = storeList[i].address as String
            val categories = storeList[i].categoryNames as List<String>
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
    }

    fun categoryFiltering(filteredcategoryIdx:Int): ArrayList<Store> {
        categoryStoreList = categoryHashMap!![filteredcategoryIdx]?.get("storeNames") as List<String>
        Log.d("상점명 리스트_filtered", categoryStoreList.toString())
        storeList=ArrayList<Store>()

        for (i in 0 until (categoryStoreList?.size!!)){
            // 상점명
            val storeName = categoryStoreList!![i]
            // 상점 정보
            val filteredstorehash=storeHashMap!!.get(storeName)
            val StoreIndi = Store()
            StoreIndi.address= filteredstorehash?.get("address") as String?
            StoreIndi.categoryNames= filteredstorehash?.get("categoryNames") as List<String>?
            StoreIndi.distance= filteredstorehash?.get("distance") as HashMap<String, Double>?
            StoreIndi.currentLongitude= filteredstorehash?.get("currentLongitude") as Double?
            StoreIndi.currentLatitude= filteredstorehash?.get("currentLatitude") as Double?
            StoreIndi.storeName= filteredstorehash?.get("storeName") as String?
            StoreIndi.storeImg= filteredstorehash?.get("storeImg") as String?
//            // 상점의 리뷰 개수
//            val storereviewHashMap=reviewHashMap!!.get(storeName)
//            var reviewTotal= storereviewHashMap?.size
//            if (reviewTotal==null) reviewTotal=0
//
//            // 상점의 프로덕트 개수
//            // 상점의 최대 할인율
//            var prodNumTotal=0
//            var salePercentMax:Double=0.0
//            val productList = ArrayList<String>(productHashMap!!.keys)
//            for (i in 0 until productList!!.size){
//                val storeproductHashMap=productHashMap!!.get(productList[i])
//
//                if (storeproductHashMap?.get("storeName") == storeName) {
//                    prodNumTotal+=1
//                    salePercentMax= java.lang.Double.max(
//                        salePercentMax,
//                        storeproductHashMap?.get("discountRate") as Double
//                    )
//                }

//            }

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
    inner class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        var mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            if (poiItem != null) {
                val market_dist_hash= storeHashMap!!.get(poiItem?.itemName)?.get("distance") as HashMap<String, HashMap<String, Any>>
                val market_dist=market_dist_hash.get(uid) as Double

                (mCalloutBalloon.findViewById(R.id.ball_tv_name) as TextView).text =poiItem?.itemName
                (mCalloutBalloon.findViewById(R.id.ball_tv_address) as TextView).text =market_dist.toString() + "km, "+round((market_dist / 3.5) * 60).toString()+"분"
                Log.d("시간", "벌룬용(" + poiItem?.itemName + "): " + market_dist.toString() + ", " + round((market_dist / 3.5) * 60).toString())
            }
            return mCalloutBalloon

        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            return mCalloutBalloon
        }
    }

    inner class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
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

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {

            val dialog = BottomSheetDialog(this@DBMarketMapActivity)
            dialog.setContentView(R.layout.dialog_fmi_market)

            val storeName=poiItem?.itemName
            val selectedstoreHashMap=storeHashMap?.get(storeName) as HashMap<String, HashMap<String, Any>>

            val market_dist = selectedstoreHashMap.get("distance")?.get(uid) as Double
            val market_address = selectedstoreHashMap.get("address") as String

            val tv_marketmapactivity_dialog_title =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_title)
            tv_marketmapactivity_dialog_title!!.setText("${poiItem?.itemName}")

            val tv_marketmapactivity_dialog_minute =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_minute)
            tv_marketmapactivity_dialog_minute!!.setText("도보 "+ round((market_dist / 3.5) * 60).toString() + "분")

            val tv_marketmapactivity_dialog_distance =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_distance)
            tv_marketmapactivity_dialog_distance!!.setText(""+ market_dist + "km")

            tv_marketmapactivity_dialog_salepercent
            tv_marketmapactivity_dialog_saleproduct

            // 상점의 최대 할인율과 그 품목
            var salePercentMax= 0.0
            var salePercentMaxProduct=""
            val productList = ArrayList<String>(productHashMap!!.keys)
            for (i in 0 until productList!!.size) {
                val storeproductHashMap =
                    productHashMap!!.get(productList[i])

                if (storeproductHashMap?.get("storeName") == storeName) {
                    if (salePercentMax<storeproductHashMap?.get("discountRate") as Double){
                        salePercentMax=storeproductHashMap?.get("discountRate") as Double
                        salePercentMaxProduct = storeproductHashMap?.get("productName") as String
                    }
                }
            }

            val tv_marketmapactivity_dialog_salepercent =
                dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_salepercent)
            tv_marketmapactivity_dialog_salepercent!!.setText(""+ round(salePercentMax*100) + "%")

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

}

