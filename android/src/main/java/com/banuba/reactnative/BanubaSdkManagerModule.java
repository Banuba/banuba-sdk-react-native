package com.banuba.reactnative;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.banuba.sdk.effect_player.Effect;
import com.banuba.sdk.entity.RecordedVideoInfo;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.manager.BanubaSdkTouchListener;
import com.banuba.sdk.manager.IEventCallback;
import com.banuba.sdk.types.Data;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.UIManagerModule;
import com.banuba.sdk.camera.Facing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class BanubaSdkManagerModule extends ReactContextBaseJavaModule implements PermissionListener, IEventCallback {
  private static final String TAG = "BanubaSdkManagerModule";
  private static final int REQUEST_CODE_PERMISSION = 20002;
  private BanubaSdkManager mSdkManager;
  private int mListenerCount = 0;

  private String mScreenshotPath;

  BanubaSdkManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @ReactMethod
  public void initialize(@NonNull ReadableArray resourcePath, @NonNull String clientTokenString) {
    BanubaSdkManager.initialize(this.getReactApplicationContext(), clientTokenString, resourcePath.toArrayList().toArray(new String[]{}));
  }

  @ReactMethod
  public void deinitialize() {
    BanubaSdkManager.deinitialize();
  }

  @ReactMethod
  public void attachView(int tag) {

    UIManagerModule uiManagerModule = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManagerModule.addUIBlock((viewHierarchyManager) -> {
      SurfaceView epView = (SurfaceView) viewHierarchyManager.resolveView(tag);

      if (epView == null) {
        throw new RuntimeException("Invalid EffectPlayerView with tag " + tag);
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
    });

    // This will fire all the events left in `addUIBlock` queue
    getCurrentActivity().runOnUiThread(()->
      getCurrentActivity().findViewById(android.R.id.content).requestLayout());
  }

  @ReactMethod
  public void closeCamera() {
    getSdkManager().closeCamera();
  }

  @ReactMethod
  public void openCamera() {
    if (!isPermissionGranted()) {
      requestPermission();
    } else {
      getSdkManager().openCamera();
    }
  }

  @ReactMethod
  public void setCameraFacing(boolean front)
  {
    if (front) {
      getSdkManager().setCameraFacing(Facing.FRONT);
    } else {
      getSdkManager().setCameraFacing(Facing.BACK, false);
    }
  }

  @ReactMethod
  public void enableFlashlight(boolean enabled)
  {
    getSdkManager().setFlashlightEnabled(enabled);
  }

  @ReactMethod
  public void startPlayer() {
    getSdkManager().effectPlayerPlay();
  }

  @ReactMethod
  public void stopPlayer() {
    getSdkManager().effectPlayerPause();
  }

  @ReactMethod
  public void loadEffect(@NonNull String path) {
    getSdkManager().loadEffect(path, true);
  }

  @ReactMethod
  public void evalJs(@NonNull String script) {
    Effect current = getSdkManager().getEffectManager().current();
    if (current != null) {
      current.evalJs(script, null);
    }
  }

  @ReactMethod
  public void startVideoRecording(@NonNull String path, boolean mirrorFrontCamera) {
    getSdkManager().setCallback(this);
    getSdkManager().startVideoRecording(path, true, null, 1.f);
  }

  @ReactMethod
  public void stopVideoRecording() {
    getSdkManager().stopVideoRecording();
  }

  @ReactMethod
  public void pauseVideoRecording() {
    getSdkManager().pauseVideoRecording();
  }

  @ReactMethod
  public void resumeVideoRecording() {
    getSdkManager().unpauseVideoRecording();
  }

  @ReactMethod
  public void takeScreenshot(String path) {
    mScreenshotPath = path;
    getSdkManager().setCallback(this);
    getSdkManager().takePhoto(null);
  }

  @ReactMethod
  public void addListener(String eventName) {
    mListenerCount += 1;
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    mListenerCount -= count;
  }

  private BanubaSdkManager getSdkManager() {
    if (mSdkManager == null) {
      mSdkManager = new BanubaSdkManager(getReactApplicationContext());
    }
    return mSdkManager;
  }

  private void requestPermission() {
    if (!isPermissionGranted()) {
      String[] permissionsList = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

      PermissionAwareActivity activity = (PermissionAwareActivity) getCurrentActivity();
      if (activity == null) {
          throw new RuntimeException("Failed to get PermissionAwareActivity");
      }

      activity.requestPermissions(permissionsList, REQUEST_CODE_PERMISSION, this);
    }
  }

  private boolean isPermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
      return getReactApplicationContext().checkSelfPermission(Manifest.permission.CAMERA)
          == PackageManager.PERMISSION_GRANTED
        && getReactApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO)
          == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void sendEvent(String eventName, Object params) {
      getReactApplicationContext()
        .getJSModule(RCTDeviceEventEmitter.class)
        .emit(eventName, params);
  }


  @Override
  public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

  @NonNull
  @Override
  public String getName() {
    return "BanubaSdkManager";
  }

  @Override
  public void onCameraOpenError(@NonNull Throwable throwable) {

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
    } catch (FileNotFoundException e) {
      Log.e(TAG, "Failed to write screenshot file", e);
    }
    sendEvent("onScreenshotReady", success);
  }

  @Override
  public void onHQPhotoReady(@NonNull Bitmap bitmap) {

  }

  @Override
  public void onVideoRecordingFinished(@NonNull RecordedVideoInfo recordedVideoInfo) {
    if (mListenerCount > 0) {
      sendEvent("onVideoRecordingFinished",
        !TextUtils.isEmpty(recordedVideoInfo.getFilePath()));
    }
  }

  @Override
  public void onVideoRecordingStatusChange(boolean started) {
    if(mListenerCount > 0) {
      sendEvent("onVideoRecordingStatus", started);
    }
  }

  @Override
  public void onImageProcessed(@NonNull Bitmap bitmap) {

  }

  @Override
  public void onFrameRendered(@NonNull Data data, int i, int i1) {

  }
}
