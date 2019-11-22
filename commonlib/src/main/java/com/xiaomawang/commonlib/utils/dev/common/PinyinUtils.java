package com.xiaomawang.commonlib.utils.dev.common;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * JPinyin拼音工具类
 */
public class PinyinUtils {

    private static String[] sections = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    /**
     * 转换为有声调的拼音字符串
     * @param pinYinStr 汉字
     * @return 有声调的拼音字符串
     */
    public static String changeToMarkPinYin(String pinYinStr){

        String tempStr = null;

        try
        {
            tempStr =  PinyinHelper.convertToPinyinString(pinYinStr,  " ", PinyinFormat.WITH_TONE_MARK);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempStr;

    }


    /**
     * 转换为数字声调字符串
     * @param pinYinStr 需转换的汉字
     * @return 转换完成的拼音字符串
     */
    public static String changeToNumberPinYin(String pinYinStr){

        String tempStr = null;

        try
        {
            tempStr = PinyinHelper.convertToPinyinString(pinYinStr, " ", PinyinFormat.WITH_TONE_NUMBER);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return tempStr;

    }

    /**
     * 转换为不带音调的拼音字符串
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String changeToTonePinYin(String pinYinStr){

        String tempStr = null;

        try
        {
            tempStr =  PinyinHelper.convertToPinyinString(pinYinStr, " ", PinyinFormat.WITHOUT_TONE);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempStr;
    }

    /**
     * 首字母
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String getFirstLetter(String pinYinStr){

        String firstLetter = "#";

        try
        {
           String tempStr =  PinyinHelper.convertToPinyinString(pinYinStr, " ", PinyinFormat.WITHOUT_TONE);
           firstLetter = StringUtils.upperFirstLetter(tempStr);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return firstLetter;
    }

    /**
     * 首字母 + 拼音
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String[] getFirstLetterAndPinyin(String pinYinStr){

        String[] strings = new String[2];
        String tempStr = "";
        String firstLetter = "#";

        try {
            tempStr =  PinyinHelper.convertToPinyinString(pinYinStr, " ", PinyinFormat.WITHOUT_TONE);
            if (tempStr != null && tempStr.length() > 0) {
                firstLetter = tempStr.substring(0,1).toUpperCase();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        strings[0] = firstLetter;
        strings[1] = tempStr;
        return strings;
    }

    /**
     * 转换为每个汉字对应拼音首字母字符串
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String changeToGetShortPinYin(String pinYinStr){

        String tempStr = null;

        try
        {
            tempStr = PinyinHelper.getShortPinyin(pinYinStr);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempStr;

    }

    /**
     * 检查汉字是否为多音字
     * @param pinYinStr 需检查的汉字
     * @return true 多音字，false 不是多音字
     */
    public static boolean checkPinYin(char pinYinStr){

        boolean check  = false;
        try
        {
            check = PinyinHelper.hasMultiPinyin(pinYinStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    /**
     * 简体转换为繁体
     * @param pinYinStr
     * @return
     */
    public static String changeToTraditional(String pinYinStr){

        String tempStr = null;
        try
        {
            tempStr = ChineseHelper.convertToTraditionalChinese(pinYinStr);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempStr;

    }

    /**
     * 繁体转换为简体
     * @param pinYinSt
     * @return
     */
    public static String changeToSimplified(String pinYinSt){

        String tempStr = null;

        try
        {
            tempStr = ChineseHelper.convertToSimplifiedChinese(pinYinSt);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return tempStr;
    }

    public static int getFirstLetterIndex(String firstLetter){
        int index = 0;
        for (int i = 0; i < sections.length; i++) {
            if (sections[i].equals(firstLetter)){
                index = i;
                break;
            }
        }
        return index;
    }
}
