package org.hpdroid.base.net.down;

import android.os.AsyncTask;
import android.os.StatFs;
import android.text.TextUtils;

import org.hpdroid.base.util.FileUtil;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by luoliuqing on 16/11/21.
 */
public class DownloadManager {
	private static final String TAG = "DownloadManager";
	private static DownloadManager instance = new DownloadManager();
	private static ConcurrentHashMap<String, DownloadTask> mHashMap = new ConcurrentHashMap<>();
	private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
	private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
	private static final int MAX_THREAD_POOL = 5;
	private static final int TIME_OUT = 15000;

	private OkHttpClient mHttpClient;
	private File mCacheFile;

	public OkHttpClient getHttpClient() {
		return mHttpClient;
	}

	public File getCacheFile() {
		if (!mCacheFile.exists()){
			mCacheFile.mkdirs();
		}
		return mCacheFile;
	}

	private DownloadManager() {
		mCacheFile = createDefaultCacheDir();
		mHttpClient = new OkHttpClient.Builder()
				.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
				.readTimeout(TIME_OUT, TimeUnit.SECONDS)
				.writeTimeout(TIME_OUT, TimeUnit.SECONDS)
				.cache(new okhttp3.Cache(mCacheFile, calculateDiskCacheSize(mCacheFile)))
				.build();
	}

	public static DownloadManager getInstance() {
		return instance;
	}

	public static boolean isHttpUrl(String url) {
		if (TextUtils.isEmpty(url)) return false;
		if (url.startsWith("http"))
			return true;
		return false;
	}

	/**
	 * 创建缓存目录
	 */
	private File createDefaultCacheDir() {

		File cache = new File(FileUtil.getLocalProductDownloadPath());
		if (!cache.exists()) {
			cache.mkdirs();
		}
		return cache;
	}


	/**
	 * 分配缓存空间大小
	 */
	private long calculateDiskCacheSize(File dir) {
		long size = MIN_DISK_CACHE_SIZE;
		try {
			StatFs statFs = new StatFs(dir.getAbsolutePath());
			long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
			size = available / 50;
		} catch (IllegalArgumentException ignored) {
		}
		return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
	}

	public void remove(String url) {
		mHashMap.remove(url);
	}


	/**
	 * 取消下载
	 */
	public void cancel(String url) {
		if(!TextUtils.isEmpty(url)) {
			if (mHashMap.containsKey(url)) {
				mHashMap.get(url).cancel();
				mHashMap.remove(url);
			}
		}
	}

	/**
	 * 取消所有请求
	 */
	public void cancelAll() {
		if (mHashMap.isEmpty()) return;
		for (String item : mHashMap.keySet()) {
			mHashMap.get(item).cancel();
			mHashMap.remove(item);
		}
	}



	public void download(String url, boolean isNeedWhenFnameEqual,DownloadProgressListener listener) {
		DownloadTask task = new DownloadTask(url, listener);
		task.setNeedDown(isNeedWhenFnameEqual);
		mHashMap.put(url, task);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
