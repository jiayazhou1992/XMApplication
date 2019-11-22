package com.xiaomawang.commonlib.utils.dev.common.validator;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * detail: 校验工具类
 * Created by Ttt
 */
public final class ValidatorUtils {

    private ValidatorUtils() {
    }

    // 日志TAG
    private static final String TAG = ValidatorUtils.class.getSimpleName();

    /** 正则表达式：验证数字 */
    static final String REGEX_NUMBER = "^[0-9]*$";

    /** 正则表达式：不能输入特殊字符   ^[\u4E00-\u9FA5A-Za-z0-9]+$ 或 ^[\u4E00-\u9FA5A-Za-z0-9]{2,20}$ */
    static final String REGEX_SPECIAL = "^[\\u4E00-\\u9FA5A-Za-z0-9]+$";

    /** 正则表达式：验证微信号  ^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$ */
    static final String REGEX_WX = "^[a-zA-Z\\\\d_]{5,19}$";

    /** 正则表达式：验证真实姓名       |[\w·•])$  ^[\u4e00-\u9fa5]+(·[\u4e00-\u9fa5]+)*$ */
    static final String REGEX_REALNAME = "^[\\u4e00-\\u9fa5]+(•[\\u4e00-\\u9fa5]*)*$|^[\\u4e00-\\u9fa5]+(·[\\u4e00-\\u9fa5]*)*$";

    /** 正则表达式：验证昵称 */
    static final String REGEX_NICKNAME = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";

    /** 正则表达式:验证用户名(不包含中文和特殊字符)如果用户名使用手机号码或邮箱 则结合手机号验证和邮箱验证 */
    static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

    /** 正则表达式:验证密码(不包含特殊字符) */
    static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,18}$";

    static final String REGEX_PASSWORD2 = "^[a-zA-Z0-9\\.]{6}$";

    /** 正则表达式:验证邮箱 */
    static final String REGEX_EMAIL = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";//"^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /** 正则表达式:验证URL */
    static final String REGEX_URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";//"^(https?|ftp|file)://[-a-zA-Z0-9+&@/%?=~_|!:,.;]*[-a-zA-Z0-9+&@/%=~_|]$";//"http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";

    /** 正则表达式:验证URL */
    static final String REGEX_ANDROID_URL = "^(https?|ftp|file|content)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    /** 正则表达式:验证IP地址 */
    static final String REGEX_IP_ADDR = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";

    // ==== 内部方法 =====

    /**
     * 判断是否为null
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 通用匹配函数
     * @param regex
     * @param input
     * @return
     */
    public static boolean match(String regex, String input) {
        return Pattern.matches(regex, input);
    }

    // ======

    /**
     * 检验数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (!isEmpty(str)){
            return match(REGEX_NUMBER, str);
        }
        return false;
    }

    /**
     * 判断字符串是不是全是字母
     * @param str
     * @return
     */
    public static boolean isLetter(String str) {
        if (!isEmpty(str)){
            return match("^[A-Za-z]+$", str);
        }
        return false;
    }

    /**
     * 判断字符串是不是只含字母和数字
     * @param str
     * @return
     */
    public static boolean isNumberLetter(String str) {
        if (!isEmpty(str)){
            return match("^[A-Za-z0-9]+$", str);
        }
        return false;
    }

    /**
     * 检验特殊符号
     * @param str
     * @return
     */
    public static boolean isSpec(String str) {
        if (!isEmpty(str)){
            return match(REGEX_SPECIAL, str);
        }
        return false;
    }

    /**
     * 检验微信号
     * @param str
     * @return
     */
    public static boolean isWx(String str) {
        if (!isEmpty(str)){
            return match(REGEX_WX, str);
        }
        return false;
    }

    /**
     * 检验真实姓名
     * @param str
     * @return
     */
    public static boolean isRealName(String str) {
        if (!isEmpty(str)){
            return match(REGEX_REALNAME, str);
        }
        return false;
    }

    /**
     * 校验昵称
     * @param str
     * @return
     */
    public static boolean isNickName(String str) {
        if (!isEmpty(str)){
            return match(REGEX_NICKNAME, str);
        }
        return false;
    }

    /**
     * 校验用户名
     * @param str
     * @return
     */
    public static boolean isUserName(String str) {
        if (!isEmpty(str)){
            return match(REGEX_USERNAME, str);
        }
        return false;
    }

    /**
     * 校验密码
     * @param str
     * @return
     */
    public static boolean isPassword(String str) {
        if (!isEmpty(str)){
            return match(REGEX_PASSWORD, str);
        }
        return false;
    }

    public static boolean isPassword2(String str) {
        if (!isEmpty(str)){
            return match(REGEX_PASSWORD2, str);
        }
        return false;
    }

    /**
     * 校验邮箱
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        if (!isEmpty(str)){
            return match(REGEX_EMAIL, str);
        }
        return false;
    }

    /**
     * 校验URL
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (!isEmpty(str)){
            return match(REGEX_URL, str);
        }
        return false;
    }

    /**
     * 校验URL
     * @param str
     * @return
     */
    public static boolean isAndroidUrl(String str) {
        if (!isEmpty(str)){
            return match(REGEX_ANDROID_URL, str);
        }
        return false;
    }

    /**
     * 校验IP地址
     * @param str
     * @return
     */
    public static boolean isIPAddress(String str) {
        if (!isEmpty(str)){
            return match(REGEX_IP_ADDR, str);
        }
        return false;
    }

    /**
     * IP地址校验
     * @param str 待校验是否是IP地址的字符串
     * @return
     */
    public static boolean isIP(String str) {
        if (!isEmpty(str)){
            Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
        return false;
    }

    // =======

//     // http://blog.csdn.net/myfuturein/article/details/6885216
//    [\\u0391-\\uFFE5]匹配双字节字符(汉字+符号)
//    [\\u4e00-\\u9fa5]注意只匹配汉字，不匹配双字节字符

    /** 正则表达式:验证汉字 */
    static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]+$";
    /** 正则表达式:验证汉字(含双角符号) */
    static final String REGEX_CHINESE_ALL = "^[\u0391-\uFFE5]+$";

    /**
     * 校验汉字(无符号,纯汉字)
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (!isEmpty(str)){
            return match(REGEX_CHINESE, str);
        }
        return false;
    }

    /**
     * 判断字符串是不是全是中文
     * @param str
     * @return
     */
    public static boolean isChineseAll(String str) {
        if (!isEmpty(str)){
            return match(REGEX_CHINESE_ALL, str);
        }
        return false;
    }

    /**
     * 判断字符串中包含中文、包括中文字符标点等
     * @param data 可能包含中文的字符串
     * @return 是否包含中文
     */
    public static boolean isContainChinese(String data) {
        try {
            String chinese = "[\u0391-\uFFE5]";
            int length;
            if(data != null && (length = data.length()) != 0) {
                char[] dChar = data.toCharArray();
                for (int i = 0; i < length; i++) {
                    boolean flag = String.valueOf(dChar[i]).matches(chinese);
                    if (flag) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            JCLogUtils.eTag(TAG, e, "isContainChinese");
        }
        return false;
    }

}
