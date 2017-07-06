package com.jiangda.qiucheng.birdspreliminary;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiucheng on 2017/7/3.
 */

public class MarkerOverlay {
    private List<LatLng> pointList = new ArrayList<LatLng>();
    private AMap aMap;
    private LatLng centerPoint;
    private Marker centerMarker;
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();


    public MarkerOverlay(AMap amap,List<LatLng> points){
        this.aMap = amap;
        initPointList(points);
    }

//    public MarkerOverlay(AMap amap,List<LatLng> points,LatLng centerPoint){
//        this.aMap = amap;
//        this.centerPoint = centerPoint;
//        initPointList(points);
//
//    }

    //初始化list
    private void initPointList(List<LatLng> points){
        if (points != null && points.size()>0){
            for (LatLng point:points){
                pointList.add(point);
            }
        }
    }



    /**
     * 添加Marker到地图中。
     */
    public void addToMap(){
        try {
            for (int i = 0;i < pointList.size(); i++){
                Marker marker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        .position(pointList.get(i))
                        .title("设备" + i));
                marker.setObject(i);
                mMarkers.add(marker);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }


    /**
     * 去掉MarkerOverlay上所有的Marker。
     */
    public void removeFromMap(){
        for (Marker marker:mMarkers){
            marker.remove();
        }
        centerMarker.remove();
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中，且地图中心点不变。
     */
    public void zoomToSpanWithCenter(){
        if (pointList != null && pointList.size() > 0){
            if (aMap == null){
                return;
            }
            LatLngBounds bounds = getLatLngBounds(centerPoint,pointList);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,50));
        }
    }

    //根据中心点和自定义内容获取缩放bounds
    private LatLngBounds getLatLngBounds(LatLng centerPoint,List<LatLng> pointList){
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (centerPoint != null){
            for (int i = 0;i<pointList.size();i++){
                LatLng p = pointList.get(i);
                LatLng p1 = new LatLng((centerPoint.latitude * 2) - p.latitude,(centerPoint.longitude * 2) - p.longitude);
                b.include(p);
                b.include(p1);
            }
        }
        return b.build();
    }

    /**
     *  缩放移动地图，保证所有自定义marker在可视范围中。
     */
    public void zoomToSpan(){
        if (pointList != null && pointList.size() > 0){
            if (aMap == null){
                return;
            }
            centerMarker.setVisible(false);
            LatLngBounds bounds = getLatLngBounds(pointList);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,50));
        }
    }

    /**
     * 根据自定义内容获取缩放bounds
     */
    private LatLngBounds getLatLngBounds(List<LatLng> pointList){
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0;i < pointList.size();i++){
            LatLng p = pointList.get(i);
            b.include(p);
        }
        return b.build();
    }

}
