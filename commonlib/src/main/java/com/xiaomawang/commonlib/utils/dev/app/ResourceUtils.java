package com.xiaomawang.commonlib.utils.dev.app;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.DimenRes;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * detail: 资源文件工具类
 * Created by Ttt
 */
public final class ResourceUtils {

	private ResourceUtils() {
	}

	// 日志TAG
	private static final String TAG = ResourceUtils.class.getSimpleName();

	public static final String LAYTOUT = "layout";
	public static final String DRAWABLE = "drawable";
	public static final String MIPMAP = "mipmap";
	public static final String MENU = "menu";
	public static final String RAW = "raw";
	public static final String ANIM = "anim";
	public static final String STRING = "string";
	public static final String STYLE = "style";
	public static final String STYLEABLE = "styleable";
	public static final String INTEGER = "integer";
	public static final String ID = "id";
	public static final String DIMEN = "dimen";
	public static final String COLOR = "color";
	public static final String BOOL = "bool";
	public static final String ATTR = "attr";

	/**
	 * 获取字符串
	 * @param strId 字符串id
	 * @return 字符串
	 */
	public static String getString(int strId) {
		try {
			return DevUtils.getContext().getResources().getString(strId);
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getString");
		}
		return "";
	}

	/**
	 * 获取字符串数字
	 * @param strArrId
	 * @return
	 */
	public static String[] getStringArray(int strArrId){
		try {
			return DevUtils.getContext().getResources().getStringArray(strArrId);
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getStringArray");
		}
		return null;
	}

	/**
	 * 获取尺寸
	 * @param dimen
	 * @return
	 */
	public static int getDimensionPX(@DimenRes int dimen){
		try {
			return DevUtils.getContext().getResources().getDimensionPixelSize(dimen);
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getDimensionPX");
		}
		return 0;
	}

	/**
	 * 获取 Color
	 * @param colorId 颜色id
	 * @return 颜色
	 */
	public static int getColor(int colorId) {
		try {
			return DevUtils.getContext().getResources().getColor(colorId);
		} catch (Exception e) {
			LogPrintUtils.eTag(TAG, e, "getColor");
		}
		return -1;
	}

	/**
	 * 获取 Drawable
	 * @param drawableId Drawable的id
	 * @return Drawable
	 */
	public static Drawable getDrawable(int drawableId) {
		try {
			return DevUtils.getContext().getResources().getDrawable(drawableId);
		} catch (Exception e) {
			LogPrintUtils.eTag(TAG, e, "getDrawable");
		}
		return null;
	}

	/**
	 * 根据资源名获取资源id
	 * @param name 资源名
	 * @param type 资源类型
	 * @return 资源id，找不到返回0
	 */
	public static int getResourceId(String name, String type) {
		try {
			//PackageManager pm = DevUtils.getContext().getPackageManager();
			Resources resources = DevUtils.getContext().getResources();
			return resources.getIdentifier(name, type, DevUtils.getContext().getPackageName());
		} catch (Exception e) {
			LogPrintUtils.eTag(TAG, e, "getResourceId");
		}
		return 0;
	}

	/**
	 * 获取 drawable id
	 * @param imageName
	 * @return
	 */
	public static int getDrawableId2(String imageName){
		Class mipmap = R.drawable.class;
		try {
			Field field = mipmap.getField(imageName);
			int resId = field.getInt(imageName);
			return resId;
		} catch (NoSuchFieldException e) { // 如果没有在"drawable"下找到imageName,将会返回0
			LogPrintUtils.eTag(TAG, e, "getDrawableId2");
			return 0;
		} catch (Exception e) {
			LogPrintUtils.eTag(TAG, e, "getDrawableId2");
			return 0;
		}
	}

	// ==

