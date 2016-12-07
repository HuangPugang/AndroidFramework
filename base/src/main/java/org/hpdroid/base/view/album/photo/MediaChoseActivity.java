package org.hpdroid.base.view.album.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hpdroid.base.R;
import org.hpdroid.base.view.DesignToolbar;
import org.hpdroid.base.view.album.MultiAlbumFinishEvent;
import org.hpdroid.base.view.album.photo.adapter.FolderAdapter;
import org.hpdroid.base.view.album.photo.adapter.PhotoAdapter;
import org.hpdroid.base.view.album.photo.mode.ImageFloder;
import org.hpdroid.base.view.album.photo.widget.PickConfig;
import org.hpdroid.base.view.album.ucrop.UCrop;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

//import com.huanxiao.base.view.album.photo.adapter.PhotoAdapter;



/**
 * 调用媒体选择库
 * 需要在inten中传递2个参数
 * 1. 选择模式 chose_mode  0  //单选 1多选
 * 2. 选择张数 max_chose_count  多选模式默认 9 张
 */
public class MediaChoseActivity extends AppCompatActivity implements Handler.Callback {//
    public static final String EXTRA_RESULT_DATA="data";
    public static final String KEY_CHOOSE_MODE = "chosemode";
    public static final String KEY_IMAGE_FILE_PATH = "ImageFilePath";
    public static final String KEY_IS_NEED_CROP = "isneedCrop";
    public static final int REQUEST_CODE_CAMERA = 2001;
    public static final int REQUEST_CODE_CROP = 2002;
    public static final int REQUEST_CODE_PREVIEW = 2003;

    public static final int RESULT_CODE_CONFIRM=3001;//预览界面发送的code
    public static final int RESULT_CODE_BACK=3002;//预览界面返回的code

    public List<String> mImageChooseList = new ArrayList<>();
    private int mChooseMode = PickConfig.MODE_SINGLE_PICK;//选择模式

    private boolean isneedCrop = false;//是否需要裁剪
    private int spanCount = 3;//一行显示几张
    private int actionBarcolor;
    private int statusBarcolor;
    private boolean isSquareCrop;
    private UCrop.Options options = null;

    protected File currentFile;//拍照后当前文件
    private boolean isPriview = false;

    private boolean isCropOver = false;

    //——————————————————————————————————————————————————————————————————————
    public int maxChooseCount = 9;//最大值
    public boolean isNeedfcamera = false;//是否需要相机
    private RecyclerView mRecyclerView;//图片展示
    private TextView mTvOpenGallery;//打开相册列表
    //    private ImageView clear;
    private TextView mTvPreview;//预览
    private PhotoAdapter mAdapter;
    private ArrayList<String> mAllImageList = new ArrayList<>();

    private List<String> mCurrentImageList = new ArrayList<>();
    private Handler mHandler;

    private boolean isFolderShow = false;//相册列表是否显示
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();


