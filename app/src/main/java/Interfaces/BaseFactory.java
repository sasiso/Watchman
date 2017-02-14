package Interfaces;

import Interfaces.BaseAlarmManager;
import Interfaces.BaseImageSource;

/**
 * Created by satbir on 11/02/17.
 */

public interface BaseFactory {
    BaseAlarmManager getAlertManager();
    BaseImageSource getImageSource();
    BaseImageCompare getImageCompare();

}
