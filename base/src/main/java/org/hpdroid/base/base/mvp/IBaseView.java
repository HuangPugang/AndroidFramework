package org.hpdroid.base.base.mvp;

import android.content.Context;

/**
 * Created by paul on 16/10/18.
 */

public interface IBaseView {

    void showLoading(int messageId);

    void showLoading(String msg);

    void hideLoading();

    /**
     * TOAST
     *
     * @param msg
     */
    void showToast(String msg);

    /**
     * TOAST
     *
     * @param msgId
     */
    void showToast(int msgId);

    /**
     * 获取上下文
     */
    Context getCtx();
}
