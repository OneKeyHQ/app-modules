package cn.jiguang.bg;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.internal.JNAction;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public final class RecoverTypeaf2af8bf {
   private static final AtomicBoolean recoverField_a = new AtomicBoolean(false);
   private final Context recoverField_b = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
   private final RecoverTypeaf2af8be recoverField_c = new RecoverTypeaf2af8be(this.recoverField_b);

   private RecoverTypeaf2af8bf() {
   }

   public static RecoverTypeaf2af8bf recoverMethod_a_c69e84c7() {
      return RecoverTypeaf2af8bf.Member904fc9bd.recoverField_a;
   }

   public boolean recoverMethod_a_aad3b203(String var1) {
      if (recoverField_a.get()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "loaded, skip");
         return true;
      }

      String var2 = RecoverTypeaf2af8bd.recoverMethod_a_a4ea6d43(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), var1);
      boolean var3 = RecoverTypeaf2b07c9.recoverMethod_f_aad3b203(var2) && this.recoverMethod_d_aad3b203(var2);
      if (var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "local ok, load");
         boolean var4 = this.recoverMethod_b_aad3b203(var2);
         if (var4 && RecoverTypeaf2af8c0.recoverMethod_f_9b79()) {
            this.recoverMethod_c_9b75();
            RecoverTypeaf2af8c0.recoverMethod_h_9b75();
         }

         return var4;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "local invalid, d async");
         this.recoverMethod_a_e925fa09(var1, var2);
         return false;
      }
   }

   private void recoverMethod_a_e925fa09(String var1, String var2) {
      this.recoverMethod_a_3b99f9eb(var1, var2, true);
   }

   private void recoverMethod_a_3b99f9eb(final String var1, final String var2, final boolean var3) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(
         "NORMAL_TASK",
         new Runnable() {
            @Override
            public void run() {
               try {
                  RecoverTypeaf2af8bf.Member904fc9bc var1x = RecoverTypeaf2af8bf.this.recoverField_c
                     .recoverMethod_a_d95a863e(RecoverTypeaf2af8c0.recoverMethod_j_73cf11cb(), var1);
                  if (var1x.recoverField_a) {
                     RecoverTypeaf2af8c0.recoverMethod_g_9b75();
                     RecoverTypeaf2af8bf.recoverMethod_e_aad3b1ff(var1x.recoverField_b);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "d ok, load=" + var3);
                     if (var3) {
                        boolean var2x = RecoverTypeaf2af8bf.recoverMethod_a_c69e84c7().recoverMethod_b_aad3b203(var2);
                        if (var2x) {
                           RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "load ok");
                           if (RecoverTypeaf2af8c0.recoverMethod_f_9b79()) {
                              RecoverTypeaf2af8bf.this.recoverMethod_c_9b75();
                              RecoverTypeaf2af8c0.recoverMethod_h_9b75();
                           }
                        } else {
                           RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "load f after d");
                        }
                     }
                  } else {
                     RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "d f" + (var3 ? ", no load" : ""));
                  }
               } catch (Exception var3x) {
                  RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "err d" + (var3 ? " load" : "") + ": " + var3x.getMessage());
               }
            }
         }
      );
   }

   private boolean recoverMethod_d_aad3b203(String var1) {
      String var2 = RecoverTypeaf2af8c0.recoverMethod_b_73cf11cb();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "c m: " + (var2 != null ? var2 : "null"));
      if (var2 != null && !var2.trim().isEmpty()) {
         String var3 = RecoverTypeaf2b07c9.recoverMethod_g_e96ea081(var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "a m: " + (var3 != null ? var3 : "null"));
         return TextUtils.equals(var2, var3);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "no c m, no verify");
         return false;
      }
   }

   public boolean recoverMethod_b_aad3b203(String var1) {
      if (recoverField_a.get()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "loaded, skip");
         return true;
      }

      try {
         JNAction.loadPath(var1);
         RecoverTypeaf2af8c1.recoverMethod_b_9b79();
         recoverField_a.set(true);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "load complete");
         return true;
      } catch (Exception var3) {
         recoverField_a.set(false);
         return false;
      }
   }

   private void recoverMethod_c_9b75() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("report_type", 2);
         var1.put("local_v", RecoverTypeaf2af8c1.recoverMethod_c_73cf11cb());
         RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), var1, "xstats");
      } catch (Exception var2) {
      }
   }

   public static void recoverMethod_b_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "force d...");
      RecoverTypeaf2af8bf var0 = recoverMethod_a_c69e84c7();
      String var1 = RecoverTypeaf2af8bd.recoverMethod_a_a4ea6d43(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), RecoverTypeaf2af8bd.recoverField_g);
      var0.recoverMethod_a_3b99f9eb(RecoverTypeaf2af8bd.recoverField_g, var1, false);
   }

   private static void recoverMethod_e_aad3b1ff(String var0) {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("report_type", 1);
         var1.put("local_v", RecoverTypeaf2af8c1.recoverMethod_c_73cf11cb());
         if (var0 != null && !var0.trim().isEmpty()) {
            JSONObject var2 = new JSONObject(var0);
            var1.put("download_v", var2.optString("v"));
         }

         RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), var1, "xstats");
      } catch (Exception var3) {
      }
   }

   public static class Member904fc9bc {
      public final boolean recoverField_a;
      public final String recoverField_b;

      public Member904fc9bc(boolean var1, String var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }
   }

   private static final class Member904fc9bd {
      private static final RecoverTypeaf2af8bf recoverField_a = new RecoverTypeaf2af8bf();
   }
}
