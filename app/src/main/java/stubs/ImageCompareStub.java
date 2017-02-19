package stubs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.nio.ByteBuffer;

import Interfaces.BaseImage;
import Interfaces.BaseImageCompare;
import utils.ImageProcessingUtils;

/**
 * Created by satbir on 12/02/17.
 */

public class ImageCompareStub implements BaseImageCompare {

    static int togrey(int pixel){
        return (int)((0.21 * Color.red(pixel)) +
                (0.72 *Color.green(pixel)) +
                (0.07 * Color.blue(pixel)));
    }

    @Override
    public Result getDifference(BaseImage old, BaseImage newer) {

        Bitmap prev = old.getBitmap();
        Bitmap curr  = newer.getBitmap();
        assert prev.getWidth() == curr.getWidth();
        assert prev.getHeight() == curr.getHeight();

        int w = prev.getWidth();
        int h  = prev.getHeight();
        Result rv = new Result();
        rv.bmp = curr.copy(curr.getConfig(),true);

        for(int y=0; y< h-1; y+=5)
            for(int x=0; x<w-1;x+=5) {
                int p1 = togrey(prev.getPixel(x, y));
                int p2 = togrey(curr.getPixel(x, y));
                p1 -= p1%10;
                p2 -=p2%10;
                if(Math.abs(p1-p2)>10) {
                    rv.diff +=1;
                    rv.bmp.setPixel(x, y, Color.RED);
                }
            }
        return  rv;
    }
}
