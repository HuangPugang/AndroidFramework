package org.hpdroid.base.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hpdroid.base.R;
import org.hpdroid.base.base.mvp.IBaseView;
import org.hpdroid.base.common.bean.EventMessage;
import org.hpdroid.base.constant.EventConst;
import org.hpdroid.base.view.DesignToolbar;
import org.hpdroid.base.view.loading.LoadingView;
import org.hpdroid.util.StringHelper;
import org.hpdroid.util.ToastHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by paul on 16/10/11.
 */

public abstract class BaseActivity extends AppCompatActivity implements EventConst, IBaseView {
    protected Context mContext;
    protected Application mApplication;
    private ProgressDialog mProgressDialog;
    protected DesignToolbar mToolbar;

    protected LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isBindEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mContext = this;
        mApplication = getApplication();
        initPresenter();


        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
            initDataBinding();
            initView();
            initListener();
        } else {
            initDataBinding();
        }
        if (getIntent().getExtras() != null) {
            initBundleExtras(getIntent().getExtras());
        }
        initData(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加友盟统计相关调用
        MobclickAgent.onResume(this);    //添加session统计，用来计算启动次数
    }

    @Override
    protected void onPause() {
        super.onPause();
        //添加友盟统计相关调用
        MobclickAgent.onPause(this);    //添session统计
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (isBindEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View loadingView = fvById(R.id.loading_view);
        if (loadingView != null && loadingView instanceof LoadingView) {
            mLoadingView = (LoadingView) loadingView;
        }
        View toolbar = fvById(R.id.toolbar);
        if (toolbar != null && toolbar instanceof DesignToolbar) {
            mToolbar = (DesignToolbar) toolbar;

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackNavigationClick();
                }
            });

            if (setNavigationIcon() != 0) {
                mToolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), setNavigationIcon(), null));
            }


        }
    }

    protected void initDataBinding() {
    }

    /**
     * 获取布局layout
     */
    protected abstract int getLayoutId();

    /**
     * 初始化presenter
     */
    protected abstract void initPresenter();


    /**
     * @param extras 获取传过来的数据
     */
    public abstract void initBundleExtras(Bundle extras);


    /**
     * 根据id查找view
     */
    protected abstract void initView();

    /**
     * 注册事件监听
     */
    protected abstract void initListener();

    /**
     * 初始化presenter
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 是否注册EventBus
     */
    protected abstract boolean isBindEventBus();


    /**
     * 显示loading加载框
     */
    @Override
    public void showLoading(String message) {
        if (message == null || TextUtils.isEmpty(message)) {
            mProgressDialog.setMessage("正在加载...");
        } else {
            mProgressDialog.setMessage(message);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void showLoading(int messageId) {
        showLoading(getString(messageId));
    }

    /**
     * toast 消息
     *
     * @param msg
     */
    @Override
    public void showToast(String msg) {
        ToastHelper.showMessage(msg);
    }

    /**
     * toast 消息
     *
     * @param msgId
     */
    @Override
    public void showToast(int msgId) {
        ToastHelper.showMessage(msgId);
    }

    @Override
    public Context getCtx() {
        return this;
    }

    /**
     * 隐藏loading框
     */

    @Override
    public void hideLoading() {
        mProgressDialog.dismiss();
    }


    /**
     * eventbus 回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusListener(EventMessage message) {

    }

    /**
     * 发送eventbus消息
     *
     * @param eventMessage
     */
    protected void sendEventBus(int eventMessage) {
        EventMessage message = new EventMessage(eventMessage);
        EventBus.getDefault().post(message);
    }
    /**
     * 发送eventbus消息
     *
     * @param eventMessage
     */
    protected <T> void sendEventBus(int eventMessage,T t) {
        EventMessage message = new EventMessage(eventMessage,t);
        EventBus.getDefault().post(message);
    }
    /**
     * 获取view
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T fvById(int id) {
        // return返回view时,加上泛型T
        return (T) findViewById(id);
    }

    /**
     * toolbar 返回键
     */
    protected void onBackNavigationClick() {
        onBackPressed();
    }

    /**
     * 设置回退导航栏的icon
     *
     * @return
     */
    protected int setNavigationIcon() {
        return 0;
    }

    /**
     * 设置标题
     *
     * @param text
     */
    protected void setNavigationText(String text) {
        if (mToolbar != null && mToolbar instanceof DesignToolbar) {
            mToolbar.setTitleText(text);
        }
    }

    /**
     * 判断activity是否存在
     *
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T extends Activity> boolean isActivityExist(Class<T> clazz) {
        boolean isExist = false;
        ActivityManager mAm = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            if (rti.baseActivity.getClassName().equals(clazz.getName())) {
                isExist = true;
            }
        }
        return isExist;
    }


    protected void backToHome() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    public void exitProgram() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        android.os.Process.killProcess(android.os.Process.myPid());
    }



    protected boolean isEmpty(String content) {
        if (TextUtils.isEmpty(content)) {
            return true;
        } else {
            return false;
        }
    }


    protected boolean isEmpty(EditText editText) {
        if (StringHelper.isEmpty(editText)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示输入法
     *
     * @param view
     */
    protected void showInputSoftware(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏输入法
     */
    protected void hideInputSoftware(View view) {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 设置状态栏颜色
     *
     * @return
     */
    protected int getStatusBarColor() {
        return R.color.white;
    }

}
