package com.sky.cookbooksa;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.sky.cookbooksa.map.GeocoderHelper;
import com.sky.cookbooksa.map.PoiKeywordSearchHelper;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.widget.RadarScanView;

/**
 * Created by arvin.li on 2015/8/5.
 */
public class NearbyMapActivity extends BaseActivity {

    private AMap amap;

    private MapView mapView;

    private RadarScanView radarScanView;

    private ImageButton backBtn;

    private AutoCompleteTextView searchEdit;

    private Button searchBtn;

    private TextView titleText;

    private EditText keyEdit;

    private AMapLocationListener aMapLocationListener;

    private LocationSource.OnLocationChangedListener locationChangedListener;

    private LocationManagerProxy locationManagerProxy;

    private LocationSource mLoactionSource;

    private GeocoderHelper geocoderHelper;

    private PoiKeywordSearchHelper poiKeywordSearchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_nearby);

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.onCreate(savedInstanceState);

        init();
    }

    private void init() {

        searchBtn = (Button) findViewById(R.id.searchBtn);
        keyEdit = (EditText) findViewById(R.id.addrKeyEdit);
        searchEdit = (AutoCompleteTextView) findViewById(R.id.searchEdit);
        radarScanView = (RadarScanView) findViewById(R.id.radarScanView);
        backBtn = (ImageButton) findViewById(R.id.back);
        titleText = (TextView) findViewById(R.id.title);
        backBtn.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);

        titleText.setText("附近的人");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String keyStr = keyEdit.getText().toString();

                if (StringUtil.isEmpty(keyStr.trim())) {

                    ToastUtil.toastShort(context, "不能为空！");

                    return;
                }

                geocoderHelper.getLatLngByAddress(keyStr);

                keyEdit.setText("");

                toggleKeyboard(keyEdit, false);
            }
        });

        initMap();

        addMarkers();

        geocoderHelper = new GeocoderHelper(NearbyMapActivity.this, amap);

        poiKeywordSearchHelper = new PoiKeywordSearchHelper(NearbyMapActivity.this, amap, searchEdit);
        poiKeywordSearchHelper.setSearchCity("深圳");
    }

    private void addMarkers() {

        amap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .draggable(false).position(new LatLng(22.546253, 114.142628))
                .title("小贱").snippet("大家好")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                .showInfoWindow();

        amap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .draggable(false).position(new LatLng(22.546222, 114.141941))
                .title("小猪").snippet("大家好")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        amap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .draggable(false).position(new LatLng(22.547223, 114.142038))
                .title("小木").snippet("大家好")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    /**
     * 初始化地图信息
     */
    private void initMap() {

        amap = mapView.getMap();

        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.anchor(0.5f, 0.5f);
        locationStyle.strokeColor(Color.BLACK);
        locationStyle.radiusFillColor(Color.parseColor("#50FF00FF"));// 设置圆形填充色
        locationStyle.strokeWidth(3);
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));

        amap.setMyLocationStyle(locationStyle);// 设置自定义定位小蓝点

        mLoactionSource = new MyLocationSource();
        amap.setLocationSource(mLoactionSource);// 设置定位监听

        amap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        amap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        //地图加载完毕回调函数
        amap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                //显示所有LatLngBounds区域
//                LatLngBounds bounds = new LatLngBounds.Builder()
//                        .include(new LatLng(22.54, 113.58))
//                        .include(new LatLng(22.78, 113.87))
//                        .include(new LatLng(22.42, 114.12))
//                        .build();
//                amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NearbyMapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                radarScanView.setVisibility(View.GONE);
                            }
                        });
                    }
                }, 2000);

                amap.moveCamera(CameraUpdateFactory.zoomTo(18));//设置地图缩放级别，缩放级别是在4-20 之间。
            }
        });

        //marker点击事件
        amap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.showInfoWindow();

                return false;
            }
        });

        //获取InfoWindow
        amap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {


            /**
             * 监听自定义infowindow窗口的infowindow事件回调
             * @param marker
             * @return
             */
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            /**
             * 监听自定义infowindow窗口的infocontents事件回调
             * @param marker
             * @return
             */
            @Override
            public View getInfoContents(Marker marker) {

                View view = LayoutInflater.from(context).inflate(R.layout.map_infocontent, null);

                updateView(view, marker);

                return view;
            }
        });

        //InfoWindow点击事件
        amap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                ToastUtil.toastShort(context, marker.getSnippet());
            }
        });

        //Map点击事件
        amap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                geocoderHelper.getAddressByLatLng(latLng);
            }
        });
    }

    /**
     * 设置自定义InfoWindow内容
     *
     * @param view
     * @param marker
     */
    private void updateView(View view, Marker marker) {
        ImageView img = (ImageView) view.findViewById(R.id.infoImg);
        TextView titleText = (TextView) view.findViewById(R.id.titlteText);
        TextView snippetText = (TextView) view.findViewById(R.id.snippeText);

        img.setImageResource(R.drawable.love_icon);
        titleText.setText(marker.getTitle());
        snippetText.setText(marker.getSnippet());
    }

    /**
     * 定位接口
     */
    class MyLocationSource implements LocationSource {

        /**
         * 开始定位
         *
         * @param onLocationChangedListener
         */
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {

            locationChangedListener = onLocationChangedListener;

            if (locationManagerProxy == null) {
                locationManagerProxy = LocationManagerProxy.getInstance(context);

                aMapLocationListener = new MyAmapLocationListener();
                locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork,
                        2000, 10, aMapLocationListener);
            }

        }

        /**
         * 停止定位
         */
        @Override
        public void deactivate() {

            locationChangedListener = null;

            if (locationManagerProxy != null) {
                locationManagerProxy.removeUpdates(aMapLocationListener);
                locationManagerProxy.destroy();
            }

            locationManagerProxy = null;
        }
    }

    /**
     * 位置改变接口
     */
    class MyAmapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (locationChangedListener != null && aMapLocation != null) {
                locationChangedListener.onLocationChanged(aMapLocation);//显示系统小蓝点
            }

            mLoactionSource.deactivate();//停止定位
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mapView.onPause();

        mLoactionSource.deactivate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();

        mLoactionSource.deactivate();
    }
}
