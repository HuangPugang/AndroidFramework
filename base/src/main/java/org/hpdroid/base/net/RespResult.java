package org.hpdroid.base.net;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by paul on 16/10/13.
 */

public class RespResult<T> implements Serializable {
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
