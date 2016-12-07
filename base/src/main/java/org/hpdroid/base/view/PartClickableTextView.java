package org.hpdroid.base.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.hpdroid.base.R;


/**
 * Created by LiuJiangHao on 15/7/22.
 */
public class PartClickableTextView extends TextView {
    private final static int NONE_CLICKABLE_TEXT_STYLE = 0x06;
    private boolean isInit = false;

    public enum ClickableTextStyle {
        undlerline(0x00), url(0x01), phone(0x02), italic(0x03), bold(0x04), strikethrough(0x05), none(0x06);
        int value;

        ClickableTextStyle(int value) {
            this.value = value;
        }

        public static ClickableTextStyle getInstance(int value) {
            switch (value) {
                case 0x00:
                    return undlerline;
                case 0x01:
                    return url;
                case 0x02:
                    return phone;
                case 0x03:
                    return italic;
                case 0x04:
                    return bold;
                case 0x05:
                    return strikethrough;
                default:
                    return none;
            }
        }
    }

    private OnPartTextClickListener mSpanClickListener;
    private ClickableTextStyle mClickableTextStyle;
    private int mClickableTextColor;
    private String mClickableText;
    private float mClickableTextSize;
    private String mClickableValue;
    private boolean mIsPartClickable;
    private SpannableString mSpannable;

    public PartClickableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PartClickableTextView);
        try {
            mClickableText = typedArray.getString(R.styleable.PartClickableTextView_clickable_text);
            mClickableTextColor = typedArray.getColor(R.styleable.PartClickableTextView_clickable_text_color, getCurrentTextColor());
            mClickableTextSize = typedArray.getDimension(R.styleable.PartClickableTextView_clickable_text_size, getTextSize());
            mClickableTextStyle = ClickableTextStyle.getInstance(typedArray.getInt(R.styleable.PartClickableTextView_clickable_text_style, -NONE_CLICKABLE_TEXT_STYLE));
            mClickableValue = typedArray.getString(R.styleable.PartClickableTextView_clickable_value);
            mIsPartClickable = typedArray.getBoolean(R.styleable.PartClickableTextView_ispart_text_clickable, true);
        } finally {
            typedArray.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit) {
            initClickTextUI();
            isInit = true;
        }
    }

    public void initClickTextUI() {
        mSpannable = new SpannableString(getText().toString());
        int startIndex = 0, endIndex = 0;
        String originalText = getText().toString();
        if (TextUtils.isEmpty(mClickableText) || TextUtils.isEmpty(originalText)) {
            return;
        }
        startIndex = originalText.indexOf(mClickableText);
        if (startIndex < 0) {
            return;
        }
        endIndex = startIndex + mClickableText.length();


        mSpannable.setSpan(new AbsoluteSizeSpan((int) mClickableTextSize), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mIsPartClickable && mSpanClickListener != null) {
            mSpannable.setSpan(new SpannClickable(mSpanClickListener), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        switch (mClickableTextStyle) {
            case undlerline:
                mSpannable.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case url:
                mSpannable.setSpan(new URLSpan(mClickableValue), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case phone:
                mSpannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mClickableText));
                        getContext().startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case italic:
                mSpannable.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case bold:
                mSpannable.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case strikethrough:
                mSpannable.setSpan(new StrikethroughSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case none:
                mSpannable.setSpan(new StyleSpan(Typeface.NORMAL), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }
        mSpannable.setSpan(new ForegroundColorSpan(mClickableTextColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.setText(mSpannable);
        this.setHighlightColor(Color.TRANSPARENT);
        this.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public interface OnPartTextClickListener {
        public void onSpanClick(View widget);
    }

    public class SpannClickable extends ClickableSpan {
        private OnPartTextClickListener clickListener;

        public SpannClickable(OnPartTextClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View widget) {
            clickListener.onSpanClick(widget);
        }
    }

    public void setOnPartTextClickListener(OnPartTextClickListener listener) {
        this.mSpanClickListener = listener;
    }

    public OnPartTextClickListener getmSpanClickListener() {
        return mSpanClickListener;
    }

    public void setmSpanClickListener(OnPartTextClickListener mSpanClickListener) {
        this.mSpanClickListener = mSpanClickListener;
    }

    public ClickableTextStyle getmClickableTextStyle() {
        return mClickableTextStyle;
    }

    public void setmClickableTextStyle(ClickableTextStyle mClickableTextStyle) {
        this.mClickableTextStyle = mClickableTextStyle;
        initClickTextUI();
    }

    public int getmClickableTextColor() {
        return mClickableTextColor;
    }

    public void setmClickableTextColor(int mClickableTextColor) {
        this.mClickableTextColor = mClickableTextColor;
    }

    public String getmClickableText() {
        return mClickableText;
    }

    public void setmClickableText(String mClickableText) {
        this.mClickableText = mClickableText;
    }

    public float getmClickableTextSize() {
        return mClickableTextSize;
    }

    public void setmClickableTextSize(float mClickableTextSize) {
        this.mClickableTextSize = mClickableTextSize;
    }

    public String getmClickableValue() {
        return mClickableValue;
    }

    public void setmClickableValue(String mClickableValue) {
        this.mClickableValue = mClickableValue;
    }

    public boolean ismIsPartClickable() {
        return mIsPartClickable;
    }

    public void setmIsPartClickable(boolean mIsPartClickable) {
        this.mIsPartClickable = mIsPartClickable;
    }

    public SpannableString getmSpannable() {
        return mSpannable;
    }

    public void setmSpannable(SpannableString mSpannable) {
        this.mSpannable = mSpannable;
    }

    public void setClickableText(String str) {
        this.mClickableText = str;
        initClickTextUI();
    }

    public void setTextContent(int textId) {
        setText(textId);
        initClickTextUI();
    }

    public void setTextContent(String text) {
        setText(text);
        initClickTextUI();
    }
}
