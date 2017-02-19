package Interfaces;

import android.graphics.Bitmap;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseImageCompare <T>{
    Bitmap getDifference(BaseImage<T> one, BaseImage<T> another);
}
