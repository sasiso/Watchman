package Interfaces;

import android.graphics.Bitmap;

/**
 * Created by satbir on 19/02/17.
 */

public interface UserInterface {
    void onImageChanged(Bitmap image);
    void onFailure();
    void onMessage(String message);
    void onDifference(int diff);
}