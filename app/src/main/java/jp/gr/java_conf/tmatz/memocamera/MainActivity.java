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
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity 
{
    private static final String TAG = "MainActivity";
    private static final String FILE_PROVIDER_AUTHORITY = "jp.gr.java_conf.tmatz.memocamera.fileprovider";

    private static final int RESULT_CAMERA = 0;

    private ImageView mImageView;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mImageView = (ImageView)findViewById(R.id.mainImageButton1);
        mImageView.setImageMatrix(new Matrix());

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener()
            {
                float mPreviousScale;

                @Override
                public boolean onScale(ScaleGestureDetector detector)
                {
                    float scale = detector.getScaleFactor() / mPreviousScale;
                    mPreviousScale = detector.getScaleFactor();

                    mImageView.getImageMatrix().postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
                    mImageView.invalidate();

                    return super.onScale(detector);
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector)
                {
                    mPreviousScale = 1.0f;
                    return super.onScaleBegin(detector);
                }
            });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onDoubleTap(MotionEvent ev)
                {
                    takePhoto();
                    return super.onDoubleTap(ev);
                }

                @Override
                public boolean onScroll(android.view.MotionEvent e1, android.view.MotionEvent e2, float distanceX, float distanceY)
                {
                    mImageView.getImageMatrix().postTranslate(-distanceX, -distanceY);
                    mImageView.invalidate();
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }
            });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        mGestureDetector.onGenericMotionEvent(event);
        return super.onGenericMotionEvent(event);
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
        Uri contentUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, newFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, RESULT_CAMERA);
    }

    private void setPhotoImage()
    {
        File file = getImageFile();
        if (!file.exists())
        {
            mImageView.setImageURI(null);
            return;
        }
        try
        {
            Uri imageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, getImageFile());
            mImageView.setImageURI(imageUri);
        }
        catch (Exception ex)
        {
            mImageView.setImageURI(null);
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
