package cn.jiguang.api;

import cn.jiguang.api.utils.ByteBufferUtils;
import java.nio.ByteBuffer;

public abstract class JResponse extends JProtocol {
   public int code;

   public JResponse(int var1, int var2, long var3, long var5, int var7, String var8) {
      super(false, var1, var2, var3, -1, var5);
      this.code = var7;
   }

   @Override
   protected void writeBody() {
      if (this.code >= 0) {
         this.writeInt2(this.code);
      }
   }

   public JResponse(Object var1, ByteBuffer var2) {
      super(false, var1, var2);
   }

   public JResponse(ByteBuffer var1, byte[] var2) {
      super(false, var1, var2);
   }

   @Override
   protected void parseBody() {
      if (this.isNeedParseeErrorMsg()) {
         this.code = ByteBufferUtils.getShort(this.body, this);
      }
   }

   @Override
   public String toString() {
      return "JResponse{code=" + this.code + '}';
   }
}
