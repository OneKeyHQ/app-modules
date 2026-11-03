package cn.jpush.android.local;

import java.nio.ByteBuffer;

public class JPushResponse {
   public int cmd;
   public long rid;
   public long rquestId;
   public ByteBuffer body;

   public JPushResponse(int var1, long var2, long var4, ByteBuffer var6) {
      this.cmd = var1;
      this.rid = var2;
      this.body = var6;
      this.rquestId = var4;
   }

   protected void parseBody() {
   }

   public int getCmd() {
      return this.cmd;
   }

   public void setCmd(int var1) {
      this.cmd = var1;
   }

   public long getRid() {
      return this.rid;
   }

   public void setRid(long var1) {
      this.rid = var1;
   }

   public ByteBuffer getBody() {
      return this.body;
   }

   public void setBody(ByteBuffer var1) {
      this.body = var1;
   }

   public long getRquestId() {
      return this.rquestId;
   }

   public void setRquestId(long var1) {
      this.rquestId = var1;
   }

   @Override
   public String toString() {
      return "JPushResponse{cmd=" + this.cmd + ", rid=" + this.rid + ", rquestId=" + this.rquestId + '}';
   }
}
