package com.sss.watchman;

import java.util.List;
import java.util.Vector;

/**
 * Created by sss on 10-Feb-17.
 */

public class GrayScale6BitImage extends BaseImage<Byte> {
    @Override
    int getHeight() {
        return 0;
    }

    @Override
    int getWidth() {
        return 0;
    }

    @Override
    List<Byte> getRow(int index) {
        return null;
    }

    @Override
    List<Byte> getColumn(int index) {
        return null;
    }

    @Override
    Vector<Byte> getPixels() {
        return null;
    }

    @Override
    void setPixel(Vector<Byte> pixels) {

    }
}
