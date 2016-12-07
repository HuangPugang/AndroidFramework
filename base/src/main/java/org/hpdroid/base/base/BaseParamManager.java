package org.hpdroid.base.base;

import android.util.Log;

import com.google.gson.Gson;
import org.hpdroid.base.util.AppConfig;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by paul on 16/10/18.
 */

public abstract class BaseParamManager {

    public static final String S_NET_PARAMS_SIGN_KEY = "c0e17d5418074da684843e12a16051d9";

    /**
     * json转对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            T t = gson.fromJson(json, clazz);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    protected static Map<String, String> signMap(Map<String, String> params) {
        params.put("time", String.valueOf((int) ((new Date()).getTime() / 1000)));
        params.put("app_version", AppConfig.getInstance().versionName);
        params.put("device_type", "1");
        params.put("protocol_version", "1.0.0");
        int len = params.size();

        Set<String> key = params.keySet();
        String[] paramNames = new String[len];
        Iterator<String> iterator = key.iterator();
        int j = 0;
        while (iterator.hasNext()) {
            paramNames[j] = iterator.next();
            j++;
        }
        Arrays.sort(paramNames);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(paramNames[i] + "=" + params.get(paramNames[i]) + "&");
        }
        sb.append(S_NET_PARAMS_SIGN_KEY);
        String md5 = md5s(sb.toString());
        params.put("sign", md5);
        Gson gson = new Gson();
        Log.e("BaseParamManager",gson.toJson(params));
        return params;
    }


    public static String md5s(String plainText) {
        String str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes("utf-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
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

}
