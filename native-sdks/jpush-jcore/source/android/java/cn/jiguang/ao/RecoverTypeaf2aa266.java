package cn.jiguang.ao;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2aa266 {
   private long recoverField_a;
   private Map recoverField_b = new HashMap();

   private RecoverTypeaf2aa266() {
   }

   public Integer recoverMethod_a_bd10275c() {
      return null;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      RecoverTypeaf2aa26a.recoverMethod_a_1a552341(var1, false);
   }

   public static RecoverTypeaf2aa266 recoverMethod_b_c6941000() {
      return RecoverTypeaf2aa266.Member8f0ba5a3.recoverField_a;
   }

   public RecoverTypeaf2aa266 recoverMethod_b_5ee0067e(Context var1) {
      try {
         long var2 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_X_48794abf());
         if (this.recoverField_a == 0L || this.recoverField_a != var2) {
            this.recoverField_a = var2;
            String var4 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_V_48794abf());
            if (!TextUtils.isEmpty(var4)) {
               try {
                  JSONObject var5 = new JSONObject(var4);
                  this.recoverMethod_a_be7fc169(var5);
               } catch (JSONException var6) {
               }
            }
         }
      } catch (Throwable var7) {
      }

      return this;
   }

   public RecoverTypeaf2aa266 recoverMethod_c_5ee0067e(Context var1) {
      try {
         long var2 = System.currentTimeMillis();
         this.recoverMethod_b_5ee0067e(var1);
         long var4 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_Y_48794abf());
         if (var4 < 0L) {
            var4 = 3600000L;
         } else if (var4 < 60000L) {
            var4 = 60000L;
         }

         if (var4 > 604800000L) {
            var4 = 604800000L;
         }

         SimpleDateFormat var6 = RecoverTypeaf2b07c6.recoverMethod_a_5318097a("yyyy-MM-dd HH:mm:ss");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "AddressGroupManager",
            "lastUpdateTime=" + var6.format(new Date(this.recoverField_a)) + " now=" + var6.format(new Date(var2)) + " expire=" + var4 / 1000L
         );
         if (this.recoverField_a == 0L || this.recoverField_a + var4 < var2) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AddressGroupManager", "cache invalid, fetch new urls");
            boolean var7 = this.recoverField_b == null || this.recoverField_b.isEmpty();
            RecoverTypeaf2aa26a.recoverMethod_a_1a552341(var1, var7);
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AddressGroupManager", "refresh e" + var8);
      }

      return this;
   }

   public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
      JSONArray var3 = var2.optJSONArray("sis_ips");

      long var4;
      try {
         var4 = var2.getLong("ttl");
         if (var4 < 0L) {
            var4 = 3600000L;
         }
      } catch (JSONException var7) {
         var4 = 3600000L;
      }

      JSONObject var6 = var2.optJSONObject("ips");
      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var1,
         RecoverType5a690d1.recoverMethod_W_48794abf().recoverMethod_a_4e9722a3(var3.toString()),
         RecoverType5a690d1.recoverMethod_Y_48794abf().recoverMethod_a_4e9722a3(var4 * 1000L),
         RecoverType5a690d1.recoverMethod_X_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis()),
         RecoverType5a690d1.recoverMethod_V_48794abf().recoverMethod_a_4e9722a3(var6 != null ? var6.toString() : "")
      );
      this.recoverMethod_a_be7fc169(var6);
   }

   private void recoverMethod_a_be7fc169(JSONObject var1) {
      if (var1 != null && var1.length() != 0) {
         try {
            HashMap var2 = new HashMap();
            Iterator var3 = var1.keys();

            while (var3.hasNext()) {
               String var4 = (String)var3.next();
               JSONArray var5 = var1.optJSONArray(var4);
               LinkedHashSet var6 = new LinkedHashSet();
               if (var5 != null) {
                  for (int var7 = 0; var7 < var5.length(); var7++) {
                     var6.add(var5.getString(var7));
                  }
               }

               var2.put(var4, var6);
            }

            if (!var2.isEmpty()) {
               this.recoverField_b = var2;
            }
         } catch (JSONException var8) {
         }
      }
   }

   public String recoverMethod_a_b555d336(Set var1) {
      try {
         if (var1 != null && !var1.isEmpty()) {
            String var2 = null;

            for (Object recoveryElement_147 : var1) {
               String var4 = (String)recoveryElement_147;
               String var5 = this.recoverMethod_a_e96ea081(var4);
               if (var2 == null) {
                  var2 = var5;
               } else if (!var2.equals(var5)) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AddressGroupManager", "Report JSONArray belong more than one space, using normal-space");
                  return "normal";
               }
            }

            return var2;
         } else {
            return "normal";
         }
      } catch (Throwable var6) {
         return "normal";
      }
   }

   public String recoverMethod_a_e96ea081(String var1) {
      if (TextUtils.isEmpty(var1)) {
         return "normal";
      }

      String var2;
      switch (var1) {
         case "android_awake2":
         case "android_awake":
         case "android_awake_target2":
         case "android_awake_target":
         case "awake":
         case "aa3":
         case "aat3":
            var2 = "awake";
            break;
         case "active_launch":
         case "active_terminate":
         case "active_user":
            var2 = "active_user";
            break;
         case "identify_account":
         case "detach_account":
         case "account":
            var2 = "account";
            break;
         case "android_notification_state":
            var2 = "android_notification_state";
            break;
         case "normal":
            var2 = "normal";
            break;
         default:
            if (!this.recoverField_b.containsKey(var1)) {
               var2 = "normal";
            } else {
               var2 = var1;
            }
      }

      return var2;
   }

   public Set recoverMethod_b_40b97519(Set var1) {
      if (var1 != null && !var1.isEmpty()) {
         Set var2 = null;

         for (Object recoveryElement_213 : var1) {
            String var4 = (String)recoveryElement_213;
            String var5 = this.recoverMethod_a_e96ea081(var4);
            Set var6 = (Set)this.recoverField_b.get(var5);
            if (var6 == null || var6.isEmpty()) {
               return (Set)this.recoverField_b.get("normal");
            }

            if (var2 == null) {
               var2 = var6;
            } else {
               var2.retainAll(var6);
            }

            if (var2.isEmpty()) {
               return (Set)this.recoverField_b.get("normal");
            }
         }

         return var2;
      } else {
         return (Set)this.recoverField_b.get("normal");
      }
   }

   private static final class Member8f0ba5a3 {
      private static final RecoverTypeaf2aa266 recoverField_a = new RecoverTypeaf2aa266();
   }
}
