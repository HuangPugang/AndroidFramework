package org.hpdroid.base.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by paul on 16/10/11.
 */

public abstract class BaseFragment extends Fragment implements EventConst, IBaseView {
    protected Context mContext = null;
    protected BaseActivity mActivity;

    protected DesignToolbar mToolbar;
    protected LoadingView mLoadingView;


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = (BaseActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isBindEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        initPresenter();
        if (null != extras) {
            initBundleExtras(extras);
        }
        if (getLayoutID() != 0) {
            return inflater.inflate(getLayoutID(), container, false);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        View loadingView = view.findViewById(R.id.loading_view);
//        if (loadingView instanceof LoadingView) {
//            mLoadingView = (LoadingView) loadingView;
//        }
//        View mroot = fvById(view, R.id.toolbar);
//        if (mroot instanceof DesignToolbar) {
//            mToolbar = (DesignToolbar) mroot;
//            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getActivity().finish();
//                }
//            });
//        }

        View loadingView = fvById(view, R.id.loading_view);
        if (loadingView != null && loadingView instanceof LoadingView) {
            mLoadingView = (LoadingView) loadingView;
        }
        View toolbar = fvById(view, R.id.toolbar);
        if (toolbar != null && toolbar instanceof DesignToolbar) {
            mToolbar = (DesignToolbar) toolbar;

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackNavigationClick();
                }
            });

            mToolbar.setOnRightMenuClickListener(new DesignToolbar.OnRightMenuClickListener() {
                @Override
                public void onRightClick() {
                    onRightMenuClick();
                }
            });

            if (getNavigationIcon() != 0) {
                mToolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), getNavigationIcon(), null));
            }
        }

        initDataBinding(view);
        initView(view);
        initListener();
        initData();

    }

    protected void initDataBinding(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBindEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取绑定布局ID
     *
     * @return 返回布局的ID
     */
    public abstract int getLayoutID();

    /**
     * Fragment 获取控件
     */
    public abstract void initView(View view);

    /**
     * 初始化Presenter
     */
    public abstract void initPresenter();

    /**
     * 初始化控件值
     */
    public abstract void initData();


    /**
     * @param extras 获取传过来的数据
     */
    public abstract void initBundleExtras(Bundle extras);


    /**
     * 注册控件监听
     */
    public abstract void initListener();


    /**
     * 是否在此类中绑定Eventbus?
     *
     * @return
     */
    public abstract boolean isBindEventBus();


    /**
     * 显示loading加载框
     */
    @Override
    public void showLoading(String message) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showLoading(message);
        }
    }

    /**
     * 隐藏loading框
     */
    @Override
    public void hideLoading() {
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).hideLoading();
        }
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
    public void showLoading(int messageId) {
        showLoading(getString(messageId));
    }


    @Override
    public Context getCtx() {
        return mActivity;
    }

    protected <T extends View> T fvById(View root, int id) {
        // return返回view时,加上泛型T
        return (T) root.findViewById(id);
    }

    /**
     * 发送eventbus消息
     *
     * @param message
     */
    protected void sendMessage(EventMessage message) {
        EventBus.getDefault().post(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusListener(EventMessage message) {

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
        ActivityManager mAm = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            if (rti.baseActivity.getClassName().equals(clazz.getName())) {
                isExist = true;
            }
        }
        return isExist;
    }


    /**
     * toolbar 返回键
     */
    protected void onBackNavigationClick() {
        mActivity.finish();
    }

    /**
     * toolbar 右侧按钮点击
     */
    protected void onRightMenuClick() {

    }

    /**
     * 设置回退导航栏的icon
     *
     * @return
     */
    protected int getNavigationIcon() {
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

    protected void showNaviBackIcon(boolean isShow) {
        if (mToolbar != null && mToolbar instanceof DesignToolbar) {
            mToolbar.setShowNabButton(isShow);
        }
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
     * @param view
     */
    protected void showInputSoftware(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏输入法
     */
    protected void hideInputSoftware() {
        ((InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
