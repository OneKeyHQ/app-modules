package com.cancue.RNWebViewCleaner;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.webkit.WebView;
import android.webkit.WebStorage;
import android.webkit.CookieManager;

public class RNWebViewCleanerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNWebViewCleanerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNWebViewCleaner";
  }

  @ReactMethod
  public void clearAll() {
    CookieManager.getInstance().removeAllCookies(null);
    CookieManager.getInstance().flush();
    WebStorage.getInstance().deleteAllData();
  }
}