    private ListPopupWindow mFolderListView;//相册列表
    private FolderAdapter folderAdapter;//相册列表适配器
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_chose);
        EventBus.getDefault().register(this);
        DesignToolbar toobar = (DesignToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toobar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(false);
            tintManager.setTintColor(getResources().getColor(R.color.toolbar_black_color));
        }

        initBundle();

        if (savedInstanceState != null) {
            int mChooseMode = savedInstanceState.getInt(KEY_CHOOSE_MODE);
            if (mChooseMode == PickConfig.MODE_SINGLE_PICK) {
                currentFile = new File(savedInstanceState.getString(KEY_IMAGE_FILE_PATH));
                boolean isneedCrop = savedInstanceState.getBoolean(KEY_IS_NEED_CROP);
                if (isneedCrop && !isCropOver) {
                    sendStarCrop(currentFile.getAbsolutePath());
                } else {
                    Intent intent = new Intent();
                    ArrayList<String> img = new ArrayList<>();
                    img.add(currentFile.getAbsolutePath());
                    intent.putExtra(EXTRA_RESULT_DATA, img);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                insertImage(currentFile.getAbsolutePath());
            } else {
                getImageChoseList().add(currentFile.getAbsolutePath());
                invalidateOptionsMenu();
                insertImage(currentFile.getAbsolutePath());
            }
        }
        initGallery();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent intent = new Intent();
            ArrayList<String> img = new ArrayList<>();
            String crop_path = resultUri.getPath();
            isCropOver = true;
            if (crop_path != null && new File(crop_path) != null) {
                img.add(crop_path);
                intent.putExtra(EXTRA_RESULT_DATA, img);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "截取图片失败", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && (mChooseMode == PickConfig.MODE_SINGLE_PICK)) {
            if (currentFile != null && currentFile.exists() && currentFile.length() > 10) {
                if (isneedCrop && !isCropOver) {
                    sendStarCrop(currentFile.getAbsolutePath());
                } else {
                    insertImage(currentFile.getAbsolutePath());
                    Intent intent = new Intent();
                    ArrayList<String> img = new ArrayList<>();
                    img.add(currentFile.getAbsolutePath());
                    intent.putExtra(EXTRA_RESULT_DATA, img);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                Toast.makeText(MediaChoseActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && (mChooseMode == PickConfig.MODE_MULTIP_PICK)) {

            if (currentFile != null && currentFile.exists() && currentFile.length() > 10) {
                getImageChoseList().add(currentFile.getAbsolutePath());
                invalidateOptionsMenu();
                insertImage(currentFile.getAbsolutePath());
            } else {
                Toast.makeText(MediaChoseActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
            }
        }else if (resultCode== RESULT_OK &&requestCode==REQUEST_CODE_PREVIEW){
            ArrayList<String> list = data.getStringArrayListExtra(EXTRA_RESULT_DATA);
            mImageChooseList.clear();
            mImageChooseList.addAll(list);
            mAdapter.setDataChanged(list);
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 初始化配置值
     */
    private void initBundle(){
        Bundle bundle = getIntent().getBundleExtra(PickConfig.EXTRA_PICK_BUNDLE);
        statusBarcolor = bundle.getInt(PickConfig.EXTRA_STATUS_BAR_COLOR);
        actionBarcolor = bundle.getInt(PickConfig.EXTRA_ACTION_BAR_COLOR);
        spanCount = bundle.getInt(PickConfig.EXTRA_SPAN_COUNT, PickConfig.DEFAULT_SPANCOUNT);
        mChooseMode = bundle.getInt(PickConfig.EXTRA_PICK_MODE, PickConfig.MODE_SINGLE_PICK);
        maxChooseCount = bundle.getInt(PickConfig.EXTRA_MAX_SIZE, PickConfig.DEFAULT_PICKSIZE);
        isneedCrop = bundle.getBoolean(PickConfig.EXTRA_IS_NEED_CROP, false);
        isNeedfcamera = bundle.getBoolean(PickConfig.EXTRA_IS_NEED_CAMERA, false);
        options = bundle.getParcelable(PickConfig.EXTRA_UCROP_OPTIONS);
        isSquareCrop = bundle.getBoolean(PickConfig.EXTRA_IS_SQUARE_CROP);
        if (mChooseMode == PickConfig.MODE_MULTIP_PICK) {
            isneedCrop = false;
        }
    }

    /**
     * 初始化照片列表
     */
    private void initGallery() {
        mHandler = new Handler(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mTvOpenGallery = (TextView) findViewById(R.id.open_gallery);
        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        mTvOpenGallery.setEnabled(false);
        if (mAdapter == null) {
            mAdapter = new PhotoAdapter(this, mCurrentImageList, spanCount, mChooseMode);
            mAdapter.setDir("");
            mAdapter.setNeedCamera(isNeedfcamera);
            mAdapter.setMaxChosenCount(maxChooseCount);
        }
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mTvOpenGallery.setText("所有图片");
        loadAllImages();
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> photos = mAdapter.getChooseImages();
                if (photos.size() == 0)
                    return;
                ArrayList<String> choose = new ArrayList<>();
                for (String s : photos) {
                    choose.add(s);
                }
                PhotoPreviewActivity.launch(MediaChoseActivity.this, choose, choose, "", 0, REQUEST_CODE_PREVIEW,maxChooseCount);

            }
        });

        mTvOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFolderShow) {
                    isFolderShow = false;
                    mFolderListView.dismiss();
                } else {
                    isFolderShow = true;
                    mFolderListView.show();
                }
            }
        });

    }


    public void loadAllImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_MODIFIED};
                Cursor cursor = MediaChoseActivity.this.getContentResolver().query(MediaStore.Images.
                                Media.EXTERNAL_CONTENT_URI, columns,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/gif"},
                        MediaStore.Images.Media.DATE_ADDED + " DESC");
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    String photoPath = cursor.getString(dataColumnIndex);
                    if (photoPath != null && new File(photoPath).exists()) {
                        mAllImageList.add(photoPath);
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                mCurrentImageList.clear();
                mCurrentImageList.addAll(mAllImageList);
                mHandler.sendEmptyMessage(0);
                getImages();
            }
        }).start();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = MediaChoseActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path);
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getParentFile().getAbsolutePath();
                    ImageFloder imageFloder = null;
                    File file = new File(dirPath);
                    if (file != null && file.isDirectory() && file.list().length > 0) {
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            // 初始化imageFloder
                            imageFloder = new ImageFloder();
                            imageFloder.setDir(dirPath);
                            imageFloder.setFirstImagePath(path);
                        }

                        int picSize = file.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String filename) {
                                if (filename == null) {
                                    return false;
                                }
                                if (filename.endsWith(".jpg")
                                        || filename.endsWith(".gif")
                                        || filename.endsWith(".png")
                                        || filename.endsWith(".jpeg"))
                                    return true;
                                return false;
                            }
                        }).length;
                        imageFloder.setCount(picSize);
                        mImageFloders.add(imageFloder);
                    }
                }
                mCursor.close();
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(1);

            }
        }).start();
    }



    public void startPreview(List<String> allImages, String currentimage, List<String> map) {
        if (isneedCrop && !isCropOver) {
            sendStarCrop(currentimage);
        } else {
            ArrayList<String> all = new ArrayList<>();
            int pos = 0;
            for (int i = 0; i < allImages.size(); i++) {
                all.add(allImages.get(i));
                if (allImages.get(i).equals(currentimage)) {
                    pos = i;
                }
            }
            ArrayList<String> choose = new ArrayList<>();
            for (String s : map) {
                choose.add(s);
            }
            PhotoPreviewActivity.launch(this, all, choose, currentimage, pos, REQUEST_CODE_PREVIEW,maxChooseCount);


            invalidateOptionsMenu();
        }
    }

    public List<String> getImageChoseList() {
        return mImageChooseList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_gallery_menu, menu);
        menu.findItem(R.id.menu_photo_delete).setVisible(false);
        if (mImageChooseList.size() < 1) {
            menu.findItem(R.id.menu_photo_count).setEnabled(false);
            menu.findItem(R.id.menu_photo_count).setVisible(false);
            mTvPreview.setText("预览");
            mTvPreview.setTextColor(Color.parseColor("#FF666666"));
            mTvPreview.setEnabled(false);
        } else {

            menu.findItem(R.id.menu_photo_count).setEnabled(true);
            menu.findItem(R.id.menu_photo_count).setVisible(true);
            if (mChooseMode == PickConfig.MODE_MULTIP_PICK) {
                menu.findItem(R.id.menu_photo_count).setTitle("完成(" + mImageChooseList.size() + "/" + maxChooseCount + ")");
            } else {
                menu.findItem(R.id.menu_photo_count).setTitle("完成(1)");
            }
            mTvPreview.setEnabled(true);
            mTvPreview.setTextColor(Color.parseColor("#ffffff"));
            mTvPreview.setText("预览(" + mImageChooseList.size() + ")");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isPriview) {
            } else {
                finish();
            }
        } else if (item.getItemId() == R.id.menu_photo_count) {
            sendImages();
        }



        return super.onOptionsItemSelected(item);
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void sendImages() {
        if (isneedCrop && !isCropOver) {
            if (mImageChooseList.size() == 0) {
                return;
            }
            File file = new File(mImageChooseList.get(0).toString());
            if (!file.exists()) {
                Toast.makeText(this, "获取文件失败", Toast.LENGTH_SHORT).show();
            }
            sendStarCrop(file.getAbsolutePath());
        } else {
            Intent intent = new Intent();
            ArrayList<String> img = new ArrayList<>();

            for (String s : mImageChooseList) {
                img.add(s);
            }
            intent.putExtra(MediaChoseActivity.EXTRA_RESULT_DATA, img);
            setResult(RESULT_OK, intent);
//            EventBus.getDefault().post(new PrintImageEvent(PrintImageEvent.CODE_CONFIRM, img));
            finish();
        }
    }




    public void insertImage(String fileName) {
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    fileName, new File(fileName).getName(),
                    new File(fileName).getName());
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(fileName));
            intent.setData(uri);
            sendBroadcast(intent);
            MediaScannerConnection.scanFile(this, new String[]{fileName}, new String[]{"image/jpeg"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    addCaptureFile(path);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启相机
     */
    public void sendStarCamera() {
        currentFile = getTempFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 开始裁剪
     *
     * @param path
     */
    public void sendStarCrop(String path) {
        UCrop uCrop = UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(new File(getCropFile().getAbsolutePath())));
        if (isSquareCrop) {
            uCrop = uCrop.withAspectRatio(1, 1);
        } else {
            uCrop = uCrop.useSourceImageAspectRatio();
        }
        if (options == null) {
            options = new UCrop.Options();
        }
        options.setStatusBarColor(statusBarcolor);
        options.setToolbarColor(actionBarcolor);
        uCrop.withOptions(options);
        uCrop.start(this);

    }

    /**
     * 获取当前文件
     *
     * @return
     */
    public File getTempFile() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "IMG_" + str + ".jpg");
    }

    public File getCropFile() {
        return new File(getTmpPhotos());
    }

    /**
     * 获取tmp path
     *
     * @return
     */
    public String getTmpPhotos() {
        return new File(getCacheFile(), ".tmpcamara" + System.currentTimeMillis() + ".jpg").getAbsolutePath();
    }

    /**
     * 临时缓存目录
     *
     * @return
     */
    public String getCacheFile() {
        return getDir("post_temp", Context.MODE_PRIVATE).getAbsolutePath();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFile != null && currentFile.exists()) {
            outState.putString(KEY_IMAGE_FILE_PATH, currentFile.getAbsolutePath());
            outState.putInt(KEY_CHOOSE_MODE, mChooseMode);
            outState.putBoolean(KEY_IS_NEED_CROP, isneedCrop);
        }
    }

