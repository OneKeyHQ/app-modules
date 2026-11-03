package cn.jiguang.az;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ba.RecoverTypeaf2ae239;
import cn.jiguang.ba.RecoverTypeaf2ae23a;
import cn.jiguang.ba.RecoverTypeaf2ae23b;
import cn.jiguang.ba.RecoverTypeaf2ae23c;
import cn.jiguang.ba.RecoverTypeaf2ae23d;
import cn.jiguang.ba.RecoverTypeaf2ae23e;
import cn.jiguang.ba.RecoverTypeaf2ae23f;
import cn.jiguang.bb.RecoverTypeaf2ae5f8;
import cn.jiguang.bf.RecoverTypeaf2af4fd;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.internal.ActionManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2acbb4 {
   public static RecoverTypeaf2ae23b recoverField_a;

   public static JSONObject recoverMethod_a_e86c016d(boolean var0) {
      try {
         JSONObject var1 = new JSONObject();
         if (var0 && recoverField_a != null) {
            var1.put("event_id", recoverField_a.toString());
         }

         JSONObject var2 = new JSONObject();
         ActionManager.getInstance().wrapSdkVersionInfo(var2);
         var2.put("core_sdk_ver", RecoverType5a681cd.recoverField_b);
         var1.put("sdks", var2);
         return var1;
      } catch (JSONException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse SentryEnvelopeHeader.", var3);
         return null;
      }
   }

   public static JSONObject recoverMethod_a_fd5e2d6d(String var0) {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("content_type", "application/json");
         var1.put("type", var0);
         return var1;
      } catch (JSONException var2) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse SentryEnvelopeItemHeader.", var2);
         return null;
      }
   }

   public static JSONObject recoverMethod_a_e6f7d4fd(Context var0, RecoverTypeaf2ae23f var1) {
      try {
         JSONObject var2 = new JSONObject();
         if (var1.recoverMethod_b_a9fb56d5() != null) {
            var2.put("sid", var1.recoverMethod_b_a9fb56d5().toString());
         }

         var2.put("did", RecoverTypeaf2b07c8.recoverMethod_a_e501eb0d(var0));
         if (var1.recoverMethod_c_12a799b2() != null) {
            var2.put("init", var1.recoverMethod_c_12a799b2());
         }

         Date var3 = var1.recoverMethod_a_a911e122();
         if (var3 != null) {
            var2.put("started", RecoverTypeaf2b07c6.recoverMethod_a_b044460a(var3));
         }

         RecoverTypeaf2ae23f.Member8ffb533c var4 = var1.recoverMethod_e_8ed5cb2a();
         if (var4 != null) {
            var2.put("status", var4.name().toLowerCase(Locale.ROOT));
         }

         if (var1.recoverMethod_f_10c66500() != null) {
            var2.put("seq", var1.recoverMethod_f_10c66500());
         }

         int var5 = var1.recoverMethod_d_9b68();
         if (var5 > 0) {
            var2.put("errors", var5);
         }

         if (var1.recoverMethod_g_51f06e0b() != null) {
            var2.put("duration", var1.recoverMethod_g_51f06e0b());
         }

         if (var1.recoverMethod_h_a911e122() != null) {
            var2.put("timestamp", RecoverTypeaf2b07c6.recoverMethod_a_b044460a(var1.recoverMethod_h_a911e122()));
         }

         return var2;
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse SentrySessionEnvelopeItem.", var6);
         return null;
      }
   }

   public static JSONObject recoverMethod_a_47aa97d0(Context var0, Throwable var1) {
      try {
         JSONObject var2 = new JSONObject();
         JSONArray var3 = recoverMethod_c_d28ad78e(var0, var1);
         if (var3 != null && var3.length() > 0) {
            JSONObject var4 = new JSONObject();
            var4.put("values", var3);
            var2.put("exception", var4);
            if (var1 instanceof RecoverTypeaf2acbb3) {
               RecoverTypeaf2ae239 var7 = ((RecoverTypeaf2acbb3)var1).recoverMethod_a_c69bca8d();
               if (var7 != null && var7.recoverMethod_a_73cf11cb().equalsIgnoreCase("UncaughtExceptionHandler")) {
                  var2.put("level", "fatal");
               }
            }

            JSONArray var8 = recoverMethod_a_317afb10(var1);
            if (var8 != null && var8.length() > 0) {
               JSONObject var5 = new JSONObject();
               var5.put("values", var8);
               var2.put("threads", var5);
            }

            var2.put("timestamp", RecoverTypeaf2b07c6.recoverMethod_a_b044460a(RecoverTypeaf2b07c6.recoverMethod_c_a911e122()));
            JSONObject var9 = new JSONObject();
            var9.put("app", RecoverTypeaf2af4fd.recoverMethod_a_16ce4761(var0));
            var9.put("os", RecoverTypeaf2af4fd.recoverMethod_d_16ce4761(var0));
            var9.put("device", RecoverTypeaf2af4fd.recoverMethod_c_16ce4761(var0));
            var2.put("contexts", var9);
            var2.put("dist", Integer.toString(RecoverType5a681cd.recoverField_c));
            var2.put("environment", "production");
            var2.put("platform", "java");
            recoverField_a = new RecoverTypeaf2ae23b();
            var2.put("event_id", recoverField_a.toString());
            var2.put("sdk", RecoverTypeaf2af4fd.recoverMethod_a_a7dbfe3());
            var2.put("user", RecoverTypeaf2af4fd.recoverMethod_b_16ce4761(var0));
            return var2;
         } else {
            return null;
         }
      } catch (JSONException var6) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse SentryEventEnvelopeItem.", var6);
         return null;
      }
   }

   public static void recoverMethod_b_554ebe2(Context var0, Throwable var1) {
      try {
         RecoverTypeaf2acbbb var2 = new RecoverTypeaf2acbbb();
         RecoverTypeaf2acbba var3 = new RecoverTypeaf2acbba(var2);

         for (Object recoveryElement_156 : var3.recoverMethod_a_be259aa7(var1)) {
            RecoverTypeaf2ae23a var6 = (RecoverTypeaf2ae23a)recoveryElement_156;
            if (var6.recoverMethod_e_c69bcb09() != null && var6.recoverMethod_e_c69bcb09().recoverMethod_a_a9863e32() != null) {
               List var7 = var6.recoverMethod_e_c69bcb09().recoverMethod_a_a9863e32();

               for (int var8 = 0; var8 < var7.size(); var8++) {
                  RecoverTypeaf2ae23c var9 = (RecoverTypeaf2ae23c)var7.get(var8);
                  if (null != var9) {
                     String var10 = RecoverTypeaf2af4fd.recoverMethod_a_e96ea081(var9.recoverMethod_c_73cf11cb());
                     if (!TextUtils.isEmpty(var10)) {
                        boolean var11 = null != var6.recoverMethod_f_c69bca8d()
                           && TextUtils.equals("ANR", var6.recoverMethod_f_c69bca8d().recoverMethod_a_73cf11cb());
                        RecoverTypeaf2ae5f8.recoverMethod_a_c69c3eae().recoverMethod_a_9d23c587(var0, var10, var11 ? "EX_ANR" : "EX_CRASH");
                        RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptionParseUtils", "sdk exception occurred, anr " + var11);
                        return;
                     }
                  }
               }
            }
         }
      } catch (Throwable var12) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptionParseUtils", "count ex failed, " + var12.getMessage());
      }
   }

   private static JSONArray recoverMethod_c_d28ad78e(Context var0, Throwable var1) {
      try {
         RecoverTypeaf2acbbb var2 = new RecoverTypeaf2acbbb();
         RecoverTypeaf2acbba var3 = new RecoverTypeaf2acbba(var2);
         List var4 = var3.recoverMethod_a_be259aa7(var1);
         if (var4 != null && var4.size() > 0) {
            JSONArray var5 = new JSONArray();
            int var6 = var4.size();

            for (int var7 = 0; var7 < var6; var7++) {
               JSONObject var8 = new JSONObject();
               RecoverTypeaf2ae23a var9 = (RecoverTypeaf2ae23a)var4.get(var7);
               if (var9 != null) {
                  recoverMethod_a_4f662e9f(var8, var9.recoverMethod_f_c69bca8d());
                  if (var9.recoverMethod_c_73cf11cb() != null) {
                     var8.put("module", var9.recoverMethod_c_73cf11cb());
                  }

                  if (var9.recoverMethod_d_10c66500() != null) {
                     var8.put("thread_id", var9.recoverMethod_d_10c66500());
                  }

                  if (var9.recoverMethod_a_73cf11cb() != null) {
                     var8.put("type", var9.recoverMethod_a_73cf11cb());
                  }

                  if (var9.recoverMethod_b_73cf11cb() != null) {
                     var8.put("value", var9.recoverMethod_b_73cf11cb());
                  }

                  if (var9.recoverMethod_e_c69bcb09() != null) {
                     recoverMethod_a_4f68001b(var8, var9.recoverMethod_e_c69bcb09());
                  }

                  if (var8.length() > 0) {
                     var5.put(var8);
                  }
               }
            }

            if (var5.length() > 0) {
               return var5;
            }
         }
      } catch (JSONException var10) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse Exception.", var10);
      }

      return null;
   }

   private static JSONArray recoverMethod_a_317afb10(Throwable var0) {
      try {
         if (var0 instanceof RecoverTypeaf2acbb3) {
            RecoverTypeaf2acbb3 var1 = (RecoverTypeaf2acbb3)var0;
            RecoverTypeaf2acbbb var2 = new RecoverTypeaf2acbbb();
            RecoverTypeaf2acbbc var3 = new RecoverTypeaf2acbbc(var2);
            ArrayList var4 = new ArrayList();
            var4.add(var1.recoverMethod_c_943922f2().getId());
            List var5 = var3.recoverMethod_a_3038db21(var4);
            if (var5 != null && var5.size() > 0) {
               JSONArray var6 = new JSONArray();
               int var7 = var5.size();

               for (int var8 = 0; var8 < var7; var8++) {
                  JSONObject var9 = new JSONObject();
                  RecoverTypeaf2ae23e var10 = (RecoverTypeaf2ae23e)var5.get(var8);
                  if (var10 != null) {
                     if (var10.recoverMethod_c_12a799b2() != null) {
                        var9.put("crashed", var10.recoverMethod_c_12a799b2());
                     }

                     if (var10.recoverMethod_d_12a799b2() != null) {
                        var9.put("current", var10.recoverMethod_d_12a799b2());
                     }

                     if (var10.recoverMethod_b_73cf11cb() != null) {
                        var9.put("name", var10.recoverMethod_b_73cf11cb());
                     }

                     if (var10.recoverMethod_g_12a799b2() != null) {
                        var9.put("daemon", var10.recoverMethod_g_12a799b2());
                     }

                     if (var10.recoverMethod_a_10c66500() != null) {
                        var9.put("id", var10.recoverMethod_a_10c66500());
                     }

                     if (var10.recoverMethod_f_bd10275c() != null) {
                        var9.put("priority", var10.recoverMethod_f_bd10275c());
                     }

                     if (var10.recoverMethod_e_c69bcb09() != null) {
                        recoverMethod_a_4f68001b(var9, var10.recoverMethod_e_c69bcb09());
                     }

                     if (var10.recoverMethod_h_73cf11cb() != null) {
                        var9.put("state", var10.recoverMethod_h_73cf11cb());
                     }

                     if (var9.length() > 0) {
                        var6.put(var9);
                     }
                  }
               }

               if (var6.length() > 0) {
                  return var6;
               }
            }
         }
      } catch (JSONException var11) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse Exception.", var11);
      }

      return null;
   }

   private static void recoverMethod_a_4f662e9f(JSONObject var0, RecoverTypeaf2ae239 var1) {
      try {
         if (var1 == null) {
            return;
         }

         JSONObject var2 = new JSONObject();
         if (var1.recoverMethod_a_73cf11cb() != null) {
            var2.put("type", var1.recoverMethod_a_73cf11cb());
         }

         if (var1.recoverMethod_b_12a799b2() != null) {
            var2.put("handled", var1.recoverMethod_b_12a799b2());
         }

         var0.put("mechanism", var2);
      } catch (JSONException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse writeMechanism.", var3);
      }
   }

   private static void recoverMethod_a_4f68001b(JSONObject var0, RecoverTypeaf2ae23d var1) {
      try {
         if (var1 == null) {
            return;
         }

         JSONObject var2 = new JSONObject();
         if (var1.recoverMethod_a_a9863e32() != null && var1.recoverMethod_a_a9863e32().size() > 0) {
            if (var1.recoverMethod_b_12a799b2() != null) {
               var2.put("snapshot", var1.recoverMethod_b_12a799b2());
            }

            int var3 = var1.recoverMethod_a_a9863e32().size();
            JSONArray var4 = new JSONArray();

            for (int var5 = 0; var5 < var3; var5++) {
               JSONObject var6 = new JSONObject();
               RecoverTypeaf2ae23c var7 = (RecoverTypeaf2ae23c)var1.recoverMethod_a_a9863e32().get(var5);
               if (var7 != null) {
                  recoverMethod_a_4f678bbc(var6, var7);
                  if (var6.length() > 0) {
                     var4.put(var6);
                  }
               }
            }

            if (var4.length() > 0) {
               var2.put("frames", var4);
            }
         }

         if (var2.length() > 0) {
            var0.put("stacktrace", var2);
         }
      } catch (JSONException var8) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse StackTrace.", var8);
      }
   }

   private static void recoverMethod_a_4f678bbc(JSONObject var0, RecoverTypeaf2ae23c var1) {
      try {
         if (var1 == null) {
            return;
         }

         if (var1.recoverMethod_n_12a799b2() != null) {
            var0.put("native", var1.recoverMethod_n_12a799b2());
         }

         if (var1.recoverMethod_a_73cf11cb() != null) {
            var0.put("filename", var1.recoverMethod_a_73cf11cb());
         }

         if (var1.recoverMethod_b_73cf11cb() != null) {
            var0.put("function", var1.recoverMethod_b_73cf11cb());
         }

         if (var1.recoverMethod_h_12a799b2() != null) {
            var0.put("in_app", var1.recoverMethod_h_12a799b2());
         }

         if (var1.recoverMethod_d_bd10275c() != null) {
            var0.put("lineno", var1.recoverMethod_d_bd10275c());
         }

         if (var1.recoverMethod_c_73cf11cb() != null) {
            var0.put("module", var1.recoverMethod_c_73cf11cb());
         }

         if (var1.recoverMethod_i_73cf11cb() != null) {
            var0.put("package", var1.recoverMethod_i_73cf11cb());
         }

         if (var1.recoverMethod_f_73cf11cb() != null) {
            var0.put("abs_path", var1.recoverMethod_f_73cf11cb());
         }

         if (var1.recoverMethod_e_bd10275c() != null) {
            var0.put("colno", var1.recoverMethod_e_bd10275c());
         }

         if (var1.recoverMethod_g_73cf11cb() != null) {
            var0.put("context_line", var1.recoverMethod_g_73cf11cb());
         }

         if (var1.recoverMethod_j_73cf11cb() != null) {
            var0.put("platform", var1.recoverMethod_j_73cf11cb());
         }

         if (var1.recoverMethod_k_73cf11cb() != null) {
            var0.put("image_addr", var1.recoverMethod_k_73cf11cb());
         }

         if (var1.recoverMethod_l_73cf11cb() != null) {
            var0.put("symbol_addr", var1.recoverMethod_l_73cf11cb());
         }

         if (var1.recoverMethod_m_73cf11cb() != null) {
            var0.put("instruction_addr", var1.recoverMethod_m_73cf11cb());
         }

         if (var1.recoverMethod_o_73cf11cb() != null) {
            var0.put("raw_function", var1.recoverMethod_o_73cf11cb());
         }
      } catch (JSONException var3) {
         RecoverTypeaf2a9ae9.recoverMethod_a_1765c98c("ExceptionParseUtils", "Error parse StackFrame.", var3);
      }
   }
}
