package Interfaces;

import java.util.List;
import java.util.Vector;

/**
 * Created by sss on 10-Feb-17.
 */

public interface BaseImage <T>{
    abstract int getHeight();
    abstract int getWidth();
    abstract List<T> getRow(final int index);
    abstract List<T> getColumn(final int index);
    abstract byte[] getPixels();
    abstract boolean isSame(BaseImage another);
}
