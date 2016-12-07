package org.hpdroid.base.common.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 16/10/21.
 */

public class Banner {
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("image_width")
    private int ImageWidth;
    @SerializedName("image_height")
    private int imageHeight;
    @SerializedName("url")
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageWidth() {
        return ImageWidth;
    }

    public void setImageWidth(int imageWidth) {
        ImageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