	/**
	 * 获取 layout 布局文件 id
	 * @param resName layout xml 的文件名
	 * @return layout
	 */
	public static int getLayoutId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "layout", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getLayoutId");
		}
		return 0;
	}

	/**
	 * 获取 string id
	 * @param resName string name的名称
	 * @return string
	 */
	public static int getStringId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "string", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getStringId");
		}
		return 0;
	}

	/**
	 * 获取 drawable id
	 * @param resName drawable 的名称
	 * @return drawable
	 */
	public static int getDrawableId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "drawable", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getDrawableId");
		}
		return 0;
	}

	/**
	 * 获取 mipmap id
	 * @param resName
	 * @return
	 */
	public static int getMipmapId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "mipmap", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getMipmapId");
		}
		return 0;
	}


	/**
	 * 获取 style id
	 * @param resName style的名称
	 * @return style
	 */
	public static int getStyleId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "style", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getStyleId");
		}
		return 0;
	}

	/**
	 * 获取 styleable id
	 * @param resName styleable 的名称
	 * @return styleable
	 */
	public static Object getStyleableId(String resName){
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "styleable", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getStyleableId");
		}
		return 0;
	}


	/**
	 * 获取 anim id
	 * @param resName anim xml 文件名称
	 * @return anim
	 */
	public static int getAnimId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "anim", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getAnimId");
		}
		return 0;
	}

	/**
	 * 获取 id
	 * @param resName id 的名称
	 * @return
	 */
	public static int getId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "id", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getId");
		}
		return 0;
	}

	/**
	 * 获取 color id
	 * @param resName color 名称
	 * @return
	 */
	public static int getColorId(String resName) {
		try {
			return DevUtils.getContext().getResources().getIdentifier(resName, "color", DevUtils.getContext().getPackageName());
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getColorId");
		}
		return 0;
	}

	// == ----------------------------------------- ==

	/**
	 * 获取 Assets 资源文件数据
	 * @param fileName 资源文件名，可分成，如根目录，a.txt 或者子目录 /www/a.html
	 * @return byte[] , - > new String(byte[],encode)
	 */
	public static byte[] readBytesFromAssets(String fileName) {
		if (DevUtils.getContext() != null && !TextUtils.isEmpty(fileName)) {
			InputStream iStream = null;
			try {
				iStream = DevUtils.getContext().getResources().getAssets().open(fileName);
				int length = iStream.available();
				byte[] buffer = new byte[length];
				iStream.read(buffer);
				iStream.close();
				iStream = null;
				return buffer;
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "readBytesFromAssets");
			} finally {
				if(iStream != null) {
					try {
						iStream.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return null;
	}

	/**
	 * 读取字符串 来自 Assets文件
	 * @param fileName
	 * @return
	 */
	public static String readStringFromAssets(String fileName) {
		try {
			return new String(readBytesFromAssets(fileName), "UTF-8");
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "readStringFromAssets");
		}
		return null;
	}
	
	/**
	 * 从res/raw 中获取内容。
	 * @param resId 资源id
	 * @return byte[] , - > new String(byte[],encode)
	 */
	public static byte[] readBytesFromRaw(int resId) {
		if (DevUtils.getContext() != null) {
			InputStream iStream = null;
			try {
				iStream = DevUtils.getContext().getResources().openRawResource(resId);
				int length = iStream.available();
				byte[] buffer = new byte[length];
				iStream.read(buffer);
				iStream.close();
				iStream = null;
				return buffer;
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "readBytesFromRaw");
			} finally {
				if(iStream != null) {
					try {
						iStream.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return null;
	}

	/**
	 * 读取字符串 来自Raw 文件
	 * @param resId
	 * @return
	 */
	public static String readStringFromRaw(int resId) {
		try {
			return new String(readBytesFromRaw(resId), "UTF-8");
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "readStringFromRaw");
		}
		return null;
	}

	// ==

	/**
	 * 获取 Assets 资源文件数据(返回List<String> 一行的全部内容属于一个索引)
	 * @param fileName 资源文件名，可分成，如根目录，a.txt 或者子目录 /www/a.html
	 * @return
	 */
	public static List<String> geFileToListFromAssets(String fileName) {
		if (DevUtils.getContext() != null && !TextUtils.isEmpty(fileName)) {
			InputStream iStream = null;
			InputStreamReader inReader = null;
			BufferedReader bufReader = null;
			try {
				iStream = DevUtils.getContext().getResources().getAssets().open(fileName);
				inReader = new InputStreamReader(iStream);
				bufReader = new BufferedReader(inReader);
				List<String> fileContent = new ArrayList<>();
				String line;
				while ((line = bufReader.readLine()) != null) {
					fileContent.add(line);
				}
				return fileContent;
			} catch (IOException e) {
				LogPrintUtils.eTag(TAG, e, "geFileToListFromAssets");
			} finally {
				try {
					bufReader.close();
					inReader.close();
					iStream.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
	
	/**
	 * 从res/raw 中获取内容。(返回List<String>一行的全部内容属于一个索引)
	 * @param resId 资源id
	 * @return
	 */
	public static List<String> geFileToListFromRaw(int resId) {
		if (DevUtils.getContext() != null) {
			InputStream iStream = null;
			InputStreamReader inReader = null;
			BufferedReader bufReader = null;
			try {
				iStream = DevUtils.getContext().getResources().openRawResource(resId);
				inReader = new InputStreamReader(iStream);
				bufReader = new BufferedReader(inReader);
				List<String> fileContent = new ArrayList<>();
				String line = null;
				while ((line = bufReader.readLine()) != null) {
					fileContent.add(line);
				}
				return fileContent;
			} catch (IOException e) {
				LogPrintUtils.eTag(TAG, e, "geFileToListFromRaw");
			} finally {
				try {
					bufReader.close();
					inReader.close();
					iStream.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
	
	
	// ========
	/**
	 * 从Assets 资源中获取内容并保存到本地
	 * @param fileName 资源文件名，可分成，如根目录，a.txt 或者子目录 /www/a.html
	 * @param file 保存地址
	 * @return 是否保存成功
	 */
	public static boolean saveAssetsFormFile(String fileName, File file) {
		if (DevUtils.getContext() != null) {
			try {
				// 获取 Assets 文件
				InputStream iStream = DevUtils.getContext().getResources().getAssets().open(fileName);
				// 存入SDCard
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				// 设置数据缓冲
				byte[] buffer = new byte[1024];
				// 创建输入输出流
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				int len = 0;
				while ((len = iStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				// 保存数据
				byte[] bytes = outStream.toByteArray();
				// 写入保存的文件
				fileOutputStream.write(bytes);
				// 关闭流
				outStream.close();
				iStream.close();
				// --
				fileOutputStream.flush();
				fileOutputStream.close();
				return true;
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "saveAssetsFormFile");
			}
		}
		return false;
	}
	
	/**
	 * 从res/raw 中获取内容并保存到本地
	 * @param resId 资源id
	 * @param file 保存地址
	 * @return 是否保存成功
	 */
	public static boolean saveRawFormFile(int resId, File file) {
		if (DevUtils.getContext() != null) {
			try {
				// 获取raw 文件
				InputStream iStream = DevUtils.getContext().getResources().openRawResource(resId);
				// 存入SDCard
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				// 设置数据缓冲
				byte[] buffer = new byte[1024];
				// 创建输入输出流
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				int len = 0;
				while ((len = iStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				// 保存数据
				byte[] bytes = outStream.toByteArray();
				// 写入保存的文件
				fileOutputStream.write(bytes);
				// 关闭流
				outStream.close();
				iStream.close();
				// --
				fileOutputStream.flush();
				fileOutputStream.close();
				return true;
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "saveRawFormFile");
			}
		}
		return false;
	}
}
