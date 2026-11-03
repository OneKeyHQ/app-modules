package cn.jpush.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import cn.jpush.android.ac.RecoverType9b4c5726;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aef;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.k.RecoverType2e4ce86e;
import cn.jpush.android.local.ProxyActivityAction;
import cn.jpush.android.webview.bridge.HostJsScope;
import cn.jpush.android.webview.bridge.RecoverType8afd8fa0;
import cn.jpush.android.webview.bridge.RecoverType8afd8fa3;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.io.File;

public class RecoverType9b558516 extends ProxyActivityAction {
   private String recoverField_b;
   private WebView recoverField_c;
   public static RecoverType8afd8fa3 recoverField_a = null;
   private RecoverType2e4cce2a recoverField_d = null;

   @Override
   public boolean onCreateOptionsMenu(Activity var1, Menu var2) {
      return false;
   }

   @Override
   public void onConfigurationChanged(Activity var1, Configuration var2) {
   }

   @Override
   public boolean onKeyDown(Activity var1, int var2, KeyEvent var3) {
      return false;
   }

   @Override
   public void onCreate(Activity var1, Bundle var2) {
      RecoverType9b4c5ae5.recoverMethod_e_f0554df3(var1);
      this.recoverMethod_a_f9c5dd07(var1);
   }

   @Override
   public void onStart(Activity var1, Intent var2) {
   }

   @Override
   public void onNewIntent(Activity var1, Intent var2) {
   }

   @Override
   public void onRestart(Activity var1) {
   }

   @Override
   public void onResume(Activity var1) {
      if (null != this.recoverField_c) {
         if (VERSION.SDK_INT >= 11) {
            this.recoverField_c.onResume();
         }

         HostJsScope.setWebViewHelper(recoverField_a);
      }
   }

   @Override
   public void onPause(Activity var1) {
      if (null != this.recoverField_c && VERSION.SDK_INT >= 11) {
         this.recoverField_c.onPause();
      }
   }

   @Override
   public void onStop(Activity var1) {
   }

   @Override
   public void onDestroy(Activity var1) {
      if (null != this.recoverField_c) {
         this.recoverField_c.removeAllViews();
         this.recoverField_c.destroy();
         this.recoverField_c = null;
      }
   }

