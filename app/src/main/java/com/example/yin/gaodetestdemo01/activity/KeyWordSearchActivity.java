package com.example.yin.gaodetestdemo01.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.maps.AMapUtils;
import com.example.yin.gaodetestdemo01.R;
import com.example.yin.gaodetestdemo01.util.DialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yin on 2016/3/28.
 * 关键字搜索
 */
public class KeyWordSearchActivity extends Activity implements AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, View.OnClickListener, TextWatcher, PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener {

    private MapView mMapView;
    private Button mBtnSearch, mBtnNext;
    private EditText mEtCity;
    private AutoCompleteTextView mEtKeyword;
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
        initData();
    }
    private void initViews(Bundle state) {
        mMapView = (MapView) findViewById(R.id.map_view_keyword);
        mMapView.onCreate(state);

        mBtnSearch = (Button) findViewById(R.id.btn_searchButton);
        mBtnNext = (Button) findViewById(R.id.btn_nextButton);
        mBtnSearch.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        mEtKeyword = (AutoCompleteTextView) findViewById(R.id.et_keyWord);
        mEtCity = (EditText) findViewById(R.id.et_city);
        mEtKeyword.addTextChangedListener(this);
    }



    private void initData() {
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
                nextResult();
                break;
            default:
                break;
        }
    }

    private void nextResult() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() - 1 > currentPage) {
                currentPage++;
                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                Toast.makeText(KeyWordSearchActivity.this, R.string.no_result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        DialogUtils.showProgressDialog(KeyWordSearchActivity.this, "正在搜索", mkeyWord);// 显示进度框
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
        String newText = s.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, mEtCity.getText().toString());
            Inputtips inputTips = new Inputtips(KeyWordSearchActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
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
        DialogUtils.dissmissProgressDialog();// 隐藏对话框
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

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            mEtKeyword.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
//            ToastUtil.showerror(this, rCode);
            Toast.makeText(this, rCode, Toast.LENGTH_SHORT).show();
        }
    }
}
