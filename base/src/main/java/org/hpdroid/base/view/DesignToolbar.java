package org.hpdroid.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hpdroid.base.R;


/**
 * Created by paul on 16/7/7.
 */
public class DesignToolbar extends Toolbar {
    private TextView mTitleTextView;
    //是否显示按钮nav
    private boolean mNavIsButton = true;
    private Drawable mNavButtonIcon;
    private CharSequence mTitle;
    private boolean mShowRightButton = false;
    private CharSequence mRightButtonText;
    private Drawable mRightButtonIcon;

    private OnRightMenuClickListener mOnRightMenuClickListener;


    public DesignToolbar(Context context) {
        super(context);
    }

    public DesignToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(attrs, context.obtainStyledAttributes(attrs,
                R.styleable.DesignToolbar), context);
    }

    private void getAttributes(AttributeSet attrs, TypedArray a, Context context) {
        mTitleTextView = new TextView(context);
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTitleTextView.setGravity(Gravity.CENTER);

        try {
            mNavIsButton = a.getBoolean(R.styleable.DesignToolbar_navigationIsButton, false);
            mNavButtonIcon = a.getDrawable(R.styleable.DesignToolbar_navigationLeftIcon);
            mTitle = a.getString(R.styleable.DesignToolbar_titleText);
            mShowRightButton = a.getBoolean(R.styleable.DesignToolbar_showRightButton, false);
            mRightButtonText = a.getString(R.styleable.DesignToolbar_rightText);
            mRightButtonIcon = a.getDrawable(R.styleable.DesignToolbar_rightIcon);
        } finally {
            a.recycle();
        }

        setShowRightButton(mShowRightButton);


        setShowNabButton(mNavIsButton);
        final TypedArray a2 = context.obtainStyledAttributes(attrs,
                android.support.v7.appcompat.R.styleable.Toolbar);
        try {
            mTitleTextView.setTextAppearance(context, a.getResourceId(android.support.v7.appcompat.R.styleable.Toolbar_titleTextAppearance, 0));
        } finally {
            a2.recycle();
        }


        setTitle("");
        setSubtitle("");
        addTitleView();
    }

    private void addTitleView() {
        ViewGroup.LayoutParams lp = super.generateDefaultLayoutParams();
        LayoutParams params = (LayoutParams) lp;
        params.gravity = Gravity.CENTER;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        setTitleText(mTitle);
        addView(mTitleTextView, params);
    }


    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }


    public void setTitleText(CharSequence title) {
        mTitle = title;
        if (mTitleTextView != null) {
            mTitleTextView.setText(mTitle);
        }
    }

    public void setTitleText(int resId) {
        mTitle = getResources().getString(resId);
        if (mTitleTextView != null) {
            mTitleTextView.setText(mTitle);
        }
    }

    public void setRightButtonText(CharSequence rightButtonText) {
        this.mRightButtonText = rightButtonText;
        if (mShowRightButton) {
            if (!TextUtils.isEmpty(mRightButtonText)) {
                getMenu().getItem(0).setVisible(true);
                getMenu().getItem(0).setIcon(null);
                getMenu().getItem(0).setTitle(mRightButtonText.toString());
            }
        }
    }

    public void setRightButtonIcon(Drawable drawable) {
        this.mRightButtonIcon = drawable;
        if (mShowRightButton)
            if (mRightButtonIcon != null) {
                getMenu().getItem(0).setVisible(true);
                getMenu().getItem(0).setIcon(mRightButtonIcon);
            }
    }


    public void setRightButtonIcon(int resourceId){
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),resourceId,null);
        setRightButtonIcon(drawable);
    }

    public void setShowRightButton(boolean isShown) {
        this.mShowRightButton = isShown;
        getMenu().clear();
        if (this.mShowRightButton) {
            inflateMenu(R.menu.menu);
            setRightButtonText(mRightButtonText);
            setRightButtonIcon(mRightButtonIcon);
            setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_right_text && mOnRightMenuClickListener != null) {
                        mOnRightMenuClickListener.onRightClick();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void setShowNabButton(boolean showNabButton) {
        this.mNavIsButton = showNabButton;
        if (mNavIsButton){
            setNavigationIcon(mNavButtonIcon);
        }else {
            setNavigationIcon(null);
        }
    }


    public void setOnRightMenuClickListener(
            OnRightMenuClickListener onRightMenuClickListener) {
        mOnRightMenuClickListener = onRightMenuClickListener;
    }


    @Override
    public void setTitleTextColor(int color) {
        mTitleTextView.setTextColor(color);
    }

    @Override
    public void setNavigationIcon(Drawable icon) {
        super.setNavigationIcon(icon);
    }

    @Override
    public void setNavigationIcon(int resId) {
        super.setNavigationIcon(resId);
    }

    @Override
    public void setNavigationOnClickListener(OnClickListener listener) {
        super.setNavigationOnClickListener(listener);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    public interface OnRightMenuClickListener {
        void onRightClick();
    }

}