   @Override
   public void onBackPressed(Activity var1) {
      if (this.recoverField_d != null && this.recoverField_d.recoverField_ai != 0) {
         RecoverType8b455831.recoverMethod_a_2ef42797(this.recoverField_b, "", this.recoverField_d.recoverField_ai, 1006L, var1);
      } else {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_b, 1006L, var1);
      }
   }

   @Override
   public void onEvent(Activity var1, String var2, Bundle var3) {
      if (!TextUtils.isEmpty(var2) && var2.equals("start_push_activity") && this.recoverField_d != null && this.recoverField_c != null) {
         String var4 = var3.getString("url");
         if (!TextUtils.isEmpty(var4)) {
            this.recoverField_d.recoverField_Z = var4;
            Intent var5 = new Intent(var1, PushActivity.class);
            var5.putExtra("msg_data", this.recoverField_d.recoverMethod_c_73cf11cb());
            var5.putExtra("from_way", true);
            var5.setFlags(335544320);
            var1.startActivity(var5);
         }

         var1.finish();
      }
   }

   private void recoverMethod_a_f9c5dd07(Activity var1) {
      if (null != var1.getIntent()) {
         try {
            if (!RecoverType2e4d193c.recoverMethod_a_c4d2ecb4(var1, var1.getIntent())) {
               return;
            }

            this.recoverField_d = this.recoverMethod_a_c27f6fd7(var1, var1.getIntent());
            if (null != this.recoverField_d) {
               this.recoverField_b = this.recoverField_d.recoverField_d;
               this.recoverMethod_b_f9c5dd07(var1);
               this.recoverMethod_c_f9c5dd07(var1);
               RecoverType2e4ce86e.recoverMethod_a_ae3b7eb8(var1, this.recoverField_d);
            } else {
               Logger.recoverMethod_ww_e925fa09("PopWinActivityImpl", "Warning，null message entity! Close PopWinActivity!");
               var1.finish();
            }
         } catch (Throwable var3) {
            Logger.recoverMethod_ee_e925fa09("PopWinActivityImpl", "Extra data is not serializable!" + var3);
            var1.finish();
         }
      } else {
         Logger.recoverMethod_ww_e925fa09("PopWinActivityImpl", "PopWinActivity get NULL intent!");
         var1.finish();
      }
   }

   private RecoverType2e4cce2a recoverMethod_a_c27f6fd7(Activity var1, Intent var2) {
      RecoverType2e4cce2a var3 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var1.getApplicationContext(), var1.getIntent());
      if (null != var3) {
         return var3;
      }

      Logger.recoverMethod_dd_e925fa09("PopWinActivityImpl", "parse entity form plugin plateform");
      String var4 = null;
      if (var2.getData() != null) {
         var4 = var2.getData().toString();
      }

      if (TextUtils.isEmpty(var4) && var2.getExtras() != null) {
         var4 = var2.getExtras().getString("JMessageExtra");
      }

      return RecoverType9b4c5726.recoverMethod_a_39f271f4(var1, var4, "");
   }

   private void recoverMethod_b_f9c5dd07(Activity var1) {
      int var2 = var1.getResources().getIdentifier("jpush_popwin_layout", "layout", var1.getPackageName());
      if (0 == var2) {
         Logger.recoverMethod_ee_e925fa09("PopWinActivityImpl", "Please add layout resource jpush_popwin_layout.xml to res/layout !");
         var1.finish();
      } else {
         var1.setContentView(var2);
         int var3 = var1.getResources().getIdentifier("wvPopwin", "id", var1.getPackageName());
         if (0 == var3) {
            Logger.recoverMethod_ee_e925fa09("PopWinActivityImpl", "Please use default code in jpush_popwin_layout.xml!");
            var1.finish();
         } else {
            this.recoverField_c = (WebView)var1.findViewById(var3);
            if (null == this.recoverField_c) {
               Logger.recoverMethod_ee_e925fa09("PopWinActivityImpl", "Can not get webView in layout file!");
               var1.finish();
            } else {
               this.recoverField_c.setScrollbarFadingEnabled(true);
               this.recoverField_c.setScrollBarStyle(33554432);
               WebSettings var4 = this.recoverField_c.getSettings();
               var4.setDomStorageEnabled(true);
               RecoverType9b4c5ae5.recoverMethod_c_4221005a(var4);
               RecoverType9b4c5ae5.recoverMethod_a_60006d18(this.recoverField_c);
               RecoverType9b4c5ae5.recoverMethod_a_4221005a(var4);
               var4.setSavePassword(false);
               this.recoverField_c.setBackgroundColor(0);
               recoverField_a = new RecoverType8afd8fa3(var1, this.recoverField_d);
               if (VERSION.SDK_INT >= 17) {
                  Logger.recoverMethod_dd_e925fa09("PopWinActivityImpl", "Android sdk version greater than or equal to 17, Java—Js interact by annotation!");
                  this.recoverMethod_a_9b75();
               }

               this.recoverField_c.setWebChromeClient(new RecoverType8afd8fa0("JPushWeb", HostJsScope.class, null, null));
               this.recoverField_c.setWebViewClient(new RecoverType9b558515(this.recoverField_d, var1));
               HostJsScope.setWebViewHelper(recoverField_a);
            }
         }
      }
   }

   private void recoverMethod_a_9b75() {
      Object[] var1 = new Object[2];
      Class[] var2 = new Class[2];
      var1[0] = recoverField_a;
      var1[1] = "JPushWeb";
      var2[0] = Object.class;
      var2[1] = String.class;

      try {
         RecoverType9b4c5aef.recoverMethod_a_222a2ac4(this.recoverField_c, "addJavascriptInterface", var2, var1);
      } catch (Throwable var4) {
         Logger.recoverMethod_e_e925fa09("PopWinActivityImpl", "addJavascriptInterface failed:" + var4.toString());
      }
   }

   private void recoverMethod_c_f9c5dd07(Activity var1) {
      String var2 = this.recoverField_d.recoverField_ab;
      String var3 = this.recoverField_d.recoverField_Z;
      Logger.recoverMethod_d_e925fa09("PopWinActivityImpl", "showUrl = " + var3);
      if (!TextUtils.isEmpty(var2) && new File(var2.replace("file://", "")).exists()) {
         this.recoverField_c.loadUrl(var2);
      } else {
         this.recoverField_c.loadUrl(var3);
      }

      if (this.recoverField_d.recoverField_ai == 0) {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_b, 1000L, var1);
      } else {
         RecoverType8b455831.recoverMethod_a_2ef42797(this.recoverField_d.recoverField_d, "", this.recoverField_d.recoverField_ai, 1000L, var1);
      }
   }
}
