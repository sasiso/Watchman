package com.sss.watchman;

import android.app.Activity;

import Interfaces.BaseAlarmManager;
import Interfaces.BaseImage;
import Interfaces.BaseImageCompare;
import Interfaces.BaseImageSource;
import Interfaces.ImageChangedCallback;
import factory.Factory;

/**
 * Created by satbir on 12/02/17.
 */

public class Watchman implements ImageChangedCallback{
    /**
     *  Will decide image capture frequency
     */
     private int mImageIntervalSec;

    /**
     * On what difference alarm will be raised
     */
    private int mAlarmThresholdPercentage = 10;

    private BaseImageSource mImageSource = null;
    private BaseImageCompare mBaseImageCompare =null;
    private BaseImage lastImage = null;
    private BaseAlarmManager mAlarmManager= null;
    private ImageChangedCallback mImageChangedCallback = null;


    public Watchman(Activity activity, ImageChangedCallback cb)
    {
        mImageChangedCallback = cb;
        Factory f = new Factory(activity, this);
        mImageSource = f.getImageSource();
        mBaseImageCompare = f.getImageCompare();
        mAlarmManager = f.getAlertManager();
        mAlarmThresholdPercentage = 10;
        mImageIntervalSec = 1;
    }

    void start() throws InterruptedException {
        mImageSource.start(mImageIntervalSec);
    }

    @Override
    public void onImageChanged(BaseImage image) {
           if(lastImage ==null){
               lastImage = image;
               return;
           }
        int diff = mBaseImageCompare.getDifference(lastImage, image);
        if( diff > mAlarmThresholdPercentage)
                mAlarmManager.raiseAlarm(BaseAlarmManager.FailureType.Breach, diff);

        lastImage = image;
        try {
            mImageChangedCallback.onImageChanged(image);
        }catch (Exception e) { e.printStackTrace();}

    }

    @Override
    public void onFailure() {
        mImageChangedCallback.onFailure();
        mImageSource.stop();

    }

    void stop(){
        mImageSource.stop();
    }
}
