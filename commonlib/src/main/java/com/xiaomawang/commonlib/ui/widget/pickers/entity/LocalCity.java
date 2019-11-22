package com.xiaomawang.commonlib.ui.widget.pickers.entity;

import java.util.List;

/**
 * 地市
 * <br/>
 * Author:matt : addapp.cn
 * DateTime:2016-10-15 19:07
 *
 */
public class LocalCity extends JavaBean {
    private String code;
    private String name;
    private String firstLetter;
    private String fullPinyin;
    private List<LocalCounty> cityList;

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

    public List<LocalCounty> getCityList() {
        return cityList;
    }

    public void setCityList(List<LocalCounty> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }
}