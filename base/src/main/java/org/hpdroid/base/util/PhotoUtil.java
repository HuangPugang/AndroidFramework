package org.hpdroid.base.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;
import com.hpdroid.base.R;
import org.hpdroid.base.ui.activity.CropImageActivity;
import org.hpdroid.util.ToastHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by luoliuqing on 16/10/27.
 * 照片选择
 */
public class PhotoUtil {
	private static final int CROP_SIZE = 600;
	/***
	 * 4.4以下(也就是kitkat以下)的版本
	 */
	public static final int KITKAT_LESS = 100;
	/***
	 * 4.4以上(也就是kitkat以上)的版本,当然也包括最新出的5.0棒棒糖
	 */
	public static final int KITKAT_ABOVE = 101;

	/***
	 * 裁剪图片成功后返回
	 */
	public static final int INTENT_CROP = 102;

	/**
	 * 调用系统相机
	 */
	public static final int KITKAT_TAKEPHOTO = 103;

	/**
	 *
	 */
	public static final int REQUEST_STORAGE = 0;
	public static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
	public static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;

	private static PhotoUtil utils;

	public static PhotoUtil getInstance() {
		if (utils == null) {
			utils = new PhotoUtil();
		}
		return utils;
	}


	/***
	 * 选择一张图片
	 * 图片类型，这里是image/*，当然也可以设置限制
	 * 如：image/jpeg等
	 * @param activity Activity
	 */
	@SuppressLint("InlinedApi")
	public void selectPicture(Activity activity) {Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			//由于startActivityForResult()的第二个参数"requestCode"为常量，
			activity.startActivityForResult(intent, KITKAT_LESS);
		}
		else {
			activity.startActivityForResult(intent, KITKAT_ABOVE);

		}
	}

	/**
	 * 调用系统相机拍照
	 */
	public void takePhote(Activity activity,String path){
		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定调用相机拍照后照片的储存路径
		cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
		activity.startActivityForResult(cameraintent, KITKAT_TAKEPHOTO);

	}
	/***
	 * 裁剪图片
	 * @param activity Activity
	 * @param uri 图片的Uri
	 */
	public void cropPicture(Activity activity, Uri uri) {

		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
			try {
				Intent innerIntent = new Intent("com.android.camera.action.CROP");
				innerIntent.setDataAndType(uri, "image/*");
				innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
				innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
				innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X   这里的比例为：   1:1
				innerIntent.putExtra("outputX", CROP_SIZE);  //这个是限制输出图片大小
				innerIntent.putExtra("outputY", CROP_SIZE);
				innerIntent.putExtra("return-data", true);
				innerIntent.putExtra("scale", true);
				activity.startActivityForResult(innerIntent, INTENT_CROP);
			} catch (ActivityNotFoundException anfe) {
				ToastHelper.showMessage("your device doesn't support the crop action!");
			}
		}else{
			Intent cropIntent = new Intent(activity, CropImageActivity.class);
			cropIntent.putExtra("from", getPath(activity, uri));
			cropIntent.putExtra("outputX", CROP_SIZE);
			cropIntent.putExtra("outputY", CROP_SIZE);
			activity.startActivityForResult(cropIntent, INTENT_CROP);
		}
	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @author paulburke
	 */
	@SuppressLint("NewApi")
	public String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}


	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public String getDataColumn(Context context, Uri uri, String selection,
	                            String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	public  boolean checkNeedsPermission(Activity activity) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
	}


	/**
	 * This checks to see if there is a suitable activity to handle the `ACTION_PICK` intent
	 * and returns it if found. {@link Intent#ACTION_PICK} is for picking an image from an external app.
	 *
	 * @return A prepared intent if found.
	 */
	@Nullable
	public  Intent createPickIntent(Activity activity) {
		Intent picImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (picImageIntent.resolveActivity(activity.getPackageManager()) != null) {
			return picImageIntent;
		} else {
			return null;
		}
	}

	/**
	 * This checks to see if there is a suitable activity to handle the {@link MediaStore#ACTION_IMAGE_CAPTURE}
	 * intent and returns it if found. {@link MediaStore#ACTION_IMAGE_CAPTURE} is for letting another app take
	 * a picture from the camera and store it in a file that we specify.
	 *
	 * @return A prepared intent if found.
	 */
	@Nullable
	public  Intent createCameraIntent(Activity activity) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
			return takePictureIntent;
		} else {
			return null;
		}
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public  void requestStoragePermission(Activity activity) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
		} else {
			// Eh, prompt anyway
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
		}
	}

	public  String processCropImage(Bitmap bitmap) {
		File temp = new File(FileUtil.getLocalProductPath());
		if (!temp.exists()) {
			temp.mkdir();
		}
		File tempFile = new File(temp.getAbsolutePath() + "/"
				+ Calendar.getInstance().getTimeInMillis() + ".jpg");
		FileOutputStream foutput;
		try {
			foutput = new FileOutputStream(tempFile);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, foutput)) {
				if (!bitmap.isRecycled()){
					bitmap.recycle();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tempFile.getAbsolutePath();
	}


	/**
	 * 弹出单张图片选择器
	 */
	public void showSheetView(final Activity activity, final BottomSheetLayout bottomSheetLayout,final String mHeadFilePath) {
		ImagePickerSheetView sheetView = new ImagePickerSheetView.Builder(activity)
				.setTitle(R.string.app_select_img)
				.setMaxItems(30)
				.setShowCameraOption(PhotoUtil.getInstance().createCameraIntent(activity) != null)
				.setShowPickerOption(PhotoUtil.getInstance().createPickIntent(activity) != null)
				.setImageProvider(new ImagePickerSheetView.ImageProvider() {
					@Override
					public void onProvideImage(ImageView imageView, Uri imageUri, int size) {
						Glide.with(activity)
								.load(imageUri)
								.centerCrop()
								.crossFade()
								.into(imageView);
					}
				})
				.setOnTileSelectedListener(new ImagePickerSheetView.OnTileSelectedListener() {
					@Override
					public void onTileSelected(ImagePickerSheetView.ImagePickerTile selectedTile) {
						bottomSheetLayout.dismissSheet();
						if (selectedTile.isCameraTile()) {
							PhotoUtil.getInstance().takePhote(activity,mHeadFilePath);
						} else if (selectedTile.isPickerTile()) {
							activity.startActivityForResult(PhotoUtil.getInstance().createPickIntent(activity), PhotoUtil.REQUEST_LOAD_IMAGE);
						} else if (selectedTile.isImageTile()) {
							PhotoUtil.getInstance().cropPicture(activity,selectedTile.getImageUri());
						} else {
						}
					}
				})
				.create();

		bottomSheetLayout.showWithSheetView(sheetView);
	}
}
