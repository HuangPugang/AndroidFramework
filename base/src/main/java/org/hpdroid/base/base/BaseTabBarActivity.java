package org.hpdroid.base.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;


import com.hpdroid.base.R;

import org.hpdroid.base.common.bean.FragmentItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 16/12/6.
 */

public abstract class BaseTabBarActivity extends BaseActivity {
    // 静态变量部分————————————————————————————————————————————————————————————————————————————————————————————————————————————————
    private static final int MAX_UNREAD_COUNT = 99;
    private static final String EXTRA_TAB_INDEX = "extra_tab_index";
    // 成员变量View部分————————————————————————————————————————————————————————————————————————————————————————————————————————————————
    private TabHost mTabHost;
    private TextView mTvOrderNumber;
    // 成员变量部分————————————————————————————————————————————————————————————————————————————————————————————————————————————————

    private LayoutInflater mLayoutInflater;
    private List<FragmentItem> mChooseList;
    private FragmentManager mFragmentManager;
    private List<View> tabViewList = new ArrayList<View>();
    // 默认为第一个
    private int mLastTabIndex = 0;
    //当前标题
    private String mCurrentTag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tab_bar;
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    public void initBundleExtras(Bundle extras) {

    }

    @Override
    protected void initView() {
        mLayoutInflater = LayoutInflater.from(this);
        mChooseList = getFragmentList();
        mFragmentManager = getSupportFragmentManager();
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        initTabHost();
    }


    //初始化tab
    private void initTabHost() {
        mTabHost.clearAllTabs();
        // 添加选项卡导航页
        for (int i = 0; i < mChooseList.size(); i++) {
            mTabHost.addTab(mTabHost
                    .newTabSpec(mChooseList.get(i).getTitle())
                    .setContent(mNullContentFactory)
                    .setIndicator(
                            createTabIndicator(
                                    mChooseList.get(i).getTabResourceId(),
                                    mChooseList.get(i).getTitle())));
        }
        // 添加标签切换监听
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                showFragment(tabId);
            }
        });

        mLastTabIndex = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
        mTabHost.setCurrentTab(mLastTabIndex);
        mCurrentTag = mChooseList.get(mLastTabIndex).getTitle();
        showFragment(mCurrentTag);
    }
    @Override
    protected void initListener() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected boolean isBindEventBus() {
        return false;
    }


    private TabHost.TabContentFactory mNullContentFactory = new TabHost.TabContentFactory() {

        @Override
        public View createTabContent(String tag) {
            return new TextView(BaseTabBarActivity.this);
        }
    };


    //创建tabHost Item
    private LinearLayout createTabIndicator(int imageResId, String str) {
        LinearLayout indicator = (LinearLayout) mLayoutInflater.inflate(
                R.layout.item_tab_host, null);
        TextView tvTitle = (TextView) indicator.findViewById(R.id.tv_title);
        ImageView ivTitle = (ImageView) indicator.findViewById(R.id.iv_title);
        ivTitle.setImageDrawable(getResources()
                .getDrawable(imageResId));
        tvTitle.setTextColor(getResources()
                .getColorStateList(R.color.selector_tab_text_color));
        tvTitle.setText(str);

        tabViewList.add(indicator);

        return indicator;
    }
    //显示指定fragment
    public void showFragment(String tabStr) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (FragmentItem item : mChooseList) {
            if (item.getFragment() != null) {
                transaction.hide(item.getFragment());
            }
        }
        for (FragmentItem item : mChooseList) {
            Fragment f = item.getFragment();
            String title = item.getTitle();
            if (tabStr.equals(title)) {
                if (f == null) {
                    f = getFragment(item.getId());
                    item.setFragment(f);
                    transaction.add(R.id.tabRealContent, f);
                } else {
                    transaction.show(f);
                }

            } else {
                if (f != null) {
                    transaction.hide(f);
                }
            }
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 获得列表
     * @return
     */
    public abstract List<FragmentItem> getFragmentList();

    /**
     * 获得fragment
     * @param index
     * @return
     */
    public abstract Fragment getFragment(int index);
}
