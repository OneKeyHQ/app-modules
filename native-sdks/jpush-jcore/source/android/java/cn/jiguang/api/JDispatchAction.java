package cn.jiguang.api;

import android.content.Context;
import android.os.Bundle;
import java.nio.ByteBuffer;

public abstract class JDispatchAction {
   public static final int SDK_NORMAL_TYPE = 0;
   public static final int SDK_CUSTOM_TYPE = 1;
   public static final int SDK_NORMAL_GOOGLE_TYPE = 2;
   public static final int SDK_NORMAL_PURE_TYPE = 3;
   public static final int SDK_CUSTOM_GOOGLE_TYPE = 4;
   public static final int SDK_CUSTOM_PURE_TYPE = 5;

   public abstract boolean isSupportedCMD(String var1, int var2);

   public abstract void dispatchMessage(Context var1, String var2, int var3, int var4, long var5, long var7, ByteBuffer var9);

   public void onActionRun(Context var1, String var2, String var3, Bundle var4) {
   }

   public void onEvent(Context var1, String var2, int var3, int var4, String var5) {
   }

   public void handleMessage(Context var1, String var2, Object var3) {
   }

   public abstract String getSdkVersion(String var1);

   public abstract String getReportVersionKey(String var1);

   public void dispatchTimeOutMessage(Context var1, String var2, long var3, int var5) {
   }

   public boolean checkAction(String var1, int var2) {
      return true;
   }

   public Object beforRegister(Context var1, String var2, int var3, String var4) {
      return null;
   }

   public Object beforLogin(Context var1, String var2, int var3, String var4) {
      return null;
   }

   public Object onSendData(Context var1, String var2, long var3, int var5, int var6) {
      return null;
   }

   public abstract short getRegFlag(String var1);

   public abstract short getLoginFlag(String var1);

   public abstract short getRegPriority(String var1);

   public abstract short getLogPriority(String var1);

   public Bundle getPInfo(String var1) {
      return null;
   }

   public abstract short getUserCtrlProperty(String var1);

   public Object getDataByCmd(Context var1, int var2) {
      return null;
   }

   public Object getDataByAction(Context var1, String var2, String var3, Bundle var4, Object var5) {
      return null;
   }

   public int getSdkType() {
      return 0;
   }
}
