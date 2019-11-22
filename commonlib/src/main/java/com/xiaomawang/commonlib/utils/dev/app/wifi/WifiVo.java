package com.xiaomawang.commonlib.utils.dev.app.wifi;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * detail: Wifi信息实体类
 * Created by Ttt
 */
public class WifiVo implements Parcelable {

	// 日志TAG
	private static final String TAG = WifiVo.class.getSimpleName();
	/** wifi SSID */
	@Keep
	public String wSSID = null;
	/** wifi 密码 */
	@Keep
	public String wPwd = null;
	/** wifi 加密类型 */
	@Keep
	public int wType = WifiUtils.NOPWD;
	/** wifi 信号等级 */
	@Keep
	public int wLevel = 0;

	// --

	public  WifiVo(){
	}

	/**
	 * 获取Wifi信息
	 * @param sResult 扫描的Wifi信息
	 */
	public static WifiVo createWifiVo(ScanResult sResult){
		if (sResult != null){
			try {
				// 防止wifi名长度为0
				if(sResult.SSID.length() == 0){
					return null;
				}
				// 初始化wifi信息实体类
				WifiVo wifiVo = new WifiVo();
				// 保存ssid
				wifiVo.wSSID = WifiUtils.formatSSID(sResult.SSID, false);
				// 保存加密类型
				wifiVo.wType =  WifiUtils.getWifiType(sResult.capabilities);
				// 保存wifi信号等级
				wifiVo.wLevel = sResult.level;
				return wifiVo;
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "createWifiVo");
			}
		}
		return null;
	}

	/**
	 * 扫描Wifi信息
	 * @param listWifiVos 处理后数据源
	 * @param listScanResults 扫描返回的数据
	 */
	public static void scanWifiVos(ArrayList<WifiVo> listWifiVos, List<ScanResult> listScanResults){
		// 清空旧数据
		listWifiVos.clear();
		// 遍历wifi列表数据
		for(int i = 0, len = listScanResults.size();i < len;i++){
			// 如果出现异常，或者失败，则无视当前的索引wifi信息
			try {
				// 获取当前索引的wifi信息
				ScanResult sResult = listScanResults.get(i);
				// 防止wifi名长度为0
				if(sResult.SSID.length() == 0){
					continue;
				}
				// 保存wifi信息
				listWifiVos.add(createWifiVo(sResult));
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "scanWifiVos");
			}
		}
	}

	// ===============  Parcelable  ===============

	protected WifiVo(Parcel in) {
		wSSID = in.readString();
		wPwd = in.readString();
		wType = in.readInt();
		wLevel = in.readInt();
	}

	public static final Creator<WifiVo> CREATOR = new Creator<WifiVo>() {
		@Override
		public WifiVo createFromParcel(Parcel in) {
			return new WifiVo(in);
		}
		@Override
		public WifiVo[] newArray(int size) {
			return new WifiVo[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(wSSID);
		dest.writeString(wPwd);
		dest.writeInt(wType);
		dest.writeInt(wLevel);
	}
}
