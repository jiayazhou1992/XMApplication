package com.xiaomawang.commonlib.ui.widget.pickers.picker;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomawang.commonlib.ui.widget.pickers.adapter.ArrayWheelAdapter;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalCity;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalCounty;
import com.xiaomawang.commonlib.ui.widget.pickers.entity.LocalProvince;
import com.xiaomawang.commonlib.ui.widget.pickers.listeners.OnItemPickListener;
import com.xiaomawang.commonlib.ui.widget.pickers.listeners.OnLocalLinkageListener;
import com.xiaomawang.commonlib.ui.widget.pickers.listeners.OnMoreWheelListener;
import com.xiaomawang.commonlib.ui.widget.pickers.widget.WheelListView;
import com.xiaomawang.commonlib.ui.widget.pickers.widget.WheelView;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 地址选择器（包括省级、地级、县级），地址数据见示例项目assets目录下。
 * @author matt
 * blog: addapp.cn
 * @see LocalProvince
 * @see LocalCity
 * @see LocalCounty
 */
public class LocalAddressPicker extends LinkagePicker {

    private WheelView provinceView, cityView, countyView;

    private WheelListView provinceListView, cityListView, countyListView;


    private OnLocalLinkageListener onLinkageListener;
    private OnMoreWheelListener onMoreWheelListener;
    //只显示地市及区县
    private boolean hideProvince = false;
    //只显示省份及地市
    private boolean hideCounty = false;
    //省市区数据
    private List<LocalProvince> provinces = new ArrayList<>();

    public LocalAddressPicker(Activity activity) {
        super(activity);
        String province_local = ResourceUtils.readStringFromAssets("province_local.json");
        if (!StringUtils.isEmpty(province_local)) {
            Gson gson = new Gson();
            this.provinces = gson.fromJson(province_local, new TypeToken<List<LocalProvince>>() {}.getType());
        }else {
            throw new IllegalArgumentException("province_local is null");
        }
        this.provider = new AddressProvider(this.provinces);
    }


    /**
     * 设置默认选中的省市县
     */
    public void setSelectedItem(String province, String city, String county) {
        super.setSelectedItem(province, city, county);
    }

    public LocalProvince getSelectedProvince() {
        return provinces.get(selectedFirstIndex);
    }

    public LocalCity getSelectedCity() {
        if (getSelectedProvince().getCityList() != null && getSelectedProvince().getCityList().size() > 0) {
            return getSelectedProvince().getCityList().get(selectedSecondIndex);
        } else {
            return null;
        }
    }

    public LocalCounty getSelectedCounty() {
        if (getSelectedCity() != null && getSelectedCity().getCityList() != null && getSelectedCity().getCityList().size() > 0) {
            return getSelectedCity().getCityList().get(selectedThirdIndex);
        }else {
            return null;
        }
    }

    /**
     * 隐藏省级行政区，只显示地市级和区县级。
     * 设置为true的话，地址数据中只需要某个省份的即可
     * 参见示例中的“assets/city2.json”
     */
    public void setHideProvince(boolean hideProvince) {
        this.hideProvince = hideProvince;
    }

    /**
     * 隐藏县级行政区，只显示省级和市级。
     * 设置为true的话，hideProvince将强制为false
     * 数据源依然使用“assets/city.json” 仅在逻辑上隐藏县级选择框，实际项目中应该去掉县级数据。
     */
    public void setHideCounty(boolean hideCounty) {
        this.hideCounty = hideCounty;
    }

    /**
     * 设置滑动监听器
     */
    public void setOnMoreWheelListener(OnMoreWheelListener onMoreWheelListener) {
        this.onMoreWheelListener = onMoreWheelListener;
    }

