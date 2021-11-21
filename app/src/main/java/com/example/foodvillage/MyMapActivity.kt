package com.example.foodvillage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.foodvillage.databinding.ActivityMyMapBinding
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import net.daum.mf.map.api.*
import net.daum.mf.map.api.MapPoint.GeoCoordinate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.round


class MyMapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener {

    // 뷰 바인딩
    private var mBinding: ActivityMyMapBinding? = null
    private val binding get() = mBinding!!

    private var mapView: MapView?=null

    var curr_lat=37.5406564
    var curr_lon=126.8809048
    var AddressData:String=""
    var marker_distance:Int = 0

    var marker_dist:Int=0

    // 위치 추적을 위한 변수들
    val TAG: String = "로그"

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_map)

        // 바인딩
        mBinding = ActivityMyMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnMymapactivitySavemylocation.setOnClickListener{
            binding.tvMymapactivityMysavedlocation.setText("현재 위치: " + curr_lat + ", " + curr_lon)
            // 현재 위치 디비에서 받아오기
            val mDatabase = FirebaseDatabase.getInstance()
            val uid= FirebaseAuth.getInstance().uid
            val DbRefUsers = mDatabase.getReference("users/" + uid)

            // 현재위치 주소값
            var reverseGeoCoder = MapReverseGeoCoder(
                getApiKeyFromManifest(this),
                MapPoint.mapPointWithGeoCoord(curr_lat, curr_lon),
                object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                    override fun onReverseGeoCoderFoundAddress(
                        mapReverseGeoCoder: MapReverseGeoCoder,
                        s: String
                    ) {
                        AddressData = s
                        binding.tvMymapactivityMylocation.setText(AddressData)
                    }

                    override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                        binding.tvMymapactivityMylocation.setText("address not found")
                    }
                },
                this
            )

            reverseGeoCoder.startFindingAddress()

            DbRefUsers.child("address").setValue(AddressData)
                .addOnFailureListener { e -> Log.d(ContentValues.TAG, e.localizedMessage) }
                .addOnSuccessListener {
                    Log.d("파베", AddressData)
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
            Log.d("파베", ""+AddressData+", "+curr_lat+", "+curr_lon)
        }

        binding.btnMymapactivityFloating.setOnClickListener{
            var mapPoint = MapPoint.mapPointWithGeoCoord(curr_lat, curr_lon)
            mapView?.setMapCenterPoint(mapPoint, true)
        }


        // 맵
        // 임포트 잘 해줘야함... mf들어간걸로!
        mapView = MapView(this)
        binding.clKakaoMapView.addView(mapView)
        mapView!!.setCurrentLocationEventListener(this);

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
            startLocationUpdates()

            // 현위치 트래킹 - 이건 주소 설정할 때 해서 최초로 받는거
            mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            //TrackingModeOnWithMarkerHeadingWithoutMapMoving);

        }

    }
    override fun onDestroy() {
        super.onDestroy()
        mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff)
        mapView!!.setShowCurrentLocationMarker(false)

        // 마트까지의 거리 계산
        val mDatabase = FirebaseDatabase.getInstance()

        val uid = FirebaseAuth.getInstance().uid
        val DbRefUsers = mDatabase.getReference("users/" + uid)

        DbRefUsers.get()
            .addOnFailureListener {
                Toast.makeText(this@MyMapActivity, "아직 위치가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show()
                Log.d("유저", "못 가져옴")
            }
            .addOnSuccessListener {
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
                curr_lat = location!!.latitude
                curr_lon = location!!.longitude
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
                        ) {
                            AddressData = s
                            binding.tvMymapactivityMylocation.setText(AddressData)
                        }

                        override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                            binding.tvMymapactivityMylocation.setText("address not found")
                        }
                    },
                    this
                )

                reverseGeoCoder.startFindingAddress()


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
//        txtLat.text = "LATITUDE : " + mLastLocation.latitude // 갱신 된 위도
//        txtLong.text = "LONGITUDE : " + mLastLocation.longitude // 갱신 된 경도
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

//                // View Button 활성화 상태 변경
//                btnStartupdate.isEnabled = false
//                btnStopUpdates.isEnabled = true


            } else {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this@MyMapActivity, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCurrentLocationUpdate(p0: MapView, p1: MapPoint, p2: Float) {
        val mapPointGeo: GeoCoordinate = p1.getMapPointGeoCoord()
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
        return round((R * c)) /100
    }
}