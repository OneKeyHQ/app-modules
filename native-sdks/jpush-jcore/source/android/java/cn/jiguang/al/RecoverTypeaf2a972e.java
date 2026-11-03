package cn.jiguang.al;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.ac.RecoverTypeaf2a755c;
import cn.jiguang.ag.RecoverTypeaf2a845e;
import cn.jiguang.ah.RecoverTypeaf2a881f;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa266;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.az.RecoverTypeaf2acbb9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.dy.JDynamicLoadBase;
import cn.jiguang.j.RecoverType5a6a398;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a972e {
   private static RecoverTypeaf2a972e.Member8ee1886c recoverMethod_a_ab088404(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      try {
         String var1 = var0.trim();
         if (var1.startsWith("[") && var1.endsWith("]")) {
            String[] var2 = var1.split(",");
            if (var2.length != 2) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "ResetConfigurationHelper", "Failed to parse version range: invalid number of parts, expected 2 parts separated by comma, content: " + var1
               );
               return null;
            }

            Integer var3 = null;
            Integer var4 = null;
            String var5 = var2[0].trim();
            if (var5.startsWith("[")) {
               var5 = var5.substring(1).trim();
            }

            if (!var5.isEmpty()) {
               var3 = Integer.parseInt(var5);
            }

            String var6 = var2[1].trim();
            if (var6.endsWith("]")) {
               var6 = var6.substring(0, var6.length() - 1).trim();
            }

            if (!var6.isEmpty()) {
               var4 = Integer.parseInt(var6);
            }

            return new RecoverTypeaf2a972e.Member8ee1886c(var3, var4);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "ResetConfigurationHelper", "Parse version range failed: invalid format, should be like [1,5] or [1,] or [,5] or []"
            );
            return null;
         }
      } catch (Exception var7) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ResetConfigurationHelper", "Parse version range failed: " + var7.getMessage() + ", input: " + var0);
         return null;
      }
   }

   public static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      if (var0 != null && var1 != null) {
         try {
            JSONArray var2 = var1.optJSONArray("configs");
            if (var2 == null || var2.length() == 0) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ResetConfigurationHelper", "resetConfiguration failed: configs is empty");
               return;
            }

            for (int var3 = 0; var3 < var2.length(); var3++) {
               try {
                  JSONObject var4 = var2.getJSONObject(var3);
                  String var5 = var4.optString("type");
                  if (TextUtils.isEmpty(var5)) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ResetConfigurationHelper", "Skip config: type is empty");
                  } else {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ResetConfigurationHelper", "config :" + var4);
                     RecoverTypeaf2a972e.Member8ee1886c var6 = recoverMethod_a_ab088404(var4.optString("ver_range"));
                     RecoverTypeaf2a972e.Member8ee1886c var7 = recoverMethod_a_ab088404(var4.optString("sdk_range"));
                     boolean var8 = var4.optBoolean("partial_update", false);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ResetConfigurationHelper", "verRange :" + var6);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ResetConfigurationHelper", "sdkRange :" + var7);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ResetConfigurationHelper", "partialUpdate :" + var8);
                     if (var7 != null && !var7.recoverMethod_a_134632(RecoverType5a681cd.recoverField_c)) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                           "ResetConfigurationHelper",
                           "Skip config type: "
                              + var5
                              + ", current SDK version "
                              + RecoverType5a681cd.recoverField_c
                              + " not in range ["
                              + (var7.recoverField_a != null ? var7.recoverField_a : "")
                              + ","
                              + (var7.recoverField_b != null ? var7.recoverField_b : "")
                              + "]"
                        );
                     } else {
                        RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c var9 = RecoverTypeaf2a972e.Member8ee1886b.recoverMethod_a_af6d4c4(var5);
                        if (var9 != null) {
                           recoverMethod_a_e8bda95(var0, var4, var6, var8, var9, var5);
                        } else {
                           RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ResetConfigurationHelper", "Unknown config type: " + var5);
                        }
                     }
                  }
               } catch (Throwable var10) {
                  RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ResetConfigurationHelper", "Process config at index " + var3 + " failed: " + var10.getMessage());
               }
            }
         } catch (Throwable var11) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ResetConfigurationHelper", "resetConfiguration failed: " + var11.getMessage());
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ResetConfigurationHelper", "resetConfiguration failed: context or jsonContent is null");
      }
   }

   private static void recoverMethod_a_e8bda95(
      Context var0, JSONObject var1, RecoverTypeaf2a972e.Member8ee1886c var2, boolean var3, RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c var4, String var5
   ) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "ResetConfigurationHelper",
         "Handle " + var5 + " config: verRange=[" + (var2 != null ? var2.recoverField_a + "," + var2.recoverField_b : "null") + "], partialUpdate=" + var3
      );
      Integer var6 = var4.recoverMethod_a_bd10275c();
      if (var6 != null && var2 != null && !var2.recoverMethod_a_134632(var6)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "ResetConfigurationHelper",
            "Skip "
               + var5
               + " update: current protocol version "
               + var6
               + " not in range ["
               + (var2.recoverField_a != null ? var2.recoverField_a : "")
               + ","
               + (var2.recoverField_b != null ? var2.recoverField_b : "")
               + "]"
         );
      } else {
         var4.recoverMethod_a_3c4029ab(var0, var1);
      }
   }

   private static class Member8ee1886b {
      private static final Map recoverField_a = new HashMap();

      public static RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c recoverMethod_a_af6d4c4(String var0) {
         return (RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c)recoverField_a.get(var0);
      }

      static {
         recoverField_a.put("awake", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e68());
         recoverField_a.put("banned", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e69());
         recoverField_a.put("dy", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6b());
         recoverField_a.put("loc2", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6e());
         recoverField_a.put("sis", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e70());
         recoverField_a.put("data_conf", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6a());
         recoverField_a.put("sentry", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6f());
         recoverField_a.put("limit", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6d());
         recoverField_a.put("uaid", new RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e71());
      }

      private static class Member5ca11e68 implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e68() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2a881f.recoverMethod_b_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2a881f.recoverMethod_b_f0554df3(var1);
         }
      }

      private static class Member5ca11e69 implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e69() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2a972a.recoverMethod_c_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2a972a.recoverMethod_b_f0554df3(var1);
         }
      }

      private static class Member5ca11e6a implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e6a() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverType5a6a398.recoverMethod_b_487b90d8().recoverMethod_a_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverType5a6a398.recoverMethod_b_487b90d8().recoverMethod_a_f0554df3(var1);
         }
      }

      private static class Member5ca11e6b implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e6b() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            try {
               if (RecoverType5a681cd.recoverMethod_b_9b79()) {
                  try {
                     return JDynamicLoadBase.getInstance().getProtocolVersion();
                  } catch (Throwable var2) {
                     RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
                        "ConfigHandlerManager", "getProtocolVersion failed - DY supported but error: " + var2.getMessage()
                     );
                  }
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConfigHandlerManager", "getProtocolVersion - DY not supported");
               }
            } catch (Throwable var3) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ConfigHandlerManager", "getProtocolVersion failed - check DY support error: " + var3.getMessage());
            }

            return null;
         }

         @Override
         public void recoverMethod_a_3c4029ab(final Context var1, JSONObject var2) {
            try {
               if (var1 == null || var2 == null) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConfigHandlerManager", "updateImmediately failed: context or config is null");
                  return;
               }

               if (!var2.has("dy_id")) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConfigHandlerManager", "updateImmediately failed: dy_id not found in config");
                  return;
               }

               final int var3 = var2.optInt("dy_id");
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConfigHandlerManager", "Processing dy config with dy_id: " + var3);
               if (RecoverType5a681cd.recoverMethod_b_9b79()) {
                  RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(
                     new RecoverTypeaf2b0401() {
                        @Override
                        public void recoverMethod_a_9b75() {
                           try {
                              JDynamicLoadBase.getInstance().updateImmediately(var1, var3, "");
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConfigHandlerManager", "Successfully updated dy config for dy_id: " + var3);
                           } catch (Throwable var2x) {
                              RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
                                 "ConfigHandlerManager", "Failed to update dy config for dy_id: " + var3 + ", error: " + var2x.getMessage()
                              );
                           }
                        }
                     }
                  );
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConfigHandlerManager", "Skip dy config update: dynamic loading not supported");
               }
            } catch (Throwable var4) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ConfigHandlerManager", "updateImmediately failed: " + var4.getMessage());
            }
         }
      }

      public interface Member5ca11e6c {
         Integer recoverMethod_a_bd10275c();

         void recoverMethod_a_3c4029ab(Context var1, JSONObject var2);
      }

      private static class Member5ca11e6d implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e6d() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2a972b.recoverMethod_c_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2a972b.recoverMethod_b_f0554df3(var1);
         }
      }

      private static class Member5ca11e6e implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e6e() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2a755c.recoverMethod_a_c68e9bca().recoverMethod_b_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2a755c.recoverMethod_a_c68e9bca().recoverMethod_a_f0554df3(var1);
         }
      }

      private static class Member5ca11e6f implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e6f() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2acbb9.recoverMethod_b_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2acbb9.recoverMethod_c_f0554df3(var1);
         }
      }

      private static class Member5ca11e70 implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e70() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2aa266.recoverMethod_b_c6941000().recoverMethod_a_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2aa266.recoverMethod_b_c6941000().recoverMethod_a_f0554df3(var1);
         }
      }

      private static class Member5ca11e71 implements RecoverTypeaf2a972e.Member8ee1886b.Member5ca11e6c {
         private Member5ca11e71() {
         }

         @Override
         public Integer recoverMethod_a_bd10275c() {
            return RecoverTypeaf2a845e.recoverMethod_a_c6906d08().recoverMethod_d_bd10275c();
         }

         @Override
         public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
            RecoverTypeaf2a845e.recoverMethod_a_c6906d08().recoverMethod_c_f0554df3(var1);
         }
      }
   }

   private static class Member8ee1886c {
      Integer recoverField_a;
      Integer recoverField_b;

      Member8ee1886c(Integer var1, Integer var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }

      public boolean recoverMethod_a_134632(int var1) {
         if (this.recoverField_a == null && this.recoverField_b == null) {
            return true;
         } else {
            return this.recoverField_a != null && var1 < this.recoverField_a ? false : this.recoverField_b == null || var1 <= this.recoverField_b;
         }
      }

      @Override
      public String toString() {
         return "VersionRange{min=" + this.recoverField_a + ", max=" + this.recoverField_b + '}';
      }
   }
}
