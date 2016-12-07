package org.hpdroid.base.contorller;


import org.hpdroid.base.constant.API;

/**
 * Created by Serena on 15/7/21.
 * get fragment by factor enum
 */
public class UrlSelectorManager {
    public static final int URL_TYPE_TEMAI = 1;
    public static final int URL_TYPE_STAGE = 2;
    public static final int URL_TYPE_MAIN = 3;
    public static final int URL_TYPE_QA = 4;

    public static String getServiceUrl(int type) {
        setBaseUrl(type);
        switch (type) {
            case URL_TYPE_TEMAI:
                return API.TEMAI_URL;
            case URL_TYPE_STAGE:
                return API.STAGE_URL;
            case URL_TYPE_MAIN:
                return API.PRODUCTION_URL;
            case URL_TYPE_QA:
                return API.QA_URL;
            default:
                return null;
        }
    }

    public static void setBaseUrl(int type) {

    }
}
