package com.rifeng.agriculturalstation.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chw on 2017/3/3.
 *
 * 从assets目录下读取城市json文件转化为String类型
 */
public class CityJson {

    private JSONObject mJsonObj;
    private Context mContext;
    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * key - 省 value - 市s
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<>();
    /**
     * key - 市 values - 区s
     */
    private Map<String, String[]> mAreaDatasMap = new HashMap<>();

    public CityJson(Context context){
        this.mContext = context;
        this.initCitisDatas();
    }

    public String[] getmProvinceDatas() {
        return mProvinceDatas;
    }

    public Map<String, String[]> getmCitisDatasMap() {
        return mCitisDatasMap;
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initCitisDatas() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = mContext.getClass().getClassLoader().getResourceAsStream("assets/" + "city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1){
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());

            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p").trim();// 省名字
                mProvinceDatas[i] = province;
                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n").trim();// 市名字
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }
                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }
                mCitisDatasMap.put(province, mCitiesDatas);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }
}
