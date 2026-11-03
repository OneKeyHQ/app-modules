package cn.jiguang.ar;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class RecoverTypeaf2aadae {
   private LinkedHashSet recoverField_a = new LinkedHashSet();
   private LinkedHashSet recoverField_b = new LinkedHashSet();
   private List recoverField_c = new ArrayList();

   public boolean recoverMethod_a_976926e1(RecoverTypeaf2aadaf var1) {
      if (var1 != null && var1.recoverMethod_a_9b79()) {
         if (var1.recoverField_c instanceof Inet4Address) {
            return this.recoverField_a.add(var1);
         }

         if (var1.recoverField_c instanceof Inet6Address) {
            return this.recoverField_b.add(var1);
         }
      }

      return false;
   }

   public RecoverTypeaf2aadaf recoverMethod_a_7ad58690(int var1) {
      RecoverTypeaf2aadaf var2 = null;
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IpPool", "current ipv4List=" + this.recoverField_a + " ipv6List=" + this.recoverField_b);
      switch (var1) {
         case 0:
            var2 = this.recoverMethod_a_58a83f17(false, false);
            break;
         case 1:
            var2 = this.recoverMethod_a_58a83f17(false, true);
            break;
         case 2:
            var2 = this.recoverMethod_a_58a83f17(true, true);
            break;
         case 3:
            var2 = this.recoverMethod_a_58a83f17(true, false);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IpPool", "get ipPort=" + var2);
      if (var2 != null) {
         if (var2.recoverField_c instanceof Inet4Address) {
            this.recoverField_c.add(false);
         } else if (var2.recoverField_c instanceof Inet6Address) {
            this.recoverField_c.add(true);
         }
      }

      return var2;
   }

   private boolean recoverMethod_a_138603(boolean var1) {
      int var2 = this.recoverField_c.size();
      if (var2 < 2) {
         return false;
      }

      for (int var3 = this.recoverField_c.size() - 1; var3 >= this.recoverField_c.size() - 2; var3--) {
         if ((Boolean)this.recoverField_c.get(var3) != var1) {
            return false;
         }
      }

      return true;
   }

   private RecoverTypeaf2aadaf recoverMethod_a_58a83f17(boolean var1, boolean var2) {
      LinkedHashSet var3 = var1 ? this.recoverField_b : this.recoverField_a;
      LinkedHashSet var4 = var2 ? null : (var1 ? this.recoverField_a : this.recoverField_b);
      if (var4 == null || var4.isEmpty()) {
         return (RecoverTypeaf2aadaf)recoverMethod_a_2ae35c8c(var3);
      } else if (var3 == null || var3.isEmpty()) {
         return (RecoverTypeaf2aadaf)recoverMethod_a_2ae35c8c(var4);
      } else {
         return this.recoverMethod_a_138603(var1) ? (RecoverTypeaf2aadaf)recoverMethod_a_2ae35c8c(var4) : (RecoverTypeaf2aadaf)recoverMethod_a_2ae35c8c(var3);
      }
   }

   private static Object recoverMethod_a_2ae35c8c(Collection var0) {
      if (var0 != null && !var0.isEmpty()) {
         Iterator var1 = var0.iterator();
         if (var1.hasNext()) {
            Object var2 = var1.next();
            var1.remove();
            return var2;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void recoverMethod_a_9b75() {
      try {
         this.recoverField_a.clear();
         this.recoverField_b.clear();
      } catch (Throwable var2) {
      }
   }
}
