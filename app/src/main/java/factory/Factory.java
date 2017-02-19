package factory; //todo change package name

import android.app.Activity;

import com.sss.watchman.ImageManager;

import Interfaces.BaseAlarmManager;
import Interfaces.BaseFactory;
import Interfaces.BaseImageCompare;
import Interfaces.BaseImageSource;
import Interfaces.ImageChangedCallback;
import stubs.AlarmManagerStub;
import stubs.ImageCompareStub;

/**
 * Created by satbir on 11/02/17.
 */

public class Factory  implements BaseFactory{

    private  BaseAlarmManager mAlarmManager;
    private  BaseImageSource  mImageSource;
    private  BaseImageCompare mImageCompare;

    /**
     * Creates concrete instances of various implementations
     */
    public Factory(Activity activity, ImageChangedCallback callback){
        mAlarmManager = new AlarmManagerStub(activity);
        mImageCompare = new ImageCompareStub();
        mImageSource  = new ImageManager(activity, callback);
    }

    @Override
    public BaseAlarmManager getAlertManager() {
        return mAlarmManager;
    }

    @Override
    public BaseImageSource getImageSource() {
        return mImageSource;
    }

    @Override
    public BaseImageCompare getImageCompare() {
        return mImageCompare;
    }
}
