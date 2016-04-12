package com.example.yin.gaodetestdemo01.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.yin.gaodetestdemo01.R;
import com.example.yin.gaodetestdemo01.util.DialogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yin on 2016/4/12.
 * 地理编码
 * （ps: 插件运用      compile 'com.jakewharton:butterknife:7.0.1')
 */
public class GeocoderActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener {


    @Bind(R.id.btn_regeo)
    Button mBtnRegeo;
    @Bind(R.id.btn_geo)
    Button mBtnGeo;
    @Bind(R.id.map)
    MapView mMapView;

    private AMap mAMap;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
    private Marker geoMarker;
    private Marker regeoMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_geo_coder);
        ButterKnife.bind(this);
         /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            geoMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            regeoMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        mBtnGeo = (Button) findViewById(R.id.btn_geo);
        mBtnRegeo = (Button) findViewById(R.id.btn_regeo);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
//        progDialog = new ProgressDialog(this);
    }

    @OnClick({R.id.btn_regeo, R.id.btn_geo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_regeo:
                //反地理编码
                getAddress(latLonPoint);
                break;

            case R.id.btn_geo:
                //地理编码
                getLatlon("方恒国际中心");
                break;
            default:
                break;
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        DialogUtils.showProgressDialog(GeocoderActivity.this, "正在获取地址", null);
        GeocodeQuery query = new GeocodeQuery(name, "010");// name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        DialogUtils.showProgressDialog(GeocoderActivity.this, "正在获取地址", null);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 逆向编码回调
     * @param result
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        DialogUtils.dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                regeoMarker.setPosition(latLng);
                Toast.makeText(this, addressName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, rCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 地理编码回调
     * @param result
     * @param rCode
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        DialogUtils.dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
//                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                LatLonPoint point = address.getLatLonPoint();
                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                geoMarker.setPosition(latLng);
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress();
                Toast.makeText(this, addressName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, rCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
