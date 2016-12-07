package org.hpdroid.base.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.hpdroid.base.R;
import org.hpdroid.base.ui.adapter.NormalSheetAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by luoliuqing on 16/10/27.
 *
 */
public class NormalSheetView extends LinearLayout implements View.OnClickListener{
	public View mView;
	protected RecyclerView mRcySheet;
	protected Button mBtnCancel;
	protected BottomSheetLayout mBottomSheetLayout;
	protected Activity mActivity;
	protected IOnSeetItemClickListener mListener;

	public NormalSheetView(Context context,BottomSheetLayout parent) {
		super(context);
		initNormalSheetView((Activity)context,parent);
	}

	public NormalSheetView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NormalSheetView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public interface IOnSeetItemClickListener{
		public void onSheetItemClick(String text,int position);
		public void onCancelClick();
	}

	public void initNormalSheetView(Activity activity, BottomSheetLayout parent) {
		mView = LayoutInflater.from(activity).inflate(R.layout.view_select_img_sheet, parent, false);
		mBottomSheetLayout = parent;
		this.mActivity = activity;
		initView();
		registerListener();
		initData();
		addView(mView);
	}

	private void initView(){
		mRcySheet = (RecyclerView) mView.findViewById(R.id.rcy_sheet);
		mBtnCancel = (Button) mView.findViewById(R.id.btn_cancel);
		mBtnCancel = (Button) mView.findViewById(R.id.btn_cancel);
	}

	private void registerListener(){
		mBtnCancel.setOnClickListener(this);
	}

	private void initData(){
		mRcySheet.setLayoutManager(new LinearLayoutManager(mActivity));
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.btn_cancel){
			mBottomSheetLayout.dismissSheet();
			if (mListener != null){
				mListener.onCancelClick();
			}
		}
	}

	public void setmListener(IOnSeetItemClickListener mListener) {
		this.mListener = mListener;
	}

	public void setData(String[] data){
		List<String> dataList = Arrays.asList(data);
		setData(dataList);
	}

	public void setData(List<String> data){
		NormalSheetAdapter adapter = new NormalSheetAdapter(mActivity,data,mListener);
		mRcySheet.setAdapter(adapter);
	}

	public BottomSheetLayout getmBottomSheetLayout() {
		return mBottomSheetLayout;
	}
}
