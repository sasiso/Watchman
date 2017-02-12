package stubs;

import Interfaces.BaseImage;
import Interfaces.BaseImageSource;

/**
 * Created by satbir on 12/02/17.
 */

public class ImageSourceStub implements BaseImageSource {
    @Override
    public BaseImage getLastImage() {
        return null;
    }

    @Override
    public void start(int everySecond) {

    }

    @Override
    public void stop() {

    }
}
