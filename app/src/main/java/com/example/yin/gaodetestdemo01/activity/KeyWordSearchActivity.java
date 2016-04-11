package com.example.yin.gaodetestdemo01.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.yin.gaodetestdemo01.R;

import java.util.List;

/**
 * Created by Yin on 2016/3/28.
 */
public class KeyWordSearchActivity extends Activity implements AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, View.OnClickListener, TextWatcher, PoiSearch.OnPoiSearchListener {

    private MapView mMapView;
    private Button mBtnSearch, mBtnNext;
    private EditText mEtKeyword, mEtCity;
    private AMap mAmap;

    private String mkeyWord = "";// 要输入的poi搜索关键字
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果

    private ProgressDialog progDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_keyword_search);

        initViews(savedInstanceState);
        inoitData();
    }
    private void initViews(Bundle state) {
        mMapView = (MapView) findViewById(R.id.map_view_keyword);
        mMapView.onCreate(state);

        mBtnSearch = (Button) findViewById(R.id.btn_searchButton);
        mBtnNext = (Button) findViewById(R.id.btn_nextButton);
        mBtnSearch.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        mEtKeyword = (EditText) findViewById(R.id.et_keyWord);
        mEtCity = (EditText) findViewById(R.id.et_city);
        mEtKeyword.addTextChangedListener(this);
    }



    private void inoitData() {
        if (mAmap == null){
            mAmap = mMapView.getMap();
            setupMap();
        }
    }

    private void setupMap() {
        mAmap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        mAmap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_searchButton:
                mkeyWord = checkEditText(mEtKeyword);
                if ("".equals(mkeyWord)) {
                    Toast.makeText(KeyWordSearchActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    doSearchQuery();
                }
                break;

            case R.id.btn_nextButton:

                break;
            default:
                break;
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(mkeyWord, "", mEtCity.getText().toString());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**\
     *  输入框文字监听
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        String newText = s.toString().trim();
//        if (!AMapUtil.IsEmptyOrNullString(newText)) {
//            InputtipsQuery inputquery = new InputtipsQuery(newText, editCity.getText().toString());
//            Inputtips inputTips = new Inputtips(PoiKeywordSearchActivity.this, inputquery);
//            inputTips.setInputtipsListener(this);
//            inputTips.requestInputtipsAsyn();
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 检查输入字符
     * @param editText
     * @return
     */
    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }


    /**
     * 查询结果 回调
     * @param result
     * @param rCode
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        mAmap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(mAmap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(KeyWordSearchActivity.this, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(KeyWordSearchActivity.this, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(KeyWordSearchActivity.this, rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
       Toast.makeText(KeyWordSearchActivity.this, infomation, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mkeyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

}
