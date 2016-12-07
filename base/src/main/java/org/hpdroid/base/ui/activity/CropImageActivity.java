package org.hpdroid.base.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.hpdroid.base.R;
import org.hpdroid.base.view.ClipImageLayout;
import org.hpdroid.base.view.DesignToolbar;
import org.hpdroid.util.StatusBarHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by luoliuqing on 16/10/27.
 * 图片裁剪
 */
public class CropImageActivity extends AppCompatActivity {
	public static final int DEFAULT_SIZE = 600;
	private ClipImageLayout mClipImageLayout;
	private DesignToolbar mToolBar;
	private int outputX = DEFAULT_SIZE;
	private int outputY = DEFAULT_SIZE;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_image);
		StatusBarHelper.setColor(this, ContextCompat.getColor(this, R.color.black));
		Intent intent = getIntent();
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		mToolBar = (DesignToolbar) findViewById(R.id.toolbar);
		String filePath = intent.getStringExtra("from");
		File photoFile = new File(filePath);
		Bitmap bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
		mClipImageLayout.setImageBitmap(bmp);
		outputX = getIntent().getIntExtra("outputX", DEFAULT_SIZE);
		outputY = getIntent().getIntExtra("outputY", DEFAULT_SIZE);
		mClipImageLayout.setOutputX(outputX);
		registerListener();
	}

	private void registerListener(){
		mToolBar.setOnRightMenuClickListener(new DesignToolbar.OnRightMenuClickListener() {
			@Override
			public void onRightClick() {
				Bitmap bitmap = mClipImageLayout.clip();
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte [] bitmapByte =baos.toByteArray();
				getIntent().putExtra("data", bitmapByte);
				setResult(RESULT_OK, getIntent());
				finish();
			}
		});
	}
}
