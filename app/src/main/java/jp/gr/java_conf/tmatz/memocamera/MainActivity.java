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
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mImageButton = (ImageButton)findViewById(R.id.mainImageButton1);
        mImageButton.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View p1)
                {
                    MainActivity.this.takePhoto();
                }
            });

        mImageButton.addOnLayoutChangeListener(new OnLayoutChangeListener()
            {
                @Override
                public void onLayoutChange(View p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9)
                {
                    setPhotoImage();
                }
            });
    }

    @Override
    protected void onResume()
    {
        Log.i(TAG, "onResume");
        super.onResume();
//        if (Build.VERSION.SDK_INT >= 19)
//        {
//            Window window = getWindow();
//            View view = window.getDecorView();
//            view.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                View.SYSTEM_UI_FLAG_FULLSCREEN);
//        }
        setPhotoImage();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                deletePhoto();
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private File getImageFile()
    {
        File imagePath = new File(getCacheDir(), "images");
        if (!imagePath.exists())
        {
            imagePath.mkdir();
        }
        File newFile = new File(imagePath, "camera_image.jpg");
        return newFile;
    }

    private void takePhoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File newFile = getImageFile();
//        if (newFile.exists())
//        {
//            newFile.delete();
//        }
        Uri contentUri = FileProvider.getUriForFile(this, "jp.gr.java_conf.tmatz.memocamera.fileprovider", newFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, RESULT_CAMERA);
    }

    private void setPhotoImage()
    {
        File file = getImageFile();
        if (!file.exists())
        {
            mImageButton.setImageBitmap(null);
            return;
        }
        try
        {
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bmp.getWidth() > bmp.getHeight())
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            }
            int width = mImageButton.getWidth();
            int height = mImageButton.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
            mImageButton.setImageBitmap(scaledBitmap);
        }
        catch (Exception ex)
        {
            mImageButton.setImageBitmap(null);
        }
    }

    private void deletePhoto()
    {
        File file = getImageFile();
        if (file.exists())
        {
            file.delete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(TAG, "onActivityResult " + requestCode + " " + resultCode);
        if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK)
        {
            setPhotoImage();
        }
    }
}
