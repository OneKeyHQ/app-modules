package cn.jiguang.bb;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bc.RecoverTypeaf2ae9b9;
import cn.jiguang.bd.RecoverTypeaf2aed7a;
import cn.jiguang.bd.RecoverTypeaf2aed7b;
import cn.jiguang.bd.RecoverTypeaf2aed7c;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2ae5f8 {
   private static String recoverField_a = "ex_stat.catch.";
   private static String recoverField_b = "ex_stat.catch.";
   private Context recoverField_c;

   private RecoverTypeaf2ae5f8() {
   }

   public static RecoverTypeaf2ae5f8 recoverMethod_a_c69c3eae() {
      return RecoverTypeaf2ae5f8.Member90094cb5.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (this.recoverField_c == null) {
         this.recoverField_c = var1.getApplicationContext();
         recoverField_b = recoverField_b + RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(this.recoverField_c).replace(this.recoverField_c.getPackageName(), "");
      }
   }

   public void recoverMethod_a_9d23c587(Context var1, String var2, String var3) {
      if (var1 == null || TextUtils.isEmpty(var3) || TextUtils.isEmpty(var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptMonitorManager", "exception occurred for param is null, exType is " + var3);
      } else if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_k_48794abf())) {
         RecoverTypeaf2aed7c var4;
         if (TextUtils.equals(var3, "EX_ANR")) {
            var4 = new RecoverTypeaf2aed7a(var1, var2);
         } else {
            var4 = new RecoverTypeaf2aed7b(var1, var2);
         }

         this.recoverMethod_a_b2b3f693(var4);
      }
   }

   public void recoverMethod_b_9b75() {
      long var1 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_c, RecoverType5a690d1.recoverMethod_d_48794abf());
      if (0L == var1) {
         var1 = System.currentTimeMillis();
         RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_c, RecoverType5a690d1.recoverMethod_d_48794abf().recoverMethod_a_4e9722a3(var1));
      }

      long var3 = System.currentTimeMillis();
      if (var3 - var1 < 86400000L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptMonitorManager", "it not business time, interval: 86400000");
      } else {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(
            "FUTURE_TASK",
            new RecoverTypeaf2b0401() {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     synchronized (RecoverTypeaf2ae5f8.class) {
                        this.recoverMethod_a_aad3b1ff(RecoverTypeaf2ae5f8.recoverField_b);
                        this.recoverMethod_a_aad3b1ff(RecoverTypeaf2ae5f8.recoverField_a);
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           RecoverTypeaf2ae5f8.this.recoverField_c,
                           RecoverType5a690d1.recoverMethod_d_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                        );
                     }
                  } catch (Throwable var4) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptMonitorManager", "save data error:" + var4.getMessage());
                  }
               }

               private void recoverMethod_a_aad3b1ff(String var1) {
                  JSONObject var2 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d(var1);
                  if (var2 != null && var2.has("data")) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptMonitorManager", "start report exception monitor data:" + var1);
                     RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2ae5f8.this.recoverField_c, var2, "sdk_moniter");
                     RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff(var1);
                  } else {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptMonitorManager", "exception not has data:" + var1);
                  }
               }
            }
         );
      }
   }

   private void recoverMethod_a_b2b3f693(final RecoverTypeaf2ae9b9 var1) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               synchronized (RecoverTypeaf2ae5f8.class) {
                  JSONObject var2 = var1.recoverMethod_b_a7dbfe3();
                  String var3 = var2.optString("moniter_type");
                  long var4 = var2.optLong("timestamp");
                  String var6 = var2.optJSONObject("extra").optString("sdk_type");
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptMonitorManager", "save exception data:" + var2);
                  JSONObject var7 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d(RecoverTypeaf2ae5f8.recoverField_b);
                  JSONArray var8;
                  if (var7 != null && var7.has("data")) {
                     var8 = var7.optJSONArray("data");
                  } else {
                     var7 = new JSONObject();
                     var7.put("type", "sdk_moniter");
                     var8 = new JSONArray();
                  }

                  boolean var9 = false;

                  for (int var10 = 0; var10 < var8.length(); var10++) {
                     JSONObject var11 = var8.optJSONObject(var10);
                     if (var11 != null) {
                        String var12 = var11.optString("moniter_type");
                        if (TextUtils.equals(var3, var12)) {
                           long var13 = var11.optLong("timestamp");
                           JSONObject var15 = var11.optJSONObject("extra");
                           if (null != var15) {
                              String var16 = var15.optString("sdk_type");
                              if (TextUtils.equals(var6, var16)) {
                                 int var17 = var15.optInt("count");
                                 if (RecoverTypeaf2b07d7.recoverMethod_a_2566ab9(var4, var13)) {
                                    var15.put("count", var17 + 1);
                                    var9 = true;
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (!var9) {
                     var8.put(var2);
                  }

                  var7.put("data", var8);
                  RecoverTypeaf2b07c9.recoverMethod_a_27ad0523(RecoverTypeaf2ae5f8.recoverField_b, var7);
               }
            } catch (Throwable var20) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptMonitorManager", "save data failed, " + var20.getMessage());
            }
         }
      });
   }

   private static final class Member90094cb5 {
      private static final RecoverTypeaf2ae5f8 recoverField_a = new RecoverTypeaf2ae5f8();
   }
}
