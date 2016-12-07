package org.hpdroid.base.net.down;

/**
 * Created by luoliuqing on 16/11/21.
 */
public interface DownloadProgressListener {
	/** 开始下载 **/
	void start();

	/** 取消下载 **/
	void cancel();

	/** 下载完成 **/
	void finish(String filePath);

	/** 进度发生改变 **/
	void onProgress(int progress);
}
