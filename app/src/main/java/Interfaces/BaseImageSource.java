package Interfaces;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseImageSource <T>{
    BaseImage<T> getLastImage();
    void start(int everySecond);
    void stop();
}
