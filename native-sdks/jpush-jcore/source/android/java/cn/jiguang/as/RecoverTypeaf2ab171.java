package cn.jiguang.as;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RecoverTypeaf2ab171 implements Serializable {
   private List recoverField_a = new ArrayList(1);
   private short recoverField_b = 0;
   private short recoverField_c = 0;

   public RecoverTypeaf2ab171() {
   }

   public RecoverTypeaf2ab171(RecoverTypeaf2ab172 var1) {
      this();
      this.recoverMethod_b_991ee83a(var1);
   }

   private void recoverMethod_b_991ee83a(RecoverTypeaf2ab172 var1) {
      if (this.recoverField_b == 0) {
         this.recoverField_a.add(var1);
      } else {
         this.recoverField_a.add(this.recoverField_a.size() - this.recoverField_b, var1);
      }
   }

   public synchronized void recoverMethod_a_991ee83a(RecoverTypeaf2ab172 var1) {
      if (this.recoverField_a.size() == 0) {
         this.recoverMethod_b_991ee83a(var1);
      } else {
         RecoverTypeaf2ab172 var2 = this.recoverMethod_f_c695e274();
         if (!var1.recoverMethod_a_991ee83e(var2)) {
            throw new IllegalArgumentException("record does not match rrset");
         }

         if (var1.recoverMethod_h_9b69() != var2.recoverMethod_h_9b69()) {
            if (var1.recoverMethod_h_9b69() > var2.recoverMethod_h_9b69()) {
               var1 = var1.recoverMethod_i_c695e274();
               var1.recoverMethod_a_1349ef(var2.recoverMethod_h_9b69());
            } else {
               for (int var3 = 0; var3 < this.recoverField_a.size(); var3++) {
                  RecoverTypeaf2ab172 var4 = (RecoverTypeaf2ab172)this.recoverField_a.get(var3);
                  var4 = var4.recoverMethod_i_c695e274();
                  var4.recoverMethod_a_1349ef(var1.recoverMethod_h_9b69());
                  this.recoverField_a.set(var3, var4);
               }
            }
         }

         if (!this.recoverField_a.contains(var1)) {
            this.recoverMethod_b_991ee83a(var1);
         }
      }
   }

   private synchronized Iterator recoverMethod_a_72f114a2(boolean var1, boolean var2) {
      int var5 = this.recoverField_a.size();
      int var3;
      if (var1) {
         var3 = var5 - this.recoverField_b;
      } else {
         var3 = this.recoverField_b;
      }

      if (var3 == 0) {
         return Collections.EMPTY_LIST.iterator();
      }

      int var4;
      if (var1) {
         if (!var2) {
            var4 = 0;
         } else {
            if (this.recoverField_c >= var3) {
               this.recoverField_c = 0;
            }

            var4 = this.recoverField_c++;
         }
      } else {
         var4 = var5 - this.recoverField_b;
      }

      ArrayList var6 = new ArrayList(var3);
      if (var1) {
         var6.addAll(this.recoverField_a.subList(var4, var3));
         if (var4 != 0) {
            var6.addAll(this.recoverField_a.subList(0, var4));
         }
      } else {
         var6.addAll(this.recoverField_a.subList(var4, var5));
      }

      return var6.iterator();
   }

   public synchronized Iterator recoverMethod_a_b7246de2() {
      return this.recoverMethod_a_72f114a2(true, true);
   }

   public RecoverTypeaf2ab170 recoverMethod_b_c695e236() {
      return this.recoverMethod_f_c695e274().recoverMethod_d_c695e236();
   }

   public int recoverMethod_c_9b68() {
      return this.recoverMethod_f_c695e274().recoverMethod_f_9b68();
   }

   public int recoverMethod_d_9b68() {
      return this.recoverMethod_f_c695e274().recoverMethod_g_9b68();
   }

   public synchronized long recoverMethod_e_9b69() {
      return this.recoverMethod_f_c695e274().recoverMethod_h_9b69();
   }

   public synchronized RecoverTypeaf2ab172 recoverMethod_f_c695e274() {
      if (this.recoverField_a.size() == 0) {
         throw new IllegalStateException("rrset is empty");
      } else {
         return (RecoverTypeaf2ab172)this.recoverField_a.get(0);
      }
   }

   private String recoverMethod_a_65e29cca(Iterator var1) {
      StringBuffer var2 = new StringBuffer();

      while (var1.hasNext()) {
         RecoverTypeaf2ab172 var3 = (RecoverTypeaf2ab172)var1.next();
         var2.append("[");
         var2.append(var3.recoverMethod_c_73cf11cb());
         var2.append("]");
         if (var1.hasNext()) {
            var2.append(" ");
         }
      }

      return var2.toString();
   }

   @Override
   public String toString() {
      if (this.recoverField_a.size() == 0) {
         return "{empty}";
      }

      StringBuffer var1 = new StringBuffer();
      var1.append("{ ");
      var1.append(this.recoverMethod_b_c695e236() + " ");
      var1.append(this.recoverMethod_e_9b69() + " ");
      var1.append(this.recoverMethod_a_65e29cca(this.recoverMethod_a_72f114a2(true, false)));
      if (this.recoverField_b > 0) {
         var1.append(" sigs: ");
         var1.append(this.recoverMethod_a_65e29cca(this.recoverMethod_a_72f114a2(false, false)));
      }

      var1.append(" }");
      return var1.toString();
   }
}
