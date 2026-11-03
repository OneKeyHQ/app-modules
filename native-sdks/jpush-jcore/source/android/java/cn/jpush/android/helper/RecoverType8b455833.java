package cn.jpush.android.helper;

import java.util.HashMap;

public class RecoverType8b455833 {
   private static final HashMap recoverField_a = new HashMap();

   public static String recoverMethod_a_2f33e691(long var0) {
      if (recoverField_a.get(var0) == null) {
         Logger.recoverMethod_d_e925fa09("StatusCode", "Unknown report code - " + var0);
         return "";
      } else {
         return (String)recoverField_a.get(var0);
      }
   }

   public static long recoverMethod_a_255f64a(int var0, long var1) {
      return recoverMethod_a_4806323c((byte)0, var0, var1);
   }

   public static long recoverMethod_a_4806323c(byte var0, int var1, long var2) {
      return recoverMethod_a_132bdb(var0) + recoverMethod_a_134622(var1) + var2;
   }

   private static long recoverMethod_a_134622(int var0) {
      switch (var0) {
         case 1:
            return 100000L;
         case 2:
            return 110000L;
         case 3:
            return 120000L;
         case 4:
            return 130000L;
         default:
            return -1L;
      }
   }

   private static long recoverMethod_a_132bdb(byte var0) {
      long var1;
      switch (var0) {
         case 0:
         default:
            var1 = 10000000L;
            break;
         case 1:
            var1 = 11000000L;
            break;
         case 2:
            var1 = 12000000L;
            break;
         case 3:
            var1 = 13000000L;
            break;
         case 4:
            var1 = 14000000L;
            break;
         case 5:
            var1 = 15000000L;
            break;
         case 6:
            var1 = 16000000L;
            break;
         case 7:
            var1 = 17000000L;
            break;
         case 8:
            var1 = 18000000L;
      }

      return var1;
   }

   static {
      recoverField_a.put(1060L, "need not show notification by foreground");
      recoverField_a.put(1061L, "need not show notification by isNeedShowNotification return false");
      recoverField_a.put(1032L, "MSG already cancle");
      recoverField_a.put(1033L, "overide msg already cancle");
      recoverField_a.put(1034L, "MSG already end");
      recoverField_a.put(1035L, "msg delay show");
      recoverField_a.put(1036L, "notification disabled");
      recoverField_a.put(1037L, "notification channel disabled");
      recoverField_a.put(972L, "Deep link set small icon failed");
      recoverField_a.put(973L, "Deep link set large icon failed");
      recoverField_a.put(976L, "Deep link jump success");
      recoverField_a.put(977L, "Deep link jump failed");
      recoverField_a.put(978L, "Fail Deep link jump success");
      recoverField_a.put(979L, "Fail Deep link jump failed");
      recoverField_a.put(993L, "target app uninstall,not found target app small icon");
      recoverField_a.put(986L, "Message is not in push time");
      recoverField_a.put(988L, "Deep link target app uninstalled");
      recoverField_a.put(995L, "Message JSON parsing succeed");
      recoverField_a.put(996L, "Message JSON parsing failed");
      recoverField_a.put(1000L, "User clicked and opened the Message");
      recoverField_a.put(1028L, "User clicked and opened the Message from JPushInterface.reportNotificationOpened");
      recoverField_a.put(1068L, "User clear the JPush Notification from JPushReportHelper.reportMsgResult");
      recoverField_a.put(1069L, "User clear the OS Push Notification from JPushReportHelper.reportThirdSDKMsgActionResult");
      recoverField_a.put(1006L, "User clicked 'Cancel'");
      recoverField_a.put(1100L, "Invalid param or unexpected result.");
      recoverField_a.put(1014L, "Failed to preload required resource");
      recoverField_a.put(1016L, "User clicked the webview's url");
      recoverField_a.put(1018L, "The Message show in the status bar");
      recoverField_a.put(1020L, "Down image failed");
      recoverField_a.put(1021L, "Down html failed");
      recoverField_a.put(1075L, "Open WX miniprogram failed");
      recoverField_a.put(1080L, "show notification make unknown error");
      recoverField_a.put(1106L, "the custom message show");
   }
}
