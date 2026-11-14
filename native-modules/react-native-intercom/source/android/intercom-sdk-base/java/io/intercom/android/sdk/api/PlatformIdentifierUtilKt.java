package io.intercom.android.sdk.api;

import android.content.Context;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002\u001a\b\u0010\u000b\u001a\u00020\fH\u0002\u001a\b\u0010\r\u001a\u00020\fH\u0002\u001a\b\u0010\u000e\u001a\u00020\fH\u0002\u001a\u0010\u0010\u000f\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\nH\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "ANDROID_HEADER",
         "",
         "CORDOVA_HEADER",
         "REACT_NATIVE_HEADER",
         "UNITY_HEADER",
         "FLUTTER_HEADER",
         "NATIVE_SDK",
         "getPlatform",
         "Lio/intercom/android/sdk/api/Platform;",
         "context",
         "Landroid/content/Context;",
         "isFlutterApp",
         "",
         "isReactNativeApp",
         "isUnityApp",
         "getPlatformIdentifier",
         "intercom-sdk-base_release"
   }
)
public final class PlatformIdentifierUtilKt {
   @NotNull
   public static final String ANDROID_HEADER = "intercom-android-sdk";
   @NotNull
   public static final String CORDOVA_HEADER = "intercom-sdk-cordova";
   @NotNull
   public static final String REACT_NATIVE_HEADER = "intercom-sdk-react-native";
   @NotNull
   public static final String UNITY_HEADER = "intercom-sdk-unity";
   @NotNull
   public static final String FLUTTER_HEADER = "intercom-sdk-flutter";
   @NotNull
   public static final String NATIVE_SDK = "intercom-sdk-native";

   private static final Platform getPlatform(Context var0) {
      WrapperPrefsStore var1 = WrapperPrefsStore.Companion.create(var0);
      String var2 = var1.getCordovaVersion();
      String var3 = isReactNativeApp() ? "intercom-sdk-react-native" : "";
      String var4 = isUnityApp() ? "intercom-sdk-unity" : "";
      String var5 = isFlutterApp() ? "intercom-sdk-flutter" : "";
      return !Intrinsics.areEqual(var2, "")
         ? Platform.Cordova
         : (
            !Intrinsics.areEqual(var3, "")
               ? Platform.ReactNative
               : (!Intrinsics.areEqual(var4, "") ? Platform.Unity : (!Intrinsics.areEqual(var5, "") ? Platform.Flutter : Platform.Native))
         );
   }

   private static final boolean isFlutterApp() {
      boolean var0;
      try {
         Class.forName("io.maido.intercom.IntercomFlutterPlugin");
         var0 = true;
      } catch (Exception var2) {
         var0 = false;
      }

      return var0;
   }

   private static final boolean isReactNativeApp() {
      boolean var0;
      try {
         Class.forName("com.intercom.reactnative.IntercomModule");
         var0 = true;
      } catch (Exception var2) {
         var0 = false;
      }

      return var0;
   }

   private static final boolean isUnityApp() {
      boolean var0;
      try {
         Class.forName("com.intercom.unity.IntercomPlugin");
         var0 = true;
      } catch (Exception var2) {
         var0 = false;
      }

      return var0;
   }

   @NotNull
   public static final String getPlatformIdentifier(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return getPlatform(var0).getIdentifier();
   }
}
