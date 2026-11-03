package cn.jiguang.ba;

import cn.jiguang.bk.RecoverTypeaf2b07c6;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class RecoverTypeaf2ae23f {
   private final Date recoverField_a;
   private Date recoverField_b;
   private final AtomicInteger recoverField_c;
   private final UUID recoverField_d;
   private Boolean recoverField_e;
   private RecoverTypeaf2ae23f.Member8ffb533c recoverField_f;
   private Long recoverField_g;
   private Double recoverField_h;
   private final String recoverField_i;
   private String recoverField_j;
   private final String recoverField_k;
   private final Object recoverField_l = new Object();

   public RecoverTypeaf2ae23f(
      RecoverTypeaf2ae23f.Member8ffb533c var1,
      Date var2,
      Date var3,
      int var4,
      UUID var5,
      Boolean var6,
      Long var7,
      Double var8,
      String var9,
      String var10,
      String var11
   ) {
      this.recoverField_f = var1;
      this.recoverField_a = var2;
      this.recoverField_b = var3;
      this.recoverField_c = new AtomicInteger(var4);
      this.recoverField_d = var5;
      this.recoverField_e = var6;
      this.recoverField_g = var7;
      this.recoverField_h = var8;
      this.recoverField_i = var9;
      this.recoverField_j = var10;
      this.recoverField_k = var11;
   }

   public RecoverTypeaf2ae23f() {
      this(
         RecoverTypeaf2ae23f.Member8ffb533c.recoverField_a,
         RecoverTypeaf2b07c6.recoverMethod_c_a911e122(),
         RecoverTypeaf2b07c6.recoverMethod_c_a911e122(),
         0,
         UUID.randomUUID(),
         true,
         null,
         null,
         null,
         null,
         "production"
      );
   }

   public Date recoverMethod_a_a911e122() {
      return this.recoverField_a == null ? null : (Date)this.recoverField_a.clone();
   }

   public UUID recoverMethod_b_a9fb56d5() {
      return this.recoverField_d;
   }

   public Boolean recoverMethod_c_12a799b2() {
      return this.recoverField_e;
   }

   public int recoverMethod_d_9b68() {
      return this.recoverField_c.get();
   }

   public RecoverTypeaf2ae23f.Member8ffb533c recoverMethod_e_8ed5cb2a() {
      return this.recoverField_f;
   }

   public Long recoverMethod_f_10c66500() {
      return this.recoverField_g;
   }

   public Double recoverMethod_g_51f06e0b() {
      return this.recoverField_h;
   }

   public Date recoverMethod_h_a911e122() {
      Date var1 = this.recoverField_b;
      return var1 != null ? (Date)var1.clone() : null;
   }

   public void recoverMethod_i_9b75() {
      this.recoverMethod_a_ad2a4556(RecoverTypeaf2b07c6.recoverMethod_c_a911e122());
   }

   public void recoverMethod_a_ad2a4556(Date var1) {
      synchronized (this.recoverField_l) {
         this.recoverField_e = null;
         if (this.recoverField_f == RecoverTypeaf2ae23f.Member8ffb533c.recoverField_a) {
            this.recoverField_f = RecoverTypeaf2ae23f.Member8ffb533c.recoverField_b;
         }

         if (var1 != null) {
            this.recoverField_b = var1;
         } else {
            this.recoverField_b = RecoverTypeaf2b07c6.recoverMethod_c_a911e122();
         }

         if (this.recoverField_b != null) {
            this.recoverField_h = this.recoverMethod_b_ad2a4544(this.recoverField_b);
            this.recoverField_g = this.recoverMethod_c_ad2a454a(this.recoverField_b);
         }
      }
   }

   private double recoverMethod_b_ad2a4544(Date var1) {
      long var2 = Math.abs(var1.getTime() - this.recoverField_a.getTime());
      return var2 / 1000.0;
   }

   public boolean recoverMethod_a_8b85531e(RecoverTypeaf2ae23f.Member8ffb533c var1, String var2, boolean var3) {
      synchronized (this.recoverField_l) {
         boolean var5 = false;
         if (var1 != null) {
            this.recoverField_f = var1;
            var5 = true;
         }

         if (var2 != null) {
            this.recoverField_j = var2;
            var5 = true;
         }

         if (var3) {
            this.recoverField_c.addAndGet(1);
            var5 = true;
         }

         if (var5) {
            this.recoverField_e = null;
            this.recoverField_b = RecoverTypeaf2b07c6.recoverMethod_c_a911e122();
            if (this.recoverField_b != null) {
               this.recoverField_g = this.recoverMethod_c_ad2a454a(this.recoverField_b);
            }
         }

         return var5;
      }
   }

   private long recoverMethod_c_ad2a454a(Date var1) {
      long var2 = var1.getTime();
      if (var2 < 0L) {
         var2 = Math.abs(var2);
      }

      return var2;
   }

   public RecoverTypeaf2ae23f recoverMethod_j_c69bcb47() {
      return new RecoverTypeaf2ae23f(
         this.recoverField_f,
         this.recoverField_a,
         this.recoverField_b,
         this.recoverField_c.get(),
         this.recoverField_d,
         this.recoverField_e,
         this.recoverField_g,
         this.recoverField_h,
         this.recoverField_i,
         this.recoverField_j,
         this.recoverField_k
      );
   }

   // $VF: synthetic method
   @Override
   public Object clone() {
      return this.recoverMethod_j_c69bcb47();
   }

   public enum Member8ffb533c {
      recoverField_a,
      recoverField_b,
      recoverField_c;

      // $VF: synthetic method
      private static RecoverTypeaf2ae23f.Member8ffb533c[] recoverMethod_a_ddefc811() {
         return new RecoverTypeaf2ae23f.Member8ffb533c[]{recoverField_a, recoverField_b, recoverField_c};
      }
   }
}
