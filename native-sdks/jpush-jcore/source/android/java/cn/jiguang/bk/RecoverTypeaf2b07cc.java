package cn.jiguang.bk;

import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RecoverTypeaf2b07cc {
   private static final AtomicInteger recoverField_a = new AtomicInteger(0);
   private static volatile Handler recoverField_b;
   private static final ConcurrentHashMap<String, Member9088498a> recoverField_c = new ConcurrentHashMap<>(32);

   private RecoverTypeaf2b07cc() {
      recoverField_b = new Handler(Looper.getMainLooper());
   }

   public static RecoverTypeaf2b07cc recoverMethod_a_c6a0575a() {
      return RecoverTypeaf2b07cc.Member90884989.recoverField_a;
   }

   public void recoverMethod_a_63b8af67(String var1, Runnable var2, long var3) {
      if (var2 != null && var1 != null) {
         RecoverTypeaf2b07cc.Member9088498a var5;
         if (VERSION.SDK_INT >= 24) {
            var5 = (RecoverTypeaf2b07cc.Member9088498a)recoverField_c.compute(var1, (var2x, var3x) -> {
               if (var3x != null) {
                  recoverField_b.removeCallbacks((Runnable)var3x);
               }

               return new RecoverTypeaf2b07cc.Member9088498a(var2, var1);
            });
         } else {
            RecoverTypeaf2b07cc.Member9088498a var6 = (RecoverTypeaf2b07cc.Member9088498a)recoverField_c.get(var1);
            if (var6 != null) {
               recoverField_b.removeCallbacks(var6);
            }

            var5 = new RecoverTypeaf2b07cc.Member9088498a(var2, var1);
            recoverField_c.put(var1, var5);
         }

         recoverField_b.postDelayed(var5, var3);
      }
   }

   public void recoverMethod_a_a4fc3973(Runnable var1) {
      if (var1 != null) {
         String var2 = "immediate_" + recoverField_a.getAndIncrement();
         RecoverTypeaf2b07cc.Member9088498a var3 = new RecoverTypeaf2b07cc.Member9088498a(var1, var2);
         recoverField_c.put(var2, var3);
         recoverField_b.post(var3);
      }
   }

   private static class Member90884989 {
      private static final RecoverTypeaf2b07cc recoverField_a = new RecoverTypeaf2b07cc();
   }

   private static class Member9088498a implements Runnable {
      private final Runnable recoverField_a;
      private final String recoverField_b;
      private final AtomicBoolean recoverField_c = new AtomicBoolean(false);

      Member9088498a(Runnable var1, String var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }

      @Override
      public void run() {
         try {
            if (!this.recoverField_c.get()) {
               this.recoverField_a.run();
            }
         } finally {
            RecoverTypeaf2b07cc.recoverField_c.remove(this.recoverField_b, this);
         }
      }
   }
}
