package com.example.slipwindow;
/**
 * 我的位置
 */

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class MyLocationActivity extends AppCompatActivity {
    private MapView mapView;
    private LocationClient locationClient;
    private boolean isFirstLocate=true;
    private BaiduMap baiduMap;
    private BitmapDescriptor descriptor;
    private SupportMapFragment map;
    private ProgressDialog mDialog;
    private Handler mHandler;
    private TextView myLocationTextView;
    private BDLocation bdLocation;
    private InfoWindow mInfoWindow;
    private LinearLayout baidumap_infowindow;
    private View popView;
    private TextView textView;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("我的位置");
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_my_location);
        mapView=(MapView)findViewById(R.id.my_location_map_view);//地图视图
        myLocationTextView=(TextView)findViewById(R.id.my_location_text_view);
        baiduMap=mapView.getMap();//获取地图控制器
        baiduMap.setMyLocationEnabled(true);
        List<String> permissionList=new ArrayList<>();
        //动态检查申请权限
        if(ContextCompat.checkSelfPermission(MyLocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MyLocationActivity.this, android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MyLocationActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){//动态申请权限
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MyLocationActivity.this,permissions,1);

        }else{
            requestLocation();
        }
    }

    /**
     * 第一次加载地图转到我的位置
     * @param location
     */
   private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate=false;
        }

      /* TextOptions textOptions = new TextOptions();

       textOptions.fontColor(0x60ff0000)//设置字体颜色
               .text("无良印品，窗前明月光")//文字内容
               .position(new LatLng(location.getLatitude(),location.getLongitude()))//位置
               .fontSize(24)//字体大小
               .typeface(Typeface.SERIF)//字体
               .rotate(30);//旋转
       baiduMap.addOverlay(textOptions);*/
       drawMarker(location);
      // initPop(location);

        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData=locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    /**
     * 申请位置
     */
    private void requestLocation(){
        innitLocation();
        locationClient.start();
    }

    /**
     * 实现每5秒更新位置
     */
    private void innitLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);//5秒更新位置
        option.setIsNeedAddress(true);//确切位置
        locationClient.setLocOption(option);
    }

    private void drawMarker(BDLocation location) {

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(),location.getLongitude()))//设置位置
                .icon(bitmapDescriptor)//设置覆盖物小图标
                .draggable(true)//设置是否可以拖拽，默认为否
                .title("中心");//设置标题
        baiduMap.addOverlay(markerOptions);

      /*  markerOptions = new MarkerOptions().title("向北")
                .position(new LatLng(latitude+0.001, longitude))
                .icon(bitmapDescriptor);
        baiduMap.addOverlay(markerOptions);

        ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
        bitmaps.add(bitmapDescriptor);
        bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_geo));

        markerOptions = new MarkerOptions().title("向东")
                .position(new LatLng(latitude, longitude+0.001))
                .icons(bitmaps)//图标设置一个帧动画，多个图片来回切换
                .period(10);//切换帧的时间间隔
        baiduMap.addOverlay(markerOptions);

        markerOptions = new MarkerOptions().title("向西南")
                .position(new LatLng(latitude-0.001, longitude-0.001))
                .icon(bitmapDescriptor);
        baiduMap.addOverlay(markerOptions);*/

       /* baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker result) {
                //当点击时，更新pop的位置，设置为显示

                MapViewLayoutParams layoutParams = new MapViewLayoutParams.Builder()
                        .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)//按照经纬度设置位置
                        .position(result.getPosition())//不能传null
                        .width(MapViewLayoutParams.WRAP_CONTENT)
                        .height(MapViewLayoutParams.WRAP_CONTENT)
                        .yOffset(-5)//距离position的像素 向下是正值，向上是负值
                        .build();

                mapView.updateViewLayout(popView, layoutParams);
                popView.setVisibility(View.VISIBLE);
                textView.setText(result.getTitle());

                return true;
            }
        });*/
    }

   /* private void initPop(BDLocation location) {
        //加载pop 添加到mapview,设置为隐藏

        popView = View.inflate(getApplicationContext(), R.layout.baidu_map_infowindow, null);
        MapViewLayoutParams layoutParams = new MapViewLayoutParams.Builder()
                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)//按照经纬度设置位置
                .position(new LatLng(location.getLatitude(),location.getLongitude()))//不能传null，设置为mapMode时，必须设置position
                .width(MapViewLayoutParams.WRAP_CONTENT)
                .height(MapViewLayoutParams.WRAP_CONTENT)
                .build();

        mapView.addView(popView, layoutParams);
        popView.setVisibility(View.INVISIBLE);

        textView = (TextView) popView.findViewById(R.id.tv_entname);
    }*/


    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    protected void onDestroy(){
        super.onDestroy();
        locationClient.stop();//停止更新位置，保护电量
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MyLocationActivity.this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(MyLocationActivity.this,"发生位置错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void draw(BDLocation bdLocation) {
        TextOptions textOptions = new TextOptions();

        textOptions.fontColor(0x60ff0000)//设置字体颜色
                .text("无良印品，窗前明月光")//文字内容
               // .position(bdLocation)//位置
                .fontSize(24)//字体大小
                .typeface(Typeface.SERIF)//字体
                .rotate(30);//旋转

        baiduMap.addOverlay(textOptions);
    }


    public class MyLocationListener implements BDLocationListener{
        public void onReceiveLocation(BDLocation location){
            StringBuilder currentPosition=new StringBuilder();
            if(location.getLocType()==BDLocation.TypeGpsLocation||location.getLocType()==BDLocation.TypeNetWorkLocation){
                navigateTo(location);
            }
            currentPosition.append("地址：").append(location.getCountry())
                    .append(location.getProvince())
                    .append(location.getCity())
                    .append(location.getDirection())
                    .append(location.getStreet());
            myLocationTextView.setText(currentPosition);
        }
        public void onConnectHotSpotMessage(String n,int i){

        }
    }

    private void setListener(){
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }


    /**
     * 标题栏返回按钮
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)
        {
            finish();//结束当前活动
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
