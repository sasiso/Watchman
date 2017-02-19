package datatypes;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Vector;

import Interfaces.BaseImage;
import utils.ImageProcessingUtils;

/**
 * Created by sss on 10-Feb-17.
 */

public class Image8bit implements BaseImage{

    int mHeight = 0;
    int mWidth = 0;
    int mPixelFormat = 0;
    byte[] mJpegdata = null;
    Bitmap mbitmap;

    public Image8bit (int h, int w, byte[] data, int pixelFormat)
    {
        mHeight= h;
        mWidth= w;
        mJpegdata = data;
        pixelFormat = pixelFormat;
        mbitmap = ImageProcessingUtils.fromByteArray(data);
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public List getRow(int index) {
        return null;
    }

    @Override
    public List getColumn(int index) {
        return null;
    }

    @Override
    public byte[] getPixels() {
        return mJpegdata;
    }


    @Override
    public boolean isSame(BaseImage another) {
        return false;
    }

    @Override
    public Bitmap getBitmap() {
        return mbitmap;
    }
}
