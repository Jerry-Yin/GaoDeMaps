package com.example.yin.gaodetestdemo01.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.example.yin.gaodetestdemo01.R;

public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener{

    private MapView mMapView;
    private AMap mAmap;
    private TextView mLocationErrText;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final Snackbar snackbar = Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT);
//                snackbar.setAction("Ok", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        snackbar.dismiss();
//                    }
//                });
//                snackbar.show();

               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("功能选择").setItems(new String[]{"关键字搜索", "周边搜索", "区域搜索", "地理编码及逆向编码", "导航"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                startActivity(new Intent(MainActivity.this, KeyWordSearchActivity.class));
//                                MainActivity.this.finish();
                                break;

                            case 1:
                                startActivity(new Intent(MainActivity.this, AroundSearchActivity.class));
                                break;

                            case 2:

                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, GeocoderActivity.class));
                                break;
                            case 4:
                                startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                                break;
                        }
                    }
                });
                builder.show();
            }
        });


        initViews(savedInstanceState);
        initData();
    }

    private void initViews(Bundle savedInatanceState) {
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.onCreate(savedInatanceState);  // 此方法必须重写

        mLocationErrText = (TextView) findViewById(R.id.location_errinfo_text);
        mLocationErrText.setVisibility(View.GONE);
    }

    private void initData() {
        if (mAmap == null ){
            mAmap = mMapView.getMap();
            initLocation();
        }
//        mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
//        mGPSModeGroup.setOnCheckedChangeListener(this);

    }

    /**
     * 定位设置 初始化
     * 添加定位接口 ---> 设置定位监听器 ---> 获取定位结果
     */
    private void initLocation() {
        mAmap.setLocationSource(this);
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);
        mAmap.setMyLocationEnabled(true);
//        mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE); //设置定位的类型为定位模式
//        mAmap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW); //设置定位的类型为跟随模式
        mAmap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE); //设置定位的类型为旋转模式

    }



    /**
     * 选择矢量地图/卫星地图/夜景地图/导航地图事件的响应
     */
//    private void setLayer(String layerName) {
//        if (layerName.equals(getString(R.string.normal))) {
//            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
//        } else if (layerName.equals(getString(R.string.satellite))) {
//            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
//        }else if(layerName.equals(getString(R.string.night_mode))){
//            aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
//        }
//        else if(layerName.equals(getString(R.string.navi_mode))){
//            aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
//        }
//    }


    /**
     * 激活定位
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 定位成功后回调此方法
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if ( mListener!= null && aMapLocation != null){
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0){
                //定位成功
                mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(aMapLocation);
                Log.d("LocationMessage", "定位成功"+aMapLocation.getAddress().toString());
            }else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
