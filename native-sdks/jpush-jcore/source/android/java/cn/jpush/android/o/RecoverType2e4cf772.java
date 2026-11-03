package cn.jpush.android.o;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.ad.RecoverType9b4c5aeb;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.p.RecoverType2e4cfb33;
import cn.jpush.android.p.RecoverType2e4cfb34;
import cn.jpush.android.q.RecoverType2e4cfef4;
import cn.jpush.android.r.RecoverType2e4d02b6;
import cn.jpush.android.r.RecoverType2e4d02b7;
import cn.jpush.android.r.RecoverType2e4d02b8;
import cn.jpush.android.s.RecoverType2e4d0676;
import cn.jpush.android.s.RecoverType2e4d0678;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.u.RecoverType2e4d0df9;
import cn.jpush.android.x.RecoverType2e4d193c;
import cn.jpush.android.x.RecoverType2e4d193e;
import java.util.ArrayList;
import java.util.List;

public class RecoverType2e4cf772 implements RecoverType2e4d0df9.Membercf417876 {
   private static volatile RecoverType2e4cf772 recoverField_a;
   private RecoverType2e4d0df9 recoverField_b;
   private RecoverType2e4d02b8 recoverField_c = new RecoverType2e4d02b8();
   private RecoverType2e4d0678 recoverField_d;
   private String recoverField_e;
   private List recoverField_f = new ArrayList();

   public static RecoverType2e4cf772 recoverMethod_a_cd353c7e() {
      if (null == recoverField_a) {
         synchronized (RecoverType2e4cf772.class) {
            if (null == recoverField_a) {
               recoverField_a = new RecoverType2e4cf772();
            }
         }
      }

      return recoverField_a;
   }

