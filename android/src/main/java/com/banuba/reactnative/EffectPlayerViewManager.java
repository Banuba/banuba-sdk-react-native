package com.banuba.reactnative;

import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class EffectPlayerViewManager extends SimpleViewManager<SurfaceView> {
  public static final String REACT_CLASS = "EffectPlayerView";

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public SurfaceView createViewInstance(ThemedReactContext reactContext) {
    return new SurfaceView(reactContext);
  }
}
