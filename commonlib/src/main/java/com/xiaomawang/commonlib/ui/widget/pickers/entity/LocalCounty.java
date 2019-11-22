package com.xiaomawang.commonlib.ui.widget.pickers.entity;

/**
 * 区县
 * <br/>
 * Author:matt : addapp.cn
 * DateTime:2016-10-15 19:08
 *
 */
public class LocalCounty extends JavaBean {
    private String code;
    private String name;

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

    @Override
    public String toString() {
        return name;
    }
}
