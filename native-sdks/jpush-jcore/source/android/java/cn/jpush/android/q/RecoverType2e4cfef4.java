package cn.jpush.android.q;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.d.RecoverType2e4cce28;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.o.RecoverType2e4cf772;
import cn.jpush.android.p.RecoverType2e4cfb34;
import cn.jpush.android.r.RecoverType2e4d02b6;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.util.List;

public class RecoverType2e4cfef4 {
   private static volatile RecoverType2e4cfef4 recoverField_a;
   private RecoverType2e4cfef4.Membercf0916b1 recoverField_b;
   private String recoverField_c;
   private RecoverType2e4cce2a recoverField_d;

   public static RecoverType2e4cfef4 recoverMethod_a_cd36253c() {
      if (null == recoverField_a) {
         synchronized (RecoverType2e4cfef4.class) {
            if (recoverField_a == null) {
               recoverField_a = new RecoverType2e4cfef4();
            }
         }
      }

      return recoverField_a;
   }

   private synchronized Handler recoverMethod_b_de962d31() {
      if (this.recoverField_b == null) {
         try {
            this.recoverField_b = new RecoverType2e4cfef4.Membercf0916b1(Looper.getMainLooper());
         } catch (Throwable var4) {
            Logger.recoverMethod_w_e925fa09("InAppConnect", "build handle failed, " + var4.getMessage());

            try {
               this.recoverField_b = new RecoverType2e4cfef4.Membercf0916b1(Looper.getMainLooper());
            } catch (Throwable var3) {
            }
         }
      }

      return this.recoverField_b;
   }

