package cn.jpush.android.p;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;

public class RecoverType2e4cfb34 {
   private static List recoverField_a = new LinkedList();
   private static List recoverField_b = new LinkedList();
   private static int recoverField_c = 10;
   private Context recoverField_d;
   private List recoverField_e = new ArrayList();
   private AtomicBoolean recoverField_f = new AtomicBoolean(false);

   private RecoverType2e4cfb34() {
   }

   public static RecoverType2e4cfb34 recoverMethod_a_cd35b0fc() {
      return RecoverType2e4cfb34.Membercefb02f1.recoverField_a;
   }

   private static boolean recoverMethod_b_134632(int var0) {
      return 40 == var0;
   }

   private static List recoverMethod_c_22f3aa59(int var0) {
      return recoverMethod_b_134632(var0) ? recoverField_a : recoverField_b;
   }

   private static String recoverMethod_d_47921032(int var0) {
      return recoverMethod_b_134632(var0) ? "notify_inapp_data" : "inapp_data";
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      this.recoverField_d = var1;
      this.recoverMethod_a_30cfce24(recoverField_a, "notify_inapp_data", true);
      this.recoverMethod_a_30cfce24(recoverField_b, "inapp_data", true);
   }

   private void recoverMethod_a_30cfce24(List var1, String var2, boolean var3) {
      if (var3) {
         var1.clear();
      }

      this.recoverMethod_b_1b7460f0(var2, var1);
      this.recoverMethod_d_61fb9e66(var1);
   }

   public RecoverType2e4cce2a recoverMethod_a_365d807c(String var1) {
      Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "deleteMessage msgID=" + var1);

      for (Object recoveryElement_65 : recoverField_a) {
         RecoverType2e4cce2a var3 = (RecoverType2e4cce2a)recoveryElement_65;
         if (var3.recoverField_d.equals(var1)) {
            return var3;
         }
      }

      for (Object recoveryElement_71 : recoverField_b) {
         RecoverType2e4cce2a var5 = (RecoverType2e4cce2a)recoveryElement_71;
         if (var5.recoverField_d.equals(var1)) {
            return var5;
         }
      }

