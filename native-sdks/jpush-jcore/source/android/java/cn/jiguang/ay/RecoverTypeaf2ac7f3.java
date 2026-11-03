package cn.jiguang.ay;

import android.util.SparseArray;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;

public class RecoverTypeaf2ac7f3 {
   private static final SparseArray recoverField_a = new SparseArray();

   public static String recoverMethod_a_47921032(int var0) {
      if (recoverField_a.get(var0) == null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("StatusCode", "Unknown error code - " + var0);
         return null;
      } else {
         return (String)recoverField_a.get(var0);
      }
   }

   static {
      recoverField_a.put(0, "OK");
      recoverField_a.put(-1001, "Exceed buffer size. Please contact support.");
      recoverField_a.put(-1000, "Connection failed. Please check your connection and retry later!");
      recoverField_a.put(-998, "Sending failed or timeout. Please Retry later!");
      recoverField_a.put(-997, "Receiving failed or timeout. Please Retry later!");
      recoverField_a.put(-996, "Connection is closed. Please Retry later!");
      recoverField_a.put(-994, "Response timeout. Please Retry later!");
      recoverField_a.put(-993, "Invalid socket. Please Retry later!");
      recoverField_a.put(11, "Failed to register!");
      recoverField_a.put(1005, "Your appKey and android package name are not matched. Please double check them according to Application you created on Portal.");
      recoverField_a.put(1006, "You android package name is not exist, Please register your pacakge name in Portal.");
      recoverField_a.put(1007, "Invalid Imei, Register again.");
      recoverField_a.put(1008, "Invalid appKey, Please get your Appkey from JIGUANG web console!");
      recoverField_a.put(1009, "Your appKey is related to a non-Android App.Please use your Android App's appKey, or add an Android app for this appKey.");
      recoverField_a.put(10000, "Receiver data parse error");
      recoverField_a.put(102, "102 - Incorrect password");
      recoverField_a.put(108, "108 - Incorrect uid");
      recoverField_a.put(1012, "Server reject this connection, will clear cache and restart connect.");
      recoverField_a.put(1000, "Context is null");
      recoverField_a.put(1001, "The permission should be defined - " + RecoverType5a681cd.recoverField_a);
      recoverField_a.put(1002, "The permissoin is required - android.permission.INTERNET and android.permission.ACCESS_NETWORK_STATE");
      recoverField_a.put(10001, "JCore appKey - not defined in manifest");
      recoverField_a.put(10002, "Appkey not same with meta appkey");
      recoverField_a.put(10003, "Appkey is empty");
   }
}
