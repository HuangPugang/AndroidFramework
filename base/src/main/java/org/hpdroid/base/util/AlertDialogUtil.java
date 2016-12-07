package org.hpdroid.base.util;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.hpdroid.base.R;
import org.hpdroid.util.StringHelper;

/**
 * Created by luoliuqing on 16/10/26.
 * 普通弹框
 */
public class AlertDialogUtil {
	public static android.support.v7.app.AlertDialog.Builder showDialog(Context activity, int titleId, int msgId, int leftBtnTextId, int rightBtnTextId, DialogInterface.OnClickListener leftClickListener, DialogInterface.OnClickListener rightClickListener){
		return showDialog(activity,StringHelper.ls(titleId),
				StringHelper.ls(msgId),
				leftBtnTextId <= 0 ? "" : StringHelper.ls(leftBtnTextId),
				rightBtnTextId<= 0 ? "" : StringHelper.ls(rightBtnTextId),
				leftClickListener,
				rightClickListener);
	}

	public static android.support.v7.app.AlertDialog.Builder showDialog(Context activity,String title, String msg, String leftBtnText, String rightBtnText, DialogInterface.OnClickListener leftClickListener, DialogInterface.OnClickListener rightClickListener){
		final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity, R.style.AppTheme_Dialog_Alert);
		builder.setTitle(title);
		builder.setMessage(msg);
		if (!TextUtils.isEmpty(leftBtnText)) {
			builder.setNegativeButton(leftBtnText, leftClickListener);
		}
		if (!TextUtils.isEmpty(rightBtnText)) {
			builder.setPositiveButton(rightBtnText,rightClickListener);
		}
		builder.show();
		return builder;
	}

}
