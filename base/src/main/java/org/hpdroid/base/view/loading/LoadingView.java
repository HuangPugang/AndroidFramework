package org.hpdroid.base.view.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hpdroid.base.R;


/**
 * 加载的view
 * Created by paul on 16/8/8.
 */
public class LoadingView extends LinearLayout implements ILoadingResult {
    private Drawable mFailedDrawable;
    private String mFailedText;
    private ProgressBar mProgressBar;
    private LinearLayout mLLayoutFailed;


    private TextView mTvFailed;
    private ImageView mIvFailed;

    private OnReloadListener onReloadListener;

    private OnClickListener onClickListener;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context.obtainStyledAttributes(attrs,
                R.styleable.LoadingView), context);

    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context.obtainStyledAttributes(attrs,
                R.styleable.LoadingView), context);
    }


    public void reload(OnReloadListener onReloadListener) {
        this.onReloadListener = onReloadListener;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void init(AttributeSet attrs, TypedArray a, Context context) {
        setGravity(Gravity.CENTER);
        setBackgroundColor(Color.RED);
        View view = LayoutInflater.from(context).inflate(R.layout.view_loading, null);
        mIvFailed = (ImageView) view.findViewById(R.id.iv_failed);
        mTvFailed = (TextView) view.findViewById(R.id.tv_failed);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mLLayoutFailed = (LinearLayout) view.findViewById(R.id.llayout_failed);
        boolean defaultError = false;
        try {
            mFailedDrawable = a.getDrawable(R.styleable.LoadingView_img_failed);
            if (mFailedDrawable != null) {
                mIvFailed.setImageDrawable(mFailedDrawable);
            }
            mFailedText = a.getString(R.styleable.LoadingView_text_failed);
            if (!TextUtils.isEmpty(mFailedText)) {
                mTvFailed.setText(mFailedText);
            }

            int textFailedColor = a.getColor(R.styleable.LoadingView_text_failed_color, 0xFF5A7C9D);
            mTvFailed.setTextColor(textFailedColor);
            defaultError = a.getBoolean(R.styleable.LoadingView_default_error, false);
        } finally {
            a.recycle();
        }


        addView(view);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
        start();

        if (defaultError) {
            failed();
        }


    }

    public void registerListener() {
        mIvFailed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新加载
                if (onReloadListener != null) {
                    onReloadListener.reload();
                    start();
                }
                //普通点击事件
                if (onClickListener != null) {
                    onClickListener.onClick(mIvFailed);
                }
            }
        });

        mTvFailed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onReloadListener != null) {
                    onReloadListener.reload();
                    start();
                }

                if (onClickListener != null) {
                    onClickListener.onClick(mIvFailed);
                }
            }
        });
    }


    @Override
    public void start() {
        setVisibility(View.VISIBLE);
        mLLayoutFailed.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Deprecated
    @Override
    public void stop() {

    }

    @Override
    public void failed() {
        setVisibility(View.VISIBLE);
        mLLayoutFailed.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void success() {
        setVisibility(View.GONE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    public void setErrorText(String error) {
        mTvFailed.setText(error);
    }

    public void setErrorImage(int resId) {
        mIvFailed.setImageResource(resId);
    }

    public interface OnReloadListener {
        void reload();
    }

}
