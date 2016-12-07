package org.hpdroid.base.net.exception;

import org.hpdroid.util.ToastHelper;

/**
 * Created by paul on 16/10/18.
 */

public class RespException extends RuntimeException {

    private int errCode = -1;

    private String msg;
    public RespException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
        this.msg = msg;
        ToastHelper.showMessage(msg);
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getErrCode() {
        return errCode;
    }
}
