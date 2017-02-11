package com.sss.watchman;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

import utils.ImageSaver;

/**
 * This class implements image capture service
 *
 */

//todo fix this
@TargetApi(Build.VERSION_CODES.LOLLIPOP) //camera 2 api was added in API level 21
public class ImageManager {

    /**
     * {@link Log} Tag for logging
     */
    private static final String TAG = "ImageManager";
    /**
     * {@link CameraDevice} Represents a camera device
     */
    private CameraDevice mCameraDevice;
    /**
     *  {@link ImageReader} represents image reader
     */
    private ImageReader mImageReader;
    /**
     *
     */
    private Handler mBackgroundHandler;
    /**
     *
     */
    private HandlerThread mBackgroundThread;
    /**
     *
     */
    private Activity mContext;
    /**
     *
     */
    private WindowManager mSindowManager;
    /**
     *
     */
    private CameraManager mCameraManager;
    /**
     *
     */
    private TreeMap<String, byte[]> mPicturesTaken;
    /**
     *
     */
    private OnPictureCapturedListener mCapturedListener;
    /**
     *
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    /**
     *
     */
    private String mCurrentCameraId;
    /**
     *
     */
    private Queue<String> cameraIds;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {

            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), getFile()));
        }

    };

    private  int counter = 0;
    public File getFile() {
        counter++;
        return new File(mContext.getExternalFilesDir(null), counter + "_pic.jpg");
    }
    /**
     * 
     * @param activity
     * @param capturedListener
     */
    public void startCapturing(final Activity activity,
                               final OnPictureCapturedListener capturedListener) {
        Log.v(TAG, "Entered startCapturing");

        mPicturesTaken = new TreeMap<>();
        mContext = activity;
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mSindowManager = mContext.getWindowManager();
        mCapturedListener = capturedListener;
        cameraIds = new LinkedList<>();
        try {
            final String[] cameraIdList = mCameraManager.getCameraIdList();
            if (cameraIdList != null && cameraIdList.length != 0) {
                for (final String cameraId : cameraIdList) {
                    this.cameraIds.add(cameraId);
                }
                this.mCurrentCameraId = this.cameraIds.poll();
                openCameraAndTakePicture();
            } else {
                mCapturedListener.onDoneCapturingAllPhotos(mPicturesTaken);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCameraAndTakePicture() {
        startBackgroundThread();
        Log.d(TAG, "opening camera " + mCurrentCameraId);
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                mCameraManager.openCamera(mCurrentCameraId, stateCallback, null);
            }
            else {
                Log.e(TAG, "Permission not granted");

            }

        } catch (CameraAccessException e) {
            Log.e(TAG, " exception opening camera " + mCurrentCameraId + e.getMessage());
        }
    }


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "camera " + camera.getId() + " opened");
            mCameraDevice = camera;
            Log.i(TAG, "Taking picture from camera " + camera.getId());
            takePicture();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, " camera " + camera.getId() + " disconnected");
            if (mCameraDevice != null) {
                mCameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            Log.d(TAG, "camera " + camera.getId() + " closed");
            stopBackgroundThread();
            if (!cameraIds.isEmpty()) {
                new Handler().postDelayed(() ->
                                takeAnotherPicture()
                        , 100);
            } else {
                mCapturedListener.onDoneCapturingAllPhotos(mPicturesTaken);
            }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "camera in error, int code " + error);
            if (mCameraDevice != null) {
                mCameraDevice.close();
            } else {
                mCameraDevice = null;
            }
        }
    };


    private void takePicture() {
        if (null == mCameraDevice) {
            Log.e(TAG, "mCameraDevice is null");
            return;
        }
        try {
            final CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                if (characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                            .getOutputSizes(ImageFormat.JPEG);
                }
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            final List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            final int rotation = this.mSindowManager.getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            reader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void saveImageToDisk(final byte[] bytes) {
        final File file = new File(Environment.getExternalStorageDirectory() + "/" + this.mCameraDevice.getId() + "_pic.jpg");
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
            this.mPicturesTaken.put(file.getPath(), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void startBackgroundThread() {
        if (mBackgroundThread == null) {
            mBackgroundThread = new HandlerThread("Camera Background" + mCurrentCameraId);
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            Log.e(TAG, "exception stopBackgroundThread" + e.getMessage());
        }
    }


    private void takeAnotherPicture() {
        startBackgroundThread();
        this.mCurrentCameraId = this.cameraIds.poll();
        openCameraAndTakePicture();
    }

    private void closeCamera() {
        Log.d(TAG, "closing camera " + mCameraDevice.getId());
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }


    final private CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            if (mPicturesTaken.lastEntry() != null) {
                mCapturedListener.onCaptureDone(mPicturesTaken.lastEntry().getKey(), mPicturesTaken.lastEntry().getValue());
                Log.i(TAG, "done taking picture from camera " + mCameraDevice.getId());
            }
            closeCamera();
        }
    };
}
