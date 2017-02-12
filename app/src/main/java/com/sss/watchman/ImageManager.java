package com.sss.watchman;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Interfaces.BaseImage;
import Interfaces.BaseImageSource;
import Interfaces.ImageChangedCallback;

/**
 * This class implements image capture service
 *
 */

//todo fix this
@TargetApi(Build.VERSION_CODES.LOLLIPOP) //camera 2 api was added in API level 21
public class ImageManager implements BaseImageSource, ImageChangedCallback{

    MainActivity mActivity = null;
    final Lock lock = new ReentrantLock();
    final Condition mcaptureCondition  = lock.newCondition();

    AndroidCamera mAndroidCamera = null;
    BaseImage mCurrentImage = null;
    boolean mStopped = false;
    private HandlerThread mCameraThread;

    ImageManager(MainActivity activity)
    {
        mActivity = activity;
    }


    @Override
    public BaseImage getLastImage() {
        return null;
    }

    private void submitJob()
    {
        Handler h = new Handler(mCameraThread.getLooper());
        h.post(()-> startCamera());
    }

    @Override
    public void start(int everySecond) throws InterruptedException {
        startCameraThread();
        submitJob();

    }

    public void startCamera(){
        Handler handler = new Handler(mCameraThread.getLooper());
        handler.post(()->{
            mAndroidCamera = new AndroidCamera(mActivity,this);
            mAndroidCamera.startCapturing();
        });
    }


    @Override
    public void stop() {
        stopCameraThread();
        mStopped = true;
    }

    private void startCameraThread(){
        mCameraThread = new HandlerThread("Camera Background thread");
        mCameraThread.start();
    }

    private  void stopCameraThread() {
    }

    @Override
    public void onImageChanged(BaseImage image) {
        mActivity.onImageChanged(image);
        mCurrentImage = image;
        releaseCamera();
    }

    @Override
    public void onFailure() {
        releaseCamera();
    }

    private void releaseCamera()
    {
        Handler handler = new Handler(mCameraThread.getLooper());
        handler.post(()-> mAndroidCamera.close());
    }
}