   public void recoverMethod_a_1a552341(Context var1, boolean var2) {
      if (var2) {
         RecoverType9b4c5ae7.recoverMethod_d_5ffc00fd(var1, this.recoverField_d.recoverField_d);
         RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_b_73eedeba(this.recoverField_d);
      }

      RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_c_aad3b203(this.recoverField_d.recoverField_d);
   }

   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_b_5ffc00fd(var1, var2);
   }

   public void recoverMethod_a_e6d36a82(Context var1, RecoverType2e4cce2a var2, String var3) {
      Logger.recoverMethod_d_e925fa09("InAppConnect", "processOnRemoteProcess mCurrentTarget=" + this.recoverField_c);
      if (var2 == null) {
         Logger.recoverMethod_d_e925fa09("InAppConnect", "parsing json failed");
         RecoverType8b455831.recoverMethod_a_510e8a7c(var1, var3, 2004L, 4);
      } else {
         if (40 != var2.recoverField_bt) {
            RecoverType8b455831.recoverMethod_a_510e8a7c(var1, var2.recoverField_d, 2005L, 4);
         }

         RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_a_73eedeba(var2);
         recoverMethod_a_cd36253c().recoverMethod_b_e6d36a82(var1, var2, "check_can_show_message");
      }
   }

   public void recoverMethod_a_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      if (var1 != null && var2 != null) {
         Logger.recoverMethod_d_e925fa09("InAppConnect", "start to process notify message");
         RecoverType2e4cce28.recoverMethod_a_ae3b7eb8(var1, var2);
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_b_9b68() <= 0) {
         Logger.recoverMethod_d_e925fa09("InAppConnect", "no notify inapp cache to deal");
      } else {
         this.recoverMethod_b_f0554df3(var1);
         Logger.recoverMethod_d_e925fa09("InAppConnect", "checkCacheOnRemoteProcess target=" + this.recoverField_c);
         this.recoverField_d = RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_b_365d807c(this.recoverField_c);
         if (null != this.recoverField_d) {
            Logger.recoverMethod_d_e925fa09("InAppConnect", "start to deal cache notify inapp message");
            JCoreHelper.majorExecutor(var1, "InAppConnect", new RecoverType2e4cf772.Memberceece7af(var1, this.recoverField_d));
         } else {
            Logger.recoverMethod_d_e925fa09("InAppConnect", "no find  message match target");
         }
      }
   }

   private void recoverMethod_b_f0554df3(Context var1) {
      List var2 = RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_a_22f3aa59(104);
      if (null != var2 && !var2.isEmpty() && 1 == RecoverType2e4d193c.recoverMethod_d_f0554de6(var1)) {
         Logger.recoverMethod_d_e925fa09("InAppConnect", "is Notification Enabled ON");

         for (Object recoveryElement_114 : var2) {
            RecoverType2e4cce2a var4 = (RecoverType2e4cce2a)recoveryElement_114;
            Logger.recoverMethod_d_e925fa09("InAppConnect", "is show:" + var4.recoverField_d);
            this.recoverMethod_a_ae3b7eb8(var1, var4);
         }

         RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_a_61fb9e66(var2);
      }
   }

   public void recoverMethod_b_5ffc00fd(Context var1, String var2) {
      Logger.recoverMethod_d_e925fa09("InAppConnect", "onTargetChange localTarget=" + this.recoverField_c + " newTarget=" + var2);
      this.recoverField_c = var2;
      RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_c_9b75();
   }

   public void recoverMethod_a_1934cb4b(Context var1, RecoverType2e4cce2a var2, int var3) {
      this.recoverMethod_b_1934cb4b(var1, var2, var3);
   }

   public void recoverMethod_b_e6d36a82(Context var1, RecoverType2e4cce2a var2, String var3) {
      RecoverType2e4d193c.recoverMethod_a_74ff4fff(var1, var3, var2, null);
   }

   public void recoverMethod_a_aac1ff8b(Context var1, Bundle var2, String var3) {
      if (var1 != null) {
         if (var2 == null) {
            var2 = new Bundle();
         }

         JCoreHelper.runActionWithService(var1, "JPUSH", var3, var2);
      }
   }

   public void recoverMethod_a_13462e(int var1) {
      if (this.recoverMethod_b_de962d31().hasMessages(var1)) {
         Logger.recoverMethod_d_e925fa09("InAppConnect", "removeHandler remove waht=" + var1);
         this.recoverMethod_b_de962d31().removeMessages(var1);
      }
   }

   public void recoverMethod_b_1934cb4b(Context var1, RecoverType2e4cce2a var2, int var3) {
      try {
         if (var3 == 1003 || var3 == 1002) {
            this.recoverMethod_a_13462e(1002);
         }

         long var4;
         if (1 == var2.recoverField_bu) {
            long var6 = var2 == null ? 0L : var2.recoverField_aR;
            var4 = var3 != 1002 ? 0L : RecoverType2e4d02b6.recoverMethod_a_3048b1b7(var1, var6, true);
         } else if (4 == var2.recoverField_bu) {
            long var9 = var2 == null
               ? 0L
               : (
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_b_9b79()
                     ? RecoverType2e4d02b6.recoverMethod_b_1a54e725(var1, var2.recoverField_bR)
                     : 0L
               );
            var4 = var3 != 1002 ? 0L : RecoverType2e4d02b6.recoverMethod_a_3048b1b7(var1, var9, false);
         } else {
            long var10 = var2 == null
               ? 0L
               : (
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_b_9b79()
                     ? RecoverType2e4d02b6.recoverMethod_b_1a54e725(var1, var2.recoverField_bR)
                     : var2.recoverField_bQ
               );
            var4 = var3 != 1002 ? 0L : RecoverType2e4d02b6.recoverMethod_a_3048b1b7(var1, var10, false);
         }

         if (var3 == 1005) {
            var4 = 1000L;
         }

         Bundle var11 = new Bundle();
         var11.putString("n_content", var2 != null ? var2.recoverMethod_c_73cf11cb() : "");
         Message var7 = new Message();
         var7.what = var3;
         var7.arg1 = (int)var4;
         var7.setData(var11);
         this.recoverMethod_b_de962d31().sendMessageDelayed(var7, var4);
         Logger.recoverMethod_dd_e925fa09("InAppConnect", "notify inapp send to handler succeed, delayTime: " + var4 + ", what: " + var3);
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("InAppConnect", "sendToMainHandler failed, " + var8.getMessage());
      }
   }

   static class Membercf0916b1 extends Handler {
      public Membercf0916b1(Looper var1) {
         super(var1);
      }

      @Override
      public void handleMessage(Message var1) {
         super.handleMessage(var1);
         RecoverType2e4cce2a var2 = null;

         try {
            Logger.recoverMethod_d_e925fa09("InAppConnect", "send message to handle, what: " + var1.what);
            Bundle var3 = var1.getData();
            if (null != var3) {
               String var4 = var3.getString("n_content");
               if (!TextUtils.isEmpty(var4)) {
                  var2 = RecoverType2e4cce2a.recoverMethod_b_365d807c(var4);
               }
            }

            Context var7 = JPushConstants.mApplicationContext;
            switch (var1.what) {
               case 1001:
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_ae3b7eb8(var7, var2);
                  break;
               case 1002:
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_b_ae3b7eb8(var7, var2);
                  break;
               case 1003:
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_f0554df3(var7);
                  break;
               case 1004:
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_c_ae3b7eb8(var7, var2);
                  break;
               case 1005:
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_g_ae3b7eb8(var7, var2);
                  break;
               case 1006:
                  if (RecoverType9b4c5ae5.recoverMethod_j_f0554df7(var7)) {
                     boolean var5 = RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_c_9b79();
                     if (!var5) {
                        Logger.recoverMethod_d_e925fa09("InAppConnect", "no message is showing,start to handle cache message");
                        RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_aac1ff8b(var7, null, "handle_cache_message");
                     } else {
                        Logger.recoverMethod_d_e925fa09("InAppConnect", "inapp message is showing,do nothing ");
                     }
                  } else {
                     Logger.recoverMethod_d_e925fa09("InAppConnect", " inapp message do not show in background,do nothing");
                  }
                  break;
               case 1007:
                  RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_e_ae3b7eb8(var7, var2);
            }
         } catch (Throwable var6) {
            Logger.recoverMethod_w_e925fa09("InAppConnect", "handleMessage failed, " + var6.getMessage());
         }
      }
   }
}
