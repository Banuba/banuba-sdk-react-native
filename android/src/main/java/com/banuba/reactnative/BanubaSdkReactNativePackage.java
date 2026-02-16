package com.banuba.reactnative;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.BaseReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.uimanager.ViewManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanubaSdkReactNativePackage extends BaseReactPackage {

  @Nullable
  @Override
  public NativeModule getModule(
      @NonNull String name,
      @NonNull ReactApplicationContext reactApplicationContext) {
    if (name.equals(BanubaSdkManagerModule.NAME)) {
      return new BanubaSdkManagerModule(reactApplicationContext);
    } else if (name.equals(EffectPlayerViewManager.REACT_CLASS)){
      return new EffectPlayerViewManager();
    } else {
      return null;
    }
  }

  /** @noinspection rawtypes*/
  @NonNull
  @Override
  public List<ViewManager> createViewManagers(
      @NonNull ReactApplicationContext reactContext) {
    return Collections.singletonList(new EffectPlayerViewManager());
  }

  @NonNull
  @Override
  public ReactModuleInfoProvider getReactModuleInfoProvider() {
    return () -> {
      Map<String, ReactModuleInfo> map = new HashMap<>();

      map.put(BanubaSdkManagerModule.NAME, new ReactModuleInfo(
        BanubaSdkManagerModule.NAME,    // name
        "BanubaSdkManagerModule",       // className
        false, // canOverrideExistingModule
        false, // needsEagerInit
        false, // isCXXModule
        true   // isTurboModule
      ));

      map.put(EffectPlayerViewManager.REACT_CLASS, new ReactModuleInfo(
          EffectPlayerViewManager.REACT_CLASS,  // name
          "EffectPlayerViewManager",            // className
          false, // canOverrideExistingModule
          false, // needsEagerInit
          false, // isCxxModule
          true   // isTurboModule
        ));

      return map;
    };
  }
}
