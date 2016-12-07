package org.hpdroid.base.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hpdroid.base.R;
import org.hpdroid.base.constant.API;
import org.hpdroid.base.constant.Const;
import org.hpdroid.base.view.DesignToolbar;
import org.hpdroid.base.contorller.UrlSelectorManager;
import org.hpdroid.util.SPHelper;


/**
 * Created by paul on 16/10/11.
 */

public class URLSelectorActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView mLvType;
    public static final String SP_URL_TYPE = "sp_url_type";
    private int[] mStrIds = new int[]{R.string.environment_production, R.string.environment_stage, R.string.environment_test, R.string.environment_qa};
    private int[] mTypes = new int[]{UrlSelectorManager.URL_TYPE_MAIN, UrlSelectorManager.URL_TYPE_STAGE, UrlSelectorManager.URL_TYPE_TEMAI, UrlSelectorManager.URL_TYPE_QA};
    private MyListAdapter mAdapter;
    private int mSelectedUrlType;
    private int mLastSelectedUrlType;
    private DesignToolbar mToolbar;

    public static void start(Context context) {
        Intent starter = new Intent(context, URLSelectorActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_selector);
        mSelectedUrlType = SPHelper.getInt(SP_URL_TYPE, Const.DEBUG ? UrlSelectorManager.URL_TYPE_TEMAI : UrlSelectorManager.URL_TYPE_MAIN);
        mLastSelectedUrlType = mSelectedUrlType;
        initView();
    }

    private void initView() {
        mLvType = (ListView) findViewById(R.id.lv_type);
        mAdapter = new MyListAdapter();
        mLvType.setAdapter(mAdapter);
        mLvType.setOnItemClickListener(this);
        mToolbar = (DesignToolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mSelectedUrlType = mTypes[i];
        mAdapter.notifyDataSetChanged();
    }

    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mStrIds.length;
        }

        @Override
        public Integer getItem(int position) {
            return mStrIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listitem_url_selector, parent, false);
            TextView mTitleTextView = (TextView) convertView.findViewById(R.id.url_item_title);
            TextView mSelectedTextView = (TextView) convertView.findViewById(R.id.url_item_right);

            if (mTypes[position] == mSelectedUrlType) {
                mSelectedTextView.setVisibility(View.VISIBLE);
            } else {
                mSelectedTextView.setVisibility(View.GONE);
            }
            mTitleTextView.setText(getResources().getString(getItem(position)));

            return convertView;
        }

    }

    @Override
    protected void onDestroy() {
        if (mLastSelectedUrlType != mSelectedUrlType) {
            //地址改变，更新token
            API.SERVER_URL = UrlSelectorManager.getServiceUrl(mSelectedUrlType);
            SPHelper.saveInt(SP_URL_TYPE, mSelectedUrlType);
        }
        super.onDestroy();
    }
}
