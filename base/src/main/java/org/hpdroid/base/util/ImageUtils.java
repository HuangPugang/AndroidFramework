package org.hpdroid.base.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;

import org.hpdroid.util.CtxHelper;
import com.socks.library.KLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by luoliuqing on 16/10/28.
 * 图片处理工具
 */
public class ImageUtils {
	/**
	 * 默认图片压缩大小
	 */
	public static final int DEFAULT_SCALE_WIDTH = 1080;
	public static final int DEFAULT_SCALE_HEIGHT = 1920;
	/**
	 * 默认压缩精度
	 **/
	public static final int DEFAULT_QUALITY = 90;
	public static final long DEFAULT_MAX_SIZE = 2 * 1024 * 1024 / 10;

	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static void saveMyBitmap(String bitName, byte[] b) {
		//首先将byte数组转为bitmap
		Bitmap mBitmap = bytes2Bitmap(b);
		//创建文件对象，用来存储新的图像文件
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/59store/" + bitName + ".jpg");
		try {
			//创建文件
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("在保存图片时出错：" + e.toString());
		}
		//定义文件输出流
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//将bitmap存储为jpg格式的图片
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			//刷新文件流
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//关闭文件流
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个文件转化为字节
	 *
	 * @param file
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getByte(File file) throws Exception {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			int length = (int) file.length();
			if (length > Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
			{
				System.out.println("this file is max ");
				return null;
			}
			bytes = new byte[length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			//如果得到的字节长度和file实际的长度不一致就可能出错了
			if (offset < bytes.length) {
				System.out.println("file length is error");
				return null;
			}
			is.close();
		}
		return bytes;
	}

	/**
	 * 读取图片的旋转角度
	 *
	 * @param path
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		if (!TextUtils.isEmpty(path)) {
			try {
				ExifInterface exif = new ExifInterface(path);
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						degree = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						degree = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						degree = 270;
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return degree;
	}

	public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
		return bitmap;
	}

	/**
	 * 压缩图片
	 *
	 * @param fileName
	 * @return
	 */
	public static String scaleImage(String fileName) {
		return scaleImage(fileName, DEFAULT_SCALE_WIDTH, DEFAULT_SCALE_HEIGHT, DEFAULT_QUALITY, DEFAULT_MAX_SIZE);
	}

	/**
	 * 压缩图片并返回压缩后路径
	 *
	 * @param fileName
	 * @param maxWidth
	 * @param maxHeight
	 * @param quality
	 * @param maxSize
	 * @return
	 */
	public static String scaleImage(String fileName, int maxWidth, int maxHeight, int quality,
	                                long maxSize) {
		if (TextUtils.isEmpty(fileName))
			return null;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int desWidth = 0;
			int desHeight = 0;
			double ratio = 0.0;
			if (srcWidth > srcHeight) {
				ratio = srcWidth / maxWidth;
				desWidth = maxWidth;
				desHeight = (int) (srcHeight / ratio);
			} else {
				ratio = srcHeight / maxHeight;
				desHeight = maxHeight;
				desWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) (ratio) + 1;
			newOpts.inJustDecodeBounds = false;
			newOpts.outWidth = desWidth;
			newOpts.outHeight = desHeight;
			bitmap = retryDecodeBitmap(fileName, newOpts, 2);
			//读取旋转角度
			int angle = readPictureDegree(fileName);
			if (angle != 0) {
				bitmap = rotateBitmap(angle, bitmap);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			while (bos.toByteArray().length > maxSize) {
				bos.reset();
				quality -= 10;
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			}
			String imagePathFileName = new File(fileName).getName();
			if (!imagePathFileName.endsWith(".jpg") && !imagePathFileName.endsWith("png") && !imagePathFileName.endsWith("gif")){
				imagePathFileName += ".jpg";
			}
			File tempFile = new File(FileUtil.getLocalProductPath() + imagePathFileName);
			if (!tempFile.exists()){
				tempFile.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(tempFile);
			bos.writeTo(out);
			bos.close();
			out.close();
			KLog.e("图片压缩后大小为：" + Formatter.formatFileSize(CtxHelper.getApp(), tempFile.length()));
			return tempFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			System.gc();
			return null;
		} finally {
			if (bitmap != null && !bitmap.isRecycled())
				bitmap.recycle();
		}
		return null;
	}

	public static Bitmap retryDecodeBitmap(String fileName, BitmapFactory.Options options,
	                                       int retryCount) {
		try {
			return BitmapFactory.decodeFile(fileName, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			if (retryCount > 0)
				return retryDecodeBitmap(fileName, options, retryCount - 1);
			return null;
		}
	}

	public static boolean checkSize(String filePath, long maxSize) {
		try {
			long size = new File(filePath).length();
			if (size > maxSize) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 创建bitmap缩略图
	 *
	 * @param bitMap
	 * @param thumbWidth
	 * @param thumbHeight
	 * @return
	 */
	public static Bitmap createBitmapThumbnail(Bitmap bitMap, int thumbWidth, int thumbHeight) {
		try {
			int width = bitMap.getWidth();
			int height = bitMap.getHeight();
			// 设置想要的大小
			int newWidth = thumbWidth;
			int newHeight = thumbHeight;
			// 计算缩放比例
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// 取得想要缩放的matrix参数
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 得到新的图片
			Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
					matrix, true);
			return newBitMap;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitMap;
	}

	/**
	 * drawable 转换成bitmap
	 *
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {// drawable 转换成bitmap

		// 取drawable的长宽
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = null;
		if (width > 1080) {
			float widthMultiple = width / 1080 * 1.0f;
			float heightMultiple = height / 1920 * 1.0f;
			float multiple = Math.max(widthMultiple, heightMultiple);
			width = (int) (width / multiple);
			height = (int) (height / multiple);
			config = Bitmap.Config.RGB_565;// 取drawable的颜色格式
		} else {
			config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
		}
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, config);
		} catch (Exception e) {
			KLog.e(e.getCause());
			KLog.e(e.getMessage());
			Drawable d = CtxHelper.getApp().getResources().getDrawable(com.hpdroid.util.R.drawable.img_loading_img);
			bitmap = drawableToBitmap(d, 200, 200);
//            bitmap = BitmapFactory.decodeResource(AppDelegate.getApp().getResources(), R.drawable.img_loading_img).copy(Bitmap.Config.ARGB_8888, true);
		} catch (Throwable e) {
			KLog.e(e.getCause());
			KLog.e(e.getMessage());
			Drawable d = CtxHelper.getApp().getResources().getDrawable(com.hpdroid.util.R.drawable.img_loading_img);
			bitmap = drawableToBitmap(d, 200, 200);
		}
		Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);// 把drawable内容画到画布中
		return bitmap;
	}

	/**
	 * drawable 转换成bitmap
	 *
	 * @param drawable
	 * @param currentItem
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable, View currentItem) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		int reqW = currentItem.getHeight();
		int reqH = currentItem.getWidth();
		float widthMultiple = w / reqW * 1.0f;
		float heithMultiple = h / reqH * 1.0f;
		float multiple = Math.max(widthMultiple, heithMultiple);
		w = (int) (w / multiple);
		h = (int) (h / multiple);

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * drawable 转换成bitmap
	 *
	 * @param drawable
	 * @param reqW
	 * @param reqH
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable, int reqW, int reqH) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		float widthMultiple = w / reqW * 1.0f;
		float heithMultiple = h / reqH * 1.0f;
		float multiple = Math.max(widthMultiple, heithMultiple);
		w = (int) (w / multiple);
		h = (int) (h / multiple);

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}


	/**
	 * 将drawable缩放到指定大小
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
		Matrix matrix = new Matrix();   // 创建操作图片用的 Matrix 对象
		float scaleWidth = ((float) w / width);   // 计算缩放比例
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
		return new BitmapDrawable(newbmp);       // 把 bitmap 转换成 drawable 并返回
	}
}
