package com.sky.cookbooksa.utils;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvin.li on 2015/8/6.
 */
public class MapUtil {

    /**
     * 添加Marker
     *
     * @param aMap
     * @param latLng
     * @param drawableId
     * @param title
     * @param snippet
     * @return
     */
    public static Marker addMarker2(AMap aMap, LatLng latLng, int drawableId,
                                    String title, String snippet) {

        Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(drawableId))
                .title(title).snippet(snippet));

        return marker;
    }

    /**
     * 添加Marker
     *
     * @param aMap
     * @param latLng
     * @param title
     * @param snippet
     * @return
     */
    public static Marker addMarker(AMap aMap, LatLng latLng, String title, String snippet) {

        Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(title).snippet(snippet));

        return marker;

    }

    /**
     * 获取友好距离
     *
     * @param lenMeter
     * @return
     */
    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + ChString.Meter;
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把集合体的LatLonPoint转化为集合体的LatLng
     */
    public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = MapUtil.convertToLatLng(point);
            lineShapes.add(latLngTemp);
        }
        return lineShapes;
    }
}
