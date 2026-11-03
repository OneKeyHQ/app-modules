package cn.jpush.android.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.ui.RecoverType9b558515;
import cn.jpush.android.webview.bridge.HostJsScope;
import cn.jpush.android.webview.bridge.RecoverType8afd8fa0;
import java.io.IOException;
import java.io.InputStream;

public class SystemAlertHelper {
   private static int recoverField_a = 400;
   private static int recoverField_b = 600;
   private static ImageButton recoverField_c;
   private static WindowManager recoverField_d;
   private static WebView recoverField_e;

   public static void close() {
      if (recoverField_d != null) {
         try {
            if (recoverField_e != null) {
               recoverField_d.removeView(recoverField_e);
            }

            if (recoverField_c != null) {
               recoverField_d.removeView(recoverField_c);
            }
         } catch (Throwable var4) {
         } finally {
            recoverField_d = null;
            recoverField_e = null;
            recoverField_c = null;
         }
      }
   }

   public static void systemAlert() {
   }

   public static void systemAlert(final Context var0, final RecoverType2e4cce2a var1) {
      Handler var2 = new Handler(Looper.getMainLooper());
      var2.post(
         new RecoverType9b4c5aea("SystemAlertHelper#systemAlert") {
            @SuppressLint("NewApi")
            @Override
            public void recoverMethod_a_9b75() {
               String var1x = var1.recoverField_Z;
               if (TextUtils.isEmpty(var1x)) {
                  Logger.recoverMethod_e_e925fa09("SystemAlertHelper", " not Rich Notification");
               } else {
                  SystemAlertHelper.recoverField_d = (WindowManager)var0.getSystemService("window");
                  SystemAlertHelper.recoverField_e = new WebView(var0);
                  SystemAlertHelper.recoverField_c = new ImageButton(var0);
                  SystemAlertHelper.recoverMethod_b_ad79ac7f(
                     var0, SystemAlertHelper.recoverField_d, SystemAlertHelper.recoverField_e, SystemAlertHelper.recoverField_c
                  );
                  SystemAlertHelper.recoverField_e.setHorizontalScrollBarEnabled(false);
                  SystemAlertHelper.recoverField_e.setVerticalScrollBarEnabled(false);
                  SystemAlertHelper.recoverField_e.setScrollbarFadingEnabled(true);
                  SystemAlertHelper.recoverField_e.setScrollBarStyle(33554432);
                  WebSettings var2x = SystemAlertHelper.recoverField_e.getSettings();
                  RecoverType9b4c5ae5.recoverMethod_c_4221005a(var2x);
                  RecoverType9b4c5ae5.recoverMethod_a_60006d18(SystemAlertHelper.recoverField_e);
                  SystemAlertHelper.recoverField_e.setWebChromeClient(new RecoverType8afd8fa0("JPushWeb", HostJsScope.class, null, null));
                  SystemAlertHelper.recoverField_e.setWebViewClient(new RecoverType9b558515(var1, var0));
                  if (!TextUtils.isEmpty(var1x) && var1x.startsWith("http")) {
                     SystemAlertHelper.recoverField_e.loadUrl(var1x);
                  }

                  SystemAlertHelper.recoverField_c.setOnClickListener(new OnClickListener() {
                     @Override
                     public void onClick(View var1x) {
                        SystemAlertHelper.close();
                     }
                  });
               }
            }
         }
      );
   }

   private static void recoverMethod_b_ad79ac7f(Context var0, WindowManager var1, WebView var2, ImageButton var3) {
      int var4 = 200;
      int var5 = -300;
      DisplayMetrics var6 = recoverMethod_a_f8f56096(var0);
      if (null != var6) {
         recoverField_a = var6.widthPixels * 3 / 4;
         recoverField_b = var6.heightPixels / 2;
         var4 = recoverField_a / 2;
         var5 = -(recoverField_b / 2);
      }

      LayoutParams var7 = new LayoutParams();
      var7.format = 1;
      var7.gravity = 17;
      var7.type = 2003;
      var7.flags |= 552;
      var7.width = recoverField_a;
      var7.height = recoverField_b;
      var7.x = -1;
      var7.y = -1;
      var1.addView(var2, var7);
      var3.setBackgroundColor(var0.getResources().getColor(17170445));
      int var8 = var0.getResources().getIdentifier("jpush_close", "drawable", var0.getPackageName());
      var3.setImageDrawable(var0.getResources().getDrawable(var8));
      var7.width = -2;
      var7.height = -2;
      var7.y = var5;
      var7.x = var4;
      var1.addView(var3, var7);
   }

   public static int getWindowWidth() {
      return recoverField_a;
   }

   public static int getWindowHeight() {
      return recoverField_b;
   }

   private static DisplayMetrics recoverMethod_a_f8f56096(Context var0) {
      DisplayMetrics var1 = var0.getResources().getDisplayMetrics();
      if (null != var1) {
         Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "width : " + var1.widthPixels);
         Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "height : " + var1.heightPixels);
         Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "density : " + var1.density);
         Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "densityDpi : " + var1.densityDpi);
         Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "xdpi : " + var1.xdpi);
         Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "ydpi : " + var1.ydpi);
         return var1;
      } else {
         return null;
      }
   }

   public static Bitmap getImageFromAssetsFile(Context var0, String var1) {
      Bitmap var2 = null;
      AssetManager var3 = var0.getResources().getAssets();

      try {
         InputStream var4 = var3.open(var1);
         var2 = BitmapFactory.decodeStream(var4);
         var4.close();
      } catch (IOException var5) {
      }

      return var2;
   }

   public static void startActivityByName(WebView var0, String var1, String var2) {
      Logger.recoverMethod_d_e925fa09("SystemAlertHelper", "action --- startActivityByName--------activityName : " + var1 + "----- params : " + var2);
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("SystemAlertHelper", "The activity name is null or empty, Give up..");
      }

      if (null != var0) {
         Context var3 = JPushConstants.getAppContext(var0.getContext());
         if (var3 != null) {
            Class var4 = null;

            try {
               var4 = Class.forName(var1);
            } catch (Exception var6) {
               Logger.recoverMethod_ee_e925fa09("SystemAlertHelper", "The activity name is invalid, Give up..");
               return;
            }

            if (null != var4) {
               Intent var5 = new Intent(var0.getContext(), var4);
               var5.putExtra("cn.jpush.android.ACTIVITY_PARAM", var2);
               var5.setFlags(268435456);
               var3.startActivity(var5);
               close();
            }
         }
      }
   }
}
