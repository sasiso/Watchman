package factory; //todo change package name

import Interfaces.BaseAlarmManager;
import Interfaces.BaseFactory;
import Interfaces.BaseImageCompare;
import Interfaces.BaseImageSource;

/**
 * Created by satbir on 11/02/17.
 */

public class Factory  implements BaseFactory{
    @Override
    public BaseAlarmManager getAlertManager() {
        return null;
    }

    @Override
    public BaseImageSource getImageSource() {
        return null;
    }

    @Override
    public BaseImageCompare getImageCompare() {
        return null;
    }
}
