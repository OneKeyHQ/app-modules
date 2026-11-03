package cn.jiguang.api;

import java.nio.ByteBuffer;

public abstract class JRequest extends JProtocol {
   public JRequest(int var1, int var2, long var3) {
      super(true, var1, var2, var3);
   }

   public JRequest(Object var1, ByteBuffer var2) {
      super(true, var1, var2);
   }

   public void setSid(int var1) {
      this.head.recoverMethod_b_13462e(var1);
   }

   public void setJuid(long var1) {
      this.head.recoverMethod_a_1349ef(var1);
   }
}
