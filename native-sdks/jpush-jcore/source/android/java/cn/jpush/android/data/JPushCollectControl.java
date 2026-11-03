package cn.jpush.android.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JPushCollectControl implements Serializable {
   private static final long serialVersionUID = -3135447319267654288L;
   private ArrayList mEnableDatas = new ArrayList();
   private ArrayList mDisableDatas = new ArrayList();
   private static final int GPS = 1000;
   private static final int APP_LISTS = 1001;
   private static final int IMEI = 1003;
   private static final int MAC = 1005;
   private static final int SSID = 1006;
   private static final int BSSID = 1007;
   private static final int CELL = 1008;
   public static final int IMSI = 1010;
   public static final int WIFI = 1012;
   public static final int APP_RUNNING = 1013;

   public JPushCollectControl(JPushCollectControl.Memberd6392c58 var1) {
      if (null != var1.mDisableDatas && !var1.mDisableDatas.isEmpty()) {
         this.mDisableDatas.addAll(var1.mDisableDatas);
      }

      if (null != var1.mEnableDatas && !var1.mEnableDatas.isEmpty()) {
         this.mEnableDatas.addAll(var1.mEnableDatas);
      }
   }

   public ArrayList getEnableDatas() {
      return this.mEnableDatas;
   }

   public ArrayList getDisableDatas() {
      return this.mDisableDatas;
   }

   public static class Memberd6392c58 {
      private Set mEnableDatas = new HashSet();
      private Set mDisableDatas = new HashSet();

      private void enable(int var1) {
         this.mDisableDatas.remove(var1);
         this.mEnableDatas.add(var1);
      }

      private void disable(int var1) {
         this.mEnableDatas.remove(var1);
         this.mDisableDatas.add(var1);
      }

      private void set(boolean var1, int var2) {
         if (var1) {
            this.enable(var2);
         } else {
            this.disable(var2);
         }
      }

      public JPushCollectControl.Memberd6392c58 imei(boolean var1) {
         short var2 = 1003;
         this.set(var1, var2);
         return this;
      }

      public JPushCollectControl.Memberd6392c58 mac(boolean var1) {
         short var2 = 1005;
         this.set(var1, var2);
         return this;
      }

      public JPushCollectControl.Memberd6392c58 ssid(boolean var1) {
         short var2 = 1006;
         this.set(var1, var2);
         if (!var1) {
            this.wifi(false);
         }

         return this;
      }

      public JPushCollectControl.Memberd6392c58 bssid(boolean var1) {
         short var2 = 1007;
         this.set(var1, var2);
         if (!var1) {
            this.wifi(false);
         }

         return this;
      }

      public JPushCollectControl.Memberd6392c58 cell(boolean var1) {
         short var2 = 1008;
         this.set(var1, var2);
         return this;
      }

      public JPushCollectControl.Memberd6392c58 imsi(boolean var1) {
         short var2 = 1010;
         this.set(var1, var2);
         return this;
      }

      public JPushCollectControl.Memberd6392c58 wifi(boolean var1) {
         short var2 = 1012;
         this.set(var1, var2);
         return this;
      }

      public JPushCollectControl build() {
         return new JPushCollectControl(this);
      }
   }
}
