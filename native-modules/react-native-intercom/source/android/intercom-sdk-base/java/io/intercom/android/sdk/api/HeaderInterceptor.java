package io.intercom.android.sdk.api;

import android.content.Context;
import android.util.Base64;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.identity.AppIdentity;
import io.intercom.android.sdk.utilities.commons.DeviceUtils;
import java.io.IOException;
import java.util.Random;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;
import okhttp3.Request.Builder;

class HeaderInterceptor implements Interceptor {
   private static final String INTERCOM_AGENT = "X-INTERCOM-AGENT";
   private static final String INTERCOM_AGENT_WRAPPER = "X-INTERCOM-AGENT-WRAPPER";
   private static final String IDEMPOTENCY_KEY = "Idempotency-Key";
   private static final String AUTHORIZATION = "Authorization";
   private static final String CONTENT_TYPE_KEY = "Content-Type";
   private static final String CONTENT_TYPE_VALUE = "application/json";
   private static final String ACCEPT_LANGUAGE = "Accept-Language";
   private static final String SUPPORTED_LANGUAGES_KEY = "X-INTERCOM-SUPPORTED-LANGUAGES";
   private static final String SUPPORTED_LANGUAGE_LIST = "ar,bg,bn,bs,ca,cs,da,de,de-form,el,en,es,et,fa-IR,fi,fr,hi,he,hr,hu,id,it,ja,ko,lt,lv,mn,ms,nb,nl,pl,pt-PT,pt-BR,ro,ru,sl,sr,sv,sw,th,tr,uk,vi,zh-Hant,zh-Hans";
   private static final String HOST_APP_VERSION_KEY = "X-INTERCOM-HOST-APP-VERSION";
   private final String cordovaVersion;
   private final String reactNativeVersion;
   private final String unityVersion;
   private final String flutterVersion;
   private final String versionName;
   private final AppIdentity appIdentity;
   private final String userLocale;
   private final String appVersion;
   private final Random random;

   public static HeaderInterceptor create(Context var0, AppIdentity var1) {
      WrapperPrefsStore var2 = WrapperPrefsStore.create(var0);
      String var3 = var2.getCordovaVersion();
      String var4 = var2.getReactNativeVersion();
      String var5 = var2.getUnityVersion();
      String var6 = "";
      String var7 = UserLocaleUtilKt.getUserLocaleString(var0);
      String var8 = DeviceUtils.getAppVersion(var0);
      return new HeaderInterceptor(var3, var4, var5, var6, "18.10.0", var1, var7, var8, new Random());
   }

   HeaderInterceptor(String var1, String var2, String var3, String var4, String var5, AppIdentity var6, String var7, String var8, Random var9) {
      this.cordovaVersion = var1;
      this.reactNativeVersion = var2;
      this.unityVersion = var3;
      this.flutterVersion = var4;
      this.versionName = var5;
      this.appIdentity = var6;
      this.userLocale = var7;
      this.appVersion = var8;
      this.random = var9;
   }

   @Override
   public Response intercept(Chain var1) throws IOException {
      Request var2 = var1.request();
      Builder var3 = var2.newBuilder()
         .header("Authorization", this.getBasicAuth())
         .header("Idempotency-Key", Long.toHexString(this.random.nextLong()))
         .header("X-INTERCOM-AGENT", "intercom-android-sdk/" + this.versionName)
         .header("Accept-Language", this.userLocale)
         .header(
            "X-INTERCOM-SUPPORTED-LANGUAGES",
            "ar,bg,bn,bs,ca,cs,da,de,de-form,el,en,es,et,fa-IR,fi,fr,hi,he,hr,hu,id,it,ja,ko,lt,lv,mn,ms,nb,nl,pl,pt-PT,pt-BR,ro,ru,sl,sr,sv,sw,th,tr,uk,vi,zh-Hant,zh-Hans"
         )
         .header("X-INTERCOM-HOST-APP-VERSION", this.appVersion);
      if (var2.body() == null || !var2.body().contentType().toString().startsWith("multipart/")) {
         var3.header("Content-Type", "application/json");
      }

      if (!this.cordovaVersion.isEmpty()) {
         var3.header("X-INTERCOM-AGENT-WRAPPER", "intercom-sdk-cordova/" + this.cordovaVersion);
      }

      if (!this.reactNativeVersion.isEmpty()) {
         var3.header("X-INTERCOM-AGENT-WRAPPER", "intercom-sdk-react-native/" + this.reactNativeVersion);
      }

      if (!this.unityVersion.isEmpty()) {
         var3.header("X-INTERCOM-AGENT-WRAPPER", "intercom-sdk-unity/" + this.unityVersion);
      }

      if (!this.flutterVersion.isEmpty()) {
         var3.header("X-INTERCOM-AGENT-WRAPPER", "intercom-sdk-flutter/" + this.flutterVersion);
      }

      Request var4 = var3.build();
      return var1.proceed(var4);
   }

   private String getBasicAuth() {
      String var1 = this.appIdentity.appId() + ":" + this.appIdentity.apiKey();
      return "Basic " + Base64.encodeToString(var1.getBytes(), 2);
   }

   public static void setCordovaVersion(Context var0, String var1) {
      WrapperPrefsStore var2 = WrapperPrefsStore.create(var0);
      var2.setCordovaVersion(var1);
   }

   public static void setReactNativeVersion(Context var0, String var1) {
      WrapperPrefsStore var2 = WrapperPrefsStore.create(var0);
      var2.setReactNativeVersion(var1);
   }

   public static void setUnityVersion(Context var0, String var1) {
      WrapperPrefsStore var2 = WrapperPrefsStore.create(var0);
      var2.setUnityVersion(var1);
   }

   @VisibleForTesting
   static void clearPersistedWrapperVersions(Context var0) {
      WrapperPrefsStore var1 = WrapperPrefsStore.create(var0);
      var1.clear();
   }
}