   public void recoverMethod_a_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp inflate start");
      if (var1 != null && var2 != null) {
         try {
            RecoverType2e4d02b6.recoverMethod_a_1934cb4b(var1, var2, var2.recoverField_bt);
            if (this.recoverMethod_c_9b79()) {
               Logger.recoverMethod_d_e925fa09("InAppManager", "notify inapp is showing now, return ");
               return;
            }

            RecoverType2e4d0a37 var3 = RecoverType2e4d02b6.recoverMethod_a_2b43953e(var1, var2.recoverField_bt);
            if (null == var3) {
               this.recoverMethod_f_ae3b7eb8(var1, var2);
               Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp inflate failed, layout config is null");
               return;
            }

            this.recoverField_b = RecoverType2e4d0df9.recoverMethod_a_c9df8259(var1, var3, var2, var2.recoverField_bt);
            if (this.recoverField_b.recoverMethod_f_9b79()) {
               this.recoverField_b.recoverMethod_a_151159ee(this);
               if (40 != var2.recoverField_bt) {
                  Logger.recoverMethod_dd_e925fa09("InAppManager", "inapp start to show native");
                  RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_1934cb4b(var1, var2, 1002);
               } else {
                  Logger.recoverMethod_dd_e925fa09("InAppManager", "inapp start to show js web");
                  this.recoverMethod_i_ae3b7eb8(var1, var2);
               }

               Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp inflate succeed");
               return;
            }

            this.recoverMethod_f_ae3b7eb8(var1, var2);
            Logger.recoverMethod_ww_e925fa09("InAppManager", "notify inapp inflate failed");
         } catch (Throwable var4) {
            this.recoverMethod_f_ae3b7eb8(var1, var2);
            Logger.recoverMethod_w_e925fa09("InAppManager", "inflateNotifyInApp failed, " + var4.getMessage());
         }
      } else {
         Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp inflate failed, param is null, context: " + var1);
      }
   }

   @Override
   public void recoverMethod_a_e2da561b(Context var1, View var2, Object var3) {
      Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp dismiss");
      RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_13462e(1005);
      RecoverType2e4cfb33.recoverMethod_a_1a54e731(var1, System.currentTimeMillis());
      this.recoverMethod_c_f0554df3(var1);
      this.recoverMethod_b_f0554df3(var1);
   }

   @Override
   public void recoverMethod_a_b362c7ff(Context var1, View var2) {
   }

   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      if (var1 != null && this.recoverField_c != null && null != this.recoverField_b) {
         if (this.recoverField_c.recoverMethod_a_aad3b203(var2) && this.recoverField_c.recoverMethod_a_90f0588f(this.recoverField_b)) {
            Logger.recoverMethod_d_e925fa09("InAppManager", "[onActivityDestroy] dismiss showing notify inapp, activityName: " + var2);
            this.recoverMethod_c_f0554df3(var1);
         }
      }
   }

   public void recoverMethod_a_157a5ecb(Context var1, boolean var2, String var3) {
      Logger.recoverMethod_d_e925fa09("InAppManager", "onForegroundStateChange isForeground=" + var2 + " currentActivity=" + var3);
      this.recoverField_f.clear();
      this.recoverField_e = var3;
      RecoverType2e4cfb33.recoverMethod_a_1a54e731(var1, -1L);
      this.recoverMethod_a_f0554df3(var1);
      Bundle var4 = new Bundle();
      if (!TextUtils.isEmpty(var3)) {
         var4.putString("targert", var3);
      }

      RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_aac1ff8b(var1, var4, "sync_target");
      if (var2) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "change to foreground, deal cache notify inapp");
         RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_aac1ff8b(var1, null, "handle_cache_message");
      }
   }

   public void recoverMethod_a_19350b1c(Context var1, RecoverType2e4cce2a var2, boolean var3) {
      this.recoverMethod_d_9b75();
      if (var1 != null && var2 != null && var3) {
         Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp tpl load success will display");
         RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_1934cb4b(var1, var2, 1002);
      } else {
         this.recoverMethod_f_ae3b7eb8(var1, var2);
         Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp  load failed");
         Logger.recoverMethod_d_e925fa09("InAppManager", "onLoadFail state, context: " + var1 + ", isSuccess: " + var3 + ", entity: " + var2);
      }
   }

   public void recoverMethod_b_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         if (var1 == null || null == var2) {
            Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp not display, param is null, context: " + var1);
            this.recoverMethod_f_ae3b7eb8(var1, var2);
            return;
         }

         if (!RecoverType9b4c5ae5.recoverMethod_j_f0554df7(var1)) {
            Logger.recoverMethod_ww_e925fa09("InAppManager", "notify inapp not display in background");
            return;
         }

         if (RecoverType2e4d193e.recoverMethod_a_7555e60(var1, var2.recoverField_d, "", var2.recoverField_a)) {
            Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp is canceled already, not to show");
            this.recoverMethod_j_ae3b7eb8(var1, var2);
            return;
         }

         Activity var3 = RecoverType2e4d0676.recoverMethod_a_2b1cd295(var1);

         try {
            if (!TextUtils.isEmpty(var2.recoverField_bS) && !TextUtils.equals(var2.recoverField_bS, this.recoverField_e)) {
               Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp not target activity,not to show");
               return;
            }
         } catch (Throwable var5) {
         }

         if (null != var3 && this.recoverField_c.recoverMethod_a_cb3cf88d(var3, this.recoverField_b)) {
            Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp message display success");
            this.recoverMethod_k_ae3b7eb8(var1, var2);
         } else {
            Logger.recoverMethod_ww_e925fa09("InAppManager", "notify inapp message display failed");
            this.recoverMethod_f_ae3b7eb8(var1, var2);
         }
      } catch (Throwable var6) {
         this.recoverMethod_f_ae3b7eb8(var1, var2);
         Logger.recoverMethod_ww_e925fa09("InAppManager", "notify inapp show failed" + var6.getMessage());
      }
   }

   public boolean recoverMethod_b_9b79() {
      Logger.recoverMethod_d_e925fa09("InAppManager", "hadDeleteMessage size=" + (this.recoverField_f.size() > 0));
      return this.recoverField_f.size() > 0;
   }

   public void recoverMethod_b_5ffc00fd(Context var1, String var2) {
      if (var1 != null && !TextUtils.isEmpty(var2)) {
         String[] var3 = var2.split(",");

         for (String var7 : var3) {
            if (!TextUtils.isEmpty(var7)) {
               RecoverType2e4cce2a var8 = RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_a_365d807c(var7);
               boolean var9 = RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_c_aad3b203(var7);
               if (var9) {
                  String var10 = var7;
                  if (var3.length == 2) {
                     var10 = var3[1];
                  }

                  if (var8 != null) {
                     if (40 == var8.recoverField_bt) {
                        RecoverType8b455831.recoverMethod_a_36ca9ac3(var10, 1602L, var1);
                     } else {
                        RecoverType8b455831.recoverMethod_a_510e8a7c(var1, var10, 4005L, 4);
                     }
                  } else {
                     RecoverType8b455831.recoverMethod_a_36ca9ac3(var10, 1602L, var1);
                  }
               } else {
                  RecoverType2e4cce2a var11 = new RecoverType2e4cce2a();
                  var11.recoverField_d = var7;
                  RecoverType2e4d193c.recoverMethod_a_74ff4fff(var1, "cn.jpush.android.intent.NOTIFY_INAPP_MESSAGE_CANCEL", var11, null);
               }
            }
         }
      } else {
         Logger.recoverMethod_w_e925fa09("InAppManager", "[cancelNotifyInApp] unexpected param is null, cancelIds: " + var2);
      }
   }

   public void recoverMethod_c_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      if (var1 != null && var2 != null) {
         String var3 = var2.recoverField_d;
         if (this.recoverMethod_c_9b79() && null != this.recoverField_b) {
            RecoverType2e4cce2a var4 = this.recoverField_b.recoverMethod_j_cd303cc6();
            if (null == var4) {
               return;
            }

            Logger.recoverMethod_d_e925fa09("InAppManager", "notify inapp cancel, cancel_messageId: " + var3 + ", msgId: " + var4.recoverField_d);
            if (TextUtils.equals(var3, var4.recoverField_d)) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_1934cb4b(var1, var4, 1003);
               RecoverType8b455831.recoverMethod_a_36ca9ac3(var3, 1602L, var1);
               this.recoverMethod_b_f0554df3(var1);
               RecoverType2e4cfb33.recoverMethod_a_1a54e731(var1, System.currentTimeMillis());
            }
         }
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (var1 != null) {
         this.recoverMethod_d_f0554df3(var1);
      }

      RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_13462e(1005);
   }

   public void recoverMethod_d_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      Logger.recoverMethod_dd_e925fa09("InAppManager", "notify inapp click");
      if (var2 != null) {
         RecoverType9b4c5aeb.recoverMethod_a_d8c46bdb().recoverMethod_a_ae3b7eb8(var1, var2);
      }

      RecoverType2e4cfb33.recoverMethod_a_1a54e731(var1, System.currentTimeMillis());
      this.recoverMethod_c_f0554df3(var1);
      this.recoverMethod_b_f0554df3(var1);
   }

   public void recoverMethod_a_1a552341(Context var1, boolean var2) {
      Bundle var3 = new Bundle();
      var3.putBoolean("result", var2);
      RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_aac1ff8b(var1, var3, "sync_view_show_success");
   }

   private void recoverMethod_i_ae3b7eb8(final Context var1, final RecoverType2e4cce2a var2) {
      Logger.recoverMethod_d_e925fa09("InAppManager", "start h5 load timeout timer listener");
      this.recoverMethod_d_9b75();
      this.recoverField_d = new RecoverType2e4d0678();
      this.recoverField_d.recoverMethod_a_957fb98f(new RecoverType2e4d0678.Membercf254d35() {
         @Override
         public void recoverMethod_a_9b75() {
            Logger.recoverMethod_ww_e925fa09("InAppManager", "js load callback timeout");
            RecoverType2e4cf772.this.recoverMethod_f_ae3b7eb8(var1, var2);
         }
      }, 10000L, 1000L);
   }

   private void recoverMethod_d_9b75() {
      if (null != this.recoverField_d) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "cancel js load timer");
         this.recoverField_d.recoverMethod_a_9b75();
      }
   }

   public boolean recoverMethod_c_9b79() {
      return null != this.recoverField_c && this.recoverField_c.recoverMethod_b_90f0588f(this.recoverField_b);
   }

   private void recoverMethod_j_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleCancelMessage  msgId=" + var2.recoverField_d);
         if (40 == var2.recoverField_bt) {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1602L, var1);
         } else {
            RecoverType8b455831.recoverMethod_a_510e8a7c(var1, var2.recoverField_d, 2000L, 4);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleCancelMessage  throwable=" + var4);
      }
   }

   private void recoverMethod_k_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleShowSuccess  msgId=" + var2.recoverField_d);
         RecoverType2e4d193c.recoverMethod_a_74ff4fff(var1, "cn.jpush.android.intent.IN_APP_MSG_SHOW", var2, null);
         this.recoverMethod_n_ae3b7eb8(var1, var2);
         if (40 != var2.recoverField_bt) {
            this.recoverField_b.recoverMethod_d_9b75();
            if (30 == var2.recoverField_bt) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1934cb4b(var1, var2, 1005);
            }
         }

         this.recoverMethod_l_ae3b7eb8(var1, var2);
         this.recoverField_f.add(var2.recoverField_d);
         this.recoverMethod_a_1a552341(var1, true);
         RecoverType2e4d02b6.recoverMethod_a_1a54e731(var1, var2.recoverField_bb);
      } catch (Throwable var4) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleCancelMessage  throwable=" + var4);
      }
   }

   private void recoverMethod_l_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      if (40 == var2.recoverField_bt) {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1600L, var1);
      } else {
         RecoverType8b455831.recoverMethod_a_510e8a7c(var1, var2.recoverField_d, 3000L, 4);
      }
   }

   public void recoverMethod_e_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleMessageFailed  msgId=" + var2.recoverField_d);
         this.recoverMethod_n_ae3b7eb8(var1, var2);
         this.recoverMethod_m_ae3b7eb8(var1, var2);
         this.recoverMethod_b_f0554df3(var1);
      } catch (Throwable var4) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleMessageFailed  throwable=" + var4);
      }
   }

   public void recoverMethod_f_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleShowFailed  msgId=" + var2.recoverField_d);
         this.recoverMethod_a_1a552341(var1, false);
         this.recoverMethod_n_ae3b7eb8(var1, var2);
         this.recoverMethod_c_f0554df3(var1);
         this.recoverMethod_m_ae3b7eb8(var1, var2);
         this.recoverMethod_b_f0554df3(var1);
      } catch (Throwable var4) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleShowFailed  throwable=" + var4);
      }
   }

   private void recoverMethod_m_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      if (40 == var2.recoverField_bt) {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1604L, var1);
      } else {
         RecoverType8b455831.recoverMethod_a_510e8a7c(var1, var2.recoverField_d, 3012L, 4);
      }
   }

   private void recoverMethod_b_f0554df3(Context var1) {
      try {
         if (var1 == null) {
            Logger.recoverMethod_w_e925fa09("InAppManager", "dealNextCacheMessage param is null, context: " + var1);
            return;
         }

         if (RecoverType9b4c5ae5.recoverMethod_j_f0554df7(var1) && !this.recoverMethod_c_9b79()) {
            RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_aac1ff8b(var1, null, "handle_cache_message");
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleNextCacheMessage  throwable=" + var3);
      }
   }

   private void recoverMethod_n_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         if (var1 == null || var2 == null) {
            Logger.recoverMethod_w_e925fa09("InAppManager", "checkIsNeedSeedToNotification  context: " + var1);
            return;
         }

         int var3 = var2.recoverField_az;
         if (var3 == 104) {
            Logger.recoverMethod_d_e925fa09(
               "InAppManager",
               "start process notify message, msgType: " + var2.recoverField_az + ", processName: " + RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var1)
            );
            Bundle var4 = new Bundle();
            var4.putString("content", var2 != null ? var2.recoverMethod_c_73cf11cb() : "");
            RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_aac1ff8b(var1, var4, "handle_notify_inapp_message");
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "checkIsNeedSeedToNotification  throwable=" + var5);
      }
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      try {
         this.recoverMethod_d_9b75();
         if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            this.recoverMethod_d_f0554df3(var1);
         } else {
            RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_1934cb4b(var1, this.recoverField_b.recoverMethod_j_cd303cc6(), 1003);
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp dismiss failed, " + var3.getMessage());
      }
   }

   private void recoverMethod_d_f0554df3(Context var1) {
      if (null != this.recoverField_c && this.recoverField_b != null) {
         try {
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
               this.recoverField_c.recoverMethod_b_cb3cf889(var1, this.recoverField_b);
               this.recoverField_b.recoverMethod_a_151159ee(null);
               this.recoverField_b = null;
            } else {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_1934cb4b(var1, this.recoverField_b.recoverMethod_j_cd303cc6(), 1003);
            }

            Logger.recoverMethod_d_e925fa09("InAppManager", "notify inapp release completed");
         } catch (Throwable var3) {
            Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp release failed, " + var3.getMessage());
         }
      } else {
         Logger.recoverMethod_d_e925fa09("InAppManager", "notify inapp destroy wm is null");
      }
   }

   public void recoverMethod_g_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      try {
         if (this.recoverField_b != null) {
            this.recoverField_b.recoverMethod_g_9b75();
            RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1934cb4b(var1, var2, 1005);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_d_e925fa09("InAppManager", "handleCancelMessage  throwable=" + var4);
      }
   }

   private static boolean recoverMethod_o_ae3b7ebc(Context var0, RecoverType2e4cce2a var1) {
      if (var0 != null && var1 != null) {
         RecoverType2e4cce2a var2;
         if (40 == var1.recoverField_bt) {
            var2 = RecoverType2e4d02b7.recoverMethod_a_f0b11163(var0, var1);
         } else {
            var2 = RecoverType2e4d02b7.recoverMethod_b_f0b11163(var0, var1);
         }

         if (null == var2) {
            Logger.recoverMethod_ww_e925fa09("InAppManager", "notify inapp tpl dl failed");
            return false;
         } else {
            return true;
         }
      } else {
         Logger.recoverMethod_w_e925fa09("InAppManager", "notify inapp handle param is null");
         return false;
      }
   }

   public static class Memberceece7af extends RecoverType9b4c5aea {
      Context recoverField_a;
      RecoverType2e4cce2a recoverField_b;

      public Memberceece7af(Context var1, RecoverType2e4cce2a var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            if (RecoverType2e4cf772.recoverMethod_o_ae3b7ebc(this.recoverField_a, this.recoverField_b)) {
               RecoverType2e4cfef4.recoverMethod_a_cd36253c()
                  .recoverMethod_b_e6d36a82(this.recoverField_a, this.recoverField_b, "cn.jpush.android.intent.NOTIFY_INAPP_MESSAGE_ARRIVED");
               return;
            }

            RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1a552341(this.recoverField_a, false);
            RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_e6d36a82(this.recoverField_a, this.recoverField_b, "handle_message_failed");
         } catch (Throwable var2) {
            Logger.recoverMethod_ww_e925fa09("InAppManager", "notify inapp handle failed, " + var2.getMessage());
         }
      }
   }
}
