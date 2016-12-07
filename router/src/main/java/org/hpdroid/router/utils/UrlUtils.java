package org.hpdroid.router.utils;

import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by kris on 16/3/10.
 */
public class UrlUtils {

    private static final String TAG = "UrlUtils";

    /**
     * get the path segments
     * @param url
     * @return
     */
    public static List<String> getPathSegments(String url) {
        return Uri.parse(url).getPathSegments();
    }

    /**
     * get the scheme of the url
     * @param url
     * @return
     */
    public static String getScheme(String url){
        return Uri.parse(url).getScheme();
    }


    /**
     * get the protocol of the url
     */
    public static int getPort(String url){
        return Uri.parse(url).getPort();
    }

    public static String getHost(String url){
        return Uri.parse(url).getHost();
    }

    public static HashMap<String, String> getParameters(String url){
        HashMap<String, String> parameters = new HashMap<>();
        try{
            Uri uri = Uri.parse(url);
            Set<String> keys = uri.getQueryParameterNames();

            for(String key : keys){
                parameters.put(key, uri.getQueryParameter(key));
            }
        } catch (Exception e){
            Log.e(e.getMessage(), "");
        }
        return parameters;
    }


    public static String addQueryParameters(String url, String key, String value){
        try{
            Uri uri = Uri.parse(url);
            return uri.buildUpon().appendQueryParameter(key, value).build().toString();
        } catch (Exception e){
            Log.e(e.getMessage(), "");
        }
        return url;
    }
}
