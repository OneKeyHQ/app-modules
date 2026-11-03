package cn.jpush.android.webview.bridge;

import android.webkit.WebView;
import cn.jpush.android.api.SystemAlertHelper;

public class HostJsScope {
   private static RecoverType8afd8fa3 recoverField_a;

   public static void setWebViewHelper(RecoverType8afd8fa3 var0) {
      if (var0 != null) {
         recoverField_a = var0;
      }
   }

   public static void createShortcut(WebView var0, String var1, String var2, String var3) {
      if (recoverField_a != null) {
         recoverField_a.createShortcut(var1, var2, var3);
      }
   }

   public static void click(WebView var0, String var1, String var2, String var3) {
      if (recoverField_a != null) {
         recoverField_a.click(var1, var2, var3);
      }
   }

   public static void download(WebView var0, String var1, String var2, String var3) {
      if (recoverField_a != null) {
         recoverField_a.download(var1, var2, var3);
      }
   }

   public static void startActivityByName(WebView var0, String var1, String var2) {
      if (recoverField_a != null) {
         recoverField_a.startActivityByName(var1, var2);
      }
   }

   public static void startActivityByIntent(WebView var0, String var1, String var2) {
      if (recoverField_a != null) {
         recoverField_a.startActivityByIntent(var1, var2);
      }
   }

   public static void triggerNativeAction(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.triggerNativeAction(var1);
      }
   }

   public static void startMainActivity(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.startMainActivity(var1);
      }
   }

   public static void download(WebView var0, String var1, String var2) {
      if (recoverField_a != null) {
         recoverField_a.download(var1, var2);
      }
   }

   public static void download(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.download(var1);
      }
   }

   public static void close(WebView var0) {
      if (recoverField_a != null) {
         recoverField_a.close();
      }
   }

   public static void showToast(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.showToast(var1);
      }
   }

   public static void executeMsgMessage(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.executeMsgMessage(var1);
      }
   }

   public static void startActivityByNameWithSystemAlert(WebView var0, String var1, String var2) {
      SystemAlertHelper.startActivityByName(var0, var1, var2);
   }

   public static void showTitleBar(WebView var0) {
      if (recoverField_a != null) {
         recoverField_a.showTitleBar();
      }
   }

   public static void startPushActivity(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.startPushActivity(var1);
      }
   }

   public static void inAppClick(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.inAppClick(var1);
      }
   }

   public static void onLoadCallback(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.onLoadCallback(var1);
      }
   }

   public static void reportData(WebView var0, String var1) {
      if (recoverField_a != null) {
         recoverField_a.reportData(var1);
      }
   }

   public static String getDevInfo(WebView var0) {
      return recoverField_a != null ? recoverField_a.getDevInfo() : "";
   }

   public static String getNotificationInfo(WebView var0) {
      return recoverField_a != null ? recoverField_a.getNotificationInfo() : "";
   }

   public static String getTplData(WebView var0) {
      return recoverField_a != null ? recoverField_a.getTplData() : "";
   }

   public static String getTplExtraData(WebView var0) {
      return recoverField_a != null ? recoverField_a.getTplExtraData() : "";
   }
}
