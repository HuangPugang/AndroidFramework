package org.hpdroid.base.ui.adapter;

import android.content.Context;
import android.view.View;

import com.hpdroid.base.R;
import org.hpdroid.base.ui.adapter.base.BaseViewHolder;
import org.hpdroid.base.ui.adapter.base.QuickRecyclerAdapter;
import org.hpdroid.base.view.NormalSheetView;

import java.util.List;

/**
 * Created by luoliuqing on 16/10/27.
 */
public class NormalSheetAdapter extends QuickRecyclerAdapter<String> {
	private NormalSheetView.IOnSeetItemClickListener mClickListener;
	public NormalSheetAdapter(Context context, List<String> data, NormalSheetView.IOnSeetItemClickListener listener) {
		super(context, R.layout.recycle_item_text_sheetview, data);
		this.mClickListener = listener;
	}

	@Override
	protected void convert(BaseViewHolder holder, final String item, final int position) {
		holder.setText(R.id.btn_text,item);
		holder.getView(R.id.view_line).setVisibility(position == getItemCount()-1 ?View.GONE :View.VISIBLE);
		holder.getView(R.id.btn_text).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mClickListener != null){
					mClickListener.onSheetItemClick(item,position);
				}
			}
		});
	}
}
