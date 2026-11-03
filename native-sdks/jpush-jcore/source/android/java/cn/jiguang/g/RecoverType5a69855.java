package cn.jiguang.g;

import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecoverType5a69855 {
   private static String recoverField_c = "JDataUserControl";
   public static String recoverField_a = "DISABLE_DATA";
   public static String recoverField_b = "ENABLE_DATA";
   private HashMap recoverField_d = new HashMap();

   private RecoverType5a69855() {
      ArrayList var1 = new ArrayList();
      var1.add(2000);
      var1.add(3000);
      var1.add(1000);
      this.recoverField_d.put(1502, var1);
      ArrayList var2 = new ArrayList();
      var2.add(1014);
      this.recoverField_d.put(2400, var2);
      ArrayList var3 = new ArrayList();
      var3.add(2001);
      var3.add(3001);
      var3.add(1001);
      this.recoverField_d.put(1100, var3);
      this.recoverField_d.put(1101, var3);
      this.recoverField_d.put(1104, var3);
      this.recoverField_d.put(1112, var3);
      this.recoverField_d.put(1110, var3);
      this.recoverField_d.put(1102, var3);
      this.recoverField_d.put(1013, 3003);
      this.recoverField_d.put(2009, 3003);
      this.recoverField_d.put(1014, 3004);
      ArrayList var4 = new ArrayList();
      var4.add(3005);
      var4.add(2006);
      this.recoverField_d.put(1017, var4);
      this.recoverField_d.put(2012, var4);
      ArrayList var5 = new ArrayList();
      var5.add(1009);
      var5.add(2005);
      var5.add(3006);
      this.recoverField_d.put(1009, var5);
      this.recoverField_d.put(2006, var5);
      this.recoverField_d.put(1015, 3007);
      this.recoverField_d.put(1011, 3008);
      this.recoverField_d.put(2008, 3008);
      ArrayList var6 = new ArrayList();
      var6.add(2009);
      var6.add(3009);
      var6.add(1005);
      this.recoverField_d.put(2007, var6);
      ArrayList var7 = new ArrayList();
      var7.add(2003);
      var7.add(3010);
      var7.add(1012);
      this.recoverField_d.put(1505, var7);
      ArrayList var8 = new ArrayList();
      var8.add(1006);
      var8.add(2010);
      var8.add(3011);
      this.recoverField_d.put(1608, var8);
      ArrayList var9 = new ArrayList();
      var9.add(2004);
      var9.add(3012);
      var9.add(1007);
      this.recoverField_d.put(1601, var9);
      ArrayList var10 = new ArrayList();
      var10.add(1008);
      var10.add(2011);
      var10.add(3013);
      this.recoverField_d.put(1501, var10);
      ArrayList var11 = new ArrayList();
      var11.add(2007);
      var11.add(3014);
      var11.add(1003);
      this.recoverField_d.put(2004, var11);
      ArrayList var12 = new ArrayList();
      var12.add(2008);
      var12.add(3015);
      var12.add(1004);
      this.recoverField_d.put(1012, var12);
      ArrayList var13 = new ArrayList();
      var13.add(3016);
      var13.add(1010);
      var13.add(2012);
      this.recoverField_d.put(2005, var13);
      ArrayList var14 = new ArrayList();
      var14.add(3017);
      var14.add(1011);
      var14.add(2013);
      this.recoverField_d.put(2201, var14);
      RecoverType5a69854.recoverMethod_a_9b75();
   }

   public static RecoverType5a69855 recoverMethod_a_487a33bb() {
      return RecoverType5a69855.Member3661dbd2.recoverField_a;
   }

   public synchronized void recoverMethod_a_79e5e33f(Bundle var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "updateCollectConfig bundle=" + var1);

      try {
         if (var1 == null || var1.size() == 0) {
            return;
         }

         RecoverType5a69854.recoverMethod_a_b19c9f57((ArrayList)var1.getSerializable(recoverField_a), (ArrayList)var1.getSerializable(recoverField_b));
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "updateCollectConfig mDisableDatas=" + RecoverType5a69854.recoverField_e);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "updateCollectConfig mEnableDatas=" + RecoverType5a69854.recoverField_d);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_c, "updateCollectConfig throwable=" + var3);
      }
   }

   public synchronized boolean recoverMethod_a_134632(int var1) {
      try {
         if (!this.recoverField_d.containsKey(var1)) {
            return false;
         } else {
            Object var2 = this.recoverField_d.get(var1);
            if (!RecoverType5a69854.recoverMethod_a_5d527815(var2)) {
               return false;
            } else if (var2 instanceof List) {
               List var3 = (List)var2;
               ArrayList var4 = new ArrayList(RecoverType5a69854.recoverField_d);
               var4.retainAll(var3);
               return !var4.isEmpty();
            } else {
               return RecoverType5a69854.recoverField_d.contains(var2);
            }
         }
      } catch (Throwable var5) {
         return false;
      }
   }

   public synchronized boolean recoverMethod_b_134632(int var1) {
      try {
         if (!this.recoverField_d.containsKey(var1)) {
            return false;
         } else {
            Object var2 = this.recoverField_d.get(var1);
            if (!RecoverType5a69854.recoverMethod_a_5d527815(var2)) {
               return false;
            } else if (var2 instanceof List) {
               List var3 = (List)var2;
               ArrayList var4 = new ArrayList(RecoverType5a69854.recoverField_e);
               var4.retainAll(var3);
               return !var4.isEmpty();
            } else {
               return RecoverType5a69854.recoverField_e.contains(var2);
            }
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "isContainDisableDatas throwable=" + var5);
         return false;
      }
   }

   private static final class Member3661dbd2 {
      private static final RecoverType5a69855 recoverField_a = new RecoverType5a69855();
   }
}
