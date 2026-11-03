package cn.jiguang.c;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a972e;
import cn.jiguang.am.RecoverTypeaf2a9ae5;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JDispatchAction;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.at.RecoverTypeaf2ab534;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import cn.jiguang.m.RecoverType5a6aed9;
import java.nio.ByteBuffer;
import org.json.JSONObject;

public class RecoverType5a6894f extends JDispatchAction {
   @Override
   public boolean isSupportedCMD(String var1, int var2) {
      boolean var3 = false;
      if (var2 == 0 || var2 == 1 || var2 == 19 || var2 == 25 || var2 == 26 || var2 == 30 || var2 == 32) {
         var3 = true;
      }

      return var3;
   }

   @Override
   public void dispatchMessage(Context var1, String var2, int var3, int var4, long var5, long var7, ByteBuffer var9) {
      try {
         this.recoverMethod_a_c4c47e9f(var1, var7, var3, var9);
      } catch (Throwable var11) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreDispatchAction", "dispatchMessage failed:" + var11.getMessage());
      }
   }

   private static JSONObject recoverMethod_a_c01e8fea(ByteBuffer var0) {
      try {
         if (null == var0) {
            return null;
         }

         ByteBuffer var1 = ByteBuffer.wrap(var0.array());
         long var2 = var1.getLong();
         short var4 = var1.getShort();
         byte[] var5 = new byte[var4];
         var1.get(var5);
         String var6 = new String(var5, "UTF-8");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreDispatchAction", "parseBundle2Json content: " + var6);
         return new JSONObject(var6);
      } catch (Exception var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreDispatchAction", "parseBundle2Json exception:" + var7.getMessage());
         return null;
      }
   }

   private void recoverMethod_a_371edd3e(Context var1, ByteBuffer var2) {
      JSONObject var3 = recoverMethod_a_c01e8fea(var2);
      if (null != var3) {
         try {
            int var4 = var3.optInt("cmd");
            JSONObject var5 = var3.optJSONObject("content");
            String var6 = var5 != null ? var5.toString() : "";
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreDispatchAction", "deal command, cmd: " + var4 + ", content: " + var6);
            switch (var4) {
               case 75:
                  RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_be7fc169(var5);
               case 80:
               default:
                  break;
               case 84:
                  RecoverTypeaf2a972e.recoverMethod_a_3c4029ab(var1, var5);
            }

            Bundle var7 = new Bundle();
            byte[] var8 = var2.array();
            var7.putByteArray("RESPONSE_BODY", var8);
            RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, "cmd", var7);
         } catch (Throwable var9) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreDispatchAction", "deal command failed, " + var9.getMessage());
         }
      }
   }

   private void recoverMethod_a_c4c47e9f(Context var1, long var2, int var4, ByteBuffer var5) {
      switch (var4) {
         case 19:
            RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(var1, "tcp_a18", null);
            break;
         case 25:
            this.recoverMethod_a_371edd3e(var1, var5);
            break;
         case 26:
            if (var5 != null) {
               short var6 = var5.getShort();
               if (var6 == 0) {
                  RecoverTypeaf2ab534.recoverMethod_a_c69656f2().recoverMethod_a_1a54e731(var1, var2);
               } else {
                  RecoverTypeaf2ab534.recoverMethod_a_c69656f2().recoverMethod_a_304871f2(var1, var2, var6);
               }
            }
         case 30:
         case 32:
         default:
            break;
         case 41:
            this.recoverMethod_b_371edd3e(var1, var5);
      }
   }

   private void recoverMethod_b_371edd3e(Context var1, ByteBuffer var2) {
      try {
         if (var2 != null) {
            short var3 = var2.getShort();
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreDispatchAction", "setCountry code:" + var3);
            if (0 == var3) {
               return;
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreDispatchAction", "setCountry body null");
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("JCoreDispatchAction", "setCountry fail:", var4);
      }

      RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(var1, "tcp_a25", new Bundle());
   }

   @Override
   public void handleMessage(Context var1, String var2, Object var3) {
   }

   @Override
   public void onActionRun(Context var1, String var2, String var3, Bundle var4) {
      try {
         if (var4 != null && !TextUtils.isEmpty(var3)) {
            if (var3.equals("lbsenable")) {
               if (var4 != null) {
                  boolean var5 = var4.getBoolean("enable");
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_ae_48794abf().recoverMethod_a_4e9722a3(var5));
               }
            } else if (var3.equals("lbsforenry")) {
               try {
                  long var11 = var4.getLong("forenry");
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreDispatchAction", "setLbsPermissionDialogShieldDelay=" + var11);
                  long var7 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_ab_48794abf());
                  if (var7 <= 0L) {
                     RecoverType5a690d2.recoverMethod_a_abcb8d40(
                        var1, RecoverType5a690d1.recoverMethod_ab_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                     );
                  }

                  RecoverType5a690d2.recoverMethod_a_abcb8d40(
                     var1, RecoverType5a690d1.recoverMethod_ad_48794abf().recoverMethod_a_4e9722a3(var11 > 0L ? var11 : null)
                  );
               } catch (Throwable var9) {
                  RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreDispatchAction", "onActionRun failed:" + var9);
               }
            } else if (var3.equals("notification_state")) {
               RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, var3, var4);
            } else if (var3.equals("old_cmd")) {
               RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, var3, var4);
            } else if (!var3.equals("user_present")) {
               if (var3.equals("set_wake_enable")) {
                  RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, var3, var4);
               } else if (var3.equals("open_log")) {
                  this.recoverMethod_a_69c77401(var1, var4);
               } else if ("country".equals(var3)) {
                  String var12 = var4.getString("country");
                  JConstants.testCountry = var12;
               } else if ("save_exception_data".equals(var3)) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_79e5e33f(var4);
               }
            }
         }
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreDispatchAction", "onActionRun failed:" + var10.getMessage());
      }
   }

   private void recoverMethod_a_69c77401(Context var1, Bundle var2) {
      try {
         if (var2 != null) {
            String var3 = var2.getString("aes");
            String var4 = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var3, RecoverType5a681cd.recoverField_i);
            JSONObject var5 = new JSONObject(var4);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreDispatchAction", "jsonObject=" + var5);
            RecoverType5a68d10.recoverMethod_a_3c4029ab(var1, var5);
            if (var5.has("u_l")) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreDispatchAction", "startUpload");
               RecoverTypeaf2a9ae5.recoverMethod_b_f0554df3(var1);
            }

            if (var5.has("e_l")) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreDispatchAction", "set e_l");
               RecoverType5a690d2.recoverMethod_a_abcb8d40(
                  var1, RecoverType5a690d1.recoverMethod_an_48794abf().recoverMethod_a_4e9722a3(var5.optBoolean("e_l"))
               );
            }
         }
      } catch (Throwable var6) {
      }
   }

   @Override
   public String getSdkVersion(String var1) {
      return RecoverType5a681cd.recoverField_b;
   }

   @Override
   public String getReportVersionKey(String var1) {
      return "core_sdk_ver";
   }

   @Override
   public short getRegFlag(String var1) {
      return 0;
   }

   @Override
   public short getLoginFlag(String var1) {
      return 0;
   }

   @Override
   public short getRegPriority(String var1) {
      return 3;
   }

   @Override
   public short getLogPriority(String var1) {
      return 0;
   }

   @Override
   public short getUserCtrlProperty(String var1) {
      return 6;
   }
}
