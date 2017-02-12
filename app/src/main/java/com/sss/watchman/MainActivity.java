package com.sss.watchman;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import Interfaces.BaseFactory;
import Interfaces.BaseImage;
import Interfaces.ImageChangedCallback;
import factory.Factory;


/**
 * Entry point Application
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    final BaseFactory factory = new Factory();

    final static String TAG = "Watchman";
    private ImageView mImageView;

    public static final int PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    /**
     * todo remove this
     */
    TextView tv;

    /**
     * todo initialize from factory
     */
    ImageManager mImageManager;



    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Creating activity");

        /**
         * Check if we have all the required permissions
         * We need to check every time, since user can revoke them
         */

        if(allPermissionOk())
        {
             init();
        }
        else
        {
            requestPermissions();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mImageManager.stop();
    }

    void init()
    {

        setContentView(R.layout.activity_main);
        mImageManager = new ImageManager(this);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnClickListener(v->{});
        tv.setText("Click Start Button");
        final Button btn = (Button) findViewById(R.id.start_button);
        btn.setOnClickListener(v ->
                {
                    tv.setText("Button Clicked");
                    try {
                        mImageManager.start(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        );
    }

    private boolean allPermissionOk() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permissionCheck == PackageManager.PERMISSION_DENIED)
            return false;

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                     init();
                }
                else {
                    //
                    // No Option left exit
                    //
                }

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        final String[] requiredPermissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String p : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    p) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(p);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    public void onImageChanged(BaseImage image) {
        runOnUiThread(() -> {
            byte[] pictureData = image.getPixels();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
            final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            mImageView.setImageBitmap(scaled);
        });

    }

}

