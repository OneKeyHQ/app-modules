package cn.jpush.android.api;

import android.os.Bundle;

public class CmdMessage {
   public int cmd;
   public int errorCode;
   public String msg;
   public Bundle extra;

   public CmdMessage(int var1, int var2) {
      this.cmd = var1;
      this.errorCode = var2;
   }

   public CmdMessage(int var1, int var2, String var3) {
      this.cmd = var1;
      this.errorCode = var2;
      this.msg = var3;
   }

   public CmdMessage(int var1, int var2, String var3, Bundle var4) {
      this.cmd = var1;
      this.errorCode = var2;
      this.msg = var3;
      this.extra = var4;
   }

   @Override
   public String toString() {
      return "CmdMessage{cmd=" + this.cmd + ", errorCode=" + this.errorCode + ", msg='" + this.msg + '\'' + ", extra=" + this.extra + '}';
   }
}
