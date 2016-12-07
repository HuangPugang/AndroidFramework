package org.hpdroid.base.common.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by paul on 16/10/11.
 */
public class EventMessage<T> implements Serializable {

    @SerializedName("tag")
    private int tag;
    @SerializedName("data")
    private T data;

    public EventMessage() {

    }

    public EventMessage(int tag) {
        this.tag = tag;
    }

    public EventMessage(int tag, T data) {
        this.tag = tag;
        this.data = data;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
