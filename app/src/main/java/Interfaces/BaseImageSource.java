package Interfaces;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseImageSource <T>{
    abstract public BaseImage<T> getLastImage();
    abstract public void start(int everySecond) throws InterruptedException;
    abstract public void stop();
}
