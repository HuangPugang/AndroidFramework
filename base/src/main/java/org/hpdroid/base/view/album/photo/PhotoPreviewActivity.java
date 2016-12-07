package org.hpdroid.base.view.album.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hpdroid.base.R;
import org.hpdroid.base.util.AlertDialogUtil;
import org.hpdroid.base.view.DesignToolbar;
import org.hpdroid.base.view.album.MultiAlbumFinishEvent;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by paul on 16/5/18.
 */
public class PhotoPreviewActivity extends AppCompatActivity {
    private static final String EXTRA_ALL = "EXTRA_ALL";
    private static final String EXTRA_SELECT = "EXTRA_SELECT";
    private static final String EXTRA_CURRENT_DIR = "EXTRA_CURRENT_DIR";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_MAX_COUNT = "EXTRA_MAX_COUNT";
    private DesignToolbar mToolbar;
    private ArrayList<String> mAllImages = new ArrayList<>();
    private ArrayList<String> mChooseImages = new ArrayList<>();
    private int maxCount ;
    private ViewPager mViewpager;
    private String mCurrentDir;
    private int mCurrentPosition;

    private List<View> mImageViews = new ArrayList<>();
    private MypageAdapter mAdapter;
    private LayoutInflater mInflater;
    private ImageButton mCbImage;
    private TextView mTvCb;

    //    private TextView mTvCurrentPosition;
    public static void launch(Activity activity, ArrayList<String> all, ArrayList<String> select, String current, int position, int requestCode, int maxCount) {
        Intent intent = new Intent();
        intent.setClass(activity, PhotoPreviewActivity.class);
        intent.putStringArrayListExtra(EXTRA_ALL, all);
        intent.putStringArrayListExtra(EXTRA_SELECT, select);
        intent.putExtra(EXTRA_CURRENT_DIR, current);
        intent.putExtra(EXTRA_CURRENT_POSITION, position);
        intent.putExtra(EXTRA_MAX_COUNT, maxCount);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        mToolbar = (DesignToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(false);
            tintManager.setTintColor(getResources().getColor(R.color.toolbar_black_color));
        }


        mInflater = LayoutInflater.from(this);

        mViewpager = (ViewPager) findViewById(R.id.pager);
        mCbImage = (ImageButton) findViewById(R.id.check_image);
        mTvCb = (TextView) findViewById(R.id.tv_select);
//        mTvCurrentPosition = (TextView) findViewById(R.id.tv_current_position);

        if (getIntent().hasExtra(EXTRA_ALL)) {
            mAllImages = getIntent().getStringArrayListExtra(EXTRA_ALL);
        }

        if (getIntent().hasExtra(EXTRA_SELECT)) {
            mChooseImages = getIntent().getStringArrayListExtra(EXTRA_SELECT);
        }
        if (getIntent().hasExtra(EXTRA_CURRENT_DIR)) {
            mCurrentDir = getIntent().getStringExtra(EXTRA_CURRENT_DIR);
        }
        if (getIntent().hasExtra(EXTRA_CURRENT_POSITION)) {
            mCurrentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);
            checkIsChoose(mCurrentPosition);
//            setCurrentPosition();
        }
        if (getIntent().hasExtra(EXTRA_MAX_COUNT)) {
            maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, 0);
        }

        mImageViews.clear();
        for (int i = 0; i < mAllImages.size(); i++) {
            View itemview = mInflater.inflate(R.layout.pageitem_view, null);
            ImageView iv_image = (ImageView) itemview.findViewById(R.id.iv_image);
            displayImage(mAllImages.get(i).toString(), iv_image);
            mImageViews.add(itemview);
        }
        mViewpager.removeAllViews();
        mAdapter = new MypageAdapter(mImageViews);
        mViewpager.setAdapter(mAdapter);
        if (mCurrentPosition > 0 && mCurrentPosition < mAllImages.size()) {
            mViewpager.setCurrentItem(mCurrentPosition, false);
        }


        initListener();
    }

    public void displayImage(String url, ImageView view) {
        Glide.with(this).load(url)
                .placeholder(R.drawable.img_loadfaild)
                .into(view);
    }


    private void initListener() {
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                checkIsChoose(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mCbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkboxClick();
            }
        });
        mTvCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkboxClick();
            }
        });
    }

    private void checkboxClick() {
        if (!mChooseImages.contains(mAllImages.get(mCurrentPosition)) && mChooseImages.size() >= maxCount) {
//            AlertDialogView.getInstance(PhotoPreviewActivity.this)
//                    .withMessage("你最多只能选择"+maxCount+"张照片")
//                    .withTitle(R.string.title_hint)
//                    .withButton2Text(R.string.confirm)
//                    .show();
            AlertDialogUtil.showDialog(
                    this,
                    "提示",
                    "你最多只能选择"+maxCount+"张照片",
                    "","确认",
                    null,
                    null);
        } else {
            if (mChooseImages.contains(mAllImages.get(mCurrentPosition))) {
                mChooseImages.remove(mAllImages.get(mCurrentPosition));
                mCbImage.setSelected(false);
            } else {
                mChooseImages.add(mAllImages.get(mCurrentPosition));
                mCbImage.setSelected(true);
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_preview_menu, menu);

        if (mChooseImages.size() < 1) {
            menu.findItem(R.id.menu_photo_count).setEnabled(false);
            menu.findItem(R.id.menu_photo_count).setVisible(false);
        } else {
            menu.findItem(R.id.menu_photo_count).setEnabled(true);
            menu.findItem(R.id.menu_photo_count).setVisible(true);
            menu.findItem(R.id.menu_photo_count).setTitle("完成(" + mChooseImages.size() + "/" + maxCount + ")");
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_photo_delete) {
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.menu_photo_count) {
            ArrayList<String> img = new ArrayList<>();
            for (String s : mChooseImages) {
                img.add(s);
            }
            finish();
            EventBus.getDefault().post(new MultiAlbumFinishEvent(img));

        }
        return super.onOptionsItemSelected(item);
    }


    public class MypageAdapter extends PagerAdapter {
        List<View> views;

        public MypageAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (views.size() > position) {
                container.removeView(views.get(position));
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    }


    /**
     * 判断是否选中
     *
     * @param position
     */
    private void checkIsChoose(int position) {
        String currentDir = mAllImages.get(position);
        if (mChooseImages.contains(currentDir)) {
            mCbImage.setSelected(true);
        } else {
            mCbImage.setSelected(false);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        ArrayList<String> img = new ArrayList<>();
        for (String s : mChooseImages) {
            img.add(s);
        }
        intent.putExtra(MediaChoseActivity.EXTRA_RESULT_DATA, img);
        setResult(RESULT_OK, intent);
        finish();
    }

}
