package cn.jiguang.ar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a972d;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.aw.RecoverTypeaf2ac06f;
import cn.jiguang.aw.RecoverTypeaf2ac070;
import cn.jiguang.ay.RecoverTypeaf2ac7f0;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RecoverTypeaf2aadb0 extends RecoverTypeaf2b0401 {
   private Context recoverField_b;
   public volatile boolean recoverField_a;
   private RecoverTypeaf2ac06e recoverField_c;
   private boolean recoverField_d = false;
   private final AtomicBoolean recoverField_e = new AtomicBoolean(false);
   private static final AtomicInteger recoverField_f = new AtomicInteger(0);
   private final int recoverField_g;

   public RecoverTypeaf2aadb0(Context var1) {
      this.recoverField_b = var1;
      this.recoverField_g = recoverField_f.incrementAndGet();
      if (RecoverTypeaf2aadab.recoverMethod_d_f0554df7(var1)) {
         this.recoverField_c = new RecoverTypeaf2ac06f(8128, 20);
      } else {
         this.recoverField_c = new RecoverTypeaf2ac070(8128, 20);
      }

      this.recoverField_h = "NetworkingClient";
   }

   public synchronized void recoverMethod_b_9b75() {
      try {
         this.recoverField_e.set(true);
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("TCP_CONN_TASK", this);
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("NetworkingClient", "execute networkingClient exception :" + var2);
      }
   }

   public synchronized void recoverMethod_c_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("NetworkingClient", "Action - stop");
      this.recoverField_a = true;
      this.recoverField_e.set(false);
      RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(this.recoverField_c);
      RecoverTypeaf2b0403.recoverMethod_a_aad3b1ff("TCP_CONN_TASK");
   }

   public boolean recoverMethod_d_9b79() {
      return this.recoverField_e.get();
   }

   @Override
   public void recoverMethod_a_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_g_e925fa09("NetworkingClient", "Begin to run in ConnectingThread - id:" + Thread.currentThread().getId());

      try {
         boolean var1 = this.recoverMethod_a_f0554df7(this.recoverField_b);
         this.recoverField_e.set(false);
         if (!var1) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("NetworkingClient", "prepare Push Channel failed , returned");
            return;
         }

         while (!this.recoverField_a) {
            RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("NetworkingClient", "Network listening...");

            ByteBuffer var2;
            try {
               var2 = this.recoverField_c.recoverMethod_a_d68a3c36();
            } catch (RecoverTypeaf2ab52f var8) {
               RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("NetworkingClient", " recv failed with error:" + var8 + " ,No Break!!");
               RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b, 1200, " recv failed with error:" + var8 + " ,No Break!!");
               break;
            }

            this.recoverMethod_a_9b122053(this.recoverField_c, var2);
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09(
               "NetworkingClient", "Received bytes - len:" + var2.array().length + ", pkg:" + RecoverTypeaf2b07cf.recoverMethod_a_e501eb0d(this.recoverField_b)
            );
         }
      } catch (Throwable var9) {
         this.recoverField_e.set(false);
         RecoverTypeaf2a9ae9.recoverMethod_f_1765c98c("NetworkingClient", "run exception", var9);
      } finally {
         this.recoverField_e.set(false);
      }

      if (this.recoverField_a) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("NetworkingClient", "Break receiving by wantStop");
      }

      this.recoverMethod_a_13462e(1);
   }

   public RecoverTypeaf2ac06e recoverMethod_e_c697b2f8() {
      return this.recoverField_c;
   }

   private boolean recoverMethod_a_f0554df7(Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetworkingClient", "google:true");
      RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var1);
      RecoverTypeaf2aadb3 var2 = new RecoverTypeaf2aadb3(var1);
      RecoverTypeaf2aadb2 var3 = new RecoverTypeaf2aadb2(var2);

      try {
         this.recoverField_c = var3.recoverMethod_a_762e44bf(this);
      } catch (Exception var6) {
         if (var6 instanceof RecoverTypeaf2ab52f) {
            String var5 = "connect failed, errCode: " + ((RecoverTypeaf2ab52f)var6).recoverMethod_a_9b68();
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("NetworkingClient", var5);
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b, 1400, "内部异常：" + var5);
         } else {
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b, 1401, "未知异常：" + var6);
         }

         this.recoverField_d = true;
         this.recoverMethod_a_13462e(1);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetworkingClient", "sis and connect failed:" + var6);
         return false;
      }

      if (!this.recoverMethod_b_134632(2)) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetworkingClient", "login failed");
         return false;
      } else {
         return true;
      }
   }

   private boolean recoverMethod_b_134632(int var1) {
      if (this.recoverField_a) {
         return false;
      }

      if (var1 <= 0) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("NetworkingClient", "login error,retry login too many times");
         this.recoverMethod_h_9b75();
         this.recoverMethod_a_13462e(4);
         return false;
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetworkingClient", "loginTimes:" + var1);
      if (!this.recoverMethod_g_9b79()) {
         return false;
      }

      String var2 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec()
         .recoverMethod_c_782597e1(this.recoverField_c.recoverField_h, this.recoverField_c.recoverField_i, this.recoverField_c.recoverField_g);
      int var3 = RecoverTypeaf2aadab.recoverMethod_a_112180f3(this.recoverField_b, this.recoverField_c);
      RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_f_afa300e4(var2, var3);
      if (var3 < 0) {
         this.recoverMethod_a_13462e(2);
         return false;
      }

      if (var3 > 0) {
         this.recoverMethod_h_9b75();
         if (var3 == 108) {
            RecoverTypeaf2aa628.recoverMethod_a_f0554df3(this.recoverField_b);
            return this.recoverMethod_b_134632(--var1);
         } else {
            this.recoverMethod_c_13462e(var3);
            return false;
         }
      } else {
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_b, "tcp_a10", null);
         return true;
      }
   }

   private boolean recoverMethod_g_9b79() {
      RecoverTypeaf2a972d.recoverMethod_c_f0554df3(this.recoverField_b);
      if (!RecoverType5a68d11.recoverMethod_a_f0554df7(this.recoverField_b)
         || TextUtils.isEmpty(RecoverType5a68d10.recoverMethod_j_e501eb0d(this.recoverField_b))) {
         String var1 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec()
            .recoverMethod_b_782597e1(this.recoverField_c.recoverField_h, this.recoverField_c.recoverField_i, this.recoverField_c.recoverField_g);
         int var2 = RecoverTypeaf2aadab.recoverMethod_b_112180f3(this.recoverField_b, this.recoverField_c);
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_e_afa300e4(var1, var2);
         if (var2 != 0) {
            Bundle var3 = new Bundle();
            var3.putInt("resCode", var2);
            RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_b, "tcp_a13", var3);
            this.recoverMethod_h_9b75();
            this.recoverMethod_a_13462e(3);
            return false;
         }

         RecoverTypeaf2a972d.recoverMethod_b_f0554df3(this.recoverField_b);
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_b, "tcp_a11", null);
      }

      return true;
   }

   private void recoverMethod_a_9b122053(RecoverTypeaf2ac06e var1, ByteBuffer var2) {
      RecoverTypeaf2aa628.recoverMethod_c_a4fc3973(new RecoverTypeaf2aadb9(var1, this.recoverField_b, var2.array()));
   }

   private void recoverMethod_c_13462e(int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NetworkingClient", "Action - onLoginFailed - respCode:" + var1);
      Bundle var2 = new Bundle();
      var2.putInt("resCode", var1);
      RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_b, "tcp_a12", var2);
   }

   public void recoverMethod_a_13462e(int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("NetworkingClient", "Action - closeConnection");
      RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(this.recoverField_c);
      Bundle var2 = new Bundle();
      var2.putBoolean("connect_state", this.recoverField_d);
      var2.putInt("connect_instance_id", this.recoverField_g);
      RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_b, "tcp_a19", var2);
      RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec()
         .recoverMethod_a_ce0c4cdc(this.recoverField_c.recoverField_h, this.recoverField_c.recoverField_i, this.recoverField_c.recoverField_g, var1);
   }

   private void recoverMethod_h_9b75() {
      RecoverTypeaf2aadab.recoverMethod_b_f0554df3(this.recoverField_b);
   }

   public int recoverMethod_f_9b68() {
      return this.recoverField_g;
   }
}
