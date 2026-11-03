package cn.jiguang.ar;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.av.RecoverTypeaf2abcad;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c4;

class RecoverTypeaf2aadb9 extends RecoverTypeaf2b0401 {
   private byte[] recoverField_a;
   private RecoverTypeaf2ac06e recoverField_b;
   private Context recoverField_c;

   RecoverTypeaf2aadb9(RecoverTypeaf2ac06e var1, Context var2, byte[] var3) {
      this.recoverField_c = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var2);
      this.recoverField_a = var3;
      this.recoverField_h = "TcpRecvAction";
      this.recoverField_b = var1;
   }

   @Override
   public void recoverMethod_a_9b75() {
      try {
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_c, "tcp_a22", null);
         RecoverTypeaf2abcad.recoverMethod_a_8395a015(this.recoverField_b, this.recoverField_c, this.recoverField_a);
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpRecvAction", "TcpRecvAction failed:" + var2.getMessage());
      }
   }
}
