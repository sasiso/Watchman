package Interfaces;

/**
 * Created by satbir on 12/02/17.
 */

public interface ImageChangedCallback {
    void onImageChanged(final BaseImage image);
    void onFailure();
}
