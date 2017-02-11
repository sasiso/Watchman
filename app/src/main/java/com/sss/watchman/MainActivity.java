package com.sss.watchman;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.TreeMap;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements OnPictureCapturedListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private ImageManager mImageManager;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "Creating activity");

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if(permissionCheck == PackageManager.PERMISSION_DENIED)
        {
            Log.w("MainActivity", "Application do not have permissions to start camera, Requesting...");
            askForPermissions();
        }
        else {
            Log.i("MainActivity", "Application do already have permissions to start camer");
            startApplication();
        }
    }

    void startApplication()
    {

        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        final Button btn = (Button) findViewById(R.id.start_button);
        btn.setOnClickListener(v ->
        {
            tv.setText("Button Clicked");
            new ImageManager().startCapturing(this, this);
        }

        );
    }

    void askForPermissions()
    {        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_CAMERA_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    startApplication();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("Main", "Error Permission denined for camera");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }


        private void StartApplication()
    {

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        tv.setText("onCaptureDone");
        Log.v("Main", "onCaptureDone");

    }

    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        tv.setText("onDoneCapturingAllPhotos");
        Log.v("Main", "onDoneCapturingAllPhotos");

    }
}
