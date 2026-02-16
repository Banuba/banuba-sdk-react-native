package com.banuba.reactnative;

import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.viewmanagers.EffectPlayerViewManagerDelegate;
import com.facebook.react.viewmanagers.EffectPlayerViewManagerInterface;

import java.lang.ref.WeakReference;

@ReactModule(name = EffectPlayerViewManager.REACT_CLASS)
public class EffectPlayerViewManager
    extends SimpleViewManager<SurfaceView>
    implements EffectPlayerViewManagerInterface<SurfaceView> {

  private final EffectPlayerViewManagerDelegate<
    SurfaceView, EffectPlayerViewManager> delegate =
          new EffectPlayerViewManagerDelegate<>(this);

  private static WeakReference<SurfaceView> sView;

  public static final String REACT_CLASS = "EffectPlayerView";

  public static SurfaceView getsView() {
    return sView.get();
  }

  @Override
  public EffectPlayerViewManagerDelegate<
      SurfaceView, EffectPlayerViewManager> getDelegate() {
    return delegate;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public SurfaceView createViewInstance(@NonNull ThemedReactContext reactContext) {
    SurfaceView view = new SurfaceView(reactContext);
    sView = new WeakReference<>(view);
    return view;
  }
}
