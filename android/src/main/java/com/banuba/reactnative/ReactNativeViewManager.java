package com.banuba.reactnative;

import android.graphics.Color;

import androidx.annotation.Nullable;

import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.viewmanagers.ReactNativeViewManagerDelegate;
import com.facebook.react.viewmanagers.ReactNativeViewManagerInterface;

@ReactModule(name = ReactNativeViewManager.NAME)
public class ReactNativeViewManager extends SimpleViewManager<ReactNativeView> implements ReactNativeViewManagerInterface<ReactNativeView> {

  public static final String NAME = "ReactNativeView";

  private final ViewManagerDelegate<ReactNativeView> mDelegate;

  public ReactNativeViewManager() {
    mDelegate = new ReactNativeViewManagerDelegate(this);
  }

  @Nullable
  @Override
  protected ViewManagerDelegate<ReactNativeView> getDelegate() {
    return mDelegate;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public ReactNativeView createViewInstance(ThemedReactContext context) {
    return new ReactNativeView(context);
  }

  @Override
  @ReactProp(name = "color")
  public void setColor(ReactNativeView view, String color) {
    view.setBackgroundColor(Color.parseColor(color));
  }
}
