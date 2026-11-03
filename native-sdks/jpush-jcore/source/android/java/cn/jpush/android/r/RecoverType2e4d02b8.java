package cn.jpush.android.r;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.u.RecoverType2e4d0df9;

public class RecoverType2e4d02b8 {
   private boolean recoverField_a = false;
   private String recoverField_b = "";
   private WindowManager recoverField_c;

   public boolean recoverMethod_a_cb3cf88d(Context var1, RecoverType2e4d0df9 var2) {
      try {
         this.recoverField_b = "";
         if (var2 == null) {
            Logger.recoverMethod_w_e925fa09("InAppWinManager", "notify inapp show failed, wrapper is null");
            return false;
         }

         RecoverType2e4d0a37 var3 = var2.recoverMethod_k_cd378259();
         View var4 = var2.recoverMethod_e_d08d2077();
         if (var4 == null) {
            Logger.recoverMethod_w_e925fa09("InAppWinManager", "notify inapp show failed, wbView is null");
            return false;
         }

         this.recoverField_c = this.recoverMethod_b_439957df(var1);
         if (var1 instanceof Activity) {
            this.recoverField_b = var1.getClass().getCanonicalName();
            var1 = var1.getApplicationContext();
         }

         if (null == this.recoverField_c) {
            Logger.recoverMethod_w_e925fa09("InAppWinManager", "notify inapp show failed, wm is null");
            return false;
         } else {
            LayoutParams var5 = this.recoverMethod_a_ee1e739b(var1, var3);
            if (var5 == null) {
               Logger.recoverMethod_w_e925fa09("InAppWinManager", "notify inapp show failed, layout param is null");
               return false;
            } else {
               this.recoverField_c.addView(var4, var5);
               var2.recoverMethod_h_9b75();
               this.recoverField_a = true;
               return true;
            }
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("InAppWinManager", "notify inapp show failed, " + var6.getMessage());
         return false;
      }
   }

   public boolean recoverMethod_a_aad3b203(String var1) {
      Logger.recoverMethod_d_e925fa09("InAppWinManager", "[checkShouldDestroy], showActivityName: " + this.recoverField_b + ", curActivityName: " + var1);
      return TextUtils.equals(var1, this.recoverField_b);
   }

   public boolean recoverMethod_a_90f0588f(RecoverType2e4d0df9 var1) {
      if (null != var1 && var1.recoverMethod_e_d08d2077() != null) {
         boolean var2 = var1.recoverMethod_e_d08d2077().isShown();
         Logger.recoverMethod_d_e925fa09("InAppWinManager", "webview is displayed, status: " + var2 + ", isDisplayed: " + this.recoverField_a);
         return var2 && this.recoverField_a;
      } else {
         return false;
      }
   }

   public boolean recoverMethod_b_90f0588f(RecoverType2e4d0df9 var1) {
      return null != var1 && var1.recoverMethod_e_d08d2077() != null;
   }

   public void recoverMethod_b_cb3cf889(Context var1, RecoverType2e4d0df9 var2) {
      try {
         if (null == var2 || null == var1) {
            Logger.recoverMethod_w_e925fa09("InAppWinManager", "destroy param is null, wrapper: " + var2);
            return;
         }

         View var3 = var2.recoverMethod_e_d08d2077();
         if (var3 != null) {
            if (null != this.recoverField_c && this.recoverMethod_a_90f0588f(var2)) {
               this.recoverField_c.removeViewImmediate(var3);
               this.recoverField_a = false;
               Logger.recoverMethod_d_e925fa09("InAppWinManager", "[destroy] - window manager removeViewImmediate view succeed.");
            }

            var2.recoverMethod_c_9b75();
         }

         this.recoverField_b = "";
      } catch (Throwable var4) {
         Logger.recoverMethod_w_e925fa09("InAppWinManager", "notify inapp destroy failed, " + var4.getMessage());
      }
   }

   private LayoutParams recoverMethod_a_ee1e739b(Context var1, RecoverType2e4d0a37 var2) {
      try {
         if (null != var2 && null != var1) {
            int var3 = var2.recoverMethod_a_9b68() | 131072 | 32 | 8;
            short var4 = 1003;
            LayoutParams var5 = new LayoutParams(var2.recoverMethod_c_9b68(), var2.recoverMethod_d_9b68(), var4, var3, -3);
            var5.x = 0;
            var5.y = recoverMethod_a_f0554de6(var1);
            var5.dimAmount = 0.3F;
            var5.gravity = var2.recoverMethod_b_9b68();
            var5.windowAnimations = 0;
            return var5;
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("InAppWinManager", "[getLayoutParams] error." + var6.getMessage());
      }

      return null;
   }

   private WindowManager recoverMethod_b_439957df(Context var1) {
      if (null == var1) {
         Logger.recoverMethod_w_e925fa09("InAppWinManager", "getWindowManager context is null");
         return null;
      } else {
         return (WindowManager)var1.getSystemService("window");
      }
   }

   public static int recoverMethod_a_f0554de6(Context var0) {
      int var1 = 48;

      try {
         if (var0 != null) {
            Resources var2 = var0.getResources();
            int var3 = var0.getResources().getIdentifier("status_bar_height", "dimen", "android");
            var1 = var2.getDimensionPixelSize(var3);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_w_e925fa09("InAppWinManager", "get status bar height error, " + var4.getMessage());
      }

      Logger.recoverMethod_d_e925fa09("InAppWinManager", "status bar height: " + var1);
      return var1;
   }
}
