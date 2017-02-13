package com.sss.watchman;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import Interfaces.BaseImage;
import Interfaces.BaseImageSource;
import Interfaces.ImageChangedCallback;

/**
 * This class implements image capture service
 *
 */

//todo fix this
@TargetApi(Build.VERSION_CODES.LOLLIPOP) //camera 2 api was added in API level 21
class ImageManager implements BaseImageSource, ImageChangedCallback{

    private MainActivity mActivity = null;

    private AndroidCamera mAndroidCamera = null;
    private boolean mStopped = false;
    private Handler mCameraThreadHandler;

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
        startCamera();
    }

    @Override
    public void start(int everySecond) throws InterruptedException {
        startCameraThread();
        submitJob();

    }

    private void startCamera(){
        mCameraThreadHandler.post(()->{
            mAndroidCamera = new AndroidCamera(mCameraThreadHandler);
            mAndroidCamera.startCapturing(mActivity,this);
        });
    }


    @Override
    public void stop() {
        stopCameraThread();
        mStopped = true;
    }

    private void startCameraThread(){
        HandlerThread cameraThread = new HandlerThread("Camera Background thread");
        cameraThread.start();
        mCameraThreadHandler = new Handler(cameraThread.getLooper());
    }

    private  void stopCameraThread() {
    }

    @Override
    public void onImageChanged(BaseImage image) {
        mActivity.onImageChanged(image);
        releaseCamera();
        if(!mStopped)
            submitJob();
    }

    @Override
    public void onFailure() {

        mActivity.setText("Failure");
        releaseCamera();
    }

    private void releaseCamera()
    {
        mCameraThreadHandler.post(()-> mAndroidCamera.close());
    }
}
