package com.xiaomawang.commonlib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xiaomawang.commonlib.utils.dev.app.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class NavigationUtils {
    // 1.百度地图包名
    public static final String BAIDUMAP_PACKAGENAME = "com.baidu.BaiduMap";
    // 2.高德地图包名
    public static final String AUTONAVI_PACKAGENAME = "com.autonavi.minimap";
    // 3.腾讯地图包名
    public static final String QQMAP_PACKAGENAME = "com.tencent.map";



    /** 检查手机上是否安装了指定的软件
	 * @param packageNames 可变参数 String[]
	 * @return 目标软件中已安装的列表
	 */
    public static List<String> checkInstalledPackage(String... packageNames) {
        List<String> packages = new ArrayList<>();
        for (String pack : packageNames){
            if (AppUtils.isAppInstalled(pack)){
                packages.add(pack);
            }
        }
        return packages;
    }


    /** 调用百度地图----------------
     *
     * @param context 上下文对象
	 */
    public static void invokeBaiDuMap(Context context, String lat, String lon, String name) {

        try {
            Uri uri = Uri.parse("baidumap://map/geocoder?" +
                    "location=" + lat + "," + lon +
                    "&name=" + name + //终点的显示名称
                    "&coord_type=gcj02");//坐标 （百度同样支持他自己的db0911的坐标，但是高德和腾讯不支持）
            Intent intent = new Intent();
            intent.setPackage(BAIDUMAP_PACKAGENAME);
            intent.setData(uri);

            context.startActivity(intent);
        } catch (Exception e) {

        }

    }

    /**
     * 调用高德地图
     *
     * @param context 上下文对象s
     */
    public static void invokeAuToNaveMap(Context context, String lat, String lon, String name) {

        try {
            Uri uri = Uri.parse("androidamap://route?sourceApplication={"+ AppUtils.getAppName() +"}" +
                    "&dlat=" + lat//终点的纬度
                    + "&dlon=" + lon//终点的经度
                    + "&dname=" + name////终点的显示名称
                    + "&dev=0&m=0&t=0");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            intent.addCategory("android.intent.category.DEFAULT");

            context.startActivity(intent);
        } catch (Exception e) {

        }

    }

    /**
     * 调用腾讯地图
     *
     * @param context 上下文对象s
     */
    public static void invokeQQMap(Context context, String lat, String lon, String name) {
        try {
            Uri uri = Uri.parse("qqmap://map/routeplan?type=drive" +
                    "&to=" + name//终点的显示名称 必要参数
                    + "&tocoord=" + lat + "," + lon//终点的经纬度
                    + "&referer={" + AppUtils.getAppName() +"}");
            Intent intent = new Intent();
            intent.setData(uri);

            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

}
