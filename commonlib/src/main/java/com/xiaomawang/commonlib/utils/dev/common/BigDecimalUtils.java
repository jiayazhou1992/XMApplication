package com.xiaomawang.commonlib.utils.dev.common;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import java.math.BigDecimal;


/**
 * detail: 资金运算工具类
 * Created by Ttt
 */
public final class BigDecimalUtils {

    private BigDecimalUtils(){
    }

    // 日志TAG
    private static final String TAG = BigDecimalUtils.class.getSimpleName();

    /** 默认保留位数 */
    private static final int DEF_DIV_SCALE = 10;

    // =

    /**
     * 提供精确的加法运算
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的加法运算
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(String v1, String v2) {
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.add(b2);
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "add");
        }
        return new BigDecimal(0);
    }

    /**
     * 提供精确的加法运算
     * @param v1 被加数
     * @param v2 加数
     * @param scale 保留scale 位小数
     * @return 两个参数的和
     */
    public static String add(String v1, String v2, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "add");
        }
        return "0";
    }

    /**
     * 提供精确的加法运算
     * @param v1 被加数
     * @param v2 加数
     * @param scale 保留scale 位小数
     * @return 两个参数的和
     */
    public static String add(double v1, double v2, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "add");
        }
        return "0";
    }

    /**
     * 提供精确的减法运算
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double substract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal substract(String v1, String v2) {
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.subtract(b2);
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "substract");
        }
        return new BigDecimal(0);
    }

    /**
     * 提供精确的减法运算
     * @param v1 被减数
     * @param v2 减数
     * @param scale 保留scale 位小数
     * @return 两个参数的差
     */
    public static String substract(String v1, String v2, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "substract");
        }
        return "0";
    }

    /**
     * 提供精确的乘法运算
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     * @param v1 被乘数
     * @param v2 乘数
     * @param scale 保留scale 位小数
     * @return 两个参数的积
     */
    public static double multiply(double v1, double v2, int scale) {
        if (scale < 0) {
            return 0d;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return round(b1.multiply(b2).doubleValue(), scale);
    }

    /**
     * 提供精确的乘法运算
     * @param v1 被乘数
     * @param v2 乘数
     * @param scale 保留scale 位小数
     * @return 两个参数的积
     */
    public static String multiply(String v1, String v2, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "multiply");
        }
        return "0";
    }

    /**
     * 提供(相对)精确的除法运算,当发生除不尽的情况时,
     * 精确到小数点以后10位,以后的数字四舍五入.
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double divide(double v1, double v2) {
        return divide(v1, v2, DEF_DIV_SCALE);
    }


    /**
     * 提供(相对)精确的除法运算.
     * 当发生除不尽的情况时,由scale参数指 定精度,以后的数字四舍五入.
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示需要精确到小数点以后几位
     * @return 两个参数的商
     */
    public static double divide(double v1, double v2, int scale) {
        if (scale < 0) {
            return 0d;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示需要精确到小数点以后几位
     * @return 两个参数的商
     */
    public static String divide(String v1, String v2, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "divide");
        }
        return "0";
    }

    /**
     * 提供精确的小数位四舍五入处理
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            return 0d;
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b = new BigDecimal(v);
            return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "round");
        }
        return "0";
    }

    /**
     * 提供精确的小数位获取
     * @param v 需要处理的数字
     * @param scale 小数点后保留几位
     * @return 最后的结果
     */
    public static double round(double v, int scale, int round) {
        if (scale < 0) {
            return 0d;
        }
        // https://www.cnblogs.com/liqforstudy/p/5652517.html
        // 向下取
        // round = BigDecimal.ROUND_DOWN;
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, round).doubleValue();
    }

    /**
     * 取余数
     * @param v1 被除数
     * @param v2 除数
     * @param scale 小数点后保留几位
     * @return 余数
     */
    public static String remainder(String v1, String v2, int scale) {
        if (scale < 0) {
            return "0";
        }
        try {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.remainder(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "remainder");
        }
        return "0";
    }

    /**
     * 取余数 BigDecimal
     * @param v1 被除数
     * @param v2 除数
     * @param scale 小数点后保留几位
     * @return 余数
     */
    public static BigDecimal remainder(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            return new BigDecimal(0);
        }
        try {
            return v1.remainder(v2).setScale(scale, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "remainder");
        }
        return new BigDecimal(0);
    }

    /**
     * 金额分割，四舍五入金额
     * @param s
     * @return
     */
    public static String formatMoney(BigDecimal s) {
        String retVal = "";
        String str = "";
        boolean is_positive_integer = false;
        if (null == s) {
            return "0.00";
        }

        if (0 == s.doubleValue()) {
            return "0.00";
        }
        // 判断是否正整数
        if (s.toString().indexOf("-") != -1) {
            is_positive_integer = true;
        } else {
            is_positive_integer = false;
        }
        // 是负整数
        if (is_positive_integer) {
            // 去掉 - 号
            s = new BigDecimal(s.toString().substring(1, s.toString().length()));
        }
        str = s.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        StringBuffer sb = new StringBuffer();
        String[] strs = str.split("\\.");
        int j = 1;
        for (int i = 0; i < strs[0].length(); i++) {
            char a = strs[0].charAt(strs[0].length() - i - 1);
            sb.append(a);
            if (j % 3 == 0 && i != strs[0].length() - 1) {
                sb.append(",");
            }
            j++;
        }
        String str1 = sb.toString();
        StringBuffer sb1 = new StringBuffer();
        for (int i = 0; i < str1.length(); i++) {
            char a = str1.charAt(str1.length() - 1 - i);
            sb1.append(a);
        }
        sb1.append(".");
        sb1.append(strs[1]);
        retVal = sb1.toString();

        if (is_positive_integer) {
            retVal = "-" + retVal;
        }
        return retVal;
    }

    /**
     * 四舍五入金额
     * @param s
     * @return
     */
    public static String formatMoney1(BigDecimal s) {
        String retVal = "";
        String str = "";
        boolean is_positive_integer = false;
        if (null == s) {
            return "0.00";
        }

        if (0 == s.doubleValue()) {
            return "0.00";
        }
        // 判断是否正整数
        if (s.toString().indexOf("-") != -1) {
            is_positive_integer = true;
        } else {
            is_positive_integer = false;
        }
        // 是负整数
        if (is_positive_integer) {
            // 去掉 - 号
            s = new BigDecimal(s.toString().substring(1, s.toString().length()));
        }
        str = s.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        StringBuffer sb = new StringBuffer();
        String[] strs = str.split("\\.");
        int j = 1;
        for (int i = 0; i < strs[0].length(); i++) {
            char a = strs[0].charAt(strs[0].length() - i - 1);
            sb.append(a);
            if (j % 3 == 0 && i != strs[0].length() - 1) {
                sb.append("");
            }
            j++;
        }
        String str1 = sb.toString();
        StringBuffer sb1 = new StringBuffer();
        for (int i = 0; i < str1.length(); i++) {
            char a = str1.charAt(str1.length() - 1 - i);
            sb1.append(a);
        }
        sb1.append(".");
        sb1.append(strs[1]);
        retVal = sb1.toString();

        if (is_positive_integer) {
            retVal = "-" + retVal;
        }
        return retVal;
    }

    /**
     * 比较大小
     * @param amount 输入的数值
     * @param compare 被比较的数字
     * @return true 大于被比较的数
     */
    public static Boolean compareBigDecimal(String amount, double compare) {
        try {
            BigDecimal lenth = new BigDecimal(amount);
            if (lenth.compareTo(BigDecimal.valueOf(compare)) == -1) {
                return false;
            }
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "compareBigDecimal");
        }
        return true;
    }

    /**
     * 获取自己想要的数据格式
     * @param s 需处理的数据
     * @param numOfIntPart 整数位数
     * @param numOfDecimalPart 小数位数
     * @return 处理过的数据
     */
    public static String adjustDouble(String s, int numOfIntPart, int numOfDecimalPart) {
        if (s == null){
            return null;
        }
        // 按小数点的位置分割成整数部分和小数部分
        String[] array = s.split("\\.");
        char[] tempA = new char[numOfIntPart];
        char[] tempB = new char[numOfDecimalPart];
        // 整数部分满足精度要求(情况1)
        if (array[0].length() == numOfIntPart) {
            // 直接获取整数部分长度字符
            for (int i = 0; i < array[0].length(); i++) {
                tempA[i] = array[0].charAt(i);
            }
            // 小数部分精度大于或等于指定的精度
            if (numOfDecimalPart <= array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    tempB[i] = array[1].charAt(i);
                }
            }
            // 小数部分精度小于指定的精度
            if (numOfDecimalPart > array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    if (i < array[1].length()) {
                        tempB[i] = array[1].charAt(i);
                    } else {
                        tempB[i] = '0';
                    }
                }
            }
            if (numOfDecimalPart == 0) {
                return String.valueOf(tempA) + String.valueOf(tempB);
            }
            return String.valueOf(tempA) + "." + String.valueOf(tempB);
        }
        // 整数部分位数大于精度要求(情况2)
        if (array[0].length() > numOfIntPart) {
            // 先倒序获取指定位数的整数
            for (int i = array[0].length() - 1, j = 0; (i >= array[0].length() - numOfIntPart) && (j < numOfIntPart); i--, j++) {
                tempA[j] = array[0].charAt(i);
            }
            char[] tempA1 = new char[numOfIntPart];
            // 调整顺序
            for (int j = 0, k = tempA.length - 1; j < numOfIntPart && (k >= 0); j++, k--) {
                tempA1[j] = tempA[k];

            }
            // 小数部分精度大于或等于指定的精度
            if (numOfDecimalPart <= array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    tempB[i] = array[1].charAt(i);
                }
            }
            // 小数部分精度小于指定的精度
            if (numOfDecimalPart > array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    if (i < array[1].length()) {
                        tempB[i] = array[1].charAt(i);
                    } else {
                        tempB[i] = '0';
                    }
                }
            }
            return String.valueOf(tempA1) + "." + String.valueOf(tempB);
        }
        // 整数部分满足精度要求(情况3)
        if (array[0].length() == numOfIntPart) {
            //直接获取整数部分长度字符
            for (int i = 0; i < array[0].length(); i++) {
                tempA[i] = array[0].charAt(i);
            }
            // 小数部分精度小于指定的精度
            if (numOfDecimalPart > array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    if (i < array[1].length()) {
                        tempB[i] = array[1].charAt(i);
                    } else {
                        tempB[i] = '0';
                    }
                }
            }
            // 小数部分精度大于或等于指定的精度
            if (numOfDecimalPart <= array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    tempB[i] = array[1].charAt(i);
                }
            }
            if (numOfDecimalPart == 0) {
                return String.valueOf(tempA) + String.valueOf(tempB);
            }
            return String.valueOf(tempA) + "." + String.valueOf(tempB);
        }
        // 整数部分大于精度要求(情况4)
        if (array[0].length() > numOfIntPart) {
            // 先倒序获取指定位数的整数
            for (int i = array[0].length() - 1, j = 0; (i >= array[0].length() - numOfIntPart + 1) && (j < numOfIntPart); i--, j++) {
                tempA[j] = array[0].charAt(i);
            }
            char[] tempA1 = new char[numOfIntPart];
            // 调整顺序
            for (int j = 0, k = tempA.length - 1; j < numOfIntPart && (k >= 0); j++) {
                tempA1[j] = tempA[k];
                k--;
            }
            // 小数部分精度小于指定的精度
            if (numOfDecimalPart > array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    if (i >= array[1].length()) {
                        tempB[i] = '0';
                    } else {
                        tempB[i] = array[1].charAt(i);
                    }
                }
            }
            // 小数部分精度大于或等于指定的精度
            if (numOfDecimalPart <= array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    tempB[i] = array[1].charAt(i);
                }
            }
            if (numOfDecimalPart == 0) {
                return String.valueOf(tempA1) + String.valueOf(tempB);
            }
            return String.valueOf(tempA1) + "." + String.valueOf(tempB);
        }
        // 整数部分小于精度要求(情况5)
        if (array[0].length() < numOfIntPart) {
            // 先倒序获取指定位数的整数
            char[] tempA1 = new char[numOfIntPart];
            for (int i = array[0].length() - 1, j = 0; (i >= numOfIntPart - array[0].length() - (numOfIntPart - array[0].length())) && (j < numOfIntPart); i--, j++) {
                tempA1[j] = array[0].charAt(i);
            }
            // 补0
            for (int i = array[0].length(); i < array[0].length() + numOfIntPart - array[0].length(); i++) {
                tempA1[i] = '0';
            }

            char[] tempA2 = new char[numOfIntPart];
            // 调整顺序
            for (int j = 0, k = tempA1.length - 1; j < numOfIntPart && (k >= 0); j++) {
                tempA2[j] = tempA1[k];
                k--;
            }
            // 小数部分精度小于指定的精度
            if (numOfDecimalPart > array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    if (i < array[1].length()) {
                        tempB[i] = array[1].charAt(i);
                    } else {
                        tempB[i] = '0';
                    }
                }
            }
            // 小数部分精度大于或等于指定的精度
            if (numOfDecimalPart <= array[1].length()) {
                for (int i = 0; i < numOfDecimalPart; i++) {
                    tempB[i] = array[1].charAt(i);

                }
            }
            if (numOfDecimalPart == 0) {
                return String.valueOf(tempA2) + String.valueOf(tempB);
            }
            return String.valueOf(tempA2) + "." + String.valueOf(tempB);
        }
        // 情况(6)
        if ((array[0].length() < numOfIntPart) && (array[1].length() < numOfDecimalPart)) {
            for (int i = 0; i < numOfIntPart - array[0].length(); i++) {
                s = "0" + s;
            }

            for (int i = 0; i < numOfDecimalPart - array[1].length(); i++) {
                s = s + "0";
            }
            return s;
        }
        return null;
    }
}
