package stubs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.ByteBuffer;

import Interfaces.BaseImage;
import Interfaces.BaseImageCompare;
import utils.ImageProcessingUtils;

/**
 * Created by satbir on 12/02/17.
 */

public class ImageCompareStub implements BaseImageCompare {

    @Override
    public int getDifference(BaseImage one, BaseImage another) {
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(one.getPixels(), 0, one.getPixels().length);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(another.getPixels(), 0, another.getPixels().length);

        int p1[] =  ImageProcessingUtils.getPixels(bitmap1);
        int p2[] =  ImageProcessingUtils.getPixels(bitmap2);
        return ImageProcessingUtils.compare(ImageProcessingUtils.histogram(p1), ImageProcessingUtils.histogram(p2));
    }
}
