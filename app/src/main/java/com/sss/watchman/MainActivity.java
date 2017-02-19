package com.sss.watchman;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import Interfaces.BaseFactory;
import Interfaces.BaseImage;
import Interfaces.ImageChangedCallback;
import Interfaces.UserInterface;
import factory.Factory;
import utils.ImageProcessingUtils;


/**
 * Entry point Application
 */
public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        UserInterface
{
    final static String TAG = "Watchman";
    private ImageView mImageView;
    private Watchman mTheApplication;
    boolean mRunning = false;
    int m_imageCouner =0;
    SeekBar seekBar = null;

    public static final int PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    TextView mText;
    TextView mMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);
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
        mTheApplication.stop();
    }

    public void setText(String text)
    {
        runOnUiThread(() -> mText.setText(text));
    }

    public void setMessage(String msg)
    {
        runOnUiThread(() -> mMessageText.setText(msg));
    }


    void subscribeEvents(){
        seekBar = (SeekBar)  findViewById(R.id.seekBar);
        seekBar.setMax(500*500);
        seekBar.setProgress(mTheApplication.getmAlarmThresholdPercentage());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                mTheApplication.setmAlarmThresholdPercentage(progress);


            }});
        // Example of a call to a native method
        mText = (TextView) findViewById(R.id.sample_text);
        mMessageText = (TextView) findViewById(R.id.message);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnClickListener(v->{});

        final Button btn = (Button) findViewById(R.id.start_button);
        btn.setOnClickListener(v -> {
            btn.setText(mRunning? "Start":"Stop");
            if(mRunning)
                mTheApplication.stop();
            else
                try {
                    mTheApplication.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            mRunning = !mRunning;
        });

    }

    void init()
    {
        mTheApplication = new Watchman(this,this);
        subscribeEvents();
    }

    private boolean allPermissionOk() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        return permissionCheck != PackageManager.PERMISSION_DENIED;

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

    @Override
    public void onImageChanged(Bitmap image) {
        runOnUiThread(() -> {
            setText("Image Received:"+ m_imageCouner++);
            mImageView.setImageBitmap(image);
        });

    }

    @Override
    public void onFailure() {
        setText("Error");
    }

    @Override public void onMessage(String msg){
        setMessage(msg);

    }
    @Override
    public void onDifference(int diff){
        setMessage("Activity detected:"+ diff);

    }

}

