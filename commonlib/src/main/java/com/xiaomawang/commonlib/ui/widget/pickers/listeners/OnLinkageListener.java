package com.xiaomawang.commonlib.ui.widget.pickers.listeners;


import com.xiaomawang.commonlib.ui.widget.pickers.entity.City;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.County;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.Province;

/**
 * @author matt
 * blog: addapp.cn
 */

public interface OnLinkageListener {
    /**
     * 选择地址
     *
     * @param province the province
     * @param city    the city
     * @param county   the county ，if {@code hideCounty} is true，this is null
     */
    void onAddressPicked(Province province, City city, County county);
}