      return null;
   }

   public void recoverMethod_a_73eedeba(RecoverType2e4cce2a var1) {
      if (null != var1) {
         int var2 = var1.recoverField_bt;
         recoverField_c = var1.recoverField_ay > 0 ? var1.recoverField_ay : 10;
         this.recoverMethod_a_73dd0bf5(recoverMethod_c_22f3aa59(var2), var1, recoverMethod_d_47921032(var2));
      }
   }

   private void recoverMethod_a_73dd0bf5(List var1, RecoverType2e4cce2a var2, String var3) {
      if (this.recoverMethod_a_6cccb7af(var2, var1)) {
         Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "the notify message cache already, messageId: " + var2.recoverField_d);
      } else {
         this.recoverMethod_b_61fb9e66(var1);
         var1.add(var2);
         this.recoverMethod_d_61fb9e66(var1);
         this.recoverMethod_c_1b7460f0(var3, var1);
      }
   }

   private void recoverMethod_b_61fb9e66(List var1) {
      if (var1.size() >= recoverField_c) {
         this.recoverMethod_c_61fb9e66(var1);

         for (int var2 = var1.size(); var2 >= recoverField_c; var2 = var1.size()) {
            RecoverType2e4cce2a var3 = (RecoverType2e4cce2a)var1.get(0);
            var1.remove(0);
            RecoverType9b4c5ae7.recoverMethod_d_5ffc00fd(this.recoverField_d, var3.recoverField_d);
            this.recoverMethod_a_9edab5a(var3, true);
         }

         this.recoverMethod_d_61fb9e66(var1);
      }
   }

   private void recoverMethod_c_61fb9e66(List var1) {
      if (var1 != null && var1.size() > 1) {
         Collections.sort(var1, new Comparator() {
            public int recoverMethod_a_1bd73032(RecoverType2e4cce2a var1, RecoverType2e4cce2a var2) {
               long var3 = var1.recoverField_bO - var2.recoverField_bO;
               return var3 > 0L ? 1 : -1;
            }

            // $VF: synthetic method
            @Override
            public int compare(Object var1, Object var2) {
               return this.recoverMethod_a_1bd73032((RecoverType2e4cce2a)var1, (RecoverType2e4cce2a)var2);
            }
         });
      }
   }

   private void recoverMethod_d_61fb9e66(List var1) {
      if (var1 != null && var1.size() > 1) {
         Collections.sort(var1, new Comparator() {
            public int recoverMethod_a_1bd73032(RecoverType2e4cce2a var1, RecoverType2e4cce2a var2) {
               int var3 = var1.recoverField_bu - var2.recoverField_bu;
               if (var3 != 0) {
                  return var3 > 0 ? -1 : 1;
               }

               long var4 = var1.recoverField_bO - var2.recoverField_bO;
               return var4 > 0L ? -1 : 1;
            }

            // $VF: synthetic method
            @Override
            public int compare(Object var1, Object var2) {
               return this.recoverMethod_a_1bd73032((RecoverType2e4cce2a)var1, (RecoverType2e4cce2a)var2);
            }
         });
      }
   }

   private boolean recoverMethod_a_6cccb7af(RecoverType2e4cce2a var1, List var2) {
      if (var2.size() <= 0) {
         return false;
      }

      if (null == var1) {
         return true;
      }

      for (Object recoveryElement_162 : var2) {
         RecoverType2e4cce2a var4 = (RecoverType2e4cce2a)recoveryElement_162;
         if (var4.recoverField_d.equals(var1.recoverField_d)) {
            return true;
         }
      }

      return false;
   }

   public synchronized int recoverMethod_b_9b68() {
      return recoverField_a.size() + recoverField_b.size();
   }

   public void recoverMethod_c_9b75() {
      this.recoverField_e.clear();
   }

   public List recoverMethod_a_22f3aa59(int var1) {
      synchronized (recoverField_a) {
         ArrayList var3 = new ArrayList();
         ArrayList var4 = new ArrayList();

         for (Object recoveryElement_184 : recoverField_a) {
            RecoverType2e4cce2a var6 = (RecoverType2e4cce2a)recoveryElement_184;
            if (var6 != null) {
               if (var6.recoverField_bP <= System.currentTimeMillis()) {
                  Logger.recoverMethod_dd_e925fa09("InAppCacheMgr", "getNotifyInApp out of date, endTime: " + var6.recoverField_bP);
                  this.recoverMethod_a_9edab5a(var6, false);
                  var4.add(var6);
               } else if (var6.recoverField_az == var1) {
                  var3.add(var6);
               }
            }
         }

         this.recoverMethod_a_61fb9e66(var4);
         return var3;
      }
   }

   public RecoverType2e4cce2a recoverMethod_b_365d807c(String var1) {
      RecoverType2e4cce2a var2 = null;

      try {
         for (Object recoveryElement_205 : recoverField_b) {
            RecoverType2e4cce2a var4 = (RecoverType2e4cce2a)recoveryElement_205;
            if (4 == var4.recoverField_bu) {
               if (this.recoverField_e.contains(var4.recoverField_bu)) {
                  Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "Full message had show, current type message show only once in this activity");
               } else {
                  if (TextUtils.isEmpty(var4.recoverField_bS)) {
                     Logger.recoverMethod_d_e925fa09("InAppCacheMgr", " full message target is null, so can show any activity");
                     var2 = var4;
                     break;
                  }

                  if (!TextUtils.isEmpty(var1) && var1.contains(var4.recoverField_bS)) {
                     Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "find full match target=" + var1);
                     var2 = var4;
                     break;
                  }
               }
            } else if (3 == var4.recoverField_bu) {
               if (this.recoverField_e.contains(var4.recoverField_bu)) {
                  Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "Interstitial message had show, current type message show only once in this activity");
               } else {
                  if (TextUtils.isEmpty(var4.recoverField_bS)) {
                     Logger.recoverMethod_d_e925fa09("InAppCacheMgr", " interstitial message target is null, so can show any activity");
                     var2 = var4;
                     break;
                  }

                  if (!TextUtils.isEmpty(var1) && var1.contains(var4.recoverField_bS)) {
                     Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "find interstitial match target=" + var1);
                     var2 = var4;
                     break;
                  }
               }
            } else if (2 == var4.recoverField_bu) {
               if (TextUtils.isEmpty(var4.recoverField_bS)) {
                  Logger.recoverMethod_d_e925fa09("InAppCacheMgr", " banner message target is null, so can show any activity");
                  var2 = var4;
               } else if (!TextUtils.isEmpty(var1) && var1.contains(var4.recoverField_bS)) {
                  Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "find banner match target=" + var1);
                  var2 = var4;
               }
               break;
            }
         }

         if (var2 == null && recoverField_a.size() > 0) {
            Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "find notify to inapp message match target=" + var1);
            var2 = (RecoverType2e4cce2a)recoverField_a.get(0);
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "get message throwable=" + var5);
      }

      if (var2 != null) {
         long var6 = var2.recoverField_bP;
         if (var6 <= System.currentTimeMillis()) {
            Logger.recoverMethod_dd_e925fa09("InAppCacheMgr", "getMessage  out of date, endTime: " + var6);
            this.recoverMethod_a_9edab5a(var2, false);
            this.recoverMethod_c_aad3b203(var2.recoverField_d);
            return this.recoverMethod_b_365d807c(var1);
         }
      }

      return var2;
   }

   public void recoverMethod_b_73eedeba(RecoverType2e4cce2a var1) {
      try {
         if (this.recoverField_e == null) {
            this.recoverField_e = new ArrayList();
         }

         this.recoverField_e.add(var1.recoverField_bu);
      } catch (Throwable var3) {
      }
   }

   public void recoverMethod_a_61fb9e66(List var1) {
      for (Object recoveryElement_283 : var1) {
         RecoverType2e4cce2a var3 = (RecoverType2e4cce2a)recoveryElement_283;
         recoverField_a.remove(var3);
      }

      this.recoverMethod_a_1b7460f0("notify_inapp_data", recoverField_a);
   }

   public boolean recoverMethod_c_aad3b203(String var1) {
      Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "deleteMessage msgID=" + var1);

      for (Object recoveryElement_293 : recoverField_a) {
         RecoverType2e4cce2a var3 = (RecoverType2e4cce2a)recoveryElement_293;
         if (var3.recoverField_d.equals(var1)) {
            recoverField_a.remove(var3);
            Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "deleteMessage  mNotifyInAppList remove msgID=" + var1);
            this.recoverMethod_a_1b7460f0("notify_inapp_data", recoverField_a);
            return true;
         }
      }

      for (Object recoveryElement_302 : recoverField_b) {
         RecoverType2e4cce2a var5 = (RecoverType2e4cce2a)recoveryElement_302;
         if (var5.recoverField_d.equals(var1)) {
            recoverField_b.remove(var5);
            Logger.recoverMethod_d_e925fa09("InAppCacheMgr", "deleteMessage  mInAppList remove msgID=" + var1);
            this.recoverMethod_a_1b7460f0("inapp_data", recoverField_b);
            return true;
         }
      }

      return false;
   }

   public void recoverMethod_a_1b7460f0(String var1, List var2) {
      this.recoverMethod_c_1b7460f0(var1, var2);
   }

   private void recoverMethod_b_1b7460f0(String var1, List var2) {
      Object var3 = null;

      try {
         File var4 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(this.recoverField_d, var1);
         if (null != var4 && var4.exists()) {
            String var5 = RecoverType9b4c5ae7.recoverMethod_c_af215df4(var4);
            if (!TextUtils.isEmpty(var5)) {
               JSONArray var6 = new JSONArray(var5);
               this.recoverField_f.set(false);

               for (int var7 = var6.length() - 1; var7 >= 0; var7--) {
                  RecoverType2e4cce2a var8 = RecoverType2e4cce2a.recoverMethod_b_365d807c(var6.get(var7).toString());
                  if (null != var8) {
                     long var9 = var8.recoverField_bP;
                     if (var9 <= System.currentTimeMillis()) {
                        Logger.recoverMethod_dd_e925fa09("InAppCacheMgr", "loadObjects message out of date, endTime: " + var9);
                        this.recoverField_f.set(true);
                        this.recoverMethod_a_9edab5a(var8, false);
                     } else {
                        var2.add(var8);
                        if (var2.size() == recoverField_c) {
                           Logger.recoverMethod_d_e925fa09(
                              "InAppCacheMgr",
                              "[loadObjects] reach max cache message count: " + recoverField_c + ", drop message count: " + (var6.length() - recoverField_c)
                           );
                           break;
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var14) {
         Logger.recoverMethod_w_e925fa09("InAppCacheMgr", "load objects error:" + var14.getMessage());
         this.recoverMethod_d_aad3b1ff(var1);
      } finally {
         if (this.recoverField_f.get()) {
            this.recoverMethod_a_1b7460f0(var1, var2);
         }

         RecoverType9b4c5af2.recoverMethod_a_d17885d6((Closeable)var3);
      }

      Logger.recoverMethod_i_e925fa09("InAppCacheMgr", "load object size: " + var2.size());
   }

   private void recoverMethod_d_aad3b1ff(String var1) {
      File var2 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(this.recoverField_d, var1);
      if (var2 == null) {
         Logger.recoverMethod_w_e925fa09("InAppCacheMgr", "can't get files dir");
      } else {
         if (var2.exists()) {
            var2.delete();
         }
      }
   }

   private void recoverMethod_c_1b7460f0(String var1, List var2) {
      Logger.recoverMethod_v_e925fa09("InAppCacheMgr", "Action - saveObjects");
      if (var2 == null || var2.size() < 0) {
         Logger.recoverMethod_w_e925fa09("InAppCacheMgr", "mObjectList is null or size is 0");
      } else if (var2.size() == 0) {
         this.recoverMethod_d_aad3b1ff(var1);
      } else {
         try {
            File var3 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(this.recoverField_d, var1);
            if (null == var3) {
               Logger.recoverMethod_w_e925fa09("InAppCacheMgr", "file is null");
               return;
            }

            new JSONArray();
            ArrayList var5 = new ArrayList();

            for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
               RecoverType2e4cce2a var7 = (RecoverType2e4cce2a)var2.get(var6);
               if (null != var7) {
                  long var8 = var7.recoverField_bP;
                  if (var8 <= System.currentTimeMillis()) {
                     var2.remove(var7);
                     Logger.recoverMethod_dd_e925fa09("InAppCacheMgr", "saveObjects message out of date, endTime: " + var7.recoverField_d);
                     this.recoverMethod_a_9edab5a(var7, false);
                  } else {
                     String var10 = var7.recoverMethod_c_73cf11cb();
                     var5.add(0, var10);
                     if (var5.size() == recoverField_c) {
                        Logger.recoverMethod_d_e925fa09(
                           "InAppCacheMgr",
                           "[saveObjects] reach max cache message count: " + recoverField_c + ", drop message count: " + (var2.size() - recoverField_c)
                        );
                        break;
                     }
                  }
               }
            }

            JSONArray var4 = new JSONArray(var5);
            RecoverType9b4c5ae7.recoverMethod_a_e925fa0d(var3.getAbsolutePath(), var4.toString());
         } catch (Throwable var11) {
            Logger.recoverMethod_ww_e925fa09("InAppCacheMgr", "save Objects  error:" + var11.getMessage());
         }
      }
   }

   private void recoverMethod_a_9edab5a(RecoverType2e4cce2a var1, boolean var2) {
      if (var2) {
         RecoverType8b455831.recoverMethod_a_510e8a7c(this.recoverField_d, var1.recoverField_d, 2007L, 4);
      } else if (40 == var1.recoverField_bt) {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1603L, this.recoverField_d);
      } else {
         RecoverType8b455831.recoverMethod_a_510e8a7c(this.recoverField_d, var1.recoverField_d, 2002L, 4);
      }
   }

   private static final class Membercefb02f1 {
      private static final RecoverType2e4cfb34 recoverField_a = new RecoverType2e4cfb34();
   }
}
