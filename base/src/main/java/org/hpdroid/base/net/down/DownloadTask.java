package org.hpdroid.base.net.down;

import android.os.AsyncTask;

import org.hpdroid.base.util.FileUtil;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by luoliuqing on 16/11/21.
 */
public class DownloadTask extends AsyncTask<Void, Integer, String> {
	private static final String TAG = "DownloadTask";

	private String mUrl;
	private Call mCall;
	protected boolean isNeedDown = false;//本地有是否需要重新下载
	private DownloadProgressListener mListener;
	private File mCacheFile = DownloadManager.getInstance().getCacheFile();

	public DownloadTask(String url, DownloadProgressListener listener) {
		this.mUrl = url;
		this.mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mListener != null)
			mListener.start();
	}

	public void cancel() {
		try {
			String fileName= FileUtil.getFileNameFromUrl(mUrl);
			File file = new File(mCacheFile, fileName);
			file.deleteOnExit();
			mCall.cancel();
			if (mListener != null)
				mListener.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(Void... params) {

		//1.检查本地文件是否存在此文件，如果存在，则不重新下载
		//String md5 = MD5Util.getMD5String(mUrl);
		String fileName= FileUtil.getFileNameFromUrl(mUrl);
		File file = new File(mCacheFile, fileName);
		if (file.exists() && !isNeedDown){
			return file.getAbsolutePath();
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//2.如果本地不存在此文件，重新下载此文件
		Request request = new Request.Builder().url(mUrl).build();
		mCall = DownloadManager.getInstance().getHttpClient().newCall(request);
		InputStream inputStream = null;
		FileOutputStream fos = null;
		int last = -1;
		try {
			Response response = mCall.execute();
			if (response.code() >= 300) {
				response.body().close();
				return null;
			}
			ResponseBody body = response.body();
			long total = body.contentLength();
			publishProgress(0,(int)total);
			KLog.e(TAG, "totalLength:" + total);
			inputStream = body.byteStream();
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int length;
			long current = 0;
			KLog.e(TAG, "开始读写文件" );
			while ((length = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
				current += length;
				int progress = (int) (current * 100f / total);
				if (progress != last) {
					publishProgress(progress);
					last = progress;
				}
//                publishProgress((int)current,(int)total);
			}
			String path = file.getAbsolutePath();
			return path;//file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {

				}
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (mListener != null)
			mListener.onProgress(values[0].intValue());
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		DownloadManager.getInstance().remove(mUrl);
		if (mListener != null)
			mListener.finish(s);
	}

	public void setNeedDown(boolean needDown) {
		isNeedDown = needDown;
	}
}
