package com.sky.cookbooksa.map;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.sky.cookbooksa.NearbyMapActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.utils.ProgressDialogUtil;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 关键字搜索帮助类
 * Created by arvin.li on 2015/8/11.
 */
public class PoiKeywordSearchHelper {

    private NearbyMapActivity act;
    private AMap aMap;
    private AutoCompleteTextView searchEdit;

    private PoiSearch poiSearch;
    private PoiSearch.Query query;

    private String searchCity = "";//搜索城市

    private String searchType = "";//搜索类型

    private ProgressDialogUtil dialog;

    public PoiKeywordSearchHelper(NearbyMapActivity act, AMap aMap,
                                  AutoCompleteTextView searchEdit) {
        this.act = act;
        this.aMap = aMap;
        this.searchEdit = searchEdit;

        init();
    }

    private void init() {

        //Item点击事件
        searchEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                StringUtil.toggleKeyboard(act, searchEdit, false);

                doSearchQuery();
            }
        });

        //输入框监听输入改变事件
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String editStr = searchEdit.getText().toString();

                //获取提示集监听事件
                Inputtips inputtips = new Inputtips(act, new Inputtips.InputtipsListener() {

                    @Override
                    public void onGetInputtips(List<Tip> list, int i) {

                        List<String> tipList = new ArrayList<String>();

                        for (Tip tip : list) {
                            tipList.add(tip.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,
                                R.layout.map_auto_item, R.id.tipText, tipList);

                        searchEdit.setAdapter(adapter);

                        adapter.notifyDataSetChanged();

                    }
                });

                try {
                    //请求获取提示选项 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
                    inputtips.requestInputtips(editStr, searchCity);
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置搜索城市
     *
     * @param searchCity
     */
    public void setSearchCity(String searchCity) {
        this.searchCity = searchCity;
    }

    /**
     * 设置搜索类型
     *
     * @param searchType
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    /**
     * 查询处理
     */
    private void doSearchQuery() {

        dialog = new ProgressDialogUtil(act, "处理中...");
        dialog.showDialog();

        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(searchEdit.getText().toString(), searchType, searchCity);
        query.setPageNum(0);//设置显示当前页
        query.setPageSize(20);//设置每页显示大小

        poiSearch = new PoiSearch(act, query);

        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {

                dialog.closeDialog();

                searchResultHandle(poiResult, i);
            }

            @Override
            public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    /**
     * 查询结果处理
     */
    private void searchResultHandle(PoiResult poiResult, int code) {
        switch (code) {
            case 0:

                if (poiResult != null && poiResult.getQuery() != null) {

                    if (poiResult.getQuery().equals(query)) {

                        List<PoiItem> poiItemList = poiResult.getPois();

                        List<SuggestionCity> cityList = poiResult.getSearchSuggestionCitys();

                        if (poiItemList != null && poiItemList.size() > 0) {

                            aMap.clear();// 清理之前的图标

                            PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItemList);
                            poiOverlay.removeFromMap();
                            poiOverlay.addToMap();
                            poiOverlay.zoomToSpan();

                        } else if (cityList != null && cityList.size() > 0) {

                            showSuggestCity(cityList);

                        } else {
                            ToastUtil.toastShort(act, "没有搜索到相关数据！");
                        }

                    }
                }

                break;
            case 27:

                ToastUtil.toastShort(act, "搜索失败,请检查网络连接！");

                break;
            case 32:

                ToastUtil.toastShort(act, "key验证无效！");

                break;
            default:

                ToastUtil.toastShort(act, "未知错误，请稍后重试！");

                break;
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     *
     * @param cities
     */
    private void showSuggestCity(List<SuggestionCity> cities) {

        StringBuffer infomation = new StringBuffer();
        infomation.append("推荐城市\n");

        for (int i = 0; i < cities.size(); i++) {
            infomation.append("城市名称:").append(cities.get(i).getCityName())
                    .append("城市区号:").append(cities.get(i).getCityCode())
                    .append("城市编码:").append(cities.get(i).getAdCode())
                    .append("\n");
        }

        ToastUtil.toastShort(act, infomation.toString());

    }
}
