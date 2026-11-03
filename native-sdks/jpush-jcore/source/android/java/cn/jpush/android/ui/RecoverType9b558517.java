package cn.jpush.android.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import cn.jpush.android.ac.RecoverType9b4c5726;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.ad.RecoverType9b4c5aef;
import cn.jpush.android.ad.RecoverType9b4c5af2;
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

public class RecoverType9b558517 extends ProxyActivityAction {
   private int recoverField_b = 0;
   private RecoverType2e4cce2a recoverField_c;
   private boolean recoverField_d = false;
   private String recoverField_e;
   private boolean recoverField_f = false;
   public static RecoverType8afd8fa3 recoverField_a = null;
   private WebView recoverField_g;
   private RelativeLayout recoverField_h;
   private TextView recoverField_i;
   private ImageButton recoverField_j;
   private ProgressBar recoverField_k;
   private RecoverType9b558515 recoverField_l;
   private OnClickListener recoverField_m = new OnClickListener() {
      @Override
      public void onClick(View var1) {
         try {
            if (null != var1 && var1.getContext() != null && var1.getContext() instanceof Activity) {
               ((Activity)var1.getContext()).onBackPressed();
            }
         } catch (Throwable var3) {
         }
      }
   };

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
      this.recoverMethod_b_f9c5dd07(var1);
   }

   @Override
   public void onStart(Activity var1, Intent var2) {
   }

   @Override
   public void onNewIntent(Activity var1, Intent var2) {
      var1.setIntent(var2);
      this.recoverMethod_b_f9c5dd07(var1);
   }

   @Override
   public void onRestart(Activity var1) {
   }

   @Override
   public void onResume(Activity var1) {
      if (null != this.recoverField_g) {
         if (VERSION.SDK_INT >= 11) {
            this.recoverField_g.onResume();
         }

         HostJsScope.setWebViewHelper(recoverField_a);
      }
   }

   @Override
   public void onPause(Activity var1) {
      if (null != this.recoverField_g && VERSION.SDK_INT >= 11) {
         this.recoverField_g.onPause();
      }
   }

   @Override
   public void onStop(Activity var1) {
   }

   @Override
   public void onDestroy(Activity var1) {
      if (null != this.recoverField_g) {
         ViewParent var2 = this.recoverField_g.getParent();
         if (var2 != null) {
            Logger.recoverMethod_d_e925fa09("PushActivityImpl", "webview parent view " + var2.toString());
            ((ViewGroup)var2).removeView(this.recoverField_g);
         }

         this.recoverField_g.getSettings().setJavaScriptEnabled(false);
         this.recoverField_g.clearCache(true);
         this.recoverField_g.clearHistory();
         this.recoverField_g.clearView();
         this.recoverField_g.removeAllViews();
         this.recoverField_g.clearSslPreferences();
         this.recoverField_g.destroy();
         this.recoverField_g = null;
      }
   }

   @Override
   public void onBackPressed(Activity var1) {
      if (null != this.recoverField_g && this.recoverField_g.canGoBack()) {
         this.recoverField_g.goBack();
      } else {
         if (this.recoverField_c != null && this.recoverField_c.recoverField_ai != 0) {
            RecoverType8b455831.recoverMethod_a_2ef42797(this.recoverField_e, "", this.recoverField_c.recoverField_ai, 1006L, var1);
         } else {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_e, 1006L, var1);
         }

         this.recoverMethod_a_f9c5dd07(var1);
      }
   }

   @Override
   public void onEvent(Activity var1, String var2, Bundle var3) {
      if (!TextUtils.isEmpty(var2) && var2.equals("push_show_titlebar")) {
         var1.runOnUiThread(new RecoverType9b4c5aea("PushActivityImpl#onEvent") {
            @Override
            public void recoverMethod_a_9b75() {
               RecoverType9b558517.this.recoverMethod_a_9b75();
            }
         });
      }
   }

   public void recoverMethod_a_f9c5dd07(Activity var1) {
      var1.finish();
      if (1 == this.recoverField_b) {
         try {
            ActivityManager var2 = (ActivityManager)var1.getSystemService("activity");
            ComponentName var3 = ((RunningTaskInfo)var2.getRunningTasks(1).get(0)).baseActivity;
            ComponentName var4 = ((RunningTaskInfo)var2.getRunningTasks(1).get(0)).topActivity;
            Logger.recoverMethod_d_e925fa09("PushActivityImpl", "baseActivity  = " + var3.toString());
            Logger.recoverMethod_d_e925fa09("PushActivityImpl", "topActivity  = " + var4.toString());
            if (var3 != null && var4 != null && var4.toString().equals(var3.toString())) {
               RecoverType9b4c5ae5.recoverMethod_f_f0554df3(var1);
            }
         } catch (Throwable var5) {
            Logger.recoverMethod_ww_e925fa09("PushActivityImpl", "Get running tasks failed.");
            RecoverType9b4c5ae5.recoverMethod_f_f0554df3(var1);
         }
      }
   }

   private void recoverMethod_b_f9c5dd07(Activity var1) {
      if (null != var1.getIntent()) {
         try {
            if (!RecoverType2e4d193c.recoverMethod_a_c4d2ecb4(var1, var1.getIntent())) {
               return;
            }

            this.recoverField_d = var1.getIntent().getBooleanExtra("from_way", false);
            this.recoverField_c = this.recoverMethod_a_c27f6fd7(var1, var1.getIntent());
            if (null != this.recoverField_c) {
               this.recoverField_e = this.recoverField_c.recoverField_d;
               this.recoverField_f = !TextUtils.isEmpty(this.recoverField_c.recoverField_bh);
               this.recoverMethod_a_8426c6cc(var1, this.recoverField_c);
               RecoverType2e4ce86e.recoverMethod_a_ae3b7eb8(var1, this.recoverField_c);
            } else {
               Logger.recoverMethod_ww_e925fa09("PushActivityImpl", "Warning，null message entity! Close PushActivity!");
               var1.finish();
            }
         } catch (Throwable var3) {
            Logger.recoverMethod_ee_e925fa09("PushActivityImpl", "Extra data is not serializable!" + var3);
            var1.finish();
         }
      } else {
         Logger.recoverMethod_ww_e925fa09("PushActivityImpl", "PushActivity get NULL intent!");
         var1.finish();
      }
   }

   private RecoverType2e4cce2a recoverMethod_a_c27f6fd7(Activity var1, Intent var2) {
      RecoverType2e4cce2a var3 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var1, var1.getIntent());
      if (null != var3) {
         return var3;
      }

      Logger.recoverMethod_dd_e925fa09("PushActivityImpl", "parse entity form plugin plateform");
      String var4 = null;
      if (var2.getData() != null) {
         var4 = var2.getData().toString();
      }

      if (TextUtils.isEmpty(var4) && var2.getExtras() != null) {
         var4 = var2.getExtras().getString("JMessageExtra");
      }

      Logger.recoverMethod_d_e925fa09("PushActivityImpl", "content:" + var4);
      return RecoverType9b4c5726.recoverMethod_a_39f271f4(var1, var4, "");
   }

   private void recoverMethod_a_8426c6cc(Activity var1, RecoverType2e4cce2a var2) {
      if (var2 != null) {
         switch (var2.recoverField_r) {
            case 0:
               var1.setRequestedOrientation(1);
               this.recoverMethod_b_8426c6cc(var1, var2);
               break;
            default:
               Logger.recoverMethod_w_e925fa09("PushActivityImpl", "Invalid msg type to show - " + var2.recoverField_r);
               RecoverType2e4d193c.recoverMethod_c_ae3b7eb8(var1, var2);
               var1.finish();
         }
      } else {
         Logger.recoverMethod_ww_e925fa09("PushActivityImpl", "Null message entity! Close PushActivity!");
         var1.finish();
      }
   }

   private void recoverMethod_b_8426c6cc(Activity var1, RecoverType2e4cce2a var2) {
      Logger.recoverMethod_i_e925fa09("PushActivityImpl", "Action: processShow");
      if (null == var2) {
         Logger.recoverMethod_ww_e925fa09("PushActivityImpl", "Null message entity! Close PushActivity!");
         var1.finish();
      } else {
         if (var2.recoverField_ae == 0) {
            this.recoverField_b = var2.recoverField_ac;
            int var3 = var1.getResources().getIdentifier("jpush_webview_layout", "layout", var1.getPackageName());
            if (0 == var3) {
               Logger.recoverMethod_ee_e925fa09("PushActivityImpl", "Please add layout resource jpush_webview_layout.xml to res/layout !");
               var1.finish();
               return;
            }

            var1.setContentView(var3);
            String var4 = var2.recoverField_Z;
            if (!RecoverType9b4c5ae5.recoverMethod_c_aad3b203(var4)) {
               RecoverType2e4d193c.recoverMethod_c_ae3b7eb8(var1, var2);
               var1.finish();
               return;
            }

            String var5 = var2.recoverField_ab;
            if (var2.recoverField_s) {
               int var6 = var1.getResources().getIdentifier("actionbarLayoutId", "id", var1.getPackageName());
               if (0 == var6) {
                  Logger.recoverMethod_ee_e925fa09("PushActivityImpl", "Please use default code in jpush_webview_layout.xml!");
                  var1.finish();
                  return;
               }

               View var7 = var1.findViewById(var6);
               if (null != var7) {
                  this.recoverMethod_a_88091f30(var1, var7, var2);
                  if (!TextUtils.isEmpty(var5) && new File(var5.replace("file://", "")).exists() && !this.recoverField_d) {
                     this.recoverField_g.loadUrl(var5);
                  } else {
                     this.recoverField_g.loadUrl(var4);
                  }
               }
            }

            if (!this.recoverField_d && !this.recoverField_f) {
               if (var2.recoverField_ai == 0) {
                  RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_e, 1000L, var1);
               } else {
                  RecoverType8b455831.recoverMethod_a_2ef42797(var2.recoverField_d, "", var2.recoverField_ai, 1000L, var1);
               }
            }

            if (1 == var2.recoverField_ad) {
            }
         }
      }
   }

   public void recoverMethod_a_88091f30(Activity var1, View var2, RecoverType2e4cce2a var3) {
      try {
         String var4 = var3.recoverField_aa;
         var2.setFocusable(true);
         int var5 = var1.getResources().getIdentifier("fullWebView", "id", var1.getPackageName());
         if (var5 > 0) {
            ((LinearLayout)var2).removeView(var1.findViewById(var5));
         }

         this.recoverField_g = new WebView(var1.getApplicationContext());
         this.recoverField_g.setLayoutParams(new LayoutParams(-1, -1));
         this.recoverField_g.setBackgroundColor(Color.parseColor("#000000"));
         ((LinearLayout)var2).addView(this.recoverField_g);
         this.recoverField_h = (RelativeLayout)var1.findViewById(var1.getResources().getIdentifier("rlRichpushTitleBar", "id", var1.getPackageName()));
         this.recoverField_i = (TextView)var1.findViewById(var1.getResources().getIdentifier("tvRichpushTitle", "id", var1.getPackageName()));
         this.recoverField_j = (ImageButton)var1.findViewById(var1.getResources().getIdentifier("imgRichpushBtnBack", "id", var1.getPackageName()));
         this.recoverField_k = (ProgressBar)var1.findViewById(var1.getResources().getIdentifier("pushPrograssBar", "id", var1.getPackageName()));
         if (null == this.recoverField_g || null == this.recoverField_h || null == this.recoverField_i || null == this.recoverField_j) {
            Logger.recoverMethod_ee_e925fa09("PushActivityImpl", "Please use default code in jpush_webview_layout.xml!");
            var1.finish();
         }

         if (1 == var3.recoverField_ad) {
            this.recoverField_h.setVisibility(8);
            var1.getWindow().setFlags(1024, 1024);
         } else {
            this.recoverField_i.setText(var4);
            this.recoverField_j.setOnClickListener(this.recoverField_m);
            RecoverType9b4c5af2.recoverMethod_a_563fae02(var1.getApplicationContext(), this.recoverField_j, true, 10, 6, 10, 6);
         }

         this.recoverField_g.setScrollbarFadingEnabled(true);
         this.recoverField_g.setScrollBarStyle(33554432);
         WebSettings var6 = this.recoverField_g.getSettings();
         RecoverType9b4c5ae5.recoverMethod_c_4221005a(var6);
         RecoverType9b4c5ae5.recoverMethod_a_60006d18(this.recoverField_g);
         RecoverType9b4c5ae5.recoverMethod_a_4221005a(var6);
         var6.setSavePassword(false);
         recoverField_a = new RecoverType8afd8fa3(var1, var3);
         if (VERSION.SDK_INT >= 17) {
            Logger.recoverMethod_dd_e925fa09("PushActivityImpl", "Android sdk version greater than or equal to 17, Java—Js interact by annotation!");
            Logger.recoverMethod_d_e925fa09("PushActivityImpl", "Android sdk version greater than or equal to 17, Java—Js interact by annotation!");
            this.recoverMethod_b_9b75();
         }

         this.recoverField_g.setWebChromeClient(new RecoverType8afd8fa0("JPushWeb", HostJsScope.class, this.recoverField_k, this.recoverField_i));
         this.recoverField_l = new RecoverType9b558515(var3, var1);
         this.recoverField_l.recoverMethod_a_1385ff(this.recoverField_f);
         this.recoverField_g.setWebViewClient(this.recoverField_l);
         HostJsScope.setWebViewHelper(recoverField_a);
      } catch (Throwable var7) {
      }
   }

   private void recoverMethod_b_9b75() {
      Object[] var1 = new Object[2];
      Class[] var2 = new Class[2];
      var1[0] = recoverField_a;
      var1[1] = "JPushWeb";
      var2[0] = Object.class;
      var2[1] = String.class;

      try {
         RecoverType9b4c5aef.recoverMethod_a_222a2ac4(this.recoverField_g, "addJavascriptInterface", var2, var1);
      } catch (Throwable var4) {
         Logger.recoverMethod_e_e925fa09("PushActivityImpl", "addJavascriptInterface failed:" + var4.toString());
      }
   }

   public void recoverMethod_a_9b75() {
      try {
         if (this.recoverField_h != null && this.recoverField_h.getVisibility() == 8) {
            this.recoverField_h.setVisibility(0);
            this.recoverMethod_c_f9c5dd07((Activity)this.recoverField_h.getContext());
            this.recoverField_j.setOnClickListener(this.recoverField_m);
            if (this.recoverField_g != null) {
               this.recoverField_g.postDelayed(new RecoverType9b4c5aea("PushActivityImpl#showTitleBar") {
                  @Override
                  public void recoverMethod_a_9b75() {
                     try {
                        if (RecoverType9b558517.this.recoverField_g != null) {
                           RecoverType9b558517.this.recoverField_g.clearHistory();
                        }
                     } catch (Throwable var2) {
                     }
                  }
               }, 1000L);
            }
         }
      } catch (Throwable var2) {
      }
   }

   private void recoverMethod_c_f9c5dd07(Activity var1) {
      try {
         android.view.WindowManager.LayoutParams var2 = var1.getWindow().getAttributes();
         var2.flags &= -1025;
         var1.getWindow().setAttributes(var2);
         var1.getWindow().clearFlags(512);
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("PushActivityImpl", "quitFullScreen errno");
      }
   }
}
