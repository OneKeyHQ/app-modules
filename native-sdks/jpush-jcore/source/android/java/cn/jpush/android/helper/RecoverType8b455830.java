package cn.jpush.android.helper;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.api.JDispatchAction;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.local.JPushResponse;
import java.nio.ByteBuffer;

public class RecoverType8b455830 extends JDispatchAction {
   @Override
   public boolean isSupportedCMD(String var1, int var2) {
      return ActionHelper.getInstance().isSupportedCMD(var1, var2);
   }

   @Override
   public void dispatchMessage(Context var1, String var2, int var3, int var4, long var5, long var7, ByteBuffer var9) {
      Logger.recoverMethod_d_e925fa09("JPushDispacthAction", "dispatchMessage command:" + var3 + ",ver:" + var4 + ",rid:" + var5 + ",requestid:" + var7);
      JPushResponse var10 = new JPushResponse(var3, var5, var7, var9);
      ActionHelper.getInstance().doAction(var1, "msg", var10);
   }

   @Override
   public void dispatchTimeOutMessage(Context var1, String var2, long var3, int var5) {
      Bundle var6 = new Bundle();
      var6.putLong("rid", var3);
      var6.putInt("cmd", var5);
      ActionHelper.getInstance().doAction(var1, "msg_time_out", var6);
   }

   @Override
   public void onEvent(Context var1, String var2, int var3, int var4, String var5) {
      Bundle var6 = new Bundle();
      var6.putInt("type", var3);
      var6.putInt("code", var4);
      var6.putString("msg", var5);
      ActionHelper.getInstance().doAction(var1, "jcore_on_event", var6);
   }

   @Override
   public String getSdkVersion(String var1) {
      return ActionHelper.getInstance().getSdkVersion(var1);
   }

   @Override
   public String getReportVersionKey(String var1) {
      return "sdk_ver";
   }

   @Override
   public short getRegFlag(String var1) {
      return 1;
   }

   @Override
   public short getLoginFlag(String var1) {
      return 1;
   }

   @Override
   public short getRegPriority(String var1) {
      return 0;
   }

   @Override
   public short getLogPriority(String var1) {
      return 1;
   }

   @Override
   public short getUserCtrlProperty(String var1) {
      return 1;
   }

   @Override
   public void onActionRun(Context var1, String var2, String var3, Bundle var4) {
      ActionHelper.getInstance().doAction(var1, var3, var4);
   }

   @Override
   public Object beforLogin(Context var1, String var2, int var3, String var4) {
      return ActionHelper.getInstance().beforLogin(var1, var2, var3, var4);
   }

   @Override
   public void handleMessage(Context var1, String var2, Object var3) {
      ActionHelper.getInstance().doAction(var1, "handle_msg", var3);
   }

   @Override
   public Bundle getPInfo(String var1) {
      Bundle var2 = new Bundle();
      var2.putInt("pid", 2);
      var2.putString("code", JPushConstants.SDK_VERSION_CODE + "");
      return var2;
   }

   @Override
   public Object getDataByCmd(Context var1, int var2) {
      if (30001 == var2) {
         return "sdk_ver_local";
      }

      if (30002 == var2) {
         return JPushConstants.SDK_VERSION_NAME;
      }

      try {
         return ActionHelper.getInstance().getDataByCmd(var1, var2);
      } catch (Throwable var4) {
         return null;
      }
   }

   @Override
   public int getSdkType() {
      return super.getSdkType();
   }
}
