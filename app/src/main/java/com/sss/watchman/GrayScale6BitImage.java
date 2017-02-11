package com.sss.watchman;

import java.util.List;
import java.util.Vector;

import Interfaces.BaseImage;

/**
 * Created by sss on 10-Feb-17.
 */

public class GrayScale6BitImage implements BaseImage<Byte> {
    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public List<Byte> getRow(int index) {
        return null;
    }

    @Override
    public List<Byte> getColumn(int index) {
        return null;
    }

    @Override
    public Vector<Byte> getPixels() {
        return null;
    }

    @Override
    public void setPixel(Vector<Byte> pixels) {

    }
}
