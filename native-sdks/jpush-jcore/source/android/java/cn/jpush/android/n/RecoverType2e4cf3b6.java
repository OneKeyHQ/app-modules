package cn.jpush.android.n;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455834;
import cn.jpush.android.z.RecoverType2e4d20be;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType2e4cf3b6 {
   private final LocationManager recoverField_a;
   private Context recoverField_b;
   private Handler recoverField_c;
   private HashMap recoverField_d = new HashMap();
   private volatile RecoverType2e4cf3b6.Membercedee2f3 recoverField_e;
   private volatile RecoverType2e4cf3b6.Membercedee2f4 recoverField_f;
   private boolean recoverField_g = false;

   public RecoverType2e4cf3b6(Context var1) {
      this.recoverField_b = var1.getApplicationContext();
      this.recoverField_a = (LocationManager)this.recoverField_b.getSystemService("location");
      this.recoverMethod_c_9b75();
      this.recoverMethod_i_9b75();
      this.recoverMethod_j_9b75();
   }

   private void recoverMethod_c_9b75() {
      try {
         HandlerThread var1 = new HandlerThread("jg_gph_thread") {
            @Override
            public void run() {
               try {
                  super.run();
               } catch (RuntimeException var2) {
                  Logger.recoverMethod_e_e925fa09(
                     "GeofencePullHelper", "handler thread run e:" + var2 + "  t=" + Thread.currentThread().getName() + "_" + Thread.currentThread().getId()
                  );
               }
            }
         };
         var1.start();
         this.recoverField_c = new Handler(var1.getLooper()) {
            @SuppressLint("MissingPermission")
            @Override
            public void handleMessage(Message var1) {
               switch (var1.what) {
                  case 1000:
                     RecoverType2e4cf3b6.this.recoverMethod_a_1349ef(RecoverType2e4cf3b6.this.recoverField_f.recoverField_b);
                  case 1001:
                     RecoverType2e4cf3b6.this.recoverMethod_f_9b75();
                     break;
                  case 1002:
                     if (RecoverType2e4cf3b6.this.recoverMethod_g_9b79()) {
                        RecoverType2e4cf3b6.this.recoverMethod_a_1349ef(0L);
                     }

                     RecoverType2e4cf3b6.this.recoverMethod_b_1349ef(RecoverType2e4cf3b6.this.recoverField_f.recoverField_c);
               }
            }
         };
      } catch (Throwable var2) {
         Logger.recoverMethod_ww_e925fa09("GeofencePullHelper", "init geofence pull handler failed:" + var2);
      }
   }

   public void recoverMethod_a_84fe6c32(RecoverType2e4cf3b2 var1) {
      if (var1 != null) {
         int var2 = var1.recoverMethod_a_9b68();
         Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "control pull geo type:" + var2);
         if (this.recoverField_g ^ var2 == 1) {
            this.recoverField_g = var2 == 1;
            RecoverTypea2146d45.recoverMethod_d_1a552341(this.recoverField_b, this.recoverField_g);
            if (this.recoverField_g) {
               this.recoverMethod_d_9b75();
            } else {
               this.recoverMethod_e_9b75();
            }
         }
      }
   }

   public void recoverMethod_a_9b75() {
      Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "onLogin");
      if (!this.recoverField_g) {
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "do not support pull");
      } else {
         this.recoverMethod_d_9b75();
      }
   }

   public void recoverMethod_b_9b75() {
      Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "onTcpDisconnected");
      this.recoverMethod_e_9b75();
   }

   private void recoverMethod_d_9b75() {
      Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "start schedule geofence pull");
      if (!this.recoverMethod_h_9b79() && !this.recoverMethod_g_9b79()) {
         this.recoverMethod_a_1349ef(this.recoverField_f.recoverField_b);
      } else {
         this.recoverMethod_a_1349ef(0L);
      }

      this.recoverMethod_b_1349ef(this.recoverField_f.recoverField_c);
   }

   private void recoverMethod_a_1349ef(long var1) {
      Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "pull geofence after " + var1 * 1000L + "ms");
      if (this.recoverField_c != null) {
         if (this.recoverField_c.hasMessages(1000)) {
            this.recoverField_c.removeMessages(1000);
         }

         this.recoverField_c.sendEmptyMessageDelayed(1000, var1 * 1000L);
      }
   }

   private void recoverMethod_e_9b75() {
      Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "stop schedule geofence pull");
      if (this.recoverField_c != null) {
         if (this.recoverField_c.hasMessages(1000)) {
            this.recoverField_c.removeMessages(1000);
         }

         if (this.recoverField_c.hasMessages(1001)) {
            this.recoverField_c.removeMessages(1001);
         }

         if (this.recoverField_c.hasMessages(1002)) {
            this.recoverField_c.removeMessages(1002);
         }
      }
   }

   @SuppressLint("MissingPermission")
   private void recoverMethod_f_9b75() {
      Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "try pull...");
      Location var1 = RecoverType2e4cf3b3.recoverMethod_a_3a51be92(this.recoverField_b, this.recoverField_a, false);
      if (var1 == null) {
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "stop pull,get loction failed");
      } else {
         this.recoverMethod_a_25399f5(var1.getLatitude(), var1.getLongitude());
      }
   }

   private void recoverMethod_a_25399f5(double var1, double var3) {
      try {
         JSONObject var5 = new JSONObject();
         var5.put("lat", var1);
         var5.put("lng", var3);
         var5.put("time", System.currentTimeMillis() / 1000L);
         long var6 = RecoverType8b455834.recoverMethod_a_9b69();
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "will send report geo request:" + var5 + ",requestid:" + var6);
         this.recoverField_d.put(var6 + "", var5);
         byte[] var8 = RecoverType2e4d20be.recoverMethod_c_afa28ebe(var5.toString());
         JCoreHelper.sendRequest(this.recoverField_b, "JPUSH", 37, 1, var6, 0L, var8);
      } catch (Throwable var9) {
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "send report geo request failed:" + var9.getMessage());
      }
   }

   public void recoverMethod_a_cb5e72f(long var1, int var3, RecoverType2e4cf3b4 var4) {
      JSONObject var5 = (JSONObject)this.recoverField_d.remove(var1 + "");
      if (var3 == JPushInterface.Member6357d7ea.TIMEOUT) {
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "pull geo is timeout,requestid:" + var1);
      } else if (var5 == null) {
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "can not find request from requestid:" + var1);
      } else {
         JSONObject var6 = var4.recoverMethod_a_a7dbfe3();
         if (var6 == null) {
            Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "onPullResponse empty pull response");
         } else {
            this.recoverField_f.recoverMethod_a_3c4029ab(this.recoverField_b, var6);
            JSONArray var7 = var4.recoverMethod_b_6d78e11b();
            if (var7 == null) {
               var7 = new JSONArray();
            }

            Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "onPullResponse:" + var7);
            if (var7.length() == 0) {
               this.recoverField_g = false;
               RecoverTypea2146d45.recoverMethod_d_1a552341(this.recoverField_b, this.recoverField_g);
               this.recoverMethod_e_9b75();
            }

            this.recoverField_e.recoverMethod_a_3c4029ab(this.recoverField_b, var5);
            RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_48551f8f(var7);
            RecoverType2e4cf3b1.recoverMethod_a_5ffc00fd(this.recoverField_b, var7 != null ? var7.toString() : "");
         }
      }
   }

   private void recoverMethod_b_1349ef(long var1) {
      Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "compile loc after " + var1 * 1000L + "ms");
      if (this.recoverField_c != null) {
         if (this.recoverField_c.hasMessages(1002)) {
            this.recoverField_c.removeMessages(1002);
         }

         this.recoverField_c.sendEmptyMessageDelayed(1002, var1 * 1000L);
      }
   }

   private boolean recoverMethod_g_9b79() {
      Location var1 = RecoverType2e4cf3b3.recoverMethod_a_3a51be92(this.recoverField_b, this.recoverField_a, false);
      if (var1 != null) {
         double var2 = RecoverType9b4c5af2.recoverMethod_a_bbe0f863(
            var1.getLongitude(), var1.getLatitude(), this.recoverField_e.recoverField_c, this.recoverField_e.recoverField_b
         );
         Logger.recoverMethod_d_e925fa09(
            "GeofencePullHelper", "check current distance to last pull distance:" + var2 + ",lbsKilo:" + this.recoverField_f.recoverField_d * 1000
         );
         if (var2 > this.recoverField_f.recoverField_d * 1000) {
            return true;
         }
      }

      Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "loc limit");
      return false;
   }

   private boolean recoverMethod_h_9b79() {
      long var1 = System.currentTimeMillis() / 1000L;
      long var3 = this.recoverField_e.recoverField_a;
      Logger.recoverMethod_dd_e925fa09(
         "GeofencePullHelper", "lastPullTime:" + var3 + ",currentTime:" + var1 + ",minInterval:" + this.recoverField_f.recoverField_a
      );
      if (var1 - var3 < this.recoverField_f.recoverField_a) {
         Logger.recoverMethod_dd_e925fa09("GeofencePullHelper", "time limit");
         return false;
      } else {
         return true;
      }
   }

   private void recoverMethod_i_9b75() {
      this.recoverField_e = new RecoverType2e4cf3b6.Membercedee2f3();
      this.recoverField_e.recoverMethod_a_f0554df3(this.recoverField_b);
   }

   private void recoverMethod_j_9b75() {
      this.recoverField_f = new RecoverType2e4cf3b6.Membercedee2f4();
      this.recoverField_f.recoverMethod_a_f0554df3(this.recoverField_b);
      this.recoverField_g = RecoverTypea2146d45.recoverMethod_l_f0554df7(this.recoverField_b);
   }

   public class Membercedee2f3 {
      public long recoverField_a;
      public double recoverField_b;
      public double recoverField_c;

      public void recoverMethod_a_f0554df3(Context var1) {
         String var2 = RecoverTypea2146d45.recoverMethod_n_e501eb0d(var1);
         if (!TextUtils.isEmpty(var2)) {
            Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "last pull state:" + var2);

            try {
               JSONObject var3 = new JSONObject(var2);
               this.recoverMethod_a_be7fc169(var3);
            } catch (JSONException var4) {
            }
         }
      }

      public void recoverMethod_a_be7fc169(JSONObject var1) {
         RecoverType2e4cf3b6.this.recoverField_e.recoverField_b = var1.optDouble("lat", 200.0);
         RecoverType2e4cf3b6.this.recoverField_e.recoverField_c = var1.optDouble("lng", 200.0);
         RecoverType2e4cf3b6.this.recoverField_e.recoverField_a = var1.optLong("time", 0L);
      }

      public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
         if (var2 != null) {
            Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "saveGeofenceLastPullState:" + var2.toString());
            this.recoverMethod_a_be7fc169(var2);
            RecoverTypea2146d45.recoverMethod_f_5ffc00fd(RecoverType2e4cf3b6.this.recoverField_b, var2.toString());
         }
      }
   }

   public class Membercedee2f4 {
      public int recoverField_a = 1800;
      public int recoverField_b = 10800;
      public int recoverField_c = 900;
      public int recoverField_d = 20;

      public void recoverMethod_a_f0554df3(Context var1) {
         String var2 = RecoverTypea2146d45.recoverMethod_m_e501eb0d(var1);
         if (!TextUtils.isEmpty(var2)) {
            Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "pull limit:" + var2);

            try {
               JSONObject var3 = new JSONObject(var2);
               this.recoverMethod_a_be7fc169(var3);
            } catch (Throwable var4) {
            }
         }
      }

      private void recoverMethod_a_be7fc169(JSONObject var1) {
         RecoverType2e4cf3b6.this.recoverField_f.recoverField_a = var1.optInt("minInterval", RecoverType2e4cf3b6.this.recoverField_f.recoverField_a);
         RecoverType2e4cf3b6.this.recoverField_f.recoverField_b = var1.optInt("nextInterval", RecoverType2e4cf3b6.this.recoverField_f.recoverField_b);
         RecoverType2e4cf3b6.this.recoverField_f.recoverField_c = var1.optInt("minLBSInterval", RecoverType2e4cf3b6.this.recoverField_f.recoverField_c);
         RecoverType2e4cf3b6.this.recoverField_f.recoverField_d = var1.optInt("minLBSKilo", RecoverType2e4cf3b6.this.recoverField_f.recoverField_d);
      }

      public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
         if (var2 != null) {
            try {
               this.recoverMethod_a_be7fc169(var2);
               JSONObject var3 = new JSONObject();
               this.recoverMethod_a_f75f3064(var2, var3, "minInterval", RecoverType2e4cf3b6.this.recoverField_f.recoverField_a);
               this.recoverMethod_a_f75f3064(var2, var3, "nextInterval", RecoverType2e4cf3b6.this.recoverField_f.recoverField_b);
               this.recoverMethod_a_f75f3064(var2, var3, "minLBSInterval", RecoverType2e4cf3b6.this.recoverField_f.recoverField_c);
               this.recoverMethod_a_f75f3064(var2, var3, "minLBSKilo", RecoverType2e4cf3b6.this.recoverField_f.recoverField_d);
               RecoverTypea2146d45.recoverMethod_e_5ffc00fd(var1, var3.toString());
            } catch (Throwable var4) {
               Logger.recoverMethod_d_e925fa09("GeofencePullHelper", "save limit failed");
            }
         }
      }

      private void recoverMethod_a_f75f3064(JSONObject var1, JSONObject var2, String var3, int var4) throws org.json.JSONException {
         if (var1.has(var3)) {
            var2.put(var3, var4);
         }
      }
   }
}
