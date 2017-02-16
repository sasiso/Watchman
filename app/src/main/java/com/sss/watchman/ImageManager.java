package com.sss.watchman;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.system.ErrnoException;

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

    private Activity mActivity = null;

    private AndroidCamera mAndroidCamera = null;
    private boolean mStopped = false;
    private Handler mCameraThreadHandler;
    private ImageChangedCallback mCallback;

    public ImageManager(Activity activity, ImageChangedCallback callback)
    {
        mCallback = callback;
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
    public void start(int everySecond){
        startCameraThread();
        submitJob();

    }

    private void startCamera(){
        try {
            mCameraThreadHandler.post(() -> {
                try {
                    mAndroidCamera = new AndroidCamera(mCameraThreadHandler);
                    mAndroidCamera.startCapturing(mActivity, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;

            });
        }catch (Exception e){e.printStackTrace();}
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
        mCallback.onImageChanged(image);
        releaseCamera();
        if(!mStopped)
            submitJob();
    }

    @Override
    public void onFailure() {
        releaseCamera();
    }

    @Override
    public void onMessage(String str){
    }

    private void releaseCamera()
    {
        mCameraThreadHandler.post(()-> mAndroidCamera.close());
    }
}
