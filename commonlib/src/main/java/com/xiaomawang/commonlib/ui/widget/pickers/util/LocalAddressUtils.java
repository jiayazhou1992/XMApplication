package com.xiaomawang.commonlib.ui.widget.pickers.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalCity;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalCounty;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalProvince;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LocalAddressUtils {

    private static List<LocalProvince> provinces;
    private static List<LocalCity> cities;

    public static List<LocalProvince> getLocalProvinces(){
        if (provinces == null || provinces.size() == 0){
            synchronized (LocalAddressUtils.class){
                if (provinces == null || provinces.size() == 0){
                    String province_local = ResourceUtils.readStringFromAssets("province_local.json");
                    if (!StringUtils.isEmpty(province_local)) {
                        Gson gson = new Gson();
                        provinces = gson.fromJson(province_local, new TypeToken<List<LocalProvince>>() {}.getType());
                    }else {
                        throw new IllegalArgumentException("province_local is null");
                    }
                }
            }
        }
        return provinces;
    }

    public static List<LocalCity> getLocalCities(String provinceCode){
        List<LocalCity> localCities = new ArrayList<>();
        for (int i = 0; i < getLocalProvinces().size(); i++) {
            if (TextUtils.equals(getLocalProvinces().get(i).getCode(), provinceCode)){
                localCities = getLocalProvinces().get(i).getCityList();
                break;
            }
        }
        return localCities;
    }

    public static List<LocalCounty> getLocalCounties(String cityCode){
        List<LocalCounty> localCounties = new ArrayList<>();
        String provinceCode = cityCode.substring(0,2)+"0000";
        List<LocalCity> localCities = getLocalCities(provinceCode);
        for (int i = 0; i < localCities.size(); i++) {
            if (TextUtils.equals(localCities.get(i).getCode(), cityCode)){
                localCounties = localCities.get(i).getCityList();
                break;
            }
        }
        return localCounties;
    }

    public static List<LocalCity> getLocalCities(){
        if (cities == null || cities.size() == 0){
            synchronized (LocalAddressUtils.class){
                if (cities == null || cities.size() == 0){
                    String province_local = ResourceUtils.readStringFromAssets("city.json");
                    if (!StringUtils.isEmpty(province_local)) {
                        Gson gson = new Gson();
                        cities = gson.fromJson(province_local, new TypeToken<List<LocalCity>>() {}.getType());
                    }else {
                        throw new IllegalArgumentException("city is null");
                    }
                }
            }
        }
        return cities;
    }
}
