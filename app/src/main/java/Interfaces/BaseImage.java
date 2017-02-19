package Interfaces;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Vector;

/**
 * Created by sss on 10-Feb-17.
 */

public interface BaseImage <T>{
    int getHeight();
    int getWidth();
    List<T> getRow(final int index);
    List<T> getColumn(final int index);
    byte[] getPixels();
    boolean isSame(BaseImage another);
    Bitmap getBitmap();
}
