package com.example.yin.gaodetestdemo01.activity;

import android.os.Bundle;

import com.amap.api.navi.AMapNavi;

/**
 * Created by Yin on 2016/4/12.
 */
public class NavigationActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AMapNavi mapNavi = AMapNavi.getInstance(this);
    }
}
