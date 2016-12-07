package org.hpdroid.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hpdroid.base.R;


public class AspectKeptContainer extends RelativeLayout {

	public int mWidth;
	public int mHeight;
	private final int mDebguResId;
	private ViewGroup.LayoutParams mLayoutParams = null;
	private float mHeightScale ;

	public AspectKeptContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectKeptContainer);
		Drawable logo = a.getDrawable(R.styleable.AspectKeptContainer_src);
		mDebguResId = a.getResourceId(R.styleable.AspectKeptContainer_src, -1);
		float aspect = a.getFloat(R.styleable.AspectKeptContainer_aspect, 1);
		mHeightScale = a.getFloat(R.styleable.AspectKeptContainer_height_scale, 0);
		if (logo != null) {
			mWidth = logo.getIntrinsicWidth();
			mHeight = logo.getIntrinsicHeight();
		} else {
			mWidth = 1000;
			mHeight = (int) (mWidth * aspect);
		}
		a.recycle();
	}

	public void setSrcResource(int resID) {
		Drawable logo = getResources().getDrawable(resID);
		if (logo != null) {
			mWidth = logo.getIntrinsicWidth();
			mHeight = logo.getIntrinsicHeight();
			requestLayout();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;
		int height = 0;
		int mode = MeasureSpec.EXACTLY;
		if (mLayoutParams == null) {
			mLayoutParams = getLayoutParams();
		}

		if(mHeightScale!=0){
			width = MeasureSpec.getSize(widthMeasureSpec);
			height = MeasureSpec.getSize(heightMeasureSpec);
			height = (int)(height*mHeightScale);
		}else{
			if (mWidth != 0 && mHeight != 0) {
				if ((mLayoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT ||
					 mLayoutParams.width == 0
					) ||
					(mLayoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT &&
					 mLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)) //Android太2B了！！！
				{
					width = MeasureSpec.getSize(widthMeasureSpec);
					height = width * mHeight
							/ mWidth;
				} else if ( mLayoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT &&
							(mLayoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT ||
							 mLayoutParams.height == 0)
							)
				{
					height = MeasureSpec.getSize(heightMeasureSpec);
					width = height * mWidth
							/ mHeight;
				} else if ( mLayoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT &&
						(mLayoutParams.height > 0)
						)
				{
					height = MeasureSpec.getSize(heightMeasureSpec);
					width = height * mWidth
							/ mHeight;
				} else if ( mLayoutParams.width > 0 &&
						(mLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
						)
				{
					width = MeasureSpec.getSize(widthMeasureSpec);
					height = width * mHeight
							/ mWidth;
				} else {
					throw new UnsupportedOperationException("res="+ Integer.toHexString(mDebguResId)+" width="+mLayoutParams.width+" height="+mLayoutParams.height);
				}
			}
		}
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, mode), MeasureSpec.makeMeasureSpec(height, mode));
	}
}
