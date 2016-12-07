package org.hpdroid.base.common.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hpdroid.base.R;
import org.hpdroid.base.common.bean.Banner;
import org.hpdroid.base.net.APIService;
import org.hpdroid.base.net.HttpClient;
import org.hpdroid.base.view.AspectKeptContainer;
import org.hpdroid.base.view.CirclePageIndicator;
import org.hpdroid.base.view.LoopViewPager;
import org.hpdroid.router.Router;
import org.hpdroid.util.GlideHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 通用广告位
 */
public class BannerFragment extends Fragment {
    private static final String EXTRA_POSITION = "extra_position";
    private static final String EXTRA_ADPLACE_TYPE = "extra_adplace_type";

    private View mView;
    private AspectKeptContainer slideViewContainer;
    private LoopViewPager slideViewPager;
    private CirclePageIndicator indicator;
    private TextView mTvError;
    private TopImageAdapter imgAdapter;

    private boolean isError;
    private boolean isRefreshHeight;
    private int mPosition = 0;//广告位置
    private int mAdPlaceType = 1;//类别

    public IUViewCreatedListener mListener;

    /**
     * @param position     广告位置（0-店铺首页轮播、1-店铺入口列表、2-夜猫店banner、3-零食盒banner  4-商家中心）
     * @param adplace_type 广告位类型（1-banner、2-闪屏、3-弹窗）
     * @return
     */
    public static BannerFragment newInstance(int position, int adplace_type) {
        BannerFragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);
        args.putInt(EXTRA_ADPLACE_TYPE, adplace_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(EXTRA_POSITION);
            mAdPlaceType = getArguments().getInt(EXTRA_ADPLACE_TYPE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_common_banner, container, false);
        slideViewContainer = (AspectKeptContainer) mView.findViewById(R.id.viewPagerContainer);
        slideViewPager = (LoopViewPager) mView.findViewById(R.id.viewPager);
        slideViewContainer.setVisibility(View.GONE);
        mTvError = (TextView) mView.findViewById(R.id.tv_error);


        indicator = (CirclePageIndicator) mView.findViewById(R.id.indicator);
        indicator.setEnabled(false);
        indicator.setOrientation(0);
        indicator.setCentered(true);
        indicator.setVisibility(View.GONE);
//        indicator.setPageColor(getResources().getColor(R.color.white));

        mTvError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isError) {
                    mTvError.setText("正在刷新，请稍后。");
                    requestSlidInfo();
                }
            }
        });
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //请求网络
//        requestSlidInfo();

        getHeadViewHeightOnCreate();
        refreshSlidinfo();
    }

    public void refreshSlidinfo() {
        requestSlidInfo();
    }


    public void requestSlidInfo() {
        isRefreshHeight = false;
        HttpClient.getAPIService(APIService.class)
                .tngou(new HashMap<String, String>())
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Banner>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        isError = true;
                        if (mListener != null) {
                            mListener.created(true, 0, mView);
                            isRefreshHeight = false;
                        }
                    }

                    @Override
                    public void onNext(List<Banner> banner) {
                        if (banner != null && banner.size() > 0) {
                            mView.setVisibility(View.VISIBLE);
                            imgAdapter = new TopImageAdapter(getActivity(), slideViewPager);
                            imgAdapter.setSlides(banner);
                            Banner slideItem = banner.get(0);
                            if (slideItem.getImageWidth() > 0 && slideItem.getImageHeight() > 0) {
                                slideViewContainer.mHeight = slideViewContainer.mWidth * slideItem.getImageHeight() / slideItem.getImageWidth();
                                slideViewContainer.requestLayout();
                                isRefreshHeight = true;
                            }

                            slideViewPager.setAdapter(imgAdapter);
                            indicator.setViewPager(slideViewPager, 0);
                            indicator.setOnPageChangeListener(mPagerChangeLinsener);

                            slideViewContainer.setVisibility(View.VISIBLE);
                            indicator.setVisibility(View.VISIBLE);


                            runAutoScroll();
                        } else {
                            mView.setVisibility(View.GONE);
                            if (mListener != null) {
                                mListener.created(true, 0, mView);
                                isRefreshHeight = false;
                            }
                        }
                        mTvError.setVisibility(View.GONE);
                        isError = false;
                    }
                });


    }

    private void getHeadViewHeightOnCreate() {
        ViewTreeObserver vto = mView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (isRefreshHeight) {
                    int height = mView.getHeight();
                    if (height > 0) {
                        //获取到宽度和高度后，可用于计算
                        if (mListener != null) {
                            mListener.created(true, height, mView);
                            isRefreshHeight = false;
                        }
                    } else {
                        if (mListener != null) {
                            mListener.created(true, 0, mView);
                            isRefreshHeight = false;
                        }
                    }
                }
                return true;
            }
        });
    }

    public class TopImageAdapter extends PagerAdapter {
        Context context;
        ViewGroup parent;

        public TopImageAdapter(Context context, ViewGroup parent) {
            this.context = context;
            this.parent = parent;
            initViews();

        }

        private List<Banner> slides = new ArrayList<Banner>();
        private ImageView[] mImageViews;


        public void setSlides(List<Banner> slides) {
            this.slides.clear();
            this.slides.addAll(slides);
            initViews();
            notifyDataSetChanged();
            if (slides != null && slides.size() > 0) {
                slideViewPager.setCurrentItem(0);
            }
        }

        public void initViews() {
            mImageViews = new ImageView[this.slides.size()];
            for (int i = 0; i < mImageViews.length; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(param);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mImageViews[i] = imageView;
            }
        }

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews[position];
            final Banner mSliteItem = slides.get(position);
            GlideHelper.display(mSliteItem.getUrl(), imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(mSliteItem.getUrl())) {
                        Router.open(mSliteItem.getUrl());
                    }
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }
    }

    private LoopViewPager.OnPageChangeListener mPagerChangeLinsener = new LoopViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mAutoScrollTask != null && (mAutoScrollTask.getStatus() == AsyncTask.Status.RUNNING || !mAutoScrollTask.isCancelled())) {
                mAutoScrollTask.cancel(true);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            indicator.setCurrentItem(slideViewPager.getCurrentItem());
            runAutoScroll();
        }
    };
    private AsyncTask<Void, Void, Void> mAutoScrollTask;

    private void runAutoScroll() {

        mAutoScrollTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                int currentPosition = slideViewPager.getCurrentItem();
                if (currentPosition == imgAdapter.getCount() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                indicator.setCurrentItem(currentPosition);
                slideViewPager.setCurrentItem(currentPosition, true);
                super.onPostExecute(aVoid);
            }
        };
        mAutoScrollTask.execute();
    }


    public interface IUViewCreatedListener {
        void created(boolean isSuccess, int height, View view);
    }

    public void setViewCreateListener(IUViewCreatedListener mInstener) {
        this.mListener = mInstener;
    }
}
