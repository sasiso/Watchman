package Interfaces;

import Interfaces.BaseAlarmManager;
import Interfaces.BaseImageSource;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseFactory {
    public abstract BaseAlarmManager getAlertManager();
    public abstract BaseImageSource getImageSource();
    public abstract BaseImageCompare getImageCompare();

}
