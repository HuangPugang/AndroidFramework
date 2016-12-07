package org.hpdroid.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hpdroid.base.R;

/**
 * 个人中心设置的view
 * Created by paul on 16/10/20.
 */

public class SettingLayout extends LinearLayout {
    private ImageView mIvIcon;//图标
    private TextView mTvTitle;//标题
    private PartClickableTextView mPcTvSubTitle;//副标题

    private ImageView mIvArrow;//箭头
    private View mViewPadding;//有间距的view
    private View mViewFull;//没有间距的view


    private Drawable mIconDrawable;//标题图标
    private String mTitleStr; //标题文字
    private int mTitleColor; //标题颜色
    private String mSubTitleStr;//副标题文字
    private int mSubTitleColor;//副标题颜色
    private String mSubTitleClickStr;//副标题变色的文字
    private int mSubTitleClickColor;//副标题点击文字的颜色

    private boolean mIsShowArrow = true;


    private boolean mIsShowViewShort = true;  //是否显示短的下划线
    private boolean mIsShowViewFull = false;//是否显示长的下划线

    public SettingLayout(Context context) {
        super(context);
    }

    public SettingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_setting_layout, null);
        mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mPcTvSubTitle = (PartClickableTextView) view.findViewById(R.id.pctv_sub_title);
        mIvArrow = (ImageView) view.findViewById(R.id.iv_arrow);

        mViewPadding = view.findViewById(R.id.view_padding);
        mViewFull = view.findViewById(R.id.view_full);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SettingLayout);
        try {
            mIconDrawable = a.getDrawable(R.styleable.SettingLayout_title_icon);
            mTitleStr = a.getString(R.styleable.SettingLayout_title_text);
            mTitleColor = a.getColor(R.styleable.SettingLayout_title_color, ContextCompat.getColor(getContext(),R.color.text_black_normal));
            mSubTitleStr = a.getString(R.styleable.SettingLayout_sub_title_text);
            mSubTitleColor = a.getColor(R.styleable.SettingLayout_sub_title_color, ContextCompat.getColor(getContext(),R.color.text_black_light));
            mSubTitleClickStr =a.getString(R.styleable.SettingLayout_sub_title_click_text);
            mSubTitleClickColor = a.getColor(R.styleable.SettingLayout_sub_title_click_color, ContextCompat.getColor(getContext(),R.color.text_black_light));
            mIsShowArrow = a.getBoolean(R.styleable.SettingLayout_is_show_arrow, true);

            mIsShowViewShort = a.getBoolean(R.styleable.SettingLayout_is_show_view_short, true);

            mIsShowViewFull = a.getBoolean(R.styleable.SettingLayout_is_show_view_full, false);


            mIvIcon.setImageDrawable(mIconDrawable);
            mTvTitle.setText(mTitleStr);
            mTvTitle.setTextColor(mTitleColor);

            mPcTvSubTitle.setText(mSubTitleStr);
            mPcTvSubTitle.setTextColor(mSubTitleColor);
            mPcTvSubTitle.setmClickableText(mSubTitleClickStr);
            mPcTvSubTitle.setmClickableTextColor(mSubTitleClickColor);

            mIvArrow.setVisibility(mIsShowArrow ? View.VISIBLE : View.GONE);
            mViewPadding.setVisibility(mIsShowViewShort ? View.VISIBLE : View.GONE);
            mViewFull.setVisibility(mIsShowViewFull ? View.VISIBLE : View.GONE);

        } finally {
            a.recycle();
        }
        addView(view);
    }

    /**
     * 设置副标题文字
     *
     * @param subTitle
     */
    public void setSubTitle(String subTitle) {
        this.mSubTitleStr = subTitle;
        mPcTvSubTitle.setText(mSubTitleStr);
    }

    /**
     * 设置副标题文字颜色
     *
     * @param subTitleColor
     */
    public void setSubTitleColor(int subTitleColor) {
        this.mSubTitleColor = subTitleColor;
        mPcTvSubTitle.setTextColor(mSubTitleColor);
    }

    /**
     * 设置副标题点击文字颜色
     *
     * @param subTitleClickColor
     */
    public void setSubTitleClickColor(int subTitleClickColor) {
        this.mSubTitleColor = subTitleClickColor;
        mPcTvSubTitle.setmClickableTextColor(mSubTitleColor);
    }

    public void setmSubTitleClickStr(String mSubTitleClickStr) {
        this.mSubTitleClickStr = mSubTitleClickStr;
        mPcTvSubTitle.setmClickableText(mSubTitleClickStr);
    }

    public void setmIsShowArrow(boolean mIsShowArrow) {
        this.mIsShowArrow = mIsShowArrow;
        mIvArrow.setVisibility(mIsShowArrow ? View.VISIBLE : View.GONE);
    }
}
