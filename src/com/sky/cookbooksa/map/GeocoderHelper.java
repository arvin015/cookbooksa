package com.sky.cookbooksa.map;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sky.cookbooksa.NearbyMapActivity;
import com.sky.cookbooksa.utils.MapUtil;
import com.sky.cookbooksa.utils.ToastUtil;

import java.util.List;

/**
 * Geocoder帮助类
 * Created by arvin.li on 2015/8/6.
 */
public class GeocoderHelper {

    private NearbyMapActivity act;

    private AMap aMap;

    private GeocodeSearch geocodeSearch;

    private LatLng currentLatLng;//当前坐标

    private Marker pointMarker;

    public GeocoderHelper(NearbyMapActivity act, AMap aMap) {

        this.act = act;
        this.aMap = aMap;

        init();
    }

    private void init() {

        geocodeSearch = new GeocodeSearch(act);

        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

            /**
             * 逆地理编码回调
             */
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                switch (i) {
                    case 0:

                        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {

                            RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();

                            if (address != null) {

                                if (currentLatLng != null) {

                                    if (pointMarker == null) {
                                        pointMarker = MapUtil.addMarker(aMap, currentLatLng,
                                                address.getProvince() + address.getCity(), address.getFormatAddress());
                                    } else {
                                        pointMarker.setPosition(currentLatLng);
                                        pointMarker.setTitle(address.getProvince() + address.getCity());
                                        pointMarker.setSnippet(address.getFormatAddress());
                                    }

                                    pointMarker.showInfoWindow();

                                    //该点聚焦为中心
                                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                }

                            }
                        } else {
                            ToastUtil.toastShort(act, "没有搜索到相关数据！");
                        }

                        break;
                    case 27:

                        ToastUtil.toastShort(act, "搜索失败,请检查网络连接！");

                        break;
                    case 32:

                        ToastUtil.toastShort(act, "key验证无效！");

                        break;
                    default:

                        ToastUtil.toastShort(act, "未知错误，请稍后重试！");

                        break;
                }
            }

            /**
             * 地理编码查询回调
             */
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                switch (i) {
                    case 0:

                        if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null) {

                            List<GeocodeAddress> addressList = geocodeResult.getGeocodeAddressList();

                            if (addressList != null) {

                                GeocodeAddress address = addressList.get(0);

                                LatLng latLng = MapUtil.convertToLatLng(address.getLatLonPoint());

                                if (pointMarker == null) {
                                    pointMarker = MapUtil.addMarker(aMap, latLng,
                                            address.getProvince() + address.getCity(), address.getFormatAddress());
                                } else {
                                    pointMarker.setPosition(latLng);
                                    pointMarker.setTitle(address.getProvince() + address.getCity());
                                    pointMarker.setSnippet(address.getFormatAddress());
                                }

                                pointMarker.showInfoWindow();

                                //显示目标区域
                                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }
                        } else {
                            ToastUtil.toastShort(act, "没有搜索到相关数据！");
                        }

                        break;
                    case 27:

                        ToastUtil.toastShort(act, "搜索失败,请检查网络连接！");

                        break;
                    case 32:

                        ToastUtil.toastShort(act, "key验证无效！");

                        break;
                    default:

                        ToastUtil.toastShort(act, "未知错误，请稍后重试！");

                        break;
                }
            }
        });
    }

    /**
     * 根据坐标获取地址
     *
     * @param latLng
     */
    public void getAddressByLatLng(LatLng latLng) {
        if (latLng != null) {

            currentLatLng = latLng;

            LatLonPoint point = MapUtil.convertToLatLonPoint(latLng);

            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(point,
                    200, GeocodeSearch.AMAP);

            geocodeSearch.getFromLocationAsyn(query);
        }
    }

    public void getLatLngByAddress(String address) {
        if (address != null) {

            // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            GeocodeQuery query = new GeocodeQuery(address, "深圳");

            geocodeSearch.getFromLocationNameAsyn(query);

        }
    }
}
