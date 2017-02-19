package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.SparseIntArray;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sss on 14-Feb-17.
 */

public class ImageProcessingUtils {
    public final static int MASK = 0xff;
    public static Bitmap fromByteArray_depricated(final byte[] bytes)
    {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        return scaled;
    }

    public static Bitmap fromByteArray(final byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    static public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    static public int[] getPixels(Bitmap bmp)
    {
        final int width = bmp.getWidth();
        final int height = bmp.getHeight();

        int pixels[] = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;

    }

    static public SparseIntArray histogram(final int pixels[]) {

        SparseIntArray m = new SparseIntArray();
        for (int pixel : pixels) {
            int grey = (int) ((0.21 * Color.red(pixel))
                    + (0.72 * Color.green(pixel)) + (0.07 * Color.blue(pixel)));
            m.put(grey, m.get(grey) + 1);
        }
        return m;
    }

    static public int compare (final SparseIntArray a, final SparseIntArray b) {
        int rv = 0;
        int size = a.size();
        for (int i = 0; i < size; ++i) {
            if (a.get(i) != b.get(i)) {
                rv += 1;
            }
        }
        return rv;
    }

}
