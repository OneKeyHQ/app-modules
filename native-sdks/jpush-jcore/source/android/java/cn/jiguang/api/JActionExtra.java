package cn.jiguang.api;

import android.content.Context;

public abstract class JActionExtra {
   public boolean checkAction(int var1) {
      return true;
   }

   public Object beforRegister(Context var1, int var2, String var3) {
      return null;
   }

   public Object beforLogin(Context var1, int var2, String var3) {
      return null;
   }

   public Object onSendData(Context var1, long var2, long var4, int var6, int var7) {
      return null;
   }
}
