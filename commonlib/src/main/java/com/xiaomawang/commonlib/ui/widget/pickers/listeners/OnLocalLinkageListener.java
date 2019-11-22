package com.xiaomawang.commonlib.ui.widget.pickers.listeners;


import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalCity;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalCounty;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalProvince;

/**
 * @author matt
 * blog: addapp.cn
 */

public interface OnLocalLinkageListener {
    /**
     * 选择地址
     *
     * @param province the province
     * @param city    the city
     * @param county   the county ，if {@code hideCounty} is true，this is null
     */
    void onAddressPicked(LocalProvince province, LocalCity city, LocalCounty county);
}