    public void setOnLinkageListener(OnLocalLinkageListener listener) {
        this.onLinkageListener = listener;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        if (null == provider) {
            throw new IllegalArgumentException("please set address provider before make view");
        }
        if (hideCounty) {
            hideProvince = false;
        }
        int[] widths = getColumnWidths(hideProvince || hideCounty);
        int provinceWidth = widths[0];
        int cityWidth = widths[1];
        int countyWidth = widths[2];
        if (hideProvince) {
            provinceWidth = 0;
            cityWidth = widths[0];
            countyWidth = widths[1];
        }
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        //判断是选择ios滚轮模式还是普通模式
        if(wheelModeEnable){
            provinceView = new WheelView(activity);
            provinceView.setLayoutParams(new LinearLayout.LayoutParams(provinceWidth, WRAP_CONTENT));
            provinceView.setTextSize(textSize);
            provinceView.setSelectedTextColor(textColorFocus);
            provinceView.setUnSelectedTextColor(textColorNormal);
            provinceView.setLineConfig(lineConfig);
            provinceView.setCanLoop(canLoop);
            layout.addView(provinceView);
            if (hideProvince) {
                provinceView.setVisibility(View.GONE);
            }

            cityView = new WheelView(activity);
            cityView.setLayoutParams(new LinearLayout.LayoutParams(cityWidth, WRAP_CONTENT));
            cityView.setTextSize(textSize);
            cityView.setSelectedTextColor(textColorFocus);
            cityView.setUnSelectedTextColor(textColorNormal);
            cityView.setLineConfig(lineConfig);
            cityView.setCanLoop(canLoop);
            layout.addView(cityView);

            countyView = new WheelView(activity);
            countyView.setLayoutParams(new LinearLayout.LayoutParams(countyWidth, WRAP_CONTENT));
            countyView.setTextSize(textSize);
            countyView.setSelectedTextColor(textColorFocus);
            countyView.setUnSelectedTextColor(textColorNormal);
            countyView.setLineConfig(lineConfig);
            countyView.setCanLoop(canLoop);
            layout.addView(countyView);
            if (hideCounty) {
                countyView.setVisibility(View.GONE);
            }

            provinceView.setAdapter(new ArrayWheelAdapter<LocalProvince>(provider.provideFirstData()));
            provinceView.setCurrentItem(selectedFirstIndex);
            provinceView.setOnItemPickListener(new OnItemPickListener<String>() {
                @Override
                public void onItemPicked(int index, String item) {
                    selectedFirstItem = item;
                    selectedFirstIndex = index;
                    if (onMoreWheelListener != null) {
                        onMoreWheelListener.onFirstWheeled(selectedFirstIndex, selectedFirstItem);
                    }
                    //LogUtils.verbose(this, "change cities after province wheeled");
                    selectedSecondIndex = 0;//重置地级索引
                    selectedThirdIndex = 0;//重置县级索引
                    //根据省份获取地市
                    List<String> cities = provider.provideSecondData(selectedFirstIndex);
                    if (cities.size() > 0) {
                        cityView.setAdapter(new ArrayWheelAdapter<>(cities));
                        cityView.setCurrentItem(selectedSecondIndex);
                    } else {
                        cityView.setAdapter(new ArrayWheelAdapter<>(new ArrayList<String>()));
                    }
                    //根据地市获取区县
                    List<String> counties = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
                    if (counties.size() > 0) {
                        countyView.setAdapter(new ArrayWheelAdapter<>(counties));
                        countyView.setCurrentItem(selectedThirdIndex);
                    } else {
                        countyView.setAdapter(new ArrayWheelAdapter<>(new ArrayList<String>()));
                    }
                }
            });
            cityView.setAdapter(new ArrayWheelAdapter<>(provider.provideSecondData(selectedFirstIndex)));
            cityView.setCurrentItem(selectedSecondIndex);
            cityView.setOnItemPickListener(new OnItemPickListener<String>() {
                @Override
                public void onItemPicked( int index, String item) {
                    selectedSecondItem = item;
                    selectedSecondIndex = index;
                    if (onMoreWheelListener != null) {
                        onMoreWheelListener.onSecondWheeled(selectedSecondIndex, selectedSecondItem);
                    }
                   // LogUtils.verbose(this, "change counties after city wheeled");
                    selectedThirdIndex = 0;//重置县级索引
                    //根据地市获取区县
                    List<String> counties = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
                    if (counties.size() > 0) {
                        //若不是用户手动滚动，说明联动需要指定默认项
                        countyView.setAdapter(new ArrayWheelAdapter<>(counties));
                        countyView.setCurrentItem(selectedThirdIndex);
                    } else {
                        countyView.setAdapter(new ArrayWheelAdapter<>(new ArrayList<String>()));
                    }
                }
            });

            countyView.setAdapter(new ArrayWheelAdapter<>(provider.provideThirdData(selectedFirstIndex, selectedSecondIndex)));
            countyView.setCurrentItem(selectedThirdIndex);
            countyView.setOnItemPickListener(new OnItemPickListener<String>() {
                @Override
                public void onItemPicked( int index, String item) {
                    selectedThirdItem = item;
                    selectedThirdIndex = index;
                    if (onMoreWheelListener != null) {
                        onMoreWheelListener.onThirdWheeled(selectedThirdIndex, selectedThirdItem);
                    }
                }
            });
        }else{
            provinceListView = new WheelListView(activity);
            provinceListView.setLayoutParams(new LinearLayout.LayoutParams(provinceWidth, WRAP_CONTENT));
            provinceListView.setTextSize(textSize);
            provinceListView.setSelectedTextColor(textColorFocus);
            provinceListView.setUnSelectedTextColor(textColorNormal);
            provinceListView.setLineConfig(lineConfig);
            provinceListView.setOffset(offset);
            provinceListView.setCanLoop(canLoop);
            layout.addView(provinceListView);
            if (hideProvince) {
                provinceListView.setVisibility(View.GONE);
            }

            cityListView = new WheelListView(activity);
            cityListView.setLayoutParams(new LinearLayout.LayoutParams(cityWidth, WRAP_CONTENT));
            cityListView.setTextSize(textSize);
            cityListView.setSelectedTextColor(textColorFocus);
            cityListView.setUnSelectedTextColor(textColorNormal);
            cityListView.setLineConfig(lineConfig);
            cityListView.setOffset(offset);
            cityListView.setCanLoop(canLoop);
            layout.addView(cityListView);

            countyListView = new WheelListView(activity);
            countyListView.setLayoutParams(new LinearLayout.LayoutParams(countyWidth, WRAP_CONTENT));
            countyListView.setTextSize(textSize);
            countyListView.setSelectedTextColor(textColorFocus);
            countyListView.setUnSelectedTextColor(textColorNormal);
            countyListView.setLineConfig(lineConfig);
            countyListView.setOffset(offset);
            countyListView.setCanLoop(canLoop);
            layout.addView(countyListView);
            if (hideCounty) {
                countyListView.setVisibility(View.GONE);
            }

            provinceListView.setItems(provider.provideFirstData(), selectedFirstIndex);
            provinceListView.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                @Override
                public void onItemSelected(boolean isUserScroll, int index, String item) {
                    selectedFirstItem = item;
                    selectedFirstIndex = index;
                    if (onMoreWheelListener != null) {
                        onMoreWheelListener.onFirstWheeled(selectedFirstIndex, selectedFirstItem);
                    }
                    if (!isUserScroll) {
                        return;
                    }
                    selectedSecondIndex = 0;//重置地级索引
                    selectedThirdIndex = 0;//重置县级索引
                    //根据省份获取地市
                    List<String> cities = provider.provideSecondData(selectedFirstIndex);
                    if (cities != null && cities.size() > 0) {
                        cityListView.setItems(cities, selectedSecondIndex);
                    } else {
                        cityListView.setItems(new ArrayList<String>());
                    }
                    //根据地市获取区县
                    List<String> counties = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
                    if (counties != null && counties.size() > 0) {
                        countyListView.setItems(counties, selectedThirdIndex);
                    } else {
                        countyListView.setItems(new ArrayList<String>());
                    }
                }
            });

