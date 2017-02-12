package datatypes;

import java.util.List;
import java.util.Vector;

import Interfaces.BaseImage;

/**
 * Created by sss on 10-Feb-17.
 */

public class Image8bit implements BaseImage{

    int mHeight = 0;
    int mWidth = 0;
    byte[] mdata = null;

    public Image8bit (int h, int w, byte[] data)
    {
        mHeight= h;
        mWidth= w;
        mdata = data;
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
        return mdata;
    }


    @Override
    public boolean isSame(BaseImage another) {
        return false;
    }
}
