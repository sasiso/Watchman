package stubs;

import Interfaces.BaseImage;
import Interfaces.BaseImageCompare;

/**
 * Created by satbir on 12/02/17.
 */

public class ImageCompareStub implements BaseImageCompare {

    public int returnType = 0;
    @Override
    public int getDifference(BaseImage one, BaseImage another) {
        return returnType;
    }
}
