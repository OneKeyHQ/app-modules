package cn.jpush.android.webview.bridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.o.RecoverType2e4cf772;
import cn.jpush.android.ui.PopWinActivity;
import cn.jpush.android.ui.PushActivity;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class RecoverType8afd8fa3 {
   private final WeakReference recoverField_a;
   private final RecoverType2e4cce2a recoverField_b;
   private boolean recoverField_c = false;
   private static String recoverField_d = null;

   public RecoverType8afd8fa3(Context var1, RecoverType2e4cce2a var2) {
      this.recoverField_a = new WeakReference((Activity)var1);
      this.recoverField_b = var2;
      this.recoverField_c = false;
   }

   @JavascriptInterface
   public void createShortcut(String var1, String var2, String var3) {
   }

   @JavascriptInterface
   public void click(String var1, String var2, String var3) {
      Logger.recoverMethod_d_e925fa09("WebViewHelper", "Web callback:click - actionId:" + var1 + ", shouldClose:" + var2 + ", shouldCancelNotification:" + var3);

      try {
         this.recoverMethod_a_aad3b1ff(var1);
         boolean var4 = false;
         boolean var5 = false;
         var4 = Boolean.parseBoolean(var2);
         var5 = Boolean.parseBoolean(var3);
         if (var5) {
            RecoverType2e4d193c.recoverMethod_c_ae3b7eb8(JPushConstants.mApplicationContext, this.recoverField_b);
         }

         if (var4 && null != this.recoverField_a.get()) {
            ((Activity)this.recoverField_a.get()).finish();
         }
      } catch (Throwable var6) {
      }
   }

   private void recoverMethod_a_aad3b1ff(String var1) {
      try {
         long var2 = 1100L;

         try {
            var2 = Long.parseLong(var1);
         } catch (Exception var5) {
            Logger.recoverMethod_e_e925fa09("WebViewHelper", "Invalid actionId from Web - " + var1);
         }

         RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_b.recoverField_d, var2, JPushConstants.mApplicationContext);
      } catch (Throwable var6) {
      }
   }

   @JavascriptInterface
   public void download(String var1, String var2, String var3) {
      Logger.recoverMethod_v_e925fa09("WebViewHelper", "msgType from web: " + var3);
      this.download(var1, var2);
   }

   @JavascriptInterface
   public void startActivityByName(String var1, String var2) {
      Logger.recoverMethod_d_e925fa09("WebViewHelper", "activityName = " + var1);

      try {
         if (TextUtils.isEmpty(var1)) {
            Logger.recoverMethod_ee_e925fa09("WebViewHelper", "The activity name is null or empty, Give up..");
         }

         Context var3 = JPushConstants.mApplicationContext;
         if (null == var3) {
            return;
         }

         Class var4;
         try {
            var4 = Class.forName(var1);
         } catch (Exception var6) {
            Logger.recoverMethod_ee_e925fa09("WebViewHelper", "The activity name is invalid, Give up..");
            return;
         }

         Intent var5 = new Intent(var3, var4);
         var5.putExtra("cn.jpush.android.ACTIVITY_PARAM", var2);
         var5.setFlags(268435456);
         var3.startActivity(var5);
      } catch (Throwable var7) {
      }
   }

   @JavascriptInterface
   public void startActivityByIntent(String var1, String var2) {
      Context var3 = JPushConstants.mApplicationContext;
      if (null != var3) {
         try {
            Intent var4 = new Intent(var1);
            var4.addCategory(var3.getPackageName());
            var4.putExtra("cn.jpush.android.EXTRA", var2);
            var4.setFlags(268435456);
            var3.startActivity(var4);
         } catch (Throwable var5) {
            Logger.recoverMethod_ee_e925fa09("WebViewHelper", "Unhandle intent : " + var1);
         }
      }
   }

   @JavascriptInterface
   public void triggerNativeAction(String var1) {
      Context var2 = (Context)this.recoverField_a.get();
      if (null != var2) {
         ;
      }
   }

   @JavascriptInterface
   public void startMainActivity(String var1) {
      Context var2 = (Context)this.recoverField_a.get();
      if (null != var2) {
         try {
            ((Activity)var2).finish();
            RecoverType9b4c5ae5.recoverMethod_b_5ffc00fd(var2, var1);
         } catch (Exception var4) {
            Logger.recoverMethod_ee_e925fa09("WebViewHelper", "startMainActivity failed");
         }
      }
   }

   @JavascriptInterface
   public void download(String var1, String var2) {
      if (null != this.recoverField_a.get()) {
         this.recoverMethod_a_aad3b1ff(var1);
         this.download(var2);
         RecoverType2e4d193c.recoverMethod_c_ae3b7eb8(JPushConstants.mApplicationContext, this.recoverField_b);
         ((Activity)this.recoverField_a.get()).finish();
      }
   }

   @JavascriptInterface
   public void download(String var1) {
      if (null != this.recoverField_a.get()) {
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "Web callback:download - " + var1);
      }
   }

   @JavascriptInterface
   public void close() {
      if (null != this.recoverField_a.get()) {
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "Web callback:close");
         ((Activity)this.recoverField_a.get()).finish();
      }
   }

   @JavascriptInterface
   public void showToast(String var1) {
      if (null != this.recoverField_a.get()) {
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "Web callback:showToast - " + var1);
         Toast.makeText((Context)this.recoverField_a.get(), var1, 1).show();
      }
   }

   @JavascriptInterface
   public void executeMsgMessage(String var1) {
   }

   @JavascriptInterface
   public void showTitleBar() {
      try {
         if (null != this.recoverField_a.get() && this.recoverField_a.get() instanceof PushActivity) {
            ((PushActivity)this.recoverField_a.get()).showTitleBar();
         }
      } catch (Throwable var2) {
      }
   }

   @JavascriptInterface
   public void startPushActivity(String var1) {
      try {
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "[startPushActivity]:" + var1 + ",activity:" + this.recoverField_a.get());
         if (null != this.recoverField_a.get() && this.recoverField_a.get() instanceof PopWinActivity) {
            ((PopWinActivity)this.recoverField_a.get()).startPushActivity(var1);
         }
      } catch (Throwable var3) {
      }
   }

   @JavascriptInterface
   public void inAppClick(String var1) {
      try {
         JSONObject var2 = new JSONObject(var1);
         int var3 = var2.optInt("actionType");
         String var4 = var2.optString("param");
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "h5 click, actionType: " + var3 + ", actionKey: " + var4 + ", param: " + var1);
         RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_d_ae3b7eb8(JPushConstants.mApplicationContext, this.recoverField_b);
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("WebViewHelper", "h5 click failed, " + var5.getMessage());
      }
   }

   @JavascriptInterface
   public void onLoadCallback(String var1) {
      try {
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "onLoadCallback : " + var1);
         if (this.recoverField_c) {
            Logger.recoverMethod_d_e925fa09("WebViewHelper", "js callback already, param: " + var1);
            return;
         }

         this.recoverField_c = true;
         Context var2 = JPushConstants.mApplicationContext;
         if (null == var2 && this.recoverField_a.get() != null) {
            var2 = ((Activity)this.recoverField_a.get()).getApplicationContext();
         }

         JSONObject var3 = new JSONObject(var1);
         int var4 = var3.optInt("statusCode");
         String var5 = var3.optString("msg");
         if (var4 != 0) {
            Logger.recoverMethod_ww_e925fa09("WebViewHelper", "msg tpl load failed, code: " + var4 + ", errMsg: " + var5);
         }

         RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_19350b1c(var2, this.recoverField_b, var4 == 0);
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("WebViewHelper", "[onLoadCallback] parse param error. " + var6.getMessage());
      }
   }

   @JavascriptInterface
   public String getTplData() {
      if (null == this.recoverField_b) {
         Logger.recoverMethod_w_e925fa09("WebViewHelper", "[getTplData] entity is null");
         return "";
      } else {
         Logger.recoverMethod_d_e925fa09("WebViewHelper", "[getTplData] js get template data: " + this.recoverField_b.recoverField_bl);
         return this.recoverField_b.recoverField_bl;
      }
   }

   @JavascriptInterface
   public void reportData(String var1) {
   }

   @JavascriptInterface
   public String getDevInfo() {
      return "";
   }

   @JavascriptInterface
   public String getNotificationInfo() {
      return "";
   }

   @JavascriptInterface
   public String getTplExtraData() {
      return "";
   }
}
