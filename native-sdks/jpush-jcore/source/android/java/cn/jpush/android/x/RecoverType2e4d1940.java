package cn.jpush.android.x;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecoverType2e4d1940 {
   private static Queue recoverField_a = new ConcurrentLinkedQueue();

   public static int recoverMethod_a_9b68() {
      return recoverField_a.size() > 0 ? (Integer)recoverField_a.poll() : 0;
   }

   public static boolean recoverMethod_a_134632(int var0) {
      return recoverField_a.offer(var0);
   }

   public static boolean recoverMethod_b_134632(int var0) {
      return recoverField_a.contains(var0);
   }

   public static int recoverMethod_b_9b68() {
      return recoverField_a.size();
   }
}