            cityListView.setItems(provider.provideSecondData(selectedFirstIndex), selectedSecondIndex);
            cityListView.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                @Override
                public void onItemSelected(boolean isUserScroll, int index, String item) {
                    selectedSecondItem = item;
                    selectedSecondIndex = index;
                    if (onMoreWheelListener != null) {
                        onMoreWheelListener.onSecondWheeled(selectedSecondIndex, selectedSecondItem);
                    }
                    if (!isUserScroll) {
                        return;
                    }
                  //  LogUtils.verbose(this, "change counties after city wheeled");
                    selectedThirdIndex = 0;//重置县级索引
                    //根据地市获取区县
                    List<String> counties = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
                    if (counties != null && counties.size() > 0) {
                        //若不是用户手动滚动，说明联动需要指定默认项
                        countyListView.setItems(counties, selectedThirdIndex);
                    } else {
                        countyListView.setItems(new ArrayList<String>());
                    }
                }
            });

            countyListView.setItems(provider.provideThirdData(selectedFirstIndex, selectedSecondIndex), selectedThirdIndex);
            countyListView.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                @Override
                public void onItemSelected(boolean isUserScroll, int index, String item) {
                    selectedThirdItem = item;
                    selectedThirdIndex = index;
                    if (onMoreWheelListener != null) {
                        onMoreWheelListener.onThirdWheeled(selectedThirdIndex, selectedThirdItem);
                    }
                }
            });
        }

        return layout;
    }

    @Override
    public void onSubmit() {
        if (onLinkageListener != null) {
            LocalProvince province = getSelectedProvince();
            LocalCity city = getSelectedCity();
            LocalCounty county = null;
            if (!hideCounty) {
                county = getSelectedCounty();
            }
            onLinkageListener.onAddressPicked(province, city, county);
        }
    }


    /**
     * 地址提供者
     */
    public static class AddressProvider implements DataProvider<LocalProvince, LocalCity, LocalCounty> {
        private List<LocalProvince> firstList = new ArrayList<>();
        private List<LocalCity> secondList = new ArrayList<>();
        private List<LocalCounty> thirdList = new ArrayList<>();

        public AddressProvider(List<LocalProvince> provinces) {
            firstList = provinces;
            secondList = provinces.get(0).getCityList();
            if (secondList != null && secondList.size() > 0){
                thirdList = secondList.get(0).getCityList();
            }
        }

        @Override
        public boolean isOnlyTwo() {
            return thirdList.size() == 0;
        }

        @Override
        public List<LocalProvince> provideFirstData() {
            return firstList;
        }

        @Override
        public List<LocalCity> provideSecondData(int firstIndex) {
            secondList = firstList.get(firstIndex).getCityList();
            return secondList;
        }

        @Override
        public List<LocalCounty> provideThirdData(int firstIndex, int secondIndex) {
            secondList = firstList.get(firstIndex).getCityList();
            if (secondList != null && secondList.size() > secondIndex){
                thirdList = secondList.get(secondIndex).getCityList();
            }
            return thirdList;
        }
    }

}
