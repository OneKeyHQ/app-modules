package cn.jiguang.bj;

import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.al.RecoverTypeaf2a972b;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RecoverTypeaf2b0403 {
   private static final HashMap recoverField_a = new HashMap();
   private static final HashMap recoverField_b = new HashMap();

   private static ExecutorService recoverMethod_c_3dfd5c7c(String var0) {
      String var1 = (String)recoverField_b.get(var0);
      var0 = var1 != null ? var0 : "NORMAL_TASK";
      var1 = (String)recoverField_b.get(var0);
      ExecutorService var2 = (ExecutorService)recoverField_a.get(var0);
      if (var2 == null || var2.isShutdown()) {
         synchronized (RecoverTypeaf2b0403.class) {
            var2 = (ExecutorService)recoverField_a.get(var0);
            byte var4 = 1;
            if (var2 == null || var2.isShutdown()) {
               switch (var0) {
                  case "SDK_INIT":
                  case "SDK_SERVICE_INIT":
                  case "TCP_CONN_TASK":
                  case "MAJOR_TASK":
                     var2 = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), new RecoverTypeaf2b0402(var1));
                     break;
                  case "AIDL_TASK":
                     var2 = new ThreadPoolExecutor(1, 1, 15L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), new RecoverTypeaf2b0402(var1));
                     break;
                  case "DYNAMIC_LOAD_TASK":
                  case "IPC_ACTION_TASK":
                  case "MAIN_IPC_ACTION_TASK":
                     var2 = new ThreadPoolExecutor(1, 1, 15L, TimeUnit.SECONDS, new LinkedBlockingDeque(), new RecoverTypeaf2b0402(var1));
                     break;
                  case "UPLOAD_REPORT":
                  case "SCHEDULE_TASK":
                     var2 = new ThreadPoolExecutor(2, 2, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque(), new RecoverTypeaf2b0402(var1));
                     ((ThreadPoolExecutor)var2).allowCoreThreadTimeOut(true);
                     break;
                  case "ONCE_TASK":
                     var2 = new ThreadPoolExecutor(1, 1, 3L, TimeUnit.SECONDS, new LinkedBlockingDeque(), new RecoverTypeaf2b0402(var1));
                     ((ThreadPoolExecutor)var2).allowCoreThreadTimeOut(true);
                     break;
                  case "FUTURE_TASK":
                     var2 = new ThreadPoolExecutor(3, 3, 15L, TimeUnit.SECONDS, new LinkedBlockingDeque(5), new RecoverTypeaf2b0402(var1));
                     ((ThreadPoolExecutor)var2).allowCoreThreadTimeOut(true);
                     var4 = 3;
                     break;
                  case "DELAY_TASK":
                     var2 = Executors.newScheduledThreadPool(1, new RecoverTypeaf2b0402(var1));
                     break;
                  case "NORMAL_TASK":
                  default:
                     var2 = new ThreadPoolExecutor(1, 5, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque(2), new RecoverTypeaf2b0402(var1));
               }

               ((ThreadPoolExecutor)var2).setRejectedExecutionHandler(new RecoverTypeaf2b0400(var1, var4));
               recoverField_a.put(var0, var2);
            }
         }
      }

      return var2;
   }

   public static void recoverMethod_a_97dca6fd(String var0, Runnable var1) {
      recoverMethod_a_135d7bee(var0, var1, 0, true);
   }

   public static void recoverMethod_a_63b8eb77(String var0, Runnable var1, boolean var2) {
      recoverMethod_a_135d7bee(var0, var1, 0, var2);
   }

   public static void recoverMethod_a_63b8aba6(String var0, Runnable var1, int var2) {
      recoverMethod_a_135d7bee(var0, var1, var2, true);
   }

   public static void recoverMethod_a_135d7bee(String var0, final Runnable var1, int var2, final boolean var3) {
      ExecutorService var4 = null;

      try {
         Runnable var5 = new Runnable() {
            @Override
            public void run() {
               try {
                  if (var3 && (RecoverTypeaf2a972a.recoverMethod_b_9b79() || RecoverTypeaf2a972b.recoverMethod_a_9b79())) {
                     return;
                  }

                  var1.run();
               } catch (Throwable var2x) {
                  RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SDKWorker_XExecutor", "execute run failed, e:" + var2x);
               }
            }
         };
         var4 = recoverMethod_c_3dfd5c7c(var0);
         if (null != var4) {
            if (var2 > 0 && var4 instanceof ScheduledExecutorService) {
               ((ScheduledExecutorService)var4).schedule(var5, var2, TimeUnit.MILLISECONDS);
            } else {
               var4.execute(var5);
            }
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SDKWorker_XExecutor", "execute failed, try again e:" + var6);
      }
   }

   private static void recoverMethod_a_d903b8ba(ExecutorService var0) {
      if (var0 != null) {
         try {
            var0.shutdown();
            if (!var0.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
               var0.shutdownNow();
               if (!var0.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
                  RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("SDKWorker_XExecutor", "executor did not terminate");
               }
            }
         } catch (InterruptedException var2) {
            var0.shutdownNow();
            RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("SDKWorker_XExecutor", "current thread is interrupted by self");
            Thread.currentThread().interrupt();
         } catch (Throwable var3) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SDKWorker_XExecutor", "shutDown e:" + var3);
         }
      }
   }

   public static void recoverMethod_a_aad3b1ff(String var0) {
      try {
         ExecutorService var1 = recoverMethod_c_3dfd5c7c(var0);
         recoverMethod_a_d903b8ba(var1);
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SDKWorker_XExecutor", "shutdown executor " + var0 + "error: " + var2);
      }
   }

   public static boolean recoverMethod_b_aad3b203(String var0) {
      try {
         ExecutorService var1 = recoverMethod_c_3dfd5c7c(var0);
         return var1 != null && !var1.isShutdown() && !var1.isTerminated();
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SDKWorker_XExecutor", "check executor " + var0 + " isAlive error: " + var2);
         return false;
      }
   }

   static {
      recoverField_b.put("SDK_INIT", "ic");
      recoverField_b.put("SDK_SERVICE_INIT", "mn");
      recoverField_b.put("TCP_CONN_TASK", "tc");
      recoverField_b.put("UPLOAD_REPORT", "rp");
      recoverField_b.put("ONCE_TASK", "oc");
      recoverField_b.put("SCHEDULE_TASK", "sc");
      recoverField_b.put("MAJOR_TASK", "mj");
      recoverField_b.put("NORMAL_TASK", "nr");
      recoverField_b.put("FUTURE_TASK", "fr");
      recoverField_b.put("DELAY_TASK", "dl");
      recoverField_b.put("AIDL_TASK", "al");
      recoverField_b.put("DYNAMIC_LOAD_TASK", "dl");
      recoverField_b.put("IPC_ACTION_TASK", "ca");
      recoverField_b.put("MAIN_IPC_ACTION_TASK", "m_ca");
   }
}
