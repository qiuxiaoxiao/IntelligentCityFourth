package com.jiangda.qiucheng.birdspreliminary;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AMap.OnMapLoadedListener,AMap.OnMarkerClickListener{

    private MapView mMapView = null;
    private AMap aMap = null;
    private LatLng centerPoint = new LatLng(30.7839, 114.2268);// 中心点：天河机场综合中心
    private Marker centerMarker;
    private boolean isFirstLoc = true;

    //定义获取蓝牙信息的按钮
    private Button GetBleInfo;
    private AMapLocation mCurrentLocation = null;

    private double latitude;
    private double longitude;
    private MarkerOverlay markerOverlay;
    private List<LatLng> pointList = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件使用
        mMapView = (MapView) findViewById(R.id.map_view_main);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState),实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        GetBleInfo = (Button)findViewById(R.id.getBleInfo);
        GetBleInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMarker();
            }
        });
        init();
    }

    private void init(){
        if (aMap == null){
            aMap = mMapView.getMap();
        }
        aMap.setOnMapLoadedListener(this); //地图加载完成监听
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(this);
    }

    /**
     * 地图加载完成回调
     */
    @Override
    public void onMapLoaded() {
        initCenterMarker();
    }

    //初始化中心点Marker
    private void initCenterMarker() {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(centerPoint));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        this.centerMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.location_marker))
                .position(centerPoint)
                .title("当前位置"));
        centerMarker.showInfoWindow();
    }

    // 定义 Marker 点击事件监听
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("hhhh","jjjj");
        Intent intent = new Intent(MainActivity.this,SendActivity.class);
        startActivity(intent);
        return false;
    }

    /**
     * 显示Marker
     */
    public void loadMarker(){
        //添加MarkerOnerlay
        markerOverlay = new MarkerOverlay(aMap, getPointList());
        markerOverlay.addToMap();
        markerOverlay.zoomToSpanWithCenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        markerOverlay.removeFromMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    private List<LatLng> getPointList() {
        pointList.add(new LatLng(30.7879, 114.2252));
        pointList.add(new LatLng(30.7899, 114.2267));
        pointList.add(new LatLng(30.7928, 114.2284));
        pointList.add(new LatLng(30.7956, 114.2308));
        pointList.add(new LatLng(30.7947, 114.2275));
        pointList.add(new LatLng(30.7910, 114.2241));
        pointList.add(new LatLng(30.7878, 114.2213));
        pointList.add(new LatLng(30.7854, 114.2183));
        pointList.add(new LatLng(30.7826, 114.2155));
        pointList.add(new LatLng(30.7783, 114.2117));
        return pointList;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }
}