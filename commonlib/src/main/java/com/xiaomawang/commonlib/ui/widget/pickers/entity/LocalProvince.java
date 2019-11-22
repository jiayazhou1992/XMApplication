package com.xiaomawang.commonlib.ui.widget.pickers.entity;

import java.util.List;

/**
 * 省份
 * <br/>
 * Author:matt : addapp.cn
 * DateTime:2016-10-15 19:06
 *
 */
public class LocalProvince extends JavaBean {
    private String code;
    private String name;
    private List<LocalCity> cityList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocalCity> getCityList() {
        return cityList;
    }

    public void setCityList(List<LocalCity> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return name;
    }
}