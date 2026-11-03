package cn.jpush.android.n;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.b.RecoverType2e4cc6a6;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.m.RecoverType2e4ceff0;
import java.io.Closeable;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4cf3b5 {
   protected Context recoverField_a;
   protected int recoverField_b = 100;
   public static int recoverField_c = 200;
   private volatile LinkedHashMap recoverField_d;
   private volatile LinkedHashMap recoverField_e;
   private RecoverType2e4cf3b5.Membercededf32 recoverField_f;

   private RecoverType2e4cf3b5() {
   }

   public static RecoverType2e4cf3b5 recoverMethod_a_cd34c89b() {
      return RecoverType2e4cf3b5.Membercededf33.recoverField_a;
   }

   public synchronized void recoverMethod_a_f0554df3(Context var1) {
      if (var1 != null) {
         this.recoverField_a = var1.getApplicationContext();
         this.recoverField_b = RecoverTypea2146d45.recoverMethod_e_1a54e363(var1, 10);
         this.recoverMethod_b_f0554df3(var1);
         this.recoverMethod_f_13462e(this.recoverField_b);
      }
   }

   private void recoverMethod_b_f0554df3(Context var1) {
      boolean var2 = false;
      boolean var3 = false;

      try {
         File var4 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var1, "jpush_geofence_v5_report");
         if (var4 != null && var4.exists()) {
            this.recoverField_e = this.recoverMethod_a_34f4318c(var1, 2);
         }

         File var5 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var1, "jpush_geofence_v5");
         if (var5 != null && var5.exists()) {
            this.recoverField_d = this.recoverMethod_a_34f4318c(var1, 1);
         } else {
            int var6 = (Integer)RecoverTypea0793a19.get(var1, Key.PushVerCode());
            if (var6 >= 318 && var6 <= 330) {
               LinkedHashMap var17 = (LinkedHashMap)RecoverType9b4c5ae7.recoverMethod_a_bc5e8086(
                  RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var1, "jpush_geofence_v3")
               );
               if (var17 != null && !var17.isEmpty()) {
                  Logger.recoverMethod_d_e925fa09("GeofenceDataHelper", "old geofences:" + var17);
                  LinkedHashMap var8 = new LinkedHashMap();

                  for (Object recoveryElement_70 : var17.entrySet()) {
                     Entry var10 = (Entry)recoveryElement_70;
                     var8.put((String)var10.getKey(), recoverMethod_a_fbe43da5((RecoverType2e4cce2a)var10.getValue()));
                  }

                  if (!var8.isEmpty()) {
                     this.recoverField_d = var8;
                     var2 = true;
                  }
               }
            } else if (var6 > 330 && var6 < 350) {
               File var7 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var1, "jpush_geofence_v4");
               this.recoverField_d = (LinkedHashMap)RecoverType9b4c5ae7.recoverMethod_a_bc5e8086(var7);
               var2 = this.recoverField_d != null && !this.recoverField_d.isEmpty();
            }
         }

         RecoverTypea0793a19.set(var1, Key.PushVerCode().set(RecoverType2e4ceff0.recoverField_a));
      } catch (Throwable var11) {
         Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "recover geofence failed:" + var11);
      }

      if (this.recoverField_e == null) {
         this.recoverField_e = new LinkedHashMap();
      }

      if (this.recoverField_d == null) {
         this.recoverField_d = new LinkedHashMap();
      } else {
         Set var12 = this.recoverField_d.entrySet();
         Iterator var14 = var12.iterator();

         while (var14.hasNext()) {
            Entry var16 = (Entry)var14.next();
            RecoverType2e4cc6a6 var18 = (RecoverType2e4cc6a6)var16.getValue();
            if (var18 != null && var18.recoverField_i == 2) {
               Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "save Geofence " + var18.recoverField_a + "to report!");
               this.recoverField_e.put(var18.recoverField_a, var18);
               var14.remove();
               var3 = true;
            }
         }
      }

      boolean var13 = this.recoverMethod_b_134632(2);
      boolean var15 = this.recoverMethod_b_134632(1);
      if (var2 || var15) {
         this.recoverMethod_c_13462e(1);
      }

      if (var3 || var13) {
         this.recoverMethod_c_13462e(2);
      }

      Logger.recoverMethod_dd_e925fa09(
         "GeofenceDataHelper", "Recover show geofence size:" + this.recoverField_d.size() + "  report geofence size:" + this.recoverField_e.size()
      );
   }

   private boolean recoverMethod_b_134632(int var1) {
      LinkedHashMap var2 = var1 == 2 ? this.recoverField_e : this.recoverField_d;
      if (var2 != null && !var2.isEmpty()) {
         Set var3 = var2.entrySet();
         Iterator var4 = var3.iterator();
         boolean var5 = false;

         while (var4.hasNext()) {
            Entry var6 = (Entry)var4.next();
            RecoverType2e4cc6a6 var7 = (RecoverType2e4cc6a6)var6.getValue();
            if (var7 != null && var7.recoverField_e * 1000L <= System.currentTimeMillis()) {
               var5 = true;
               Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "Geofence " + var7.recoverField_a + " is out of date!");
               var4.remove();
            }
         }

         return var5;
      } else {
         return false;
      }
   }

   private LinkedHashMap recoverMethod_a_34f4318c(Context var1, int var2) {
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("GeofenceDataHelper", "unexcepted , context is null");
         return null;
      }

      LinkedHashMap var3 = new LinkedHashMap();
      Object var4 = null;

      try {
         File var5 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var1, var2 == 2 ? "jpush_geofence_v5_report" : "jpush_geofence_v5");
         if (var5.exists()) {
            String var6 = RecoverType9b4c5ae7.recoverMethod_c_af215df4(var5);
            if (!TextUtils.isEmpty(var6)) {
               JSONObject var7 = new JSONObject(var6);
               Iterator var8 = var7.keys();

               while (var8.hasNext()) {
                  String var9 = (String)var8.next();
                  JSONObject var10 = var7.getJSONObject(var9);
                  RecoverType2e4cc6a6 var11 = RecoverType2e4cc6a6.recoverMethod_a_8c09e354(var1, var10);
                  if (var11 != null) {
                     var3.put(var11.recoverField_a, var11);
                  }
               }
            }
         }
      } catch (Throwable var15) {
         Logger.recoverMethod_w_e925fa09("GeofenceDataHelper", "load objects error:" + var15.getMessage());
      } finally {
         RecoverType9b4c5af2.recoverMethod_a_d17885d6((Closeable)var4);
      }

      return var3;
   }

   private void recoverMethod_c_13462e(int var1) {
      if (var1 == 0) {
         this.recoverMethod_c_13462e(2);
         this.recoverMethod_c_13462e(1);
      } else {
         String var3 = "";
         LinkedHashMap var2;
         if (var1 == 2) {
            var2 = this.recoverField_e;
            var3 = "jpush_geofence_v5_report";
            Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "save report geofence to file");
         } else {
            var2 = this.recoverField_d;
            var3 = "jpush_geofence_v5";
            Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "save show geofence to file");
         }

         if (var2 != null) {
            try {
               File var4 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(this.recoverField_a, var3);
               JSONObject var5 = new JSONObject();

               for (Object recoveryElement_209 : var2.entrySet()) {
                  Entry var7 = (Entry)recoveryElement_209;
                  String var8 = (String)var7.getKey();
                  RecoverType2e4cc6a6 var9 = (RecoverType2e4cc6a6)var7.getValue();
                  var5.put(var8, var9.recoverMethod_a_a7dbfe3());
               }

               RecoverType9b4c5ae7.recoverMethod_a_e925fa0d(var4.getAbsolutePath(), var5.toString());
            } catch (Throwable var10) {
               Logger.recoverMethod_ww_e925fa09("GeofenceDataHelper", "save geo to file error:" + var10.getMessage());
            }
         }
      }
   }

   public LinkedHashMap recoverMethod_b_d5e96a91() {
      LinkedHashMap var1 = new LinkedHashMap();
      var1.putAll(this.recoverField_e);
      var1.putAll(this.recoverField_d);
      return var1;
   }

   public int recoverMethod_c_9b68() {
      return this.recoverField_d.size() + this.recoverField_e.size();
   }

   private LinkedHashMap recoverMethod_d_da05ba4a(int var1) {
      return var1 == 2 ? this.recoverField_e : this.recoverField_d;
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      Logger.recoverMethod_d_e925fa09("GeofenceDataHelper", "will delete geofence=" + var1);
      if (!TextUtils.isEmpty(var1)) {
         RecoverType2e4cc6a6 var2 = (RecoverType2e4cc6a6)this.recoverField_d.get(var1);
         RecoverType2e4cc6a6 var3 = (RecoverType2e4cc6a6)this.recoverField_e.get(var1);
         if (var2 != null) {
            this.recoverMethod_a_708444be(var2);
         }

         if (var3 != null) {
            this.recoverMethod_a_708444be(var3);
         }

         if (var2 == null && var3 == null) {
            Logger.recoverMethod_d_e925fa09("GeofenceDataHelper", "geofence=" + var1 + " already not exists");
         }
      }
   }

   public void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1) {
      if (var1 == null || !TextUtils.isEmpty(var1.recoverField_a)) {
         int var2 = var1.recoverField_i;
         String var3 = var1.recoverField_a;
         LinkedHashMap var4 = this.recoverMethod_d_da05ba4a(var2);
         Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "Before delete.Current geofence size:" + var4.size() + ",type:" + var2);
         RecoverType2e4cc6a6 var5 = (RecoverType2e4cc6a6)var4.remove(var3);
         if (var5 != null) {
            this.recoverMethod_e_13462e(var2);
            if (this.recoverField_f != null) {
               this.recoverField_f.recoverMethod_b_708444be(var1);
            }
         } else {
            Logger.recoverMethod_d_e925fa09("GeofenceDataHelper", "geofence=" + var1.recoverField_a + " already not exists");
         }
      }
   }

   public void recoverMethod_a_cdf45195(String var1, JSONObject var2, boolean var3) {
      if (!TextUtils.isEmpty(var1) && var2 != null && var2.length() != 0) {
         if (var2.has("type")) {
            this.recoverMethod_a_f08ed990(var1, var2, var2.optInt("type", 1), var3);
         } else {
            this.recoverMethod_a_f08ed990(var1, var2, 1, var3);
            this.recoverMethod_a_f08ed990(var1, var2, 2, var3);
         }
      }
   }

   private void recoverMethod_a_f08ed990(String var1, JSONObject var2, int var3, boolean var4) {
      LinkedHashMap var5 = this.recoverMethod_d_da05ba4a(var3);
      Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "Before update.Current geofence size:" + var5.size() + ",type:" + var3);
      RecoverType2e4cc6a6 var6 = (RecoverType2e4cc6a6)var5.get(var1);
      if (var6 == null) {
         Logger.recoverMethod_ww_e925fa09("GeofenceDataHelper", "can not update geofence because origin one not found");
      } else {
         var6.recoverMethod_b_be7fc169(var2);
         if (var6.recoverField_e * 1000L <= System.currentTimeMillis()) {
            var5.remove(var1);
            this.recoverMethod_a_708444be(var6);
         } else if (var4 && this.recoverField_f != null) {
            this.recoverField_f.recoverMethod_a_3317e147(var6, var6);
         }

         this.recoverMethod_e_13462e(var3);
      }
   }

   public void recoverMethod_b_708444be(RecoverType2e4cc6a6 var1) {
      if (this.recoverMethod_c_708444c2(var1)) {
         int var2 = var1.recoverField_i;
         LinkedHashMap var3 = this.recoverMethod_d_da05ba4a(var2);
         Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "Before add.Current geofence size:" + var3.size() + ",type:" + var2);
         RecoverType2e4cc6a6 var4 = (RecoverType2e4cc6a6)var3.get(var1.recoverField_a);
         if (var4 != null) {
            if (var1.recoverField_e * 1000L <= System.currentTimeMillis()) {
               var3.remove(var1.recoverField_a);
               this.recoverMethod_a_708444be(var4);
            } else {
               var1.recoverMethod_a_708444be(var4);
               var3.put(var1.recoverField_a, var1);
               if (this.recoverField_f != null) {
                  this.recoverField_f.recoverMethod_a_3317e147(var4, var1);
               }
            }
         } else {
            if (var2 == 2 && var3.size() >= recoverField_c) {
               Logger.recoverMethod_dd_e925fa09(
                  "GeofenceDataHelper", "Report geofence size limit :" + recoverField_c + ",won't add geofence" + var1.recoverField_a
               );
               return;
            }

            if (var1.recoverField_e * 1000L <= System.currentTimeMillis()) {
               Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "The geofence " + var1.recoverField_a + " is out of date, will not create!");
               return;
            }

            this.recoverMethod_f_13462e(this.recoverField_b - 1);
            var3.put(var1.recoverField_a, var1);
            if (this.recoverField_f != null) {
               this.recoverField_f.recoverMethod_a_708444be(var1);
            }
         }

         this.recoverMethod_e_13462e(var2);
      }
   }

   public void recoverMethod_a_48551f8f(JSONArray var1) {
      if (var1 != null) {
         LinkedHashMap var2 = new LinkedHashMap();

         try {
            for (int var3 = 0; var3 < (var1.length() <= recoverField_c ? var1.length() : recoverField_c); var3++) {
               RecoverType2e4cc6a6 var4 = RecoverType2e4cc6a6.recoverMethod_a_ef7f2e56(var1.getJSONObject(var3));
               var2.put(var4.recoverField_a, var4);
            }
         } catch (Throwable var5) {
            Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "Replace report geofences failed:" + var5.getMessage());
            return;
         }

         this.recoverField_e = var2;
         this.recoverMethod_e_13462e(2);
         if (this.recoverField_f != null) {
            this.recoverField_f.recoverMethod_a_3317e147(null, null);
         }
      }
   }

   public void recoverMethod_a_4307a9b2(RecoverType2e4cf3b5.Membercededf32 var1) {
      this.recoverField_f = var1;
   }

   private void recoverMethod_e_13462e(int var1) {
      this.recoverMethod_c_13462e(var1);
      if (this.recoverField_f != null) {
         this.recoverField_f.recoverMethod_a_1385ff(this.recoverField_d.isEmpty() && this.recoverField_e.isEmpty());
      }
   }

   private boolean recoverMethod_c_708444c2(RecoverType2e4cc6a6 var1) {
      if (var1 != null
         && !TextUtils.isEmpty(var1.recoverField_a)
         && var1.recoverField_b != -1L
         && !(var1.recoverField_g < -90.0)
         && !(var1.recoverField_g > 90.0)
         && !(var1.recoverField_f < -180.0)
         && !(var1.recoverField_f > 180.0)) {
         return true;
      }

      Logger.recoverMethod_ww_e925fa09("GeofenceDataHelper", "The geofence is invalid, won't operate!");
      return false;
   }

   private static RecoverType2e4cc6a6 recoverMethod_a_fbe43da5(RecoverType2e4cce2a var0) {
      RecoverType2e4cc6a6 var1 = new RecoverType2e4cc6a6();
      var1.recoverField_a = var0.recoverField_ak;
      var1.recoverField_b = var0.recoverField_al;
      var1.recoverField_c = var0.recoverField_am;
      var1.recoverField_d = var0.recoverField_an;
      var1.recoverField_e = var0.recoverField_ap;
      var1.recoverField_f = var0.recoverField_aq;
      var1.recoverField_g = var0.recoverField_ar;
      var1.recoverField_h = var0.recoverField_as;
      return var1;
   }

   public void recoverMethod_a_13462e(int var1) {
      RecoverTypea2146d45.recoverMethod_d_1a54e370(this.recoverField_a, var1);
      this.recoverField_b = var1;
      this.recoverMethod_f_13462e(this.recoverField_b);
   }

   private void recoverMethod_f_13462e(int var1) {
      int var2 = this.recoverField_d.size();
      if (var2 > var1) {
         Logger.recoverMethod_dd_e925fa09("GeofenceDataHelper", "ShowGeofence count= " + var2 + ",limit=" + var1 + ",need remove earliest");

         for (Iterator var3 = this.recoverField_d.values().iterator(); var3.hasNext() && var2 > var1; var2--) {
            RecoverType2e4cc6a6 var4 = (RecoverType2e4cc6a6)var3.next();
            if (var4 != null) {
               this.recoverMethod_a_708444be(var4);
            }

            var3.remove();
         }
      }
   }

   public interface Membercededf32 {
      void recoverMethod_a_1385ff(boolean var1);

      void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1);

      void recoverMethod_a_3317e147(RecoverType2e4cc6a6 var1, RecoverType2e4cc6a6 var2);

      void recoverMethod_b_708444be(RecoverType2e4cc6a6 var1);
   }

   private static final class Membercededf33 {
      private static final RecoverType2e4cf3b5 recoverField_a = new RecoverType2e4cf3b5();
   }
}
