package cn.jiguang.bh;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cc;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2afc7e {
   private static Bundle recoverField_a;
   private static long recoverField_b;
   private Context recoverField_c;
   private AtomicBoolean recoverField_d = new AtomicBoolean(true);
   private AtomicBoolean recoverField_e = new AtomicBoolean(false);
   private final ConcurrentLinkedQueue recoverField_f = new ConcurrentLinkedQueue();
   private final AtomicInteger recoverField_g = new AtomicInteger(0);
   private final Runnable recoverField_h = new Runnable() {
      @Override
      public void run() {
         if (!RecoverTypeaf2afc7e.this.recoverField_e.get()) {
            RecoverTypeaf2afc7e.this.recoverMethod_c_9b75();
         }
      }
   };
   private static HashMap recoverField_i = new HashMap();
   private static List recoverField_j = new ArrayList();

   private RecoverTypeaf2afc7e() {
      this.recoverField_c = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
      this.recoverField_d.set((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_c, RecoverType5a690d1.recoverMethod_N_48794abf()));
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "default exception_switch=" + this.recoverField_d.get());
   }

   public void recoverMethod_a_d0657661(Integer var1, int var2) {
      if (var1 != null) {
         if (recoverField_i.containsKey(var1)) {
            this.recoverMethod_a_e925fa09(var2 + "", (String)recoverField_i.get(var1));
         }
      }
   }

   public void recoverMethod_a_255f295(int var1, int var2) {
      if (recoverField_i.containsKey(var1)) {
         this.recoverMethod_a_e925fa09(var2 + "", (String)recoverField_i.get(var1));
      }
   }

   public static RecoverTypeaf2afc7e recoverMethod_a_c69ef8e8() {
      return RecoverTypeaf2afc7e.Member905dd9bb.recoverField_a;
   }

   public void recoverMethod_a_13462e(int var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "2");
   }

   public void recoverMethod_b_13462e(int var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "3");
   }

   public void recoverMethod_a_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "4");
   }

   public void recoverMethod_b_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "7");
   }

   public void recoverMethod_c_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "6");
   }

   public void recoverMethod_d_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "8");
   }

   public void recoverMethod_e_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "9");
   }

   public void recoverMethod_f_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "11");
   }

   public void recoverMethod_g_5d527811(Object var1) {
      this.recoverMethod_a_e925fa09(var1 + "", "14");
   }

   public void recoverMethod_a_e925fa09(String var1, String var2) {
      try {
         Bundle var3 = new Bundle();
         var3.putString("code", var1);
         var3.putString("exception_type", var2);
         this.recoverMethod_a_79e5e33f(var3);
      } catch (Throwable var4) {
      }
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_d.set(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "ExceptionStatsManager", "updateSwitchState exception_switch=" + this.recoverField_d.get() + " origin switchState=" + var1
      );
   }

   public void recoverMethod_a_79e5e33f(Bundle var1) {
      if (this.recoverField_d.get() && var1 != null) {
         if (this.recoverField_f.offer(var1)) {
            int var2 = this.recoverField_g.incrementAndGet();
            if (var2 >= 50) {
               RecoverTypeaf2b07cc.recoverMethod_a_c6a0575a().recoverMethod_a_a4fc3973(this.recoverField_h);
            } else {
               RecoverTypeaf2b07cc.recoverMethod_a_c6a0575a().recoverMethod_a_63b8af67("exception_stats_save", this.recoverField_h, 500L);
            }
         }
      }
   }

   private void recoverMethod_c_9b75() {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            RecoverTypeaf2afc7e.this.recoverField_e.set(true);

            try {
               ArrayList var1 = new ArrayList();

               Bundle var2;
               while ((var2 = (Bundle)RecoverTypeaf2afc7e.this.recoverField_f.poll()) != null) {
                  var1.add(var2);
                  RecoverTypeaf2afc7e.this.recoverField_g.decrementAndGet();
               }

               if (!var1.isEmpty()) {
                  RecoverTypeaf2afc7e.this.recoverMethod_a_61fb9e66(var1);
               }
            } finally {
               RecoverTypeaf2afc7e.this.recoverField_e.set(false);
            }
         }
      });
   }

   private void recoverMethod_a_61fb9e66(List var1) {
      if (this.recoverField_c != null && var1 != null && !var1.isEmpty()) {
         try {
            JSONObject var2 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("e_s.catch");
            if (var2 == null) {
               var2 = new JSONObject();
            }

            for (Object recoveryElement_168 : var1) {
               Bundle var4 = (Bundle)recoveryElement_168;
               if (var4 != null) {
                  JSONArray var5 = var2.optJSONArray("data");
                  if (var5 == null) {
                     var5 = new JSONArray();
                  }

                  String var6 = var4.getString("exception_type");
                  if (!TextUtils.isEmpty(var6) && (!var6.equals("1") || !this.recoverMethod_b_79e5e343(var4))) {
                     boolean var7 = false;

                     for (int var8 = 0; var8 < var5.length(); var8++) {
                        JSONObject var9 = (JSONObject)var5.get(var8);
                        if (var9 != null) {
                           String var10 = var9.optString("exception_type");
                           if (!TextUtils.isEmpty(var10) && var10.equals(var6)) {
                              if (!"1".equals(var6) || !var9.has("extra")) {
                                 JSONObject var15 = this.recoverMethod_a_59a60e39(var9, var4);
                                 var7 = true;
                                 var5.remove(var8);
                                 var5.put(var15);
                                 break;
                              }

                              String var11 = var4.getString("extra");
                              String var12 = var9.getString("extra");
                              if (!TextUtils.isEmpty(var11) && !TextUtils.isEmpty(var12) && var12.equals(var11)) {
                                 JSONObject var13 = this.recoverMethod_a_59a60e39(var9, var4);
                                 var7 = true;
                                 var5.remove(var8);
                                 var5.put(var13);
                                 break;
                              }
                           }
                        }
                     }

                     if (!var7) {
                        var5.put(this.recoverMethod_a_59a60e39(null, var4));
                     }

                     var2.put("data", var5);
                  }
               }
            }

            RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("e_s.catch", var2);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "saveData success, cache=" + var2);
         } catch (Throwable var14) {
         }
      }
   }

   public void recoverMethod_b_9b75() {
      boolean var1 = this.recoverField_d.get();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "startReport exception_switch=" + var1);
      if (var1) {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(
            "DELAY_TASK",
            new RecoverTypeaf2b0401() {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     if (RecoverTypeaf2afc7e.this.recoverField_c == null) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "context is null,return");
                        return;
                     }

                     if (!RecoverTypeaf2afc7e.this.recoverMethod_d_9b79()) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "no report");
                        return;
                     }

                     RecoverTypeaf2b07c9.recoverMethod_c_e925fa09("e_s.catch", "e_s.temp");
                     RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("e_s.catch");
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "start to  report");
                     JSONObject var1x = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("e_s.temp");
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", "local cache=" + var1x);
                     if (var1x == null) {
                        var1x = new JSONObject();
                     }

                     JSONArray var2 = var1x.optJSONArray("data");
                     if (var2 == null) {
                        var2 = new JSONArray();
                     }

                     if (var2 == null || var2.length() == 0) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ExceptionStatsManager", " report content is  null");
                        return;
                     }

                     ArrayList var3 = RecoverTypeaf2afc7e.recoverMethod_b_48eba5e9(var2);
                     if (var3 == null || var3.isEmpty()) {
                        return;
                     }

                     int var4 = 0;

                     for (int var5 = var3.size(); var4 < var5; var4++) {
                        JSONObject var6 = new JSONObject();
                        JSONArray var7 = (JSONArray)var3.get(var4);
                        var6.put("slice_index", var4 + 1);
                        var6.put("slice_count", var5);
                        var6.put("data", var7);
                        var6.put("sequence", UUID.randomUUID().toString());
                        RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2afc7e.this.recoverField_c, var6, "exception_stats");
                     }

                     RecoverType5a690d2.recoverMethod_a_abcb8d40(
                        RecoverTypeaf2afc7e.this.recoverField_c,
                        RecoverType5a690d1.recoverMethod_ac_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                     );
                     RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("e_s.temp");
                  } catch (Throwable var8) {
                  }
               }
            }
         );
      }
   }

   private static ArrayList recoverMethod_b_48eba5e9(JSONArray var0) {
      if (var0 != null && var0.length() != 0) {
         try {
            int var1 = 0;
            JSONArray var2 = new JSONArray();
            ArrayList var3 = new ArrayList();

            for (int var4 = 0; var4 < var0.length(); var4++) {
               JSONObject var5 = var0.optJSONObject(var4);
               if (var5 != null && var5.length() != 0) {
                  int var6 = var5.toString().getBytes("UTF-8").length;
                  if (var1 + var6 > 102400) {
                     if (var2.length() > 0) {
                        var3.add(var2);
                     }

                     var2 = new JSONArray();
                     var2.put(var5);
                     var1 = var6;
                  } else {
                     var2.put(var5);
                     var1 += var6;
                  }
               }
            }

            if (var2.length() > 0) {
               var3.add(var2);
            }

            return var3;
         } catch (UnsupportedEncodingException var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptionStatsManager", "partition exception:" + var7.getMessage());
         } catch (Throwable var8) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptionStatsManager", "partition throwable:" + var8.getMessage());
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean recoverMethod_d_9b79() {
      long var1 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_c, RecoverType5a690d1.recoverMethod_ac_48794abf());
      long var3 = System.currentTimeMillis();
      return Math.abs(var3 - var1) >= 3600000L;
   }

   private boolean recoverMethod_b_79e5e343(Bundle var1) {
      if (recoverField_a == null) {
         this.recoverMethod_d_79e5e33f(var1);
         return false;
      }

      Bundle var2 = this.recoverMethod_c_bd038e43(var1);
      long var3 = System.currentTimeMillis();
      if (this.recoverMethod_a_cc086339(recoverField_a, var2) && var3 - recoverField_b < 300L) {
         return true;
      }

      this.recoverMethod_d_79e5e33f(var1);
      return false;
   }

   private Bundle recoverMethod_c_bd038e43(Bundle var1) {
      Bundle var2 = new Bundle();
      var2.putString("extra", var1.getString("extra"));
      var2.putString("exception_type", var1.getString("exception_type"));
      var2.putString("code", var1.getString("code"));
      return var2;
   }

   private void recoverMethod_d_79e5e33f(Bundle var1) {
      recoverField_a = this.recoverMethod_c_bd038e43(var1);
      recoverField_b = System.currentTimeMillis();
   }

   private boolean recoverMethod_a_cc086339(Bundle var1, Bundle var2) {
      if (var1 == var2) {
         return true;
      }

      if (var1 != null && var2 != null) {
         if (var1.size() != var2.size()) {
            return false;
         }

         for (String var4 : var1.keySet()) {
            Object var5 = var1.get(var4);
            Object var6 = var2.get(var4);
            if (var5 != null) {
               if (var5 instanceof Bundle) {
                  if (!(var6 instanceof Bundle) || !this.recoverMethod_a_cc086339((Bundle)var5, (Bundle)var6)) {
                     return false;
                  }
               } else if (!var5.equals(var6)) {
                  return false;
               }
            } else if (var6 != null) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private JSONObject recoverMethod_a_59a60e39(JSONObject var1, Bundle var2) {
      try {
         if (var1 == null) {
            var1 = new JSONObject();
         }

         if (TextUtils.isEmpty(var2.getString("code"))) {
            return var1;
         }

         String var3 = var2.getString("code");
         if (!var1.has("result")) {
            var1.put("result", var3 + "_1");
         } else {
            String var4 = var1.optString("result");
            String[] var5 = var4.split("&");
            StringBuilder var6 = new StringBuilder();
            boolean var7 = false;

            for (int var8 = 0; var8 < var5.length; var8++) {
               String[] var9 = var5[var8].split("_");
               String var10 = var9[0];
               int var11 = Integer.valueOf(var9[1]);
               var6.append(var10).append("_");
               if (var3.equals(var10)) {
                  int var12 = var11 + 1;
                  var6.append(var12);
                  var7 = true;
               } else {
                  var6.append(var11);
               }

               if (var8 != var5.length - 1) {
                  var6.append("&");
               }
            }

            if (!var7) {
               var6.append("&").append(var3).append("_1");
            }

            var1.put("result", var6.toString());
         }

         var1.put("exception_type", var2.getString("exception_type"));

         try {
            JSONObject var15 = new JSONObject(var2.getString("extra"));
            var1.put("extra", var15);
         } catch (Throwable var13) {
         }
      } catch (Throwable var14) {
      }

      return var1;
   }

   static {
      recoverField_j.add("JLocation");
      recoverField_j.add("JAppAll");
      recoverField_i.put(1100, "11");
      recoverField_i.put(1500, "6");
      recoverField_i.put(1502, "7");
      recoverField_i.put(1505, "9");
      recoverField_i.put(1501, "8");
   }

   private static final class Member905dd9bb {
      private static final RecoverTypeaf2afc7e recoverField_a = new RecoverTypeaf2afc7e();
   }
}
