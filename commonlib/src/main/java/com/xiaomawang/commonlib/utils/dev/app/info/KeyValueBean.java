package com.xiaomawang.commonlib.utils.dev.app.info;

import androidx.annotation.Keep;
import androidx.annotation.StringRes;

import com.xiaomawang.commonlib.utils.dev.DevUtils;


/**
 * detail: 键对值 实体类
 * Created by Ttt
 */
public class KeyValueBean {

    // 键 - 提示
    @Keep
    protected String key = "";
    // 值 - 参数值
    @Keep
    protected String value = "";

    /**
     * 构造函数
     * @param key
     * @param value
     */
    public KeyValueBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取 key
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * 获取 value
     * @return
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }

    /**
     * 通过 resId 设置key, 并且初始化 KeyValueBean
     * @param resId
     * @param value
     * @return
     */
    public static KeyValueBean get(@StringRes int resId, String value) {
        return new KeyValueBean(DevUtils.getContext().getString(resId), value);
    }
}
