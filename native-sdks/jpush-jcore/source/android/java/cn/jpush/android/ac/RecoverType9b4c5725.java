package cn.jpush.android.ac;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.helper.RecoverType8b455834;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.z.RecoverType2e4d20be;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RecoverType9b4c5725 {
   private Map recoverField_a = new HashMap();
   private Map recoverField_b = new HashMap();
   private Map recoverField_c = new HashMap();
   private static volatile RecoverType9b4c5725 recoverField_d;

   private RecoverType9b4c5725() {
   }

   public static RecoverType9b4c5725 recoverMethod_a_d8c3f6e1() {
      if (recoverField_d == null) {
         synchronized (RecoverType9b4c5725.class) {
            if (recoverField_d == null) {
               recoverField_d = new RecoverType9b4c5725();
            }
         }
      }

      return recoverField_d;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      try {
         Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "reUploadToken cache size=" + this.recoverField_b.size() + " map=" + this.recoverField_b);
         if (!this.recoverField_b.isEmpty()) {
            for (Object recoveryElement_42 : this.recoverField_b.entrySet()) {
               Entry var3 = (Entry)recoveryElement_42;
               Byte var4 = (Byte)var3.getKey();
               HashMap var5 = (HashMap)var3.getValue();

               for (Object recoveryElement_46 : var5.entrySet()) {
                  Entry var7 = (Entry)recoveryElement_46;
                  String var8 = (String)var7.getKey();
                  Boolean var9 = (Boolean)var7.getValue();
                  Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "reUploadToken platform=" + var4 + " regID=" + var8 + " unBindToken=" + var9);
                  this.recoverMethod_a_fd48ae81(var1, var4, var8, var9);
               }
            }
         }
      } catch (Throwable var10) {
      }
   }

   public synchronized void recoverMethod_a_fd48ae81(Context var1, byte var2, String var3, boolean var4) {
      if (var2 != 0) {
         if (!JPushConstants.isTcpConnected()) {
            HashMap var5 = new HashMap();
            var5.put(var3, var4);
            this.recoverField_b.put(var2, var5);
            RecoverType8b455831.recoverMethod_a_36ca9ac3("99999363732041973", 1109L, var1);
            Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "tcp disconnected,cache data=" + this.recoverField_b);
            Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "tcp disconnected,cache size=" + this.recoverField_b.size());
            return;
         }

         if (this.recoverField_a.containsKey(var2)
            && TextUtils.equals(((RecoverType9b4c5725.Memberf99326a2)this.recoverField_a.get(var2)).recoverField_b, var3)) {
            Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "same regid request, drop it");
            return;
         }

         if (this.recoverField_c.containsKey(var2) && TextUtils.equals((CharSequence)this.recoverField_c.get(var2), var3)) {
            Logger.recoverMethod_ke_e925fa09("PluginPlatformRidUpdate", "rom is " + var2 + " regId had report success,not need report again");
            return;
         }

         this.recoverMethod_b_fd48ae81(var1, var2, var3, var4);
      } else {
         Logger.recoverMethod_w_e925fa09("PluginPlatformRidUpdate", "unknown plugin platform type");
      }
   }

   private void recoverMethod_b_fd48ae81(Context var1, byte var2, String var3, boolean var4) {
      long var5 = RecoverType8b455834.recoverMethod_a_9b69();
      Logger.recoverMethod_v_e925fa09(
         "PluginPlatformRidUpdate", "sendRidRequst regid:" + var3 + ",rid:" + var5 + ",whichPlatform:" + var2 + " unBindToken:" + var4
      );
      byte[] var7 = RecoverType2e4d20be.recoverMethod_a_52995be6(var3, var2, var4);
      RecoverType9b4c5725.Memberf99326a2 var8 = new RecoverType9b4c5725.Memberf99326a2(var2, var3, var5, var7);
      this.recoverField_a.put(var2, var8);
      this.recoverMethod_a_4ac6384c(var1, var8);
   }

   private synchronized void recoverMethod_a_4ac6384c(Context var1, RecoverType9b4c5725.Memberf99326a2 var2) {
      JCoreHelper.sendRequest(var1, "JPUSH", 27, 2, var2.recoverField_c, 10000L, var2.recoverField_d);
   }

   public void recoverMethod_a_304871f2(Context var1, long var2, int var4) {
      RecoverType9b4c5725.Memberf99326a2 var5 = this.recoverMethod_a_f50ed8be(var2);
      Logger.recoverMethod_ki_e925fa09("PluginPlatformRidUpdate", "onUpdateRidFailed rid:" + var2 + ",errorCode:" + var4 + " ,pluginPlatformRegIDBean:" + var5);
      if (var5 != null) {
         if (var5.recoverField_e < 3) {
            var5.recoverField_e++;
            this.recoverMethod_a_4ac6384c(var1, var5);
         } else {
            Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "updateRegId failed");
            this.recoverField_a.remove(var5.recoverField_a);
         }
      }
   }

   private RecoverType9b4c5725.Memberf99326a2 recoverMethod_a_f50ed8be(long var1) {
      for (Object recoveryElement_117 : this.recoverField_a.entrySet()) {
         Entry var5 = (Entry)recoveryElement_117;
         if (((RecoverType9b4c5725.Memberf99326a2)var5.getValue()).recoverField_c == var1) {
            return (RecoverType9b4c5725.Memberf99326a2)var5.getValue();
         }
      }

      Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "can not find uploadBean by rid");
      return null;
   }

   public void recoverMethod_a_1a54e731(Context var1, long var2) {
      RecoverType9b4c5725.Memberf99326a2 var4 = this.recoverMethod_a_f50ed8be(var2);
      Logger.recoverMethod_ki_e925fa09("PluginPlatformRidUpdate", "onUpdateRidSuccess rid:" + var2 + " ,pluginPlatformRegIDBean:" + var4);
      if (var4 != null) {
         RecoverTypea0793a19.set(var1, Key.ThirdPush_RegID(var4.recoverField_a).set(var4.recoverField_b));
         RecoverTypea0793a19.set(var1, Key.ThirdPush_RegUpload(var4.recoverField_a).set(true));
         RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_1a54c929(var1, var4.recoverField_a);
         this.recoverField_a.remove(var4.recoverField_a);
         this.recoverField_b.remove(var4.recoverField_a);
         this.recoverField_c.put(var4.recoverField_a, var4.recoverField_b);
         RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_b_3f52113a(var1, var4.recoverField_a, var4.recoverField_b);
      }
   }

   public void recoverMethod_b_1a54e731(Context var1, long var2) {
      RecoverType9b4c5725.Memberf99326a2 var4 = this.recoverMethod_a_f50ed8be(var2);
      Logger.recoverMethod_ki_e925fa09("PluginPlatformRidUpdate", "onUpdateRidTimeout rid:" + var2 + " ,pluginPlatformRegIDBean:" + var4);
      if (var4 != null) {
         if (var4.recoverField_e < 3) {
            var4.recoverField_e++;
            this.recoverMethod_a_4ac6384c(var1, var4);
         } else {
            Logger.recoverMethod_d_e925fa09("PluginPlatformRidUpdate", "updateRegId failed by timeout");
            this.recoverField_a.remove(var4.recoverField_a);
         }
      }
   }

   private class Memberf99326a2 {
      public byte recoverField_a;
      public String recoverField_b;
      public long recoverField_c;
      public byte[] recoverField_d;
      public int recoverField_e;

      public Memberf99326a2(byte var2, String var3, long var4, byte[] var6) {
         this.recoverField_a = var2;
         this.recoverField_b = var3;
         this.recoverField_c = var4;
         this.recoverField_d = var6;
         this.recoverField_e = 0;
      }

      @Override
      public String toString() {
         return "PluginPlatformRegIDBean{pluginPlatformType="
            + this.recoverField_a
            + ", regid='"
            + this.recoverField_b
            + '\''
            + ", rid="
            + this.recoverField_c
            + ", retryCount="
            + this.recoverField_e
            + '}';
      }
   }
}
