package org.hpdroid.base.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import org.hpdroid.util.CtxHelper;

import java.util.UUID;

/**
 * app一些相关的配置
 */
public class AppConfig {

    private static AppConfig sInstance = null;
    private AndroidSystemInfo mSystemInfo;

    public static AppConfig getInstance() {
        if (sInstance == null) {
            sInstance = new AppConfig();
        }
        return sInstance;
    }

    public int versionCode = 0;
    public String versionName = "";
    public String systemVersion = "" + android.os.Build.VERSION.SDK_INT;

    public AppConfig() {
        Application delegate = CtxHelper.getApp();
        try {
            PackageInfo info = delegate.getPackageManager().getPackageInfo(
                    delegate.getPackageName(), 0);
            versionName = info.versionName;
            versionCode = info.versionCode;

            mSystemInfo = new AndroidSystemInfo();
            mSystemInfo.setmSystemVersion(getAndroidVersion());
            mSystemInfo.setmPlatform(getPlatform());
            mSystemInfo.setmManufacturer(getManufacturer());
            mSystemInfo.setmModel(getPhoneModel());
            mSystemInfo.setmUuid(getUUID());
        } catch (Exception e) {
            Log.e("AppConfig", "get package info error");
        }
    }

    public String getDeviceId() {
        Application delegate = CtxHelper.getApp();

        final TelephonyManager tm = (TelephonyManager) delegate
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + android.provider.Settings.Secure.getString(
                delegate.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }



    public String getDeviceIdString() {
        Application delegate = CtxHelper.getApp();

        final TelephonyManager tm = (TelephonyManager) delegate
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + android.provider.Settings.Secure.getString(
                delegate.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 获取设备UUID
     *
     * @return
     */
    public static String getUUID() {
        final TelephonyManager tm = (TelephonyManager) CtxHelper.getApp()
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + android.provider.Settings.Secure.getString(
                CtxHelper.getApp().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();

        return uniqueId;
    }

    /**
     * 获取系统版本号
     */

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机厂商
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机型号
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;//BRAND
    }

    /**
     * 获取系统名称
     */
    public static String getPlatform() {

        return "Android";
    }

    public AndroidSystemInfo getmSystemInfo() {
        return mSystemInfo;
    }

    public class AndroidSystemInfo {
        @SerializedName("platform")
        private String mPlatform;
        @SerializedName("version")
        private String mSystemVersion;
        @SerializedName("uuid")
        private String mUuid;
        @SerializedName("cordova")
        private String mCordova;
        @SerializedName("model")
        private String mModel;
        @SerializedName("manufacturer")
        private String mManufacturer;

        public String getmPlatform() {
            return mPlatform;
        }

        public void setmPlatform(String mPlatform) {
            this.mPlatform = mPlatform;
        }

        public String getmSystemVersion() {
            return mSystemVersion;
        }

        public void setmSystemVersion(String mSystemVersion) {
            this.mSystemVersion = mSystemVersion;
        }

        public String getmUuid() {
            return mUuid;
        }

        public void setmUuid(String mUuid) {
            this.mUuid = mUuid;
        }

        public String getmCordova() {
            return mCordova;
        }

        public void setmCordova(String mCordova) {
            this.mCordova = mCordova;
        }

        public String getmModel() {
            return mModel;
        }

        public void setmModel(String mModel) {
            this.mModel = mModel;
        }

        public String getmManufacturer() {
            return mManufacturer;
        }

        public void setmManufacturer(String mManufacturer) {
            this.mManufacturer = mManufacturer;
        }
    }
}
