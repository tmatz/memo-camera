package jp.gr.java_conf.tmatz.memocamera;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.v4.content.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity 
{
	private static final String TAG = "MainActivity";

	private static final int RESULT_CAMERA = 0;

	private ImageButton mImageButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		mImageButton = (ImageButton)findViewById(R.id.mainImageButton1);
		mImageButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					MainActivity.this.takePhoto();
				}
			});
    }

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            View view = window.getDecorView();
            view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
				View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
		setPhotoImage();
	}
	
	private File getImageFile() {
		File imagePath = new File(getCacheDir(), "images");
		if (!imagePath.exists()) {
			imagePath.mkdir();
		}
		File newFile = new File(imagePath, "camera_image.jpg");
		return newFile;
	}
	
	private void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File newFile = getImageFile();
//		if (newFile.exists()) {
//			newFile.delete();
//		}
		Uri contentUri = FileProvider.getUriForFile(this, "jp.gr.java_conf.tmatz.memocamera.fileprovider", newFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		startActivityForResult(intent, RESULT_CAMERA);
	}
	
	private void setPhotoImage() {
		File file = getImageFile();
		if (!file.exists()) {
			mImageButton.setImageBitmap(null);
			return;
		}
		try {
		Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
		if (bmp.getWidth() > bmp.getHeight()) {
			Matrix matrix = new Matrix();				matrix.postRotate(90);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		}
		int width = mImageButton.getWidth();
		int height = mImageButton.getHeight();
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
		mImageButton.setImageBitmap(scaledBitmap);
		} catch (Exception ex) {
			mImageButton.setImageBitmap(null);
		}
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK) {
			setPhotoImage();
        }
    }
}