//    public void onEventMainThread(PrintImageEvent event) {
//        if (event.getCode() == PrintImageEvent.CODE_CONFIRM) {
//            finish();
//        } else if (event.getCode() == PrintImageEvent.CODE_PREVIEW) {
//            mAdapter.setDataChanged(event.getImage());
//            invalidateOptionsMenu();
//        }
//    }

    private void initFolderPop() {
        mFolderListView = new ListPopupWindow(this);
        ImageFloder allimgslist = new ImageFloder();
        allimgslist.setDir("/所有图片");
        allimgslist.setCount(mAllImageList.size());
        if (mAllImageList.size() > 0) {
            allimgslist.setFirstImagePath(mAllImageList.get(0));
        }
        mImageFloders.add(0, allimgslist);
        folderAdapter = new FolderAdapter(mImageFloders, this);
        mFolderListView.setAdapter(folderAdapter);
        int sWidthPix = getResources().getDisplayMetrics().widthPixels;
        mFolderListView.setContentWidth(sWidthPix);
        mFolderListView.setHeight(sWidthPix + 100);
        mFolderListView.setAnchorView(mTvOpenGallery);
        mTvOpenGallery.setEnabled(true);

        mFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageFloder floder = (ImageFloder) parent.getAdapter().getItem(position);
                folderAdapter.setCheck(position);
                if (floder.getName().equals("/所有图片")) {
                    mCurrentImageList.clear();
                    mCurrentImageList.addAll(mAllImageList);
                    mAdapter = new PhotoAdapter(MediaChoseActivity.this, mCurrentImageList, spanCount, mChooseMode);
                    mAdapter.setMaxChosenCount(maxChooseCount);
                    mAdapter.setDir("");
                    mAdapter.setNeedCamera(isNeedfcamera);
                    mRecyclerView.setAdapter(mAdapter);
                    mFolderListView.dismiss();
                    mTvOpenGallery.setText("所有图片");
                } else {
                    File mImgDir = new File(floder.getDir());
                    List<String> ims =
                            Arrays.asList(mImgDir.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    if (filename.endsWith(".jpg") || filename.endsWith(".png")
                                            || filename.endsWith(".jpeg"))
                                        return true;
                                    return false;
                                }
                            }));

                    mCurrentImageList.clear();
                    mCurrentImageList.addAll(ims);
                    /**
                     * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
                     */
                    mAdapter = new PhotoAdapter(MediaChoseActivity.this, mCurrentImageList, spanCount, mChooseMode);
                    mAdapter.setMaxChosenCount(maxChooseCount);
                    mAdapter.setDir(floder.getDir());
                    mAdapter.setNeedCamera(false);
                    mRecyclerView.setAdapter(mAdapter);
                    mTvOpenGallery.setText(floder.getName());
                    mFolderListView.dismiss();
                }
            }
        });

    }

    public void time(String msg) {
        Log.i("milles", msg + System.currentTimeMillis() + "thread" + Thread.currentThread().getName());
    }

    /**
     * 当拍照之后刷新出来拍照的那张照片
     *
     * @param path
     */
    public void addCaptureFile(final String path) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (new File(path) != null && new File(path).exists() && new File(path).length() > 10) {
                    mCurrentImageList.add(0, path);
                    mAllImageList.add(0, path);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            initFolderPop();
        } else {
            mAdapter.notifyDataSetChanged();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isFolderShow){
            mFolderListView.dismiss();
            isFolderShow= false;
        }else {
            super.onBackPressed();
        }

    }

    @Subscribe
    public void onEventMainThread(MultiAlbumFinishEvent event) {
        mImageChooseList.clear();
        mImageChooseList.addAll(event.getList());
        mAdapter.setDataChanged(event.getList());
        invalidateOptionsMenu();
        sendImages();
    }
}
