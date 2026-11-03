package cn.jiguang.aa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import cn.jiguang.ab.RecoverTypeaf2a7199;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.g.RecoverType5a69853;

@SuppressLint("MissingPermission")
class RecoverTypeaf2a6dda {
   private Context recoverField_b;
   private LocationManager recoverField_c;
   private RecoverTypeaf2a7199 recoverField_d;
   private static final Object recoverField_e = new Object();
   protected Handler recoverField_a;
   private boolean recoverField_f;
   private String recoverField_g;
   private long recoverField_h = 0L;
   private static RecoverTypeaf2a6dda recoverField_i = null;
   private LocationListener recoverField_j = new LocationListener() {
      @Override
      public void onLocationChanged(Location var1) {
         try {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "onLocationChanged:" + var1 + ",provider:" + RecoverTypeaf2a6dda.this.recoverField_g);
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2054);
            RecoverTypeaf2a6dda.this.recoverMethod_a_48e8163d(var1);
            RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
         } catch (Throwable var3) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "onLocationChanged failed:" + var3.getMessage());
         }
      }

      @Override
      public void onStatusChanged(String var1, int var2, Bundle var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "onStatusChanged status:" + var2);
         if (var2 == 0) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
            RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
         }
      }

      @Override
      public void onProviderEnabled(String var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "onProviderEnabled:" + var1);
      }

      @Override
      public void onProviderDisabled(String var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "onProviderDisabled:" + var1);
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
         RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
      }
   };

   private RecoverTypeaf2a6dda(Context var1) {
      this.recoverField_b = var1;
      this.recoverField_c = (LocationManager)var1.getSystemService("location");
   }

   private void recoverMethod_f_9b75() {
      try {
         if (this.recoverField_a == null) {
            HandlerThread var1 = new HandlerThread("jg_lcn_thread");
            var1.start();
            this.recoverField_a = new RecoverTypeaf2a6dda.Member8e466417(var1.getLooper());
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JLocationGps", "start load loc-info failed - error:" + var2);
      }
   }

   public static RecoverTypeaf2a6dda recoverMethod_a_5ed9a98a(Context var0) {
      if (recoverField_i == null) {
         synchronized (recoverField_e) {
            if (recoverField_i == null) {
               recoverField_i = new RecoverTypeaf2a6dda(var0);
            }
         }
      }

      return recoverField_i;
   }

   RecoverTypeaf2a7199 recoverMethod_a_c68e272d() {
      return this.recoverField_d;
   }

   boolean recoverMethod_b_9b79() {
      return this.recoverField_f;
   }

   private void recoverMethod_g_9b75() {
      try {
         if (null != this.recoverField_j) {
            if (null != this.recoverField_c) {
               this.recoverField_c.removeUpdates(this.recoverField_j);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "locationManager is null , do nothing!");
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "Location listener is null , do nothing!");
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "remove location listener failed  e:" + var2.getMessage());
      }
   }

   private synchronized void recoverMethod_h_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "gps will done");
      this.recoverField_f = true;
      this.recoverMethod_g_9b75();
      if (this.recoverField_a != null) {
         try {
            if (this.recoverField_a.hasMessages(1004)) {
               this.recoverField_a.removeMessages(1004);
            }

            if (this.recoverField_a.hasMessages(1003)) {
               this.recoverField_a.removeMessages(1003);
            }

            if (this.recoverField_a.hasMessages(1001)) {
               this.recoverField_a.removeMessages(1001);
            }

            if (this.recoverField_a.hasMessages(1005)) {
               this.recoverField_a.removeMessages(1005);
            }

            this.recoverField_a.getLooper().quit();
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JLocationGps", "quit handler failed:" + var5.getMessage());
         } finally {
            this.recoverField_a = null;
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JLocationGps", "cellLocationManager is null,please check it");
      }
   }

   void recoverMethod_c_9b75() {
      this.recoverField_f = false;
      this.recoverField_d = null;
      if (this.recoverField_c == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "get locationManager failed");
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2050);
         this.recoverField_f = true;
      } else {
         this.recoverMethod_f_9b75();
         if (this.recoverField_a == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", " mAsyncHandler is empty");
            this.recoverField_f = true;
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2051);
         } else {
            if (this.recoverField_c.isProviderEnabled("network")) {
               this.recoverField_g = "network";
               this.recoverField_a.sendEmptyMessage(1003);
            } else if (this.recoverField_c.isProviderEnabled("gps")) {
               this.recoverField_g = "gps";
               this.recoverField_a.sendEmptyMessage(1003);
            } else {
               this.recoverField_g = "network";
               this.recoverField_a.sendEmptyMessage(1004);
            }
         }
      }
   }

   private void recoverMethod_a_48e8163d(Location var1) {
      if (var1 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "updateGpsInfo location time:" + var1.getTime());
         if (this.recoverField_d == null) {
            this.recoverField_d = new RecoverTypeaf2a7199();
         }

         this.recoverField_d.recoverField_a = RecoverType5a68d11.recoverMethod_a_1a54e725(this.recoverField_b, var1.getTime());
         this.recoverField_d.recoverField_b = var1.getProvider();
         this.recoverField_d.recoverField_c = var1.getLatitude();
         this.recoverField_d.recoverField_d = var1.getLongitude();
         this.recoverField_d.recoverField_f = var1.getBearing();
         this.recoverField_d.recoverField_g = var1.getAccuracy();
      } else {
         this.recoverField_d = null;
      }
   }

   public RecoverTypeaf2a7199 recoverMethod_d_c68e272d() {
      try {
         if (this.recoverField_h > 0L && System.currentTimeMillis() - this.recoverField_h >= 30000L) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "JLocationGps", "g scan time compare last time than: " + (System.currentTimeMillis() - this.recoverField_h)
            );
            return null;
         }

         this.recoverField_h = System.currentTimeMillis();
         Location var1 = null;
         Location var2 = null;
         Location var3 = null;
         var1 = this.recoverField_c.getLastKnownLocation("gps");
         var2 = this.recoverField_c.getLastKnownLocation("network");
         var3 = this.recoverField_c.getLastKnownLocation("passive");
         Location var4 = this.recoverMethod_a_68721e79(var1, var2)
            ? (this.recoverMethod_a_68721e79(var1, var3) ? var1 : var3)
            : (this.recoverMethod_a_68721e79(var2, var3) ? var2 : var3);
         if (var4 != null && System.currentTimeMillis() - var4.getTime() < 30000L) {
            this.recoverMethod_a_48e8163d(var4);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "bestLocation:" + this.recoverField_d + ",curTime:" + System.currentTimeMillis());
            return this.recoverField_d;
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "loadLastGpsInfo failed: " + var5.getMessage());
      }

      return null;
   }

   private boolean recoverMethod_a_68721e79(Location var1, Location var2) {
      if (var1 == null) {
         return false;
      } else if (var2 == null) {
         return true;
      } else {
         long var3 = var1.getTime() - var2.getTime();
         boolean var5 = var3 > 120000L;
         boolean var6 = var3 < -120000L;
         boolean var7 = var3 > 0L;
         if (var5) {
            return true;
         } else if (var6) {
            return false;
         } else {
            int var8 = (int)(var1.getAccuracy() - var2.getAccuracy());
            boolean var9 = var8 > 0;
            boolean var10 = var8 < 0;
            boolean var11 = var8 > 200;
            boolean var12 = this.recoverMethod_a_e925fa0d(var1.getProvider(), var2.getProvider());
            if (var10) {
               return true;
            } else {
               return var7 && !var9 ? true : var7 && !var11 && var12;
            }
         }
      }
   }

   private boolean recoverMethod_a_e925fa0d(String var1, String var2) {
      return var1 == null ? var2 == null : var1.equals(var2);
   }

   public boolean recoverMethod_e_9b79() {
      try {
         return null == this.recoverField_c
            ? false
            : this.recoverField_c.isProviderEnabled("gps")
               || this.recoverField_c.isProviderEnabled("network")
               || this.recoverField_c.isProviderEnabled("passive");
      } catch (SecurityException var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "No suitable permission is present when get GPS_PROVIDER!");
         return false;
      } catch (IllegalArgumentException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "The provider [gps] is illegal argument!");
         return false;
      } catch (Exception var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "The ILocationManager is null!");
         return false;
      }
   }

   class Member8e466417 extends Handler {
      public Member8e466417(Looper var2) {
         super(var2);
      }

      @Override
      public void handleMessage(Message var1) {
         short var2 = 1001;
         switch (var1.what) {
            case 1001:
               try {
                  if (RecoverTypeaf2a6dda.this.recoverField_g != null && RecoverTypeaf2a6dda.this.recoverField_g.equals("network")) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "get gps with network time out ");
                     RecoverTypeaf2a6dda.this.recoverField_g = "gps";
                     RecoverTypeaf2a6dda.this.recoverMethod_g_9b75();
                     boolean var8 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1502);
                     boolean var9 = RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_b_9b79();
                     if (!var9 && !var8) {
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2066);
                     } else {
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2052);
                        RecoverTypeaf2a6dda.this.recoverField_c
                           .requestLocationUpdates(RecoverTypeaf2a6dda.this.recoverField_g, 2000L, 0.0F, RecoverTypeaf2a6dda.this.recoverField_j);
                     }

                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                        "JLocationGps", "request " + RecoverTypeaf2a6dda.this.recoverField_g + " location canCollect " + var8 + " isGpsEnable=" + var9
                     );
                     RecoverTypeaf2a6dda.this.recoverField_a.sendEmptyMessageDelayed(1001, 15000L);
                  } else {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "get " + RecoverTypeaf2a6dda.this.recoverField_g + " time out ");
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
                     RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
                  }
               } catch (Throwable var7) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "when location time out " + var7.getMessage());
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
                  RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
               }
            case 1002:
            default:
               break;
            case 1004:
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationGps", "LOAD_GPS_ACTION_REQUEST_ONLY_NETWORK");
               var2 = 1005;
            case 1003:
               try {
                  boolean var3 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1502);
                  boolean var4 = RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_b_9b79();
                  if (!var4) {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2048);
                  }

                  if (var4 || var3) {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2052);
                     RecoverTypeaf2a6dda.this.recoverField_c
                        .requestLocationUpdates(RecoverTypeaf2a6dda.this.recoverField_g, 2000L, 0.0F, RecoverTypeaf2a6dda.this.recoverField_j);
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                     "JLocationGps", "request " + RecoverTypeaf2a6dda.this.recoverField_g + " location canCollect " + var3 + " isGpsEnable=" + var4
                  );
                  RecoverTypeaf2a6dda.this.recoverField_a.sendEmptyMessageDelayed(var2, 30000L);
               } catch (SecurityException var5) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "No suitable permission when get last known location!");
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
                  RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
               } catch (Throwable var6) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "The provider is illegal argument!");
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
                  RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
               }
               break;
            case 1005:
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationGps", "get only network " + RecoverTypeaf2a6dda.this.recoverField_g + " time out ");
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2053);
               RecoverTypeaf2a6dda.this.recoverMethod_h_9b75();
         }
      }
   }
}
