package Interfaces;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseImageCompare <T>{
    public abstract int getDifference(final BaseImage<T> one, final BaseImage<T> another);
}
