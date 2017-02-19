package Interfaces;

import android.graphics.Bitmap;
import android.graphics.Interpolator;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseImageCompare <T>{
    public class  Result {
        public Bitmap bmp = null;
        public int diff = 0;
    };
    Result getDifference(BaseImage<T> one, BaseImage<T> another);
}
