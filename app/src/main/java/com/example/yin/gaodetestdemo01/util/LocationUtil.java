package com.example.yin.gaodetestdemo01.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Yin on 2016/4/12.
 */
public class LocationUtil {

    private final static String TAG = "LocationUtil";

    public static LocationManager locationManager;
    public static String provider;
    public static Location mLocation;

    public static double latitude;
    public static double longitude;

    public static LocationListener locationListener = new LocationListener() {
        /**
         * 监听器， 当位置信息发生变化
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static void getLocation(Context c) {
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
// 当没有可用的位置?供器时，弹出Toast?示用户
            Toast.makeText(c, "No location provider to use", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(c,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            showLocation(location);
            return;
        }
        locationManager.requestLocationUpdates(provider, 5000, 1,
                locationListener);
    }

    private static void showLocation(Location location) {
        String currentPosition = "latitude is " + location.getLatitude() + "\n"
                + "longitude is " + location.getLongitude();
        Log.d(TAG, "currentLocation = " + currentPosition);
    }

    public static Location returnLocation(Location location) {
        mLocation = location;
        return mLocation;
    }

//    public static double getMyLatitude(Context context) {
//        Location location = getLocation(context);
//        return location.getLatitude();
//    }
//
//    public static double getMyLongitude(Context context) {
//        Location location = getLocation(context);
//        return location.getLongitude();
//    }


    public static void onDestroyMethod(Context context) {
        if (locationManager != null) {
            // 关闭程序时将监听器移除
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }
}
