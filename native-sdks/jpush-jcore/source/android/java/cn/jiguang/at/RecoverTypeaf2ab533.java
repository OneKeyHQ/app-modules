package cn.jiguang.at;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.av.RecoverTypeaf2abcae;
import cn.jiguang.ax.RecoverTypeaf2ac42f;
import cn.jiguang.ax.RecoverTypeaf2ac430;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RecoverTypeaf2ab533 {
   private Map recoverField_a;
   private RecoverTypeaf2ac42f recoverField_b = new RecoverTypeaf2ac42f() {
      @Override
      public void recoverMethod_a_c050048(Message var1) {
         long var2 = var1.what - 100000;
         Bundle var4 = new Bundle();
         var4.putLong("rid", var2);
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(JCoreManager.getAppContext(null), "tcp_a6", var4);
      }
   };
   private static long recoverField_c = 1L;

   private RecoverTypeaf2ab533() {
      this.recoverField_a = new HashMap();
   }

   public static RecoverTypeaf2ab533 recoverMethod_a_c69656d3() {
      return RecoverTypeaf2ab533.Member8f523930.recoverField_a;
   }

   public static long recoverMethod_b_9b69() {
      recoverField_c++;
      if (recoverField_c >= 2147483647L) {
         recoverField_c = 1L;
      }

      return recoverField_c;
   }

   public void recoverMethod_a_57af07a2(Context var1, long var2, int var4, int var5, byte[] var6, String var7) {
      long var8 = RecoverTypeaf2aadab.recoverMethod_c_f0554de7(var1);
      if (this.recoverField_a.containsKey(var8)) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("TcpRequestManager", "Generator same rid,not do this msg");
      } else {
         RecoverTypeaf2ab52e var10 = new RecoverTypeaf2ab52e(var2, var7, var4, var5, var8, 0L, var6);
         if (RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_d_9b79()) {
            RecoverTypeaf2ab531.recoverMethod_a_c6965695()
               .recoverMethod_c_c6956df6()
               .recoverMethod_e_c697b2f8()
               .recoverMethod_a_25e06ef(this.recoverMethod_a_7486349d(var1, var10));
         } else if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("TcpRequestManager", "send error -tcp connect was invalid");
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("TcpRequestManager", "current use not login success,wait a moment");
         }

         this.recoverField_a.put(var8, var10);
      }
   }

   public void recoverMethod_a_9e326362(Context var1, long var2, int var4, int var5, byte[] var6, String var7, long var8) {
      long var10 = 0L;
      if (var4 == 10) {
         var10 = var2;
      } else {
         var10 = RecoverTypeaf2aadab.recoverMethod_c_f0554de7(var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpRequestManager", "Generator new rid:" + var10);
         if (this.recoverField_a.containsKey(var10)) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("TcpRequestManager", "Generator same rid,not do this msg");
            return;
         }
      }

      if (var8 <= 0L) {
         var8 = 10000L;
      }

      RecoverTypeaf2ab52e var12 = new RecoverTypeaf2ab52e(var2, var7, var4, var5, var10, var8, var6);
      if (RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_d_9b79()) {
         RecoverTypeaf2ab531.recoverMethod_a_c6965695()
            .recoverMethod_c_c6956df6()
            .recoverMethod_e_c697b2f8()
            .recoverMethod_a_25e06ef(this.recoverMethod_a_7486349d(var1, var12));
      } else if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("TcpRequestManager", "send error -tcp connect was invalid");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("TcpRequestManager", "current use not login success,wait a moment");
      }

      var12.recoverField_h = System.nanoTime();
      this.recoverField_a.put(var10, var12);
      RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c((int)(100000L + var10), var8, this.recoverField_b);
   }

   public void recoverMethod_a_1a54e731(Context var1, long var2) {
      RecoverTypeaf2ab52e var4 = (RecoverTypeaf2ab52e)this.recoverField_a.remove(var2);
      if (var4 != null) {
         if (var4.recoverField_j) {
            RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e((int)(var2 + 100000L));
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpRequestManager", "handle reponse :" + var4);
      }
   }

   public RecoverTypeaf2ab52e recoverMethod_a_4880d0b2(long var1) {
      return (RecoverTypeaf2ab52e)this.recoverField_a.get(var1);
   }

   public void recoverMethod_b_1a54e731(Context var1, long var2) {
      RecoverTypeaf2ab52e var4 = (RecoverTypeaf2ab52e)this.recoverField_a.remove(var2);
      if (var4 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpRequestManager", "not found requst by rid:" + var2);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpRequestManager", "request time out:" + var4);
         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_510e8a7c(var1, var4.recoverField_c, var4.recoverField_b, var4.recoverField_d);
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (this.recoverField_a.isEmpty()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpRequestManager", "no cache request");
      } else {
         for (Object recoveryElement_131 : this.recoverField_a.entrySet()) {
            Entry var3 = (Entry)recoveryElement_131;
            if (((RecoverTypeaf2ab52e)var3.getValue()).recoverField_j) {
               long var4 = System.nanoTime();
               long var6 = var4 - ((RecoverTypeaf2ab52e)var3.getValue()).recoverField_h;
               if (((RecoverTypeaf2ab52e)var3.getValue()).recoverField_i - var6 >= 10000L) {
                  ((RecoverTypeaf2ab52e)var3.getValue()).recoverMethod_a_9b75();
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpRequestManager", "send again:" + var3.getValue());
                  RecoverTypeaf2ab531.recoverMethod_a_c6965695()
                     .recoverMethod_c_c6956df6()
                     .recoverMethod_e_c697b2f8()
                     .recoverMethod_a_25e06ef(this.recoverMethod_a_7486349d(var1, (RecoverTypeaf2ab52e)var3.getValue()));
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                     "TcpRequestManager",
                     "shoud not send again by 10000ms,hasRequestTime:" + var6 + ",timeout:" + ((RecoverTypeaf2ab52e)var3.getValue()).recoverField_i
                  );
               }
            }
         }
      }
   }

   private byte[] recoverMethod_a_7486349d(Context var1, RecoverTypeaf2ab52e var2) {
      return RecoverTypeaf2abcae.recoverMethod_a_7700bcb1(var1, var2.recoverField_d, var2.recoverField_e, var2.recoverField_f, var2.recoverField_g, 0L);
   }

   private static final class Member8f523930 {
      private static final RecoverTypeaf2ab533 recoverField_a = new RecoverTypeaf2ab533();
   }
}
