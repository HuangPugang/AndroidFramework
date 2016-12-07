package org.hpdroid.util;

import android.app.Application;

/**
 * Created by paul on 16/6/24.
 */
public class CtxHelper {
    private static Application mApp;

    public static void init(Application application){
        mApp = application;
    }

    public static Application getApp(){
        return mApp;
    }
}
