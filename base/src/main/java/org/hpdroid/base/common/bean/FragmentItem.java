package org.hpdroid.base.common.bean;

import android.support.v4.app.Fragment;

/**
 * Created by paul on 16/2/19.
 */
public class FragmentItem {
    private int id;
    private String title;
    private int resourceId;
    private boolean isChoose = false;
    private Fragment fragment;
    private int tabResourceId;

    public FragmentItem() {
    }

    public FragmentItem(int id, String title, int resourceId, boolean isChoose, Fragment fragment, int tabResourceId) {
        this.id = id;
        this.title = title;
        this.resourceId = resourceId;
        this.isChoose = isChoose;
        this.fragment = fragment;
        this.tabResourceId = tabResourceId;
    }

    public int getTabResourceId() {
        return tabResourceId;
    }

    public void setTabResourceId(int tabResourceId) {
        this.tabResourceId = tabResourceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FragmentItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setIsChoose(boolean isChoose) {
        this.isChoose = isChoose;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
