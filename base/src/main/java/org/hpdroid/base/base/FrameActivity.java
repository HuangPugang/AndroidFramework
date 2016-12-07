package org.hpdroid.base.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hpdroid.base.R;
import org.hpdroid.base.util.FragmentUtil;


/**
 * 包裹fragment用的activity
 * Created by paul on 16/9/17.
 */
public class FrameActivity extends BaseActivity {
    private static final String EXTRA_FRAGMENT_NAME = "extra_fragment_name";
    private static final String EXTRA_BUNDLE = "extra_bundle";

    /**
     * 启动被activity包裹的fragment
     *
     * @param context
     * @param fragmentName fragment类名
     */
    public static void start(Context context, String fragmentName) {
        Intent starter = new Intent(context, FrameActivity.class);
        starter.putExtra(EXTRA_FRAGMENT_NAME, fragmentName);
        context.startActivity(starter);
    }

    /**
     * 启动被activity包裹的fragment
     *
     * @param context
     * @param fragmentName
     * @param bundle
     */
    public static void start(Context context, String fragmentName, Bundle bundle) {
        Intent starter = new Intent(context, FrameActivity.class);
        starter.putExtra(EXTRA_FRAGMENT_NAME, fragmentName);
        starter.putExtra(EXTRA_BUNDLE, bundle);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_frame_layout;
    }


    @Override
    protected void initView() {
        String fragmentName = getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
        Bundle bundle = getIntent().getBundleExtra(EXTRA_BUNDLE);
        Fragment fragment = FragmentUtil.newFragment(fragmentName, bundle);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initPresenter() {

    }

    @Override
    public void initBundleExtras(Bundle extras) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected boolean isBindEventBus() {
        return false;
    }
}
