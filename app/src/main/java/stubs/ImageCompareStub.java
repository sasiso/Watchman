package stubs;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

import Interfaces.BaseImage;
import Interfaces.BaseImageCompare;
import utils.ImageProcessingUtils;

/**
 * Created by satbir on 12/02/17.
 */

public class ImageCompareStub implements BaseImageCompare {

    public int returnType = 0;
    @Override
    public int getDifference(BaseImage one, BaseImage another) {
        Bitmap bitmap1 = ImageProcessingUtils.toGrayscale(ImageProcessingUtils.fromByteArray_depricated(one.getPixels()));
        Bitmap bitmap2 = ImageProcessingUtils.toGrayscale(ImageProcessingUtils.fromByteArray_depricated(one.getPixels()));

        ByteBuffer p1 =  ImageProcessingUtils.getPixels(bitmap1);
        ByteBuffer p2 =  ImageProcessingUtils.getPixels(bitmap2);
        return ImageProcessingUtils.compare(ImageProcessingUtils.histogram(p1), ImageProcessingUtils.histogram(p2));
    }
}
