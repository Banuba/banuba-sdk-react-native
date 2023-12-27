package com.banubasdkreactnative;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.banuba.sdk.effect_player.Effect;
import com.banuba.sdk.manager.BanubaSdkManager;
import com.banuba.sdk.manager.BanubaSdkTouchListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.UIManagerModule;

class BanubaSdkManagerModule extends ReactContextBaseJavaModule implements PermissionListener {
  private static final String TAG = "BanubaSdkManagerModule";
  private static final int REQUEST_CODE_PERMISSION = 20002;
  private BanubaSdkManager mSdkManager;

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
    if (!isCameraPermissionGranted()) {
      requestPermission();
    } else {
      getSdkManager().openCamera();
    }
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

  private BanubaSdkManager getSdkManager() {
    if (mSdkManager == null) {
      mSdkManager = new BanubaSdkManager(getReactApplicationContext());
    }
    return mSdkManager;
  }

  private void requestPermission() {
    if (!isCameraPermissionGranted()) {
      String[] permissionsList = new String[]{Manifest.permission.CAMERA};

      PermissionAwareActivity activity = (PermissionAwareActivity) getCurrentActivity();
      if (activity == null) {
          throw new RuntimeException("Failed to get PermissionAwareActivity");
      }

      activity.requestPermissions(permissionsList, REQUEST_CODE_PERMISSION, this);
    }
  }

  private boolean isCameraPermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
      return getReactApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == REQUEST_CODE_PERMISSION) {
      if (isCameraPermissionGranted()) {
        getSdkManager().openCamera();
      } else {
        Log.e(TAG, "App has no camera permissions");
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
}
