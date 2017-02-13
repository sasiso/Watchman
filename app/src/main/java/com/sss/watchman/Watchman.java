package com.sss.watchman;

import Interfaces.BaseAlarmManager;
import Interfaces.BaseFactory;
import Interfaces.BaseImage;
import Interfaces.BaseImageCompare;
import Interfaces.BaseImageSource;

/**
 * Created by satbir on 12/02/17.
 */

public class Watchman {

    private boolean mStopped = false;

    /**
     *  Will decide image capture frequency
     */
    int mImageIntervalSec = 1;

    /**
     * On what difference alarm will be raised
     */
    private int mAlarmThresholdPercentage = 10;

    private BaseImageSource mImageSource = null;
    private BaseImageCompare mBaseImageCompare =null;
    private BaseImage currImage = null;
    private BaseImage lastImage = null;
    private BaseAlarmManager mAlarmManager= null;

    /**
     *
     * @param factory
     * @param imageInterval
     */

    public Watchman(BaseFactory factory, int imageInterval, int alarmThresholdPercentage)
    {
        mImageSource = factory.getImageSource();
        mBaseImageCompare = factory.getImageCompare();
        mAlarmManager = factory.getAlertManager();
        mAlarmThresholdPercentage = alarmThresholdPercentage;
    }

    public void start()
    {
        mStopped = false;
        lastImage = mImageSource.getLastImage();
        while(!mStopped)
        {
            currImage = mImageSource.getLastImage();
            if(currImage.isSame(lastImage))
                continue;
            // todo this should be event driven rather a while loop
            int diff = mBaseImageCompare.getDifference(lastImage,currImage);
            if( diff > mAlarmThresholdPercentage)
                mAlarmManager.raiseAlarm(BaseAlarmManager.FailureType.Breach, diff);

            lastImage = currImage;
        }

    }

    public  void stop()
    {
      mStopped = true;
    }



}
