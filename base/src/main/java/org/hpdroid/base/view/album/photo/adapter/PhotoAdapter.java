package org.hpdroid.base.view.album.photo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hpdroid.base.R;
import org.hpdroid.base.util.AlertDialogUtil;
import org.hpdroid.base.view.album.photo.MediaChoseActivity;
import org.hpdroid.base.view.album.photo.widget.PickConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqiong on 15/3/27.
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public int maxChoseCount ;
    private LayoutInflater inflater;
    private List<String> mAllImages;
    private Context mContext;
    public static RecyclerView.LayoutParams params;
    private List<String> mChooseImages;
    public int currentChoseMode;
    private int sWidthPix;
    private int spancount;

    public static int TYPE_IMAGE = 10;
    public static int TYPE_CAMERA = 11;

    private String imgdir;
    private boolean isNeedCamera = true;

    public PhotoAdapter(Context mContext, List<String> allImages, int spancount, int chosemode) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.spancount = spancount;
        this.mAllImages = allImages;
        sWidthPix = mContext.getResources().getDisplayMetrics().widthPixels;
        params = new RecyclerView.LayoutParams(sWidthPix / spancount, sWidthPix / spancount);
        currentChoseMode = chosemode;
        mChooseImages = ((MediaChoseActivity) mContext).getImageChoseList();
    }


    public void setDataChanged(ArrayList<String> list) {
        mChooseImages.clear();
        for (String s : list) {
            mChooseImages.add(s);
        }
        notifyDataSetChanged();
    }

    public List<String> getChooseImages() {
        return mChooseImages;
    }

    public void setMaxChosenCount(int max_chose_count) {
        this.maxChoseCount = max_chose_count;
    }


    public void setDir(String dir) {
        imgdir = dir;
    }


    public String getDir() {
        if (imgdir.equals("")) {
            return "";
        } else {
            return imgdir + "/";
        }
    }


    /**
     * 是否在第一个item现实相机
     *
     * @param isNeedCamera
     */
    public void setNeedCamera(boolean isNeedCamera) {
        this.isNeedCamera = isNeedCamera;
    }

    /**
     * DIP转换成PX
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_CAMERA) {
            holder = new CameraViewHolder(inflater.inflate(R.layout.list_photo_camera_item, parent, false));
        } else {
            holder = new ImageViewHolder(inflater.inflate(R.layout.list_photo_image_item, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_IMAGE) {
            final ImageViewHolder ivholder = (ImageViewHolder) holder;
            final String imageDir = getDir() + getItem(position);
            displayImage(imageDir, ivholder.iv_image);
            if (currentChoseMode == PickConfig.MODE_MULTIP_PICK) {
                ivholder.checkBox.setVisibility(View.VISIBLE);
                ivholder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChooseImages.contains(imageDir)) {
                            mChooseImages.remove(imageDir);
                            ivholder.alpha_view.setVisibility(View.GONE);
                            ivholder.checkBox.setSelected(false);
                        } else {
                            if (mChooseImages.size() >= maxChoseCount) {
                                AlertDialogUtil.showDialog(mContext,"提示","你最多只能选择" + maxChoseCount + "张照片","","确定",null,null);

                                return;
                            } else {
                                mChooseImages.add(imageDir);
                                ivholder.alpha_view.setVisibility(View.VISIBLE);
                                ivholder.checkBox.setSelected(true);
                            }
                        }
                        ((Activity) mContext).invalidateOptionsMenu();
                    }
                });
                if (mChooseImages.contains(imageDir)) {
                    ivholder.alpha_view.setVisibility(View.VISIBLE);
                    ivholder.checkBox.setSelected(true);
                } else {
                    ivholder.alpha_view.setVisibility(View.GONE);
                    ivholder.checkBox.setSelected(false);
                }
                ivholder.alpha_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MediaChoseActivity) mContext).startPreview(mAllImages, imageDir, getChooseImages());
                    }
                });

                ivholder.iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MediaChoseActivity) mContext).startPreview(mAllImages, imageDir, getChooseImages());
                    }
                });
            } else {
                ivholder.checkBox.setVisibility(View.GONE);
                ivholder.alpha_view.setVisibility(View.GONE);
                ivholder.iv_image.setClickable(true);
                ivholder.iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getChooseImages().clear();
                        getChooseImages().add(imageDir);
                        ((MediaChoseActivity) mContext).sendImages();
                    }
                });
            }
        } else {
            CameraViewHolder holder1 = (CameraViewHolder) holder;
            holder1.camera_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentChoseMode == PickConfig.MODE_MULTIP_PICK) {
                        if (getChooseImages().size() < maxChoseCount) {
                            ((MediaChoseActivity) mContext).sendStarCamera();
                        } else {
                            Toast.makeText(mContext, "你最多只能选择" + maxChoseCount + "张照片", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (getChooseImages().size() > 0) {
                            getChooseImages().clear();
                        }
                        ((MediaChoseActivity) mContext).sendStarCamera();
                    }
                }
            });
        }
    }

    /**
     * imageScaleType:
     * EXACTLY :图像将完全按比例缩小的目标大小
     * EXACTLY_STRETCHED:图片会缩放到目标大小完全
     * IN_SAMPLE_INT:图像将被二次采样的整数倍
     * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
     * NONE:图片不会调整
     *
     * @param url
     * @param view
     */
    public void displayImage(String url, ImageView view) {
        Glide.with(mContext).load(url).
                centerCrop()
                .crossFade()
                .override(sWidthPix / spancount, sWidthPix / spancount)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.img_loadfaild)
                .into(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isNeedCamera) {
            return TYPE_CAMERA;
        }
        return TYPE_IMAGE;
    }

    public String getItem(int postion) {
        int realPos = postion;
        if (isNeedCamera) {
            realPos = postion - 1;
        }
        if (realPos >= mAllImages.size()) return "";
        return mAllImages.get(realPos);
    }

    @Override
    public int getItemCount() {
        if (isNeedCamera) {
            return mAllImages.size() + 1;
        }
        return mAllImages.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        ImageButton checkBox;
        View alpha_view;

        public ImageViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(params);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            alpha_view = itemView.findViewById(R.id.alpha_view);
            checkBox = (ImageButton) itemView.findViewById(R.id.checkimages);
        }
    }

    public static class CameraViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout camera_lin;

        public CameraViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(params);
            camera_lin = (LinearLayout) itemView.findViewById(R.id.camera_lin);
        }
    }


}
