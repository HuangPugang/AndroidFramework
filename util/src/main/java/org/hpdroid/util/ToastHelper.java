package org.hpdroid.util;

import android.text.TextUtils;
import android.widget.Toast;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * Created by Serena on 15/8/6.
 */
public class ToastHelper {


    private static Toast toast = null;

    private static byte[] object = new byte[0];

    public static void showMessage(final String msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final int msgId) {
        showMessage(msgId, Toast.LENGTH_SHORT);
    }


    public static void showMessage(String msg, final int length) {
        if (TextUtils.isEmpty(msg)) return;
        Observable.just(msg).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        synchronized (object) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CtxHelper.getApp(), s, length);
                            toast.show();
                        }
                    }
                });

    }

    public static void showMessage(int msg, final int length) {
        Observable.just(msg).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer s) {
                        synchronized (object) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(CtxHelper.getApp(), s, length);
                            toast.show();
                        }
                    }
                });

    }
}
