package com.banuba.reactnative;

import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.util.Size;

import androidx.annotation.NonNull;

import com.banuba.sdk.effect_player.Effect;
import com.banuba.sdk.entity.RecordedVideoInfo;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.manager.BanubaSdkTouchListener;
import com.banuba.sdk.manager.IEventCallback;
import com.banuba.sdk.types.Data;
import com.banuba.sdk.types.FullImageData;
import com.banuba.sdk.types.PixelFormat;
import com.banuba.sdk.effect_player.CameraOrientation;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.banuba.sdk.camera.Facing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

class BanubaSdkManagerModule extends NativeBanubaSdkManagerSpec
    implements PermissionListener, IEventCallback {

  private static final String TAG = "BanubaSdkManagerModule";
  private static final int REQUEST_CODE_PERMISSION = 20002;
  private BanubaSdkManager mSdkManager;

  private String mScreenshotPath;

  BanubaSdkManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public void initialize(
      @NonNull ReadableArray resourcePath,
      @NonNull String clientTokenString) {
    //noinspection SuspiciousToArrayCall
    BanubaSdkManager.initialize(
      this.getReactApplicationContext(),
      clientTokenString,
      resourcePath.toArrayList().toArray(new String[0]));
  }

  @Override
  public void deinitialize() {
    BanubaSdkManager.deinitialize();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void attachView() {
    SurfaceView epView = EffectPlayerViewManager.getsView();
    if (epView == null) {
      throw new RuntimeException("Invalid EffectPlayerView");
    }
    getSdkManager().attachSurface(epView);

    epView.setOnTouchListener(
      new BanubaSdkTouchListener(
        getReactApplicationContext(),
        getSdkManager().getEffectPlayer()
      )
    );

    epView.post(() ->
      getSdkManager().onSurfaceChanged(0, epView.getWidth(), epView.getHeight()));

    getSdkManager().onSurfaceCreated();
  }

  @Override
  public void closeCamera() {
    getSdkManager().closeCamera();
  }

  @Override
  public void openCamera() {
    if (!isPermissionGranted()) {
      requestPermission();
    } else {
      getSdkManager().openCamera();
    }
  }

  @Override
  public void setCameraFacing(boolean front)
  {
    if (front) {
      getSdkManager().setCameraFacing(Facing.FRONT);
    } else {
      getSdkManager().setCameraFacing(Facing.BACK, false);
    }
  }

  @Override
  public void enableFlashlight(boolean enabled)
  {
    getSdkManager().setFlashlightEnabled(enabled);
  }

  @Override
  public void setCameraZoom(double factor)
  {
    getSdkManager().setCameraZoom((float) factor);
  }

  @Override
  public void startPlayer() {
    getSdkManager().effectPlayerPlay();
  }

  @Override
  public void stopPlayer() {
    getSdkManager().effectPlayerPause();
  }

  @Override
  public void loadEffect(@NonNull String path) {
    getSdkManager().loadEffect(path, true);
  }

  @Override
  public void evalJs(@NonNull String script) {
    Effect current = getSdkManager().getEffectManager().current();
    if (current != null) {
      current.evalJs(script, null);
    }
  }

  @Override
  public void reloadConfig(String script) {
    requireNonNull(getSdkManager().getEffectPlayer().effectManager())
      .reloadConfig(script);
  }

  @Override
  public void processImage(final String path) {
    final ReactApplicationContext reactContext = getReactApplicationContext();

    final File destFile = new File(reactContext.getCacheDir(), "processed_image.png");

    final String sourceFilePath = path.startsWith("file://")
      ? path.substring("file://".length())
      : path;

    if (!new File(sourceFilePath).exists()) {
      emitProcessImageEvent("Error while getting image from the path");
      return;
    }

    Log.d(TAG, "processImage: source file exists = " + new File(sourceFilePath).exists());

    final Callable<Bitmap> callable = () -> {
      final long start = System.currentTimeMillis();

      final Bitmap sourceBitmap = BitmapFactory.decodeFile(sourceFilePath);
      if (sourceBitmap == null) {
        throw new Exception("Error while processing image");
      }
      final FullImageData image = new FullImageData(sourceBitmap,
        new FullImageData.Orientation(CameraOrientation.DEG_0));

      try (Data processed = getSdkManager().getEffectPlayer().processImage(
        image,
        PixelFormat.RGBA
      )) {

        final int width;
        final int height;
        final CameraOrientation cameraOrientation = image.getOrientation().getCameraOrientation();
        final Size size = image.getSize();

        if (cameraOrientation == CameraOrientation.DEG_90 ||
          cameraOrientation == CameraOrientation.DEG_270) {
          width = size.getHeight();
          height = size.getWidth();
        } else {
          width = size.getWidth();
          height = size.getHeight();
        }

        final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(processed.getData());

        Log.d(TAG, "Time to process image = " + (System.currentTimeMillis() - start) + " ms");
        return result;

      }
    };

    final RunnableFuture<Bitmap> future = new FutureTask<>(callable);
    getSdkManager().runOnRenderThread(future);
    try {
      final Bitmap processedBitmap = future.get(30, TimeUnit.SECONDS);

      if (destFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        destFile.delete();
      }
      FileOutputStream fos = new FileOutputStream(destFile);
      boolean saved = processedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
      fos.flush();
      fos.close();

      if (saved) {
        emitProcessImageEvent(destFile.getAbsolutePath());
        Log.d(TAG, "Image saved at: " + destFile.getAbsolutePath());
      } else {
         emitProcessImageEvent( "Error while saving image");
      }
    } catch (Exception e) {
      Log.w(TAG, "Cannot process image!", e);
       emitProcessImageEvent("Error while processing image");
    }
  }

  @Override
  public void startVideoRecording(@NonNull String path, boolean mirrorFrontCamera) {
    getSdkManager().setCallback(this);
    getSdkManager().startVideoRecording(path, true, null, 1.f);
  }

  @Override
  public void stopVideoRecording() {
    getSdkManager().stopVideoRecording();
  }

  @Override
  public void pauseVideoRecording() {
    getSdkManager().pauseVideoRecording();
  }

  @Override
  public void resumeVideoRecoding() {
    getSdkManager().unpauseVideoRecording();
  }

  @Override
  public void takeScreenshot(String path) {
    mScreenshotPath = path;
    getSdkManager().setCallback(this);
    getSdkManager().takePhoto(null);
  }

  private BanubaSdkManager getSdkManager() {
    if (mSdkManager == null) {
      mSdkManager = new BanubaSdkManager(getReactApplicationContext());
    }
    return mSdkManager;
  }

  private void requestPermission() {
    if (!isPermissionGranted()) {
      String[] permissionsList = new String[]{
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO};

      PermissionAwareActivity activity = (PermissionAwareActivity) getCurrentActivity();
      if (activity == null) {
          throw new RuntimeException("Failed to get PermissionAwareActivity");
      }

      activity.requestPermissions(permissionsList, REQUEST_CODE_PERMISSION, this);
    }
  }

  private boolean isPermissionGranted() {
    return getReactApplicationContext().checkSelfPermission(Manifest.permission.CAMERA)
      == PackageManager.PERMISSION_GRANTED
      && getReactApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO)
      == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public boolean onRequestPermissionsResult(
      int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == REQUEST_CODE_PERMISSION) {
      if (isPermissionGranted()) {
        getSdkManager().openCamera();
      } else {
        Log.e(TAG, "App has no all the requested permissions");
      }
      return true;
    }
    return false;
  }

  @Override
  public void onCameraOpenError(@NonNull Throwable throwable) {
    Log.e(TAG, "Failed to open camera", throwable);
  }

  @Override
  public void onCameraStatus(boolean b) {

  }

  @Override
  public void onScreenshotReady(@NonNull Bitmap bitmap) {
    boolean success = false;
    try {
      FileOutputStream fos = new FileOutputStream(mScreenshotPath, false);
      if (mScreenshotPath.endsWith(".jpg") || mScreenshotPath.endsWith(".jpeg")) {
        success = bitmap.compress(CompressFormat.JPEG, 70, fos);
      } else {
        success = bitmap.compress(CompressFormat.PNG, 0, fos);
      }
      fos.close();
    } catch (IOException e) {
      success = false;
      Log.e(TAG, "Failed to write screenshot file", e);
    }
    emitOnScreenshotReady(success);
  }

  @Override
  public void onHQPhotoReady(@NonNull Bitmap bitmap) {

  }

  @Override
  public void onVideoRecordingFinished(
      @NonNull RecordedVideoInfo recordedVideoInfo) {
    emitOnVideoRecordingFinished(
      !TextUtils.isEmpty(recordedVideoInfo.getFilePath()));
  }

  @Override
  public void onVideoRecordingStatusChange(boolean started) {
      emitOnVideoRecordingStatus(started);
  }

  @Override
  public void onImageProcessed(@NonNull Bitmap bitmap) {

  }

  @Override
  public void onFrameRendered(@NonNull Data data, int i, int i1) {

  }
}
