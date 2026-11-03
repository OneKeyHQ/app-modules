package cn.jiguang.bi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import cn.jiguang.api.JAction;
import cn.jiguang.api.JActionExtra;
import cn.jiguang.api.JCoreInterface;
import cn.jiguang.api.JDispatchAction;
import cn.jiguang.api.SdkType;
import java.nio.ByteBuffer;

public class RecoverTypeaf2b0045 extends JDispatchAction {
   private JAction recoverMethod_a_7cfa4581(String var1) {
      return RecoverTypeaf2b003f.recoverMethod_a_c69f6d47().recoverMethod_a_7cfa4581(var1);
   }

   private JActionExtra recoverMethod_b_41abf0c5(String var1) {
      return RecoverTypeaf2b003f.recoverMethod_a_c69f6d47().recoverMethod_b_41abf0c5(var1);
   }

   @Override
   public boolean isSupportedCMD(String var1, int var2) {
      JAction var3 = this.recoverMethod_a_7cfa4581(var1);
      return var3 != null ? var3.isSupportedCMD(var2) : false;
   }

   @Override
   public void dispatchMessage(Context var1, String var2, int var3, int var4, long var5, long var7, ByteBuffer var9) {
      JAction var10 = this.recoverMethod_a_7cfa4581(var2);
      if (var10 != null) {
         RecoverTypeaf2b0042.recoverMethod_b_e925fa09(
            "SupportDispatchAction", "dispatchMessage ,command:" + var3 + ",ver:" + var4 + ",rid:" + var5 + ",reuqestId:" + var7
         );
         if (var2.equals(SdkType.JMESSAGE.name())) {
            int var11 = 20 + var9.limit();
            RecoverTypeaf2b0041 var12 = new RecoverTypeaf2b0041(false, var11, var4, var3, var5, JCoreInterface.getSid(), JCoreInterface.getUid());
            var10.dispatchMessage(var1, 0L, var3, var12, var9);
         } else {
            RecoverTypeaf2b0041 var13 = new RecoverTypeaf2b0041(false, var4, var3, var7);
            var10.dispatchMessage(var1, 0L, var3, var13, var9);
         }
      }
   }

   @Override
   public String getSdkVersion(String var1) {
      JAction var2 = this.recoverMethod_a_7cfa4581(var1);
      return var2 != null ? var2.getSdkVersion() : "";
   }

   @Override
   public void dispatchTimeOutMessage(Context var1, String var2, long var3, int var5) {
      JAction var6 = this.recoverMethod_a_7cfa4581(var2);
      if (var6 != null) {
         var6.dispatchTimeOutMessage(var1, 0L, var3, var5);
      }
   }

   @Override
   public void onActionRun(Context var1, String var2, String var3, Bundle var4) {
      JAction var5 = this.recoverMethod_a_7cfa4581(var2);
      if (var5 != null) {
         var5.onActionRun(var1, 0L, var4, (Handler)null);
      }
   }

   @Override
   public void onEvent(Context var1, String var2, int var3, int var4, String var5) {
      JAction var6 = this.recoverMethod_a_7cfa4581(var2);
      if (var6 != null) {
         var6.onEvent(var1, 0L, var3);
      }
   }

   @Override
   public void handleMessage(Context var1, String var2, Object var3) {
      RecoverTypeaf2b0042.recoverMethod_b_e925fa09("SupportDispatchAction", "handleMessage,sdkType:" + var2);
      JAction var4 = this.recoverMethod_a_7cfa4581(var2);
      if (var4 != null) {
         var4.handleMessage(var1, 0L, var3);
      }
   }

   @Override
   public String getReportVersionKey(String var1) {
      if (var1.equals(SdkType.JPUSH.name())) {
         return "sdk_ver";
      } else if (var1.equals(SdkType.JANALYTICS.name())) {
         return "statistics_sdk_ver";
      } else if (var1.equals(SdkType.JSHARE.name())) {
         return "share_sdk_ver";
      } else if (var1.equals(SdkType.JAD.name())) {
         return "ssp_sdk_ver";
      } else if (var1.equals(SdkType.JMESSAGE.name())) {
         return "im_sdk_ver";
      } else if (var1.equals(SdkType.JVERIFICATION.name())) {
         return "verification_sdk_ver";
      } else if (var1.equals(SdkType.JMLINK.name())) {
         return "mlink_sdk_ver";
      } else {
         return var1.equals(SdkType.JUNION.name()) ? "junion_sdk_ver" : null;
      }
   }

