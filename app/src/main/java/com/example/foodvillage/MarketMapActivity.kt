package com.example.foodvillage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.foodvillage.databinding.ActivityMarketMapBinding
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.daum.mf.map.api.*
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class MarketMapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener{
    // 뷰 바인딩
    private var mBinding: ActivityMarketMapBinding? = null
    private val binding get() = mBinding!!

    private var mapView: MapView?=null

    private val eventListener = MarkerEventListener(this)   // 마커 클릭 이벤트 리스너
    // private val reverseGeoCoder:MapReverseGeoCoder// 마커 클릭 이벤트 리스너


    // 위치 추적을 위한 변수들
    val TAG: String = "로그"
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    var curr_lat=37.5406564
    var curr_lon=126.8809048

    val market_lat1=37.544
    val market_lon1=126.884

    val market_lat2=37.539
    val market_lon2=126.882

    var selected_marker_lat:Double=curr_lat
    var selected_marker_lon:Double=curr_lon

    var AddressData:String=""
    var marker_distance:Int = 0

    var marker_dist:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_map)
        // 바인딩
        mBinding = ActivityMarketMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 맵
        // 임포트 잘 해줘야함... mf들어간걸로!
        mapView = MapView(this)
        binding.clKakaoMapView2.addView(mapView)

        mapView!!.setCurrentLocationEventListener(this)
        mapView!!.setPOIItemEventListener(eventListener)


        //mapView!!.geo

        // 현재 위치
        // LocationRequest() deprecated 되서 아래 방식으로 LocationRequest 객체 생성
        // mLocationRequest = LocationRequest() is deprecated
        mLocationRequest =  LocationRequest.create().apply {
            interval = 1000 // 업데이트 간격 단위(밀리초)
            //fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            //maxWaitTime= 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        // 위치 추척 시작
        if (checkPermissionForLocation(this)) {
            // 현위치 트래킹 - 이건 주소 설정할 때 해서 최초로 받는거
            mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving)
            startLocationUpdates()
            mapView!!.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록

        }

        binding.btnMarketMapActivityFindway.setOnClickListener{
            var intent= Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?sp="+curr_lat+","+curr_lon+"&ep="+selected_marker_lat+","+selected_marker_lon+"&by=FOOT"))
            startActivity(intent)
        }

        binding.btnMarketMapActivityFloating.setOnClickListener{
            var mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat, curr_lon)
            mapView?.setMapCenterPoint(mapPoint, true)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff)
        mapView!!.setShowCurrentLocationMarker(false)
    }


    protected fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()")

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d(TAG, "startLocationUpdates() 위치 권한이 하나라도 존재하는 경우")
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청합니다.
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

        // 여기서 curr_lat, lon 바뀌
        mFusedLocationProviderClient!!.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d("위치", "updateCurrent")
                curr_lat=location!!.latitude
                curr_lon=location!!.longitude
                Log.d("위치", "updated: " + curr_lat + ", " + curr_lon)

                // 내 위치로 중심 이동
                var mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat, curr_lon)
                mapView?.setMapCenterPoint(mapPoint, true)
                mapView?.setZoomLevel(2, true)


                // 현재위치 주소값
                var reverseGeoCoder = MapReverseGeoCoder(
                    getApiKeyFromManifest(this),
                    MapPoint.mapPointWithGeoCoord(curr_lat, curr_lon),
                    object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                        override fun onReverseGeoCoderFoundAddress(
                            mapReverseGeoCoder: MapReverseGeoCoder,
                            s: String
                        ) { AddressData= s
                            binding.tvMarketmapactivityMylocation.setText(AddressData)
                        }
                        override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                            binding.tvMarketmapactivityMylocation.setText("address not found")
                        }
                    },
                    this
                )

                reverseGeoCoder.startFindingAddress()


                //마커 생성1
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                var marker = MapPOIItem()
                marker.itemName = "나연마트1"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(market_lat1, market_lon1)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성1
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker = MapPOIItem()
                marker.itemName = "나연마트3"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.543, 126.883)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성1
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker = MapPOIItem()
                marker.itemName = "나연마트4"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.542, 126.885)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성1
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker = MapPOIItem()
                marker.itemName = "나연마트5"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.542, 126.882)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)


                //마커 생성2
                marker=MapPOIItem()
                marker.itemName = "나연마트2"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(market_lat2, market_lon2)
                marker.markerType = MapPOIItem.MarkerType.BluePin
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)


                //마커 생성
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker=MapPOIItem()
                marker.itemName = "나연마트6"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.557, 126.943)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker=MapPOIItem()
                marker.itemName = "나연마트7"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.555, 126.945)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker=MapPOIItem()
                marker.itemName = "나연마트8"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.556, 126.944)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker=MapPOIItem()
                marker.itemName = "나연마트9"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.5565, 126.9425)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker=MapPOIItem()
                marker.itemName = "나연마트10"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.5533, 126.9477)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                //마커 생성
                //val markers=arrayListOf<MapPOIItem>() 클래스는 디비와 함께 넣기
                marker=MapPOIItem()
                marker.itemName = "나연마트11"   // 마커 이름
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.553, 126.948)
                marker.markerType = MapPOIItem.MarkerType.CustomImage
                marker.customImageResourceId = R.drawable.marker_fish
                marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                marker.customSelectedImageResourceId = R.drawable.marker_fish
                //isCustomImageAutoscale = false
                marker.setCustomImageAnchor(0.5f, 1.0f)
                mapView?.addPOIItem(marker)

                // 다 보이게 레벨 조정
                //mapView!!.fitMapViewAreaToShowAllPOIItems()
            }


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
        //수원 화성의 위도, 경도
        val mapPoint = MapPoint.mapPointWithGeoCoord(
            mLastLocation.latitude,
            mLastLocation.longitude
        )
        Log.d(TAG, "위도: " + mLastLocation.latitude + ", 경도: " + mLastLocation.longitude)
        /*
            여기서 현 위치 로그가 나오는데, 지도찾기할 때 이거 사용하고
            버튼 클릭 - 현 위치로 설정? -> 예 선택 시 저장
            -> 아니오시 계속 트래킹!
         */
        curr_lat=mLastLocation.latitude
        curr_lon=mLastLocation.longitude

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
                startLocationUpdates()

                // 현위치 트래킹
                mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving);


            } else {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(
                    this@MarketMapActivity,
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
    fun getCurrLat(): Double {
        return curr_lat
    }
    fun getCurrLon(): Double {
        return curr_lon
    }
    fun getAddress():String{
        return AddressData
    }
    fun getDist():Int{
        return marker_distance
    }

    inner class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
        private var currlat:Double=0.0
        private var currlon:Double=0.0
        private var AddressData:String=""

        private var marker_name:String=""



        override fun onPOIItemSelected(mapView: MapView?, marker: MapPOIItem?) {
            Log.d("마커", "onPOIItemSelected()")

            currlat=getCurrLat()
            currlon=getCurrLon()

            Log.d("마커", "폴리라인용 기준점: " + currlat + ", " + currlon)
            mapView!!.removeAllPolylines()

            // 라인 생성
            // 폴리 라인
            val polyline = MapPolyline()
            polyline.tag = 1000
            polyline.lineColor = Color.argb(128, 0, 0, 0) // Polyline 컬러 지정.

            if (marker != null) {
                // Polyline 좌표 지정

                val market_lat=marker.mapPoint.mapPointGeoCoord.latitude
                val market_lon=marker.mapPoint.mapPointGeoCoord.longitude

                Log.d("마커", ": " + market_lat + ", " + market_lon)

                selected_marker_lat=market_lat
                selected_marker_lon=market_lon

                polyline.addPoint(MapPoint.mapPointWithGeoCoord(curr_lat, curr_lon))
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(market_lat, market_lon))

                // Polyline 지도에 올리기.
                mapView!!.addPolyline(polyline)

                val mapPointBounds = MapPointBounds(polyline.mapPoints)
                val padding = 100 // px

                mapView!!.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))

                marker_distance=getDistance(curr_lat, curr_lon, market_lat, market_lon)
                marker_dist=marker_distance
                marker_name=marker!!.itemName

                Log.d("마커", ""+marker_name+", "+marker_dist)  // 각각 잘 들어옴

                // 주소값
                var reverseGeoCoder = MapReverseGeoCoder(
                    getApiKeyFromManifest(this@MarketMapActivity),
                    MapPoint.mapPointWithGeoCoord(market_lat, market_lon),
                    object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                        override fun onReverseGeoCoderFoundAddress(
                            mapReverseGeoCoder: MapReverseGeoCoder,
                            s: String
                        ) { AddressData= s
                            binding.tvMarketmapactivityMarketlocation.setText(AddressData)
                        }
                        override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                            binding.tvMarketmapactivityMarketlocation.setText("address not found")
                        }
                    },
                    this@MarketMapActivity
                )
                reverseGeoCoder.startFindingAddress()
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

            val dialog: BottomSheetDialog = BottomSheetDialog(this@MarketMapActivity)
            dialog.setContentView(R.layout.dialog_fmi_market)
            val tv_marketmapactivity_dialog_content = dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_content)
            tv_marketmapactivity_dialog_content!!.setText("주소: "+AddressData+"\n거리: "+(marker_distance.toDouble() / 1000).toString() + "km"+ "\n이동시간: "+(round(((marker_distance.toDouble() / 1000)/3.5)*60*10)/10).toString()+"분")
            tv_marketmapactivity_dialog_content?.setOnClickListener {
                Toast.makeText(this@MarketMapActivity, "내용을 클릭하였습니다", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            val tv_marketmapactivity_dialog_title = dialog.findViewById<TextView>(R.id.tv_marketmapactivity_dialog_title)
            tv_marketmapactivity_dialog_title!!.setText("${poiItem?.itemName}")

            dialog.show()
        }

        override fun onDraggablePOIItemMoved(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            mapPoint: MapPoint?
        ) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }

    }
    // 커스텀 말풍선 클래스
    inner class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
        val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)
        var dist:Int=0

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
            dist= marker_dist // 이 친구가 문제야 문제... -> 데이터 가져올 때 동시에 가져오면 만사 해결임
            Log.d("마커 풍성", ""+poiItem?.itemName+", "+marker_dist)


            // 지금 순서때문에 거리 잘못 나온는데, 나중에 디비 연동했을 때 고치면 됨!!
            address.text =(dist.toDouble() / 1000).toString() + "km, "+(round(((dist.toDouble() / 1000)/3.5)*60*10)/10).toString()+"분"
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            return mCalloutBalloon
        }
    }
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val R = 6372.8 * 1000

        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(toRadians(lat1)) * cos(
            toRadians(lat2)
        )
        val c = 2 * asin(sqrt(a))
        return (R * c).toInt()
    }




}

