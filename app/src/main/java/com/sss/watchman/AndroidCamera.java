package com.sss.watchman;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Interfaces.BaseImage;
import Interfaces.ImageChangedCallback;
import datatypes.Image8bit;

/**
 * This class implements image capture service
 *
 */

//todo fix this
@TargetApi(Build.VERSION_CODES.LOLLIPOP) //camera 2 api was added in API level 21
class AndroidCamera {

    AndroidCamera(Handler handler)
    {
        mBackgroundHandler = handler;
    }


    /**
     * {@link Log} Tag for logging
     */
    private static final String TAG = "AndroidCamera";
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
    private WindowManager mSindowManager;
    /**
     *
     */
    private CameraManager mCameraManager;
    /**
     *
     */
    private ImageChangedCallback mCapturedListener;
    /**
     *
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    /**
     *
     */
    private String mCurrentCameraId;

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
            final Image image = reader.acquireLatestImage();
            final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            final byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            BaseImage img = new Image8bit(0,0, bytes);
            image.close();
            mBackgroundHandler.post(()->sendImage(img));
        }

    };

    private  void sendImage(BaseImage baseImage)
    {
        mCapturedListener.onImageChanged(baseImage);
    }
    /**
     *
     */
    void startCapturing(final Activity activity,
                        final ImageChangedCallback capturedListener) {
        Log.v(TAG, "Entered startCapturing");

        /*

     */
        Activity mContext = activity;
        mCapturedListener = capturedListener;

        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mSindowManager = mContext.getWindowManager();

        /*

     */
        Queue<String> cameraIds = new LinkedList<>();
        try {
            final String[] cameraIdList = mCameraManager.getCameraIdList();
            if (cameraIdList != null && cameraIdList.length != 0) {

                for (final String cameraId : cameraIdList) {
                    CameraCharacteristics characteristics
                            = mCameraManager.getCameraCharacteristics(cameraId);

                    // We don't use a front facing camera in this sample.
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
                    cameraIds.add(cameraId);
                }
                this.mCurrentCameraId = cameraIds.poll();
                openCameraAndTakePicture();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCameraAndTakePicture() {
        Log.d(TAG, "opening camera " + mCurrentCameraId);
        try {
                mCameraManager.openCamera(mCurrentCameraId, stateCallback, null);
            } catch (CameraAccessException | SecurityException e) {
            Log.e(TAG, " exception opening camera " + mCurrentCameraId + e.getMessage());
        }
    }


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            takePicture();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
                mCameraDevice.close();
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {

        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "camera in error, int code " + error);
        }
    };


    private void takePicture() {
        try {
            final CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            final List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(mImageReader.getSurface());
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            final int rotation = this.mSindowManager.getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
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
                    mCapturedListener.onFailure();

                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void closeCamera() {
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
            closeCamera();
        }
    };

    private void releaseResources(){
        closeCamera();
    }

    public void close() {
        releaseResources();
    }
}
