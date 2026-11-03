package cn.jpush.android.b;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.n.RecoverType2e4cf3b1;
import cn.jpush.android.n.RecoverType2e4cf3b3;
import cn.jpush.android.n.RecoverType2e4cf3b5;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecoverType2e4cc6a5 extends RecoverType2e4cc6a7 {
   private long recoverField_b;
   private LocationManager recoverField_c;
   private Handler recoverField_d;
   private long recoverField_e = 900000L;
   private long recoverField_f = 30000L;
   private String recoverField_g;
   private boolean recoverField_h = false;
   @SuppressLint("MissingPermission")
   private LocationListener recoverField_i = new LocationListener() {
      @Override
      public void onLocationChanged(Location var1) {
         RecoverType2e4cc6a5.this.recoverField_c.removeUpdates(this);
         RecoverType2e4cc6a5.this.recoverField_j.set(false);
         RecoverType2e4cc6a5.this.recoverMethod_a_48e8163d(var1);
      }

      @Override
      public void onStatusChanged(String var1, int var2, Bundle var3) {
      }

      @Override
      public void onProviderEnabled(String var1) {
      }

      @Override
      public void onProviderDisabled(String var1) {
      }
   };
   private final AtomicBoolean recoverField_j = new AtomicBoolean();

   public RecoverType2e4cc6a5(Context var1) {
      super(var1);
      this.recoverField_c = (LocationManager)var1.getSystemService("location");
      this.recoverField_e = RecoverTypea2146d45.recoverMethod_c_1a54e725(var1, 900000L);
      this.recoverField_b = RecoverTypea2146d45.recoverMethod_c_1a54e725(var1, -1L);
   }

   @Override
   protected synchronized void recoverMethod_a_9b75() {
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "geofence size:" + RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_c_9b68());
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "stop listen geofence");
      if (this.recoverField_h) {
         if (this.recoverField_d != null) {
            this.recoverField_d.removeMessages(1000);
         }

         this.recoverField_h = false;
      }
   }

   @Override
   protected void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1) {
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Geofence create success, id=" + var1.recoverField_a);
      if (this.recoverField_h && this.recoverField_d != null) {
         this.recoverField_d.sendEmptyMessage(1002);
      }
   }

   @Override
   protected void recoverMethod_a_3317e147(RecoverType2e4cc6a6 var1, RecoverType2e4cc6a6 var2) {
      if (var1 != null) {
         Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Geofence update success, id=" + var1.recoverField_a);
      }

      if (this.recoverField_h && this.recoverField_d != null) {
         this.recoverField_d.sendEmptyMessage(1002);
      }
   }

   @Override
   protected void recoverMethod_b_708444be(RecoverType2e4cc6a6 var1) {
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Geofence delete success, id=" + var1.recoverField_a);
   }

   @Override
   public synchronized void recoverMethod_b_9b75() {
      boolean var1 = JCoreHelper.geoEnable(this.recoverField_a);
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "start listen geofence lbsEnable=" + var1);
      if (!var1) {
         Logger.recoverMethod_ww_e925fa09("CustomGeofenAction", "lbs is disable!");
      } else if (this.recoverField_h) {
         Logger.recoverMethod_ii_e925fa09("CustomGeofenAction", "geofence is running!");
      } else if (RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_c_9b68() == 0) {
         Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "No geofence,not need listen");
      } else {
         if (this.recoverField_d == null) {
            this.recoverMethod_c_9b75();
         }

         this.recoverMethod_b_1349ef(0L);
         this.recoverField_h = true;
      }
   }

   private void recoverMethod_c_9b75() {
      try {
         HandlerThread var1 = new HandlerThread("jg_cgf_thread") {
            @Override
            public void run() {
               try {
                  super.run();
               } catch (RuntimeException var2) {
                  Logger.recoverMethod_e_e925fa09(
                     "CustomGeofenAction", "handler thread run e:" + var2 + "  t=" + Thread.currentThread().getName() + "_" + Thread.currentThread().getId()
                  );
               }
            }
         };
         var1.start();
         this.recoverField_d = new Handler(var1.getLooper()) {
            @SuppressLint("MissingPermission")
            @Override
            public void handleMessage(Message var1) {
               switch (var1.what) {
                  case 1000:
                     Logger.recoverMethod_d_e925fa09("CustomGeofenAction", "period task tid " + Thread.currentThread().getId());
                     RecoverType2e4cc6a5.this.recoverMethod_b_1349ef(RecoverType2e4cc6a5.this.recoverField_e);
                  case 1002:
                     RecoverType2e4cc6a5.this.recoverMethod_e_9b75();
                     break;
                  case 1001:
                     RecoverType2e4cc6a5.this.recoverMethod_d_9b75();

                     try {
                        if (RecoverType2e4cc6a5.this.recoverField_g != null && RecoverType2e4cc6a5.this.recoverField_g.equals("gps")) {
                           Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "GPS provider time out!");
                           RecoverType2e4cc6a5.this.recoverField_g = "network";
                           if (RecoverType2e4cc6a5.this.recoverField_c.isProviderEnabled(RecoverType2e4cc6a5.this.recoverField_g)) {
                              RecoverType2e4cc6a5.this.recoverField_c
                                 .requestLocationUpdates(RecoverType2e4cc6a5.this.recoverField_g, 2000L, 0.0F, RecoverType2e4cc6a5.this.recoverField_i);
                              RecoverType2e4cc6a5.this.recoverField_d.sendEmptyMessageDelayed(1001, RecoverType2e4cc6a5.this.recoverField_f / 2L);
                              return;
                           }

                           Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Network provider is disabled");
                        } else {
                           Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Network provider time out!");
                        }
                     } catch (Throwable var3) {
                        Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "request location error#" + var3);
                     }

                     RecoverType2e4cc6a5.this.recoverField_j.set(false);
               }
            }
         };
      } catch (Throwable var2) {
         Logger.recoverMethod_ww_e925fa09("CustomGeofenAction", "init geofence handler failed:" + var2);
      }
   }

   @SuppressLint("MissingPermission")
   private void recoverMethod_d_9b75() {
      try {
         if (null != this.recoverField_i) {
            if (null != this.recoverField_c) {
               this.recoverField_c.removeUpdates(this.recoverField_i);
            } else {
               Logger.recoverMethod_w_e925fa09("CustomGeofenAction", "locationManager is null , do nothing!");
            }
         } else {
            Logger.recoverMethod_w_e925fa09("CustomGeofenAction", "Location listener is null , do nothing!");
         }
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("CustomGeofenAction", "remove location listener failed  e:" + var2.getMessage());
      }
   }

   private void recoverMethod_e_9b75() {
      Logger.recoverMethod_d_e925fa09("CustomGeofenAction", "try locate...");
      if (!RecoverType2e4cf3b3.recoverMethod_a_c5f37608(this.recoverField_a, this.recoverField_c)) {
         Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "No enabled provider");
      } else {
         Logger.recoverMethod_d_e925fa09(
            "CustomGeofenAction", "sdk int:" + VERSION.SDK_INT + ",targetSdkVersion:" + this.recoverField_a.getApplicationInfo().targetSdkVersion
         );
         if (RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_c_9b68() <= 0) {
            this.recoverMethod_a_9b75();
         } else if (this.recoverField_j.get()) {
            Logger.recoverMethod_d_e925fa09("CustomGeofenAction", "isLocating...");
         } else {
            this.recoverMethod_f_9b75();
         }
      }
   }

   @SuppressLint("MissingPermission")
   private void recoverMethod_f_9b75() {
      try {
         if (this.recoverField_c != null) {
            Location var1 = RecoverType2e4cf3b3.recoverMethod_a_3a51be92(this.recoverField_a, this.recoverField_c, true);
            if (System.currentTimeMillis() - (var1 != null ? var1.getTime() : 0L) < 30000L) {
               Logger.recoverMethod_dd_e925fa09(
                  "CustomGeofenAction",
                  "need not restart gpslocation,the time with last:" + (System.currentTimeMillis() - (var1 != null ? var1.getTime() : 0L))
               );
               this.recoverMethod_a_48e8163d(var1);
               return;
            }

            this.recoverField_g = null;
            if (this.recoverField_c.isProviderEnabled("gps")) {
               this.recoverField_g = "gps";
            } else if (this.recoverField_c.isProviderEnabled("network")) {
               this.recoverField_g = "network";
            }

            Logger.recoverMethod_d_e925fa09("CustomGeofenAction", "provider " + this.recoverField_g);
            if (TextUtils.isEmpty(this.recoverField_g)) {
               Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "not gps nor network provider,stop scan geofence");
               return;
            }

            this.recoverField_j.set(true);
            this.recoverField_c.requestLocationUpdates(this.recoverField_g, 2000L, 0.0F, this.recoverField_i);
            this.recoverField_d.sendEmptyMessageDelayed(1001, this.recoverField_f);
         } else {
            Logger.recoverMethod_ww_e925fa09("CustomGeofenAction", "locationManager is null");
         }
      } catch (SecurityException var2) {
         Logger.recoverMethod_ww_e925fa09("CustomGeofenAction", "No suitable permission when get last known location!");
      } catch (Throwable var3) {
         Logger.recoverMethod_ww_e925fa09("CustomGeofenAction", "The provider is illegal argument!" + var3);
      }
   }

   private void recoverMethod_a_48e8163d(Location var1) {
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "current location:" + var1);
      if (this.recoverField_d != null && this.recoverField_d.hasMessages(1001)) {
         this.recoverField_d.removeMessages(1001);
      }

      if (var1 != null) {
         if (RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_c_9b68() > 0) {
            double var2 = var1.getLongitude();
            double var4 = var1.getLatitude();
            boolean var6 = false;
            LinkedHashMap var7 = RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_b_d5e96a91();
            Iterator var8 = var7.entrySet().iterator();

            while (var8.hasNext()) {
               Entry var9 = (Entry)var8.next();
               RecoverType2e4cc6a6 var10 = (RecoverType2e4cc6a6)var9.getValue();
               if (var10.recoverField_e * 1000L <= System.currentTimeMillis()) {
                  Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Out of date geofence " + var10.recoverField_a);
                  var8.remove();
                  RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_708444be(var10);
               } else {
                  double var11 = RecoverType9b4c5af2.recoverMethod_a_bbe0f863(var2, var4, var10.recoverField_f, var10.recoverField_g);
                  Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", var10.recoverField_a + " distance to center:" + var11);
                  String var13;
                  if (var11 <= var10.recoverField_b) {
                     var13 = "in";
                  } else {
                     var13 = "out";
                  }

                  if (Math.abs(var11 - var10.recoverField_b) < 1000.0) {
                     var6 = true;
                  }

                  Logger.recoverMethod_d_e925fa09("CustomGeofenAction", "lastStatus:" + var10.recoverField_h + ",currentStatus:" + var13);
                  Logger.recoverMethod_d_e925fa09(
                     "CustomGeofenAction", "geofence status :" + var10.recoverField_h + ",currentStatus:" + var13 + ",geoStatus:" + var13
                  );
                  if ((var10.recoverField_c.equals("inside") || !var13.equals(var10.recoverField_h))
                     && (!var10.recoverField_c.equals("inside") || var13 != "out")) {
                     if ((var10.recoverField_c.equals("inside") || var10.recoverField_h != null && var13.equals(var10.recoverField_c))
                        && this.recoverMethod_d_708444c2(var10)) {
                        this.recoverMethod_c_708444be(var10);
                        this.recoverMethod_a_47fde754(var10, var1);
                        RecoverType2e4cf3b1.recoverMethod_a_510bbd7d(this.recoverField_a, var10.recoverMethod_a_a7dbfe3().toString(), var4, var2);
                        if (!var10.recoverField_d) {
                           Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "No repeat geofence " + var10.recoverField_a);
                           var8.remove();
                           RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_708444be(var10);
                        }
                     }

                     var10.recoverField_h = var13;
                     RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_cdf45195(var10.recoverField_a, var10.recoverMethod_a_a7dbfe3(), false);
                  }
               }
            }

            if (this.recoverField_b == -1L) {
               int var14 = var6 ? 180000 : 900000;
               if (this.recoverField_e != var14) {
                  this.recoverField_e = var14;
                  Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "need update scan peroid to:" + this.recoverField_e);
                  this.recoverMethod_b_1349ef(var14);
               }
            }
         }
      }
   }

   private void recoverMethod_c_708444be(RecoverType2e4cc6a6 var1) {
      long var2 = System.currentTimeMillis();
      var1.recoverField_o = var2;
      Date var4 = new Date(var2);
      String var5 = RecoverType9b4c5af2.recoverMethod_a_5318097a("yyyy-DDD").format(var4);
      if (var5.equals(var1.recoverField_r)) {
         var1.recoverField_s++;
      } else {
         var1.recoverField_r = var5;
         var1.recoverField_s = 1;
      }

      String var6 = RecoverType9b4c5af2.recoverMethod_a_5318097a("yyyy-ww").format(var4);
      if (var6.equals(var1.recoverField_p)) {
         var1.recoverField_q++;
      } else {
         var1.recoverField_p = var6;
         var1.recoverField_q = 1;
      }
   }

   private boolean recoverMethod_d_708444c2(RecoverType2e4cc6a6 var1) {
      if (var1.recoverField_o <= 0L) {
         return true;
      }

      if (!var1.recoverField_d) {
         Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "can't repeat geofence");
         return false;
      }

      if (var1.recoverField_n <= 0 && var1.recoverField_c.equals("inside")) {
         var1.recoverField_n = 3600;
      }

      long var2 = System.currentTimeMillis();
      if (var1.recoverField_n > 0 && var1.recoverField_o > 0L && var2 < var1.recoverField_o + var1.recoverField_n * 1000) {
         Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "in " + var1.recoverField_n + "s, can't repeat geofence");
         return false;
      }

      if (var1.recoverField_m > 0 && !TextUtils.isEmpty(var1.recoverField_r)) {
         String var4 = RecoverType9b4c5af2.recoverMethod_a_5318097a("yyyy-DDD").format(new Date(var2));
         if (var1.recoverField_r.equals(var4) && var1.recoverField_s >= var1.recoverField_m) {
            Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "today already repeat enough:" + var1.recoverField_s + "/" + var1.recoverField_m);
            return false;
         }
      }

      if (var1.recoverField_l > 0 && !TextUtils.isEmpty(var1.recoverField_p)) {
         String var5 = RecoverType9b4c5af2.recoverMethod_a_5318097a("yyyy-ww").format(new Date(var2));
         if (var1.recoverField_p.equals(var5) && var1.recoverField_q >= var1.recoverField_l) {
            Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "this week already repeat enough:" + var1.recoverField_q + "/" + var1.recoverField_l);
            return false;
         }
      }

      return true;
   }

   private void recoverMethod_b_1349ef(long var1) {
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Scan geofence after " + var1 + "ms");
      if (this.recoverField_d != null) {
         if (this.recoverField_d.hasMessages(1000)) {
            this.recoverField_d.removeMessages(1000);
         }

         this.recoverField_d.sendEmptyMessageDelayed(1000, var1);
      }
   }

   @Override
   public void recoverMethod_a_1349ef(long var1) {
      Logger.recoverMethod_dd_e925fa09("CustomGeofenAction", "Set geofence interval " + var1);
      this.recoverField_e = var1;
      this.recoverField_b = var1;
      RecoverTypea2146d45.recoverMethod_b_1a54e731(this.recoverField_a, var1);
   }
}
