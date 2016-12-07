package org.hpdroid.base.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by luoliuqing on 16/10/21.
 * 文件基本信息
 */
public class FileUtil {
	private final static String LOCAL_PRODUCT_PATH = "/qiji/";
	private final static String LOCAL_PRODUCT_DOWNLOAD_PATH = "/qiji/download";
	/**
	 * 获得当前应用的目录
	 *
	 * @return SDCard存在则返回当前应用的路径，否则返回null
	 */
	public static String getLocalProductPath() {
		Boolean isSDPresent = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (!isSDPresent) {
			Log.e("getLocalProductPath", "SDCard disappered!");
			return null;
		}

		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + LOCAL_PRODUCT_PATH;
		File dir = new File(path);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}

		// 屏蔽MediaScanning展示
//		File file = new File(path + ".nomedia");
		File file = new File(path);
		if (!file.exists()) {
			try {
				FileWriter f = new FileWriter(file.getAbsolutePath());
				f.close();
			} catch (IOException e) {
				Log.e("getLocalProductPath", "IO Exception in file: " + path + ".nomedia");
			}
		}

		return path;
	}

	public static String getLocalProductDownloadPath() {
		Boolean isSDPresent = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (!isSDPresent) {
			Log.e("getLocalProductPath", "SDCard disappered!");
			return null;
		}

		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + LOCAL_PRODUCT_DOWNLOAD_PATH;
		File dir = new File(path);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}

		// 屏蔽MediaScanning展示
//		File file = new File(path + ".nomedia");
		File file = new File(path);
		if (!file.exists()) {
			try {
				FileWriter f = new FileWriter(file.getAbsolutePath());
				f.close();
			} catch (IOException e) {
				Log.e("getLocalProductPath", "IO Exception in file: " + path + ".nomedia");
			}
		}

		return path;
	}

	public static String getFileNameFromUrl(String path){
		if (!TextUtils.isEmpty(path)){
			return path.substring(path.lastIndexOf("/")+1,path.length());
		}
		return "";
	}

}
