package cn.jiguang.al;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.android.IDataShare;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class RecoverTypeaf2a9728 {
   public static long recoverMethod_a_79e5e333(Bundle var0) {
      return var0 == null ? -1L : var0.getLong("jpush_JMessenger_message_id", -1L);
   }

   public static long recoverMethod_b_79e5e333(Bundle var0) {
      return var0 == null ? -1L : var0.getLong("jpush_JMessenger_run_id", -1L);
   }

   public static class Member8ee171e5 {
      private static volatile RecoverTypeaf2a9728.Member8ee171e5 recoverField_a;
      private final ConcurrentLinkedQueue recoverField_b = new ConcurrentLinkedQueue();

      private Member8ee171e5() {
      }

      public static RecoverTypeaf2a9728.Member8ee171e5 recoverMethod_a_6cb381a1() {
         if (recoverField_a == null) {
            synchronized (RecoverTypeaf2a9728.Member8ee171e5.class) {
               if (recoverField_a == null) {
                  recoverField_a = new RecoverTypeaf2a9728.Member8ee171e5();
               }
            }
         }

         return recoverField_a;
      }

      public boolean recoverMethod_a_79e5e343(Bundle var1) {
         if (var1 == null) {
            return false;
         }

         long var2 = RecoverTypeaf2a9728.recoverMethod_a_79e5e333(var1);
         long var4 = RecoverTypeaf2a9728.recoverMethod_b_79e5e333(var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MessageProcessor", "isMessageProcessed Message " + var2 + " from run " + var4);
         if (var2 != -1L && var4 != -1L) {
            String var6 = var4 + "_" + var2;
            if (this.recoverField_b.contains(var6)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MessageProcessor", "Message " + var2 + " from run " + var4 + " has been processed before");
               return true;
            }

            synchronized (this) {
               if (this.recoverField_b.size() >= 1000) {
                  this.recoverField_b.poll();
               }

               this.recoverField_b.offer(var6);
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static class Member8ee171e6 {
      private final ConcurrentHashMap recoverField_a = new ConcurrentHashMap();
      private static volatile RecoverTypeaf2a9728.Member8ee171e6 recoverField_b;

      private Member8ee171e6() {
      }

      public static RecoverTypeaf2a9728.Member8ee171e6 recoverMethod_a_6cb381c0() {
         if (recoverField_b == null) {
            synchronized (RecoverTypeaf2a9728.Member8ee171e6.class) {
               if (recoverField_b == null) {
                  recoverField_b = new RecoverTypeaf2a9728.Member8ee171e6();
               }
            }
         }

         return recoverField_b;
      }

      private LinkedBlockingQueue recoverMethod_a_f828813b(String var1) {
         LinkedBlockingQueue var2 = (LinkedBlockingQueue)this.recoverField_a.get(var1);
         if (var2 == null) {
            synchronized (this.recoverField_a) {
               var2 = (LinkedBlockingQueue)this.recoverField_a.get(var1);
               if (var2 == null) {
                  var2 = new LinkedBlockingQueue(1000);
                  this.recoverField_a.put(var1, var2);
               }
            }
         }

         return var2;
      }

      public boolean recoverMethod_a_df065b4d(Bundle var1, String var2) {
         if (var1 == null || var2 == null) {
            return false;
         } else if (!var1.containsKey("jpush_JMessenger_message_id")) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "消息缺少ID，不是要处理的信息，不添加到队列");
            return false;
         } else {
            return true;
         }
      }

      public synchronized boolean recoverMethod_a_3144747b(Context var1, String var2, String var3, Bundle var4, String var5) {
         try {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "MessageQueueManager", "开始添加消息到队列 - componentCls: " + var2 + ", action: " + var3 + ", toProcess: " + var5
            );
            if (!this.recoverMethod_a_df065b4d(var4, var5)) {
               return false;
            }

            RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523 var6 = new RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523(var1, var2, var3, var4, var5);
            LinkedBlockingQueue var7 = this.recoverMethod_a_f828813b(var5);
            boolean var8 = var7.offer(var6);
            if (!var8) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "消息添加到队列失败 - 队列可能已满:" + var7.size());
            }

            return var8;
         } catch (Exception var9) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "添加消息到队列失败: " + var9.getMessage());
            return false;
         }
      }

      public synchronized void recoverMethod_a_19005124(IDataShare var1, String var2) throws RemoteException {
         if (var1 != null && var2 != null) {
            RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523 var3 = this.recoverMethod_b_ba34ed(var2);

            try {
               while (var3 != null) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                     "MessageQueueManager",
                     "sendByAidl queue, "
                        + RecoverTypeaf2a9728.recoverMethod_a_79e5e333(var3.recoverMethod_c_88d0e4f9())
                        + ","
                        + var3.recoverMethod_a_73cf11cb()
                        + ","
                        + var3.recoverMethod_b_73cf11cb()
                  );
                  var1.onAction(var3.recoverMethod_a_73cf11cb(), var3.recoverMethod_b_73cf11cb(), var3.recoverMethod_c_88d0e4f9());
                  var3 = this.recoverMethod_b_ba34ed(var2);
               }
            } catch (RemoteException var5) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "sendMessage RemoteException: " + var5.getMessage());
               this.recoverMethod_a_46a60a73(var3, var2);
               throw var5;
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "sendMessage: aidl或toProcess为空");
         }
      }

      public synchronized Bundle recoverMethod_a_f46529c2(Context var1, Uri var2, String var3) {
         if (var1 != null && var3 != null) {
            try {
               Bundle var4 = null;
               RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523 var5 = this.recoverMethod_b_ba34ed(var3);

               while (var5 != null) {
                  try {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                        "MessageQueueManager",
                        "sendByProvider, uri="
                           + RecoverTypeaf2a9728.recoverMethod_a_79e5e333(var5.recoverMethod_c_88d0e4f9())
                           + ","
                           + var2
                           + ", componentCls="
                           + var5.recoverMethod_a_73cf11cb()
                           + ", action="
                           + var5.recoverMethod_b_73cf11cb()
                     );
                     var4 = var1.getContentResolver()
                        .call(var2, var5.recoverMethod_a_73cf11cb(), var5.recoverMethod_b_73cf11cb(), var5.recoverMethod_c_88d0e4f9());
                     var5 = this.recoverMethod_b_ba34ed(var3);
                  } catch (Exception var7) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "发送消息失败: " + var7.getMessage());
                     this.recoverMethod_a_46a60a73(var5, var3);
                     throw var7;
                  }
               }

               return var4;
            } catch (Exception var8) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("MessageQueueManager", "callMessage发生异常: " + var8.getMessage());
               return null;
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "callMessage: context或toProcess为空");
            return null;
         }
      }

      private void recoverMethod_a_46a60a73(RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523 var1, String var2) {
         try {
            LinkedBlockingQueue var3 = this.recoverMethod_a_f828813b(var2);
            if (var3 != null) {
               LinkedBlockingQueue var4 = new LinkedBlockingQueue(1000);
               var4.offer(var1);

               RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523 var5;
               while ((var5 = (RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523)var3.poll()) != null) {
                  var4.offer(var5);
               }

               this.recoverField_a.put(var2, var4);
            }
         } catch (Exception var6) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("MessageQueueManager", "重新入队失败: " + var6.getMessage());
         }
      }

      private RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523 recoverMethod_b_ba34ed(String var1) {
         try {
            LinkedBlockingQueue var2 = (LinkedBlockingQueue)this.recoverField_a.get(var1);
            return var2 == null ? null : (RecoverTypeaf2a9728.Member8ee171e6.Member5c4c9523)var2.poll();
         } catch (Exception var3) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MessageQueueManager", "从队列获取消息失败: " + var3.getMessage());
            return null;
         }
      }

      public static class Member5c4c9523 {
         private Context recoverField_a;
         private String recoverField_b;
         private String recoverField_c;
         private Bundle recoverField_d;
         private String recoverField_e;

         public Member5c4c9523(Context var1, String var2, String var3, Bundle var4, String var5) {
            this.recoverField_a = var1;
            this.recoverField_b = var2;
            this.recoverField_c = var3;
            this.recoverField_d = var4;
            this.recoverField_e = var5;
         }

         public String recoverMethod_a_73cf11cb() {
            return this.recoverField_b;
         }

         public String recoverMethod_b_73cf11cb() {
            return this.recoverField_c;
         }

         public Bundle recoverMethod_c_88d0e4f9() {
            return this.recoverField_d;
         }
      }
   }

   public static class Member8ee171e7 {
      private static volatile RecoverTypeaf2a9728.Member8ee171e7 recoverField_a;
      private final AtomicLong recoverField_b = new AtomicLong(0L);
      private long recoverField_c;

      private Member8ee171e7(Context var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("context cannot be null");
         }

         this.recoverField_c = this.recoverMethod_b_f0554de7(var1);
      }

      private long recoverMethod_b_f0554de7(Context var1) {
         SharedPreferences var2 = var1.getSharedPreferences("cn.jiguang.helper.jpush_messenger", 0);
         long var3 = var2.getLong("run_id", 0L);
         long var5 = var3 + 1L;
         if (var5 > 2147483647L) {
            var5 = 0L;
         }

         var2.edit().putLong("run_id", var5).commit();
         return var5;
      }

      public static RecoverTypeaf2a9728.Member8ee171e7 recoverMethod_a_21dbd0dd(Context var0) {
         if (recoverField_a == null) {
            synchronized (RecoverTypeaf2a9728.Member8ee171e7.class) {
               if (recoverField_a == null) {
                  recoverField_a = new RecoverTypeaf2a9728.Member8ee171e7(var0);
               }
            }
         }

         return recoverField_a;
      }

      public synchronized long recoverMethod_a_9b69() {
         long var1 = this.recoverField_b.incrementAndGet();
         if (var1 >= 9223372036854774807L) {
            this.recoverField_b.set(100L);
            var1 = this.recoverField_b.incrementAndGet();
         }

         return var1;
      }

      public Bundle recoverMethod_a_bd038e43(Bundle var1) {
         if (var1 == null) {
            var1 = new Bundle();
         }

         var1.putLong("jpush_JMessenger_message_id", this.recoverMethod_a_9b69());
         var1.putLong("jpush_JMessenger_run_id", this.recoverField_c);
         return var1;
      }
   }
}
