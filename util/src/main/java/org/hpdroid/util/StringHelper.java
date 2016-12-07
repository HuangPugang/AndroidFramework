package org.hpdroid.util;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by kaka
 *         created at 16/5/27 17:41
 *         String工具类
 */
public class StringHelper {

    private static DecimalFormat sDecimalFormat = new DecimalFormat("##0.00");

    private StringHelper() {

    }

    public static String randomGUID() {
        return UUID.randomUUID().toString();
    }

    public static String ls(int id) {
        return CtxHelper.getApp().getResources().getString(id);
    }

    public static String ls(int id, Object... strings) {
        return CtxHelper.getApp().getResources().getString(id, strings);
    }

    public static String md5s(String plainText) {
        String str;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes("utf-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            str = "";
        } catch (UnsupportedEncodingException e) {
            str = "";
        }

        return str;
    }

    /**
     * TODO<判断字符串为空>
     *
     * @param str
     * @return boolean
     * @throw
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0 || str.equals("")
                || str.equals("null"))
            return true;
        else
            return false;
    }

    /**
     * 判断edittext是否为空
     * @param editText
     * @return
     */
    public static boolean isEmpty(EditText editText) {
        if (editText != null && !isEmpty(editText.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * 判断textView是否为空
     * @param textView
     * @return
     */
    public static boolean isEmpty(TextView textView) {
        if (textView != null && !isEmpty(textView.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }

    public static String parseFloat(String s) {
        if (TextUtils.isEmpty(s))
            return "";
        try {
            float value = Float.parseFloat(s);
            return parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String parseFloat(float value) {
        if (value < 0)
            return sDecimalFormat.format(0);
        return sDecimalFormat.format(value);
    }

    /**
     * 转码
     *
     * @param origin
     * @return
     */
    public static String unicode2UTF8(String origin) {
        try {
            String after = URLDecoder.decode(origin, "UTF-8");
            return after;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化数字，超过100W显示100W+，少于1W显示具体，其他以W为单位
     *
     * @param number
     * @return
     */
    public static String formatNumber(int number) {
        if (number < 10000) {
            return String.valueOf(number);
        }
        if (number > 1000000) {
            return "100万+";
        }
        return new DecimalFormat("#0万").format(number / 10000);
    }


    /**
     * 判断是否11位数字
     *
     * @param str
     * @return
     */
    public static boolean is11Number(String str) {
        Pattern pattern = Pattern.compile("^[0-9][0-9][0-9]{9}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String formatLength(String str, int length) {
        if (str == null || str.length() <= length)
            return str;
        int size = str.length();
        StringBuffer buffer = new StringBuffer();
        buffer.append(str.substring(0, length / 2)).append("…").append(str.substring(size - length / 2, size));
        return buffer.toString();
    }
}
