package cn.jiguang.as;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class RecoverTypeaf2ab16e implements Cloneable {
   private RecoverTypeaf2ab16d recoverField_a;
   private List[] recoverField_b = new List[4];
   private int recoverField_c;
   private static RecoverTypeaf2ab172[] recoverField_d = new RecoverTypeaf2ab172[0];
   private static RecoverTypeaf2ab171[] recoverField_e = new RecoverTypeaf2ab171[0];

   private RecoverTypeaf2ab16e(RecoverTypeaf2ab16d var1) {
      this.recoverField_a = var1;
   }

   public RecoverTypeaf2ab16e() {
      this(new RecoverTypeaf2ab16d());
   }

   public static RecoverTypeaf2ab16e recoverMethod_a_e45404bd(RecoverTypeaf2ab172 var0) {
      RecoverTypeaf2ab16e var1 = new RecoverTypeaf2ab16e();
      var1.recoverMethod_a_8abe9209(var0, 0);
      return var1;
   }

   RecoverTypeaf2ab16e(RecoverTypeaf2ab16b var1) throws java.io.IOException {
      this(new RecoverTypeaf2ab16d(var1));

      try {
         for (int var2 = 0; var2 < 4; var2++) {
            int var3 = this.recoverField_a.recoverMethod_c_134621(var2);
            if (var3 > 0) {
               this.recoverField_b[var2] = new ArrayList(var3);
            }

            for (int var4 = 0; var4 < var3; var4++) {
               RecoverTypeaf2ab172 var5 = RecoverTypeaf2ab172.recoverMethod_a_bcad7a81(var1, var2);
               this.recoverField_b[var2].add(var5);
            }
         }
      } catch (IOException var6) {
         throw var6;
      }

      this.recoverField_c = var1.recoverMethod_a_9b68();
   }

   public RecoverTypeaf2ab16e(byte[] var1) throws java.io.IOException {
      this(new RecoverTypeaf2ab16b(var1));
   }

   public void recoverMethod_a_8abe9209(RecoverTypeaf2ab172 var1, int var2) {
      if (this.recoverField_b[var2] == null) {
         this.recoverField_b[var2] = new LinkedList();
      }

      this.recoverField_a.recoverMethod_b_13462e(var2);
      this.recoverField_b[var2].add(var1);
   }

   public RecoverTypeaf2ab172 recoverMethod_a_c695e274() {
      List var1 = this.recoverField_b[0];
      return var1 != null && var1.size() != 0 ? (RecoverTypeaf2ab172)var1.get(0) : null;
   }

   public RecoverTypeaf2ab172[] recoverMethod_a_fdcaeb82(int var1) {
      if (this.recoverField_b[var1] == null) {
         return recoverField_d;
      }

      List var2 = this.recoverField_b[var1];
      return (RecoverTypeaf2ab172[])var2.toArray(new RecoverTypeaf2ab172[var2.size()]);
   }

   private static boolean recoverMethod_a_33251e19(RecoverTypeaf2ab172 var0, RecoverTypeaf2ab172 var1) {
      return var0.recoverMethod_f_9b68() == var1.recoverMethod_f_9b68()
         && var0.recoverMethod_g_9b68() == var1.recoverMethod_g_9b68()
         && var0.recoverMethod_d_c695e236().equals(var1.recoverMethod_d_c695e236());
   }

   public RecoverTypeaf2ab171[] recoverMethod_b_fdcaeb63(int var1) {
      if (this.recoverField_b[var1] == null) {
         return recoverField_e;
      }

      LinkedList var2 = new LinkedList();
      RecoverTypeaf2ab172[] var3 = this.recoverMethod_a_fdcaeb82(var1);
      HashSet var4 = new HashSet();

      for (int var5 = 0; var5 < var3.length; var5++) {
         RecoverTypeaf2ab170 var6 = var3[var5].recoverMethod_d_c695e236();
         boolean var7 = true;
         if (var4.contains(var6)) {
            for (int var8 = var2.size() - 1; var8 >= 0; var8--) {
               RecoverTypeaf2ab171 var9 = (RecoverTypeaf2ab171)var2.get(var8);
               if (var9.recoverMethod_c_9b68() == var3[var5].recoverMethod_f_9b68()
                  && var9.recoverMethod_d_9b68() == var3[var5].recoverMethod_g_9b68()
                  && var9.recoverMethod_b_c695e236().equals(var6)) {
                  var9.recoverMethod_a_991ee83a(var3[var5]);
                  var7 = false;
                  break;
               }
            }
         }

         if (var7) {
            RecoverTypeaf2ab171 var10 = new RecoverTypeaf2ab171(var3[var5]);
            var2.add(var10);
            var4.add(var6);
         }
      }

      return (RecoverTypeaf2ab171[])var2.toArray(new RecoverTypeaf2ab171[var2.size()]);
   }

   private int recoverMethod_a_a3113802(RecoverTypeaf2ab16c var1, int var2, RecoverTypeaf2ab16a var3, int var4) {
      int var5 = this.recoverField_b[var2].size();
      int var6 = var1.recoverMethod_a_9b68();
      int var7 = 0;
      int var8 = 0;
      RecoverTypeaf2ab172 var9 = null;

      for (int var10 = 0; var10 < var5; var10++) {
         RecoverTypeaf2ab172 var11 = (RecoverTypeaf2ab172)this.recoverField_b[var2].get(var10);
         if (var2 == 3) {
            var8++;
         } else {
            if (var9 != null && !recoverMethod_a_33251e19(var11, var9)) {
               var6 = var1.recoverMethod_a_9b68();
               var7 = var10;
            }

            var9 = var11;
            var11.recoverMethod_a_ec7c6974(var1, var2, var3);
            if (var1.recoverMethod_a_9b68() > var4) {
               var1.recoverMethod_a_13462e(var6);
               return var5 - var7 + var8;
            }
         }
      }

      return var8;
   }

   private boolean recoverMethod_a_8a6a0507(RecoverTypeaf2ab16c var1, int var2) {
      if (var2 < 12) {
         return false;
      }

      Object var3 = null;
      int var4 = var2;
      int var5 = var1.recoverMethod_a_9b68();
      this.recoverField_a.recoverMethod_a_991c2e00(var1);
      RecoverTypeaf2ab16a var6 = new RecoverTypeaf2ab16a();
      int var7 = this.recoverField_a.recoverMethod_c_9b68();

      for (int var8 = 0; var8 < 4; var8++) {
         if (this.recoverField_b[var8] != null) {
            int var9 = this.recoverMethod_a_a3113802(var1, var8, var6, var4);
         }
      }

      return true;
   }

   public byte[] recoverMethod_c_255806f(int var1) {
      RecoverTypeaf2ab16c var2 = new RecoverTypeaf2ab16c();
      this.recoverMethod_a_8a6a0507(var2, var1);
      this.recoverField_c = var2.recoverMethod_a_9b68();
      return var2.recoverMethod_b_12d408();
   }

   @Override
   public Object clone() {
      RecoverTypeaf2ab16e var1 = new RecoverTypeaf2ab16e();

      for (int var2 = 0; var2 < this.recoverField_b.length; var2++) {
         if (this.recoverField_b[var2] != null) {
            var1.recoverField_b[var2] = new LinkedList(this.recoverField_b[var2]);
         }
      }

      var1.recoverField_a = (RecoverTypeaf2ab16d)this.recoverField_a.clone();
      var1.recoverField_c = this.recoverField_c;
      return var1;
   }
}
