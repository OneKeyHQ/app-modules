package cn.jiguang.av;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bk.RecoverTypeaf2b07d3;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.d.RecoverType5a68d10;
import java.nio.ByteBuffer;

public class RecoverTypeaf2abcad {
   public static boolean recoverMethod_a_8395a015(RecoverTypeaf2ac06e var0, Context var1, byte[] var2) {
      try {
         Pair var3 = recoverMethod_a_d94c1a8e(var1, var2, "");
         if (var3 != null) {
            RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_3b99bddb(var0.recoverField_h + ":" + var0.recoverField_i, "conn", var2.length);
            long var4 = RecoverType5a68d10.recoverMethod_f_f0554de7(var1);
            long var6 = ((RecoverTypeaf2abcaf)var3.first).recoverField_g;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommands", "first:" + var3.first);
            RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_15da8d92(var1, (RecoverTypeaf2abcaf)var3.first, (ByteBuffer)var3.second);
            return true;
         }

         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var1, 1201, " recv failed parse Inner Response");
         RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_3b99bddb(var0.recoverField_h + ":" + var0.recoverField_i, "unknown_conn", var2.length);
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCommands", "dispatchMessage error:" + var8.getMessage());
      }

      return false;
   }

   public static Pair recoverMethod_a_d94c1a8e(Context var0, byte[] var1, String var2) {
      byte var3 = 20;
      if (var1.length < 20) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCommands", "Error: received body-length short than common head-length, return null");
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1300, "Error: received body-length short than common head-length, return null");
         return null;
      }

      byte[] var4 = new byte[20];
      System.arraycopy(var1, 0, var4, 0, 20);
      RecoverTypeaf2abcaf var5 = new RecoverTypeaf2abcaf(false, var4);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommands", "parsed head - " + var5.toString());
      int var7 = var5.recoverField_a - 20;
      if (var7 < 0) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCommands", "Error: totalBytes length error with no encrypted, return null");
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1301, "Error: totalBytes length error with no encrypted, return null");
         return null;
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JCommands", "body size:" + var7);
      if (TextUtils.isEmpty(var2)) {
         var2 = RecoverTypeaf2abcae.recoverMethod_a_e501eb0d(var0);
      }

      byte[] var8 = new byte[var7];
      System.arraycopy(var1, 20, var8, 0, var7);
      ByteBuffer var6;
      if (!TextUtils.isEmpty(var2)) {
         try {
            byte[] var9;
            if (var5.recoverField_d == 2) {
               var9 = new RecoverTypeaf2b07d3().recoverMethod_b_19174fd7(var8, var2);
            } else {
               var9 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var8, var2, var2.substring(0, 16), false);
            }

            var6 = ByteBuffer.wrap(var9);
         } catch (Exception var10) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommands", "decryptBytes error:" + var10.getMessage());
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1302, "decryptBytes error:" + var10.getMessage());
            return null;
         }
      } else {
         System.arraycopy(var1, 20, var8, 0, var7);
         var6 = ByteBuffer.wrap(var8);
      }

      return new Pair(var5, var6);
   }
}
