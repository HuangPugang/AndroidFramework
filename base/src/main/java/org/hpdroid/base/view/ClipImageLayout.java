package org.hpdroid.base.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by luoliuqing on 16/10/27.
 * 裁剪图片的框
 */
public class ClipImageLayout extends RelativeLayout
{

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;
	private int screenWidth;

	/**
	 * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
	 */
	private int mHorizontalPadding = 200;

	public ClipImageLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		screenWidth = calculateScreenWidth(context);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		/**
		 * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
		 */
//		mZoomImageView.setImageDrawable(getResources().getDrawable(
//				R.drawable.a));

		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);


		// 计算padding的px
//		mHorizontalPadding = (int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
//						.getDisplayMetrics());

	}

	private int calculateScreenWidth(Context context){
		WindowManager wm  = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth();
	}

	/**
	 * 对外公布设置边距的方法,单位为dp
	 *
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
	}

	public void setOutputX(int outputX){
		this.mHorizontalPadding = (screenWidth - outputX)/2;
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}

	/**
	 * 裁切图片
	 *
	 * @return
	 */
	public Bitmap clip()
	{
		return mZoomImageView.clip();
	}

	public void setImageBitmap(Bitmap bitmap){
		this.mZoomImageView.setImageBitmap(bitmap);
	}

}