   @Override
   public short getRegFlag(String var1) {
      if (var1.equals(SdkType.JPUSH.name())) {
         return 1;
      } else if (var1.equals(SdkType.JANALYTICS.name())) {
         return 4;
      } else if (var1.equals(SdkType.JSHARE.name())) {
         return 8;
      } else if (var1.equals(SdkType.JAD.name())) {
         return 128;
      } else if (var1.equals(SdkType.JMESSAGE.name())) {
         return 32;
      } else if (var1.equals(SdkType.JVERIFICATION.name())) {
         return 256;
      } else if (var1.equals(SdkType.JMLINK.name())) {
         return 512;
      } else {
         return (short)(var1.equals(SdkType.JUNION.name()) ? 1024 : 0);
      }
   }

   @Override
   public short getLoginFlag(String var1) {
      if (var1.equals(SdkType.JPUSH.name())) {
         return 1;
      } else if (var1.equals(SdkType.JANALYTICS.name())) {
         return 4;
      } else if (var1.equals(SdkType.JSHARE.name())) {
         return 8;
      } else if (var1.equals(SdkType.JAD.name())) {
         return 128;
      } else if (var1.equals(SdkType.JMESSAGE.name())) {
         return 32;
      } else if (var1.equals(SdkType.JVERIFICATION.name())) {
         return 256;
      } else if (var1.equals(SdkType.JMLINK.name())) {
         return 512;
      } else {
         return (short)(var1.equals(SdkType.JUNION.name()) ? 1024 : 0);
      }
   }

   @Override
   public short getRegPriority(String var1) {
      if (var1.equals(SdkType.JPUSH.name())) {
         return 0;
      } else if (var1.equals(SdkType.JANALYTICS.name())) {
         return 1;
      } else if (var1.equals(SdkType.JSHARE.name())) {
         return 2;
      } else if (var1.equals(SdkType.JAD.name())) {
         return 4;
      } else if (var1.equals(SdkType.JVERIFICATION.name())) {
         return 5;
      } else if (var1.equals(SdkType.JMLINK.name())) {
         return 6;
      } else {
         return (short)(var1.equals(SdkType.JUNION.name()) ? 7 : 3);
      }
   }

   @Override
   public short getLogPriority(String var1) {
      if (var1.equals(SdkType.JPUSH.name())) {
         return 1;
      } else if (var1.equals(SdkType.JANALYTICS.name())) {
         return 2;
      } else if (var1.equals(SdkType.JSHARE.name())) {
         return 4;
      } else if (var1.equals(SdkType.JAD.name())) {
         return 5;
      } else if (var1.equals(SdkType.JVERIFICATION.name())) {
         return 3;
      } else if (var1.equals(SdkType.JMLINK.name())) {
         return 6;
      } else {
         return (short)(var1.equals(SdkType.JUNION.name()) ? 7 : 0);
      }
   }

   @Override
   public short getUserCtrlProperty(String var1) {
      if (var1.equals(SdkType.JPUSH.name())) {
         return 1;
      } else if (var1.equals(SdkType.JMESSAGE.name())) {
         return 2;
      } else if (var1.equals(SdkType.JANALYTICS.name())) {
         return 4;
      } else if (var1.equals(SdkType.JSHARE.name())) {
         return 5;
      } else if (var1.equals(SdkType.JAD.name())) {
         return 9;
      } else if (var1.equals(SdkType.JVERIFICATION.name())) {
         return 10;
      } else if (var1.equals(SdkType.JMLINK.name())) {
         return 11;
      } else {
         return (short)(var1.equals(SdkType.JUNION.name()) ? 12 : 6);
      }
   }

   @Override
   public Object onSendData(Context var1, String var2, long var3, int var5, int var6) {
      JActionExtra var7 = this.recoverMethod_b_41abf0c5(var2);
      return var7 != null ? var7.onSendData(var1, 0L, var3, var5, var6) : null;
   }

   @Override
   public Object beforLogin(Context var1, String var2, int var3, String var4) {
      JActionExtra var5 = this.recoverMethod_b_41abf0c5(var2);
      return var5 != null ? var5.beforLogin(var1, var3, var4) : null;
   }

   @Override
   public Object beforRegister(Context var1, String var2, int var3, String var4) {
      JActionExtra var5 = this.recoverMethod_b_41abf0c5(var2);
      return var5 != null ? var5.beforRegister(var1, var3, var4) : null;
   }

   @Override
   public boolean checkAction(String var1, int var2) {
      JActionExtra var3 = this.recoverMethod_b_41abf0c5(var1);
      return var3 != null ? var3.checkAction(var2) : true;
   }
}
