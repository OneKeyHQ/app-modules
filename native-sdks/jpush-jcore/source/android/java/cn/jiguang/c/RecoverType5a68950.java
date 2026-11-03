package cn.jiguang.c;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.al.RecoverTypeaf2a9724;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.al.RecoverTypeaf2a9727;
import cn.jiguang.al.RecoverTypeaf2a9728;
import cn.jiguang.al.RecoverTypeaf2a9729;
import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.al.RecoverTypeaf2a972b;
import cn.jiguang.al.RecoverTypeaf2a972d;
import cn.jiguang.am.RecoverTypeaf2a9ae4;
import cn.jiguang.am.RecoverTypeaf2a9ae6;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.analytics.page.PushSA;
import cn.jiguang.ao.RecoverTypeaf2aa269;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.ap.RecoverTypeaf2aa629;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.api.ReportCallBack;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.au.RecoverTypeaf2ab8ee;
import cn.jiguang.ax.RecoverTypeaf2ac430;
import cn.jiguang.ay.RecoverTypeaf2ac7f2;
import cn.jiguang.az.RecoverTypeaf2acbb8;
import cn.jiguang.b.RecoverType5a6858e;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bg.RecoverTypeaf2af8c0;
import cn.jiguang.bh.RecoverTypeaf2afc7f;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c5;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d6;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.dy.JDynamicLoadBase;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.e.RecoverType5a690d3;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreHelperAction;
import cn.jiguang.m.RecoverType5a6aed9;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

public class RecoverType5a68950 extends JCoreHelperAction {
   public static List recoverField_a = new ArrayList();
   private static final String[] recoverField_b = new String[]{"JDevice", "JWakeCmd", "JWake", "JCommon", "JArp", "JLocation"};
   private boolean recoverField_c = false;

   private Boolean recoverMethod_a_b744682b(int var1) {
      if (!recoverField_a.contains(var1)) {
         if (RecoverTypeaf2a972a.recoverMethod_b_9b79()) {
            return var1 == 82 ? true : null;
         }

         if (RecoverTypeaf2a972b.recoverMethod_a_9b79()) {
            return null;
         }
      }

      return false;
   }

   @Override
   public Object onEvent(Context var1, String var2, int var3, String var4, Bundle var5, Object... var6) {
      try {
         boolean var7 = RecoverTypeaf2a9728.Member8ee171e5.recoverMethod_a_6cb381a1().recoverMethod_a_79e5e343(var5);
         if (var7) {
            return null;
         }

         var1 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var1);
         Boolean var8 = this.recoverMethod_a_b744682b(var3);
         if (var8 == null || var8) {
            return var8;
         }

         RecoverTypeaf2afc7f.recoverMethod_a_c69ef907().recoverMethod_a_af8a350b(var2, var3, var4, var5, var6);
         switch (var3) {
            case 0:
               recoverMethod_c_5ffc00fd(var1, var2);
               break;
            case 1:
               recoverMethod_d_5ffc00fd(var1, var2);
               break;
            case 2:
               this.recoverMethod_b_d19840b7(var1, var4, var5);
               break;
            case 3:
               recoverMethod_a_e26f8bed(var1, var2, var4, var5);
               break;
            case 4:
               return RecoverType5a68d10.recoverMethod_b_e501eb0d(var1);
            case 5:
               return RecoverType5a68d10.recoverMethod_d_e501eb0d(var1);
            case 6:
               return RecoverType5a68d10.recoverMethod_h_e501eb0d(var1);
            case 7:
               return RecoverType5a68d10.recoverMethod_e_e501eb0d(var1);
            case 8:
               return RecoverType5a68d10.recoverMethod_i_e501eb0d(var1);
            case 9:
               recoverMethod_a_5ffc00fd(var1, var4);
               break;
            case 10:
               recoverMethod_a_d19840b7(var1, var4, var5);
               break;
            case 11:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Runnable) {
                  if (!TextUtils.isEmpty(var2) && var2.toLowerCase().equals("jpush")) {
                     var4 = "MAJOR_TASK";
                  } else {
                     var4 = Arrays.asList(recoverField_b).contains(var4) ? "SCHEDULE_TASK" : var4;
                  }

                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(var4, (Runnable)var6[0]);
               }
               break;
            case 12:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Runnable) {
                  String var34 = "NORMAL_TASK";
                  if (!TextUtils.isEmpty(var2) && var2.toLowerCase().equals("jpush")) {
                     var34 = "MAJOR_TASK";
                  }

                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(var34, (Runnable)var6[0]);
               }
            case 13:
            case 32:
            case 33:
            case 44:
            case 45:
            case 47:
            case 57:
            case 64:
            case 65:
            case 69:
            case 87:
            case 104:
            case 113:
            case 114:
            default:
               break;
            case 14:
               if (var6 != null && var6.length > 0) {
                  RecoverTypeaf2aa26b.recoverMethod_a_4fa28b99(var1, var2, var6[0]);
               } else {
                  RecoverTypeaf2aa26b.recoverMethod_a_4fa28b99(var1, var2, null);
               }
               break;
            case 15:
               if (var6 != null && var6.length > 1 && var6[1] instanceof ReportCallBack) {
                  RecoverTypeaf2aa26b.recoverMethod_a_6892eae2(var1, (JSONObject)var6[0], (ReportCallBack)var6[1]);
               }
               break;
            case 16:
               recoverMethod_a_bb994ac1(var1, var2, var5, "tcp_a3");
               break;
            case 17:
               recoverMethod_a_bb994ac1(var1, var2, var5, "tcp_a5");
               break;
            case 18:
               if (var6 != null && var6.length > 2) {
                  Throwable var33 = null;
                  if (var6.length > 3 && var6[3] instanceof Throwable) {
                     var33 = (Throwable)var6[3];
                  }

                  recoverMethod_a_473e1ef3(var2, var4, (Boolean)var6[1], (Integer)var6[0], (String)var6[2], var33);
               }
               break;
            case 19:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Long) {
                  return RecoverType5a68d11.recoverMethod_a_1a54e725(var1, (Long)var6[0]);
               }

               return RecoverType5a68d11.recoverMethod_b_f0554de7(var1);
            case 20:
               return RecoverType5a68d10.recoverMethod_f_f0554de7(var1);
            case 21:
               return RecoverType5a68d11.recoverMethod_a_f0554df7(var1);
            case 22:
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_P_48794abf().recoverMethod_a_4e9722a3(var4));
               break;
            case 23:
               JConstants.testCountry = var4;

               try {
                  Bundle var32 = new Bundle();
                  var32.putString("country", var4);
                  recoverMethod_a_e26f8bed(var1, "JCore", "country", var32);
               } catch (Throwable var23) {
               }
               break;
            case 24:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Integer) {
                  Bundle var31 = new Bundle();
                  var31.putInt("cmd", (Integer)var6[0]);
                  recoverMethod_a_e26f8bed(var1, "JCore", "old_cmd", null);
               }
               break;
            case 25:
               return RecoverType5a681cd.recoverField_c;
            case 26:
               if (var6 != null && var6.length > 1 && var6[0] instanceof JSONObject && (var6[1] == null || var6[1] instanceof String)) {
                  return RecoverType5a68d10.recoverMethod_a_1b76323(var1, (JSONObject)var6[0], (String)var6[1]);
               }
            case 27:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Boolean) {
                  Bundle var30 = new Bundle();
                  var30.putBoolean("enable", (Boolean)var6[0]);
                  recoverMethod_a_e26f8bed(var1, "JCore", "lbsenable", var30);
               }
               break;
            case 28:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Long) {
                  Bundle var29 = new Bundle();
                  var29.putLong("forenry", (Long)var6[0]);
                  recoverMethod_a_e26f8bed(var1, "JCore", "lbsforenry", var29);
               }
               break;
            case 29:
               if (var6 != null && var6.length > 0 & var6[0] instanceof Integer) {
                  Bundle var28 = new Bundle();
                  var28.putInt("scence", (Integer)var6[0]);
                  recoverMethod_a_e26f8bed(var1, "JCore", "notification_state", var28);
               }
               break;
            case 30:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Integer) {
                  return recoverMethod_a_9c9f3728(var1, (Integer)var6[0], var5);
               }
               break;
            case 31:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Intent) {
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("MAJOR_TASK", new RecoverType5a68950.Member36297a0d(var1, (Intent)var6[0], var2));
               }
               break;
            case 34:
               return RecoverType5a68d10.recoverMethod_j_e501eb0d(var1);
            case 35:
               if (var6 != null && var6.length > 2 && var6[0] instanceof Long && var6[1] instanceof String && var6[2] instanceof String) {
                  RecoverType5a690d2.recoverMethod_b_abcb8d40(
                     var1,
                     RecoverType5a690d1.recoverMethod_B_48794abf().recoverMethod_a_4e9722a3((Long)var6[0]),
                     RecoverType5a690d1.recoverMethod_E_48794abf().recoverMethod_a_4e9722a3((String)var6[1]),
                     RecoverType5a690d1.recoverMethod_C_48794abf().recoverMethod_a_4e9722a3((String)var6[2]),
                     RecoverType5a690d1.recoverMethod_D_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                  );
               }
               break;
            case 36:
               if (var6 != null && var6.length > 0 && var6[0] instanceof String) {
                  RecoverTypeaf2b07c8.recoverMethod_a_5ffc00fd(var1, (String)var6[0]);
               }
               break;
            case 37:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Long) {
                  RecoverType5a68d11.recoverMethod_b_1a54e731(var1, (Long)var6[0]);
               }
               break;
            case 38:
               RecoverType5a690d2.recoverMethod_a_5ffc00fd(var1, "cn.jiguang.sdk.user.profile");
               break;
            case 39:
               RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, var6 != null ? var6[0] : null);
               break;
            case 40:
               if (var6 != null
                  && var6.length > 1
                  && var6[0] instanceof Boolean
                  && var6[1] instanceof Long
                  && var6[2] instanceof Long
                  && var6[3] instanceof Integer) {
                  RecoverType5a681cd.recoverMethod_a_1068f372(var1, (Boolean)var6[0], (Long)var6[1], (Long)var6[2], (Integer)var6[3], true);
               }
               break;
            case 41:
               if (var6 != null && var6.length > 1 && var6[0] instanceof String && var6[1] instanceof JSONObject) {
                  return RecoverTypeaf2b07c9.recoverMethod_a_27ad0523((String)var6[0], (JSONObject)var6[1]);
               }
               break;
            case 42:
               if (var6 != null && var6.length > 0 && var6[0] instanceof String) {
                  return RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d((String)var6[0]);
               }
               break;
            case 43:
               return RecoverType5a68d10.recoverMethod_b_810c345d();
            case 46:
               return RecoverType5a68d10.recoverMethod_k_f0554de6(var1);
            case 48:
               RecoverTypeaf2aa269.recoverMethod_a_f0554df3(var1);
               break;
            case 49:
               return true;
            case 50:
               recoverMethod_a_bb994ac1(var1, var2, var5, "tcp_a4");
               break;
            case 51:
               if (var6 != null && var6.length == 1 && var6[0] instanceof Integer) {
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_G_48794abf().recoverMethod_a_4e9722a3((Integer)var6[0]));
               }
               break;
            case 52:
               recoverMethod_a_79e5e33f(var5);
               break;
            case 53:
               return RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_a_f0554df7(var1);
            case 54:
               if (var6 != null && var6.length > 2) {
                  String var27 = (String)var6[0];
                  int var35 = (Integer)var6[1];
                  int var41 = (Integer)var6[2];
                  Bundle var46 = new Bundle();
                  var46.putString("name", var27);
                  var46.putInt("custom", var35);
                  var46.putInt("dynamic", var41);
                  RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, "set_sdktype_info", var46);
               }
               break;
            case 55:
               recoverMethod_a_69c77401(var1, var5);
               break;
            case 56:
               if ("resume".equals(var4)) {
                  if (JConstants.isInstrumentationHookFailed) {
                     PushSA.getInstance().onResume(var1);
                  }
               } else if ("pause".equals(var4)) {
                  if (JConstants.isInstrumentationHookFailed) {
                     PushSA.getInstance().onPause(var1);
                  }
               } else if ("kill".equals(var4)) {
                  PushSA.getInstance().onKillProcess(var1);
               } else if ("enable".equals(var4)) {
                  if (var6 != null && var6.length > 0 && var6[0] instanceof Boolean) {
                     PushSA.getInstance().setStatEnable((Boolean)var6[0]);
                  }
               } else if ("s_timeout".equals(var4)) {
                  if (var6 != null && var6.length > 0 && var6[0] instanceof Long) {
                     PushSA.getInstance().setInterval((Long)var6[0]);
                  }
               } else if ("f_resume".equals(var4)) {
                  if (var6 != null && var6.length > 0 && var6[0] instanceof String) {
                     PushSA.getInstance().onFragmentResume(var1, (String)var6[0]);
                  }
               } else if ("f_pause".equals(var4) && var6 != null && var6.length > 0 && var6[0] instanceof String) {
                  PushSA.getInstance().onFragmentPause(var1, (String)var6[0]);
               }
               break;
            case 58:
               return RecoverType5a68d10.recoverMethod_l_f0554df7(var1);
            case 59:
               recoverMethod_a_bb994ac1(var1, var2, var5, "tcp_a20");
               break;
            case 60:
               boolean var9 = false;
               String var10 = "unknown msg";
               if (var6 != null && var6.length > 0 && var6[0] instanceof Boolean) {
                  var9 = (Boolean)var6[0];
               }

               if (var6 != null && var6.length > 1 && var6[1] instanceof String) {
                  var10 = (String)var6[1];
               }

               return JConstants.isAndroidQ(var1, var9, var10);
            case 61:
               if (var6 != null && var6.length > 7) {
                  int var40 = (Integer)var6[0];
                  JSONObject var45 = (JSONObject)var6[1];
                  byte[] var48 = (byte[])var6[2];
                  int var52 = (Integer)var6[3];
                  File var59 = (File)var6[4];
                  Set var62 = (Set)var6[5];
                  ReportCallBack var63 = (ReportCallBack)var6[6];
                  String var64 = (String)var6[7];
                  RecoverTypeaf2aa26b.recoverMethod_a_77286e9d(var1, var40, var45, var48, var52, var59, var62, var63, var64);
               }
               break;
            case 62:
               if (var6 != null && var6.length > 0) {
                  String var39 = (String)var6[0];
                  RecoverTypeaf2a9727.recoverMethod_a_c692b35f().recoverMethod_a_e26f8bf1(var1, var39, var4, var5);
               }
               break;
            case 63:
               if (var6 != null && var6.length > 1) {
                  String var38 = (String)var6[0];
                  String var44 = (String)var6[1];
                  return RecoverTypeaf2a9727.recoverMethod_a_c692b35f().recoverMethod_a_1785417b(var1, var38, var4, var5, var44);
               }
               break;
            case 66:
               if (var6 == null || var6.length <= 0) {
                  break;
               }

               boolean var37 = false;
               if (JConstants.SDK_VERSION_INT < 250 && var6[0] instanceof Boolean) {
                  var37 = (Boolean)var6[0];
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "old change fore:" + var37);
                  this.recoverMethod_a_304fb7e2(var1, var37, -1);
               } else {
                  if (JConstants.SDK_VERSION_INT < 250 || !(var6[0] instanceof Integer)) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "unkown change fore");
                     return null;
                  }

                  int var42 = (Integer)var6[0];
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "new change fore:" + var42);
                  if (var42 != 0) {
                     var37 = true;
                  }

                  this.recoverMethod_a_304fb7e2(var1, var37, var42);
               }

               boolean var43 = JConstants.isCallInit.get();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "changeForegroudStat isCallInit=" + var43 + "foreground=" + var37);
               if (var43) {
                  if (var37) {
                     RecoverType5a681cd.recoverMethod_a_304fbba3(var1, true, 0L);
                     Bundle var47 = new Bundle();
                     var47.putInt("scence", 1);
                     recoverMethod_a_e26f8bed(var1, "JCore", "notification_state", var47);
                  } else if (RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_d_9b79()) {
                     RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, null);
                     RecoverTypeaf2aa26b.recoverMethod_a_4fa28b99(var1, var2, null);
                  }
               }
               break;
            case 67:
            case 68:
            case 91:
            case 94:
               return RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_a_54cf32c4(var3);
            case 70:
               RecoverType5a6858e.recoverMethod_a_4877eda2().recoverMethod_a_f0554df3(var1);
               break;
            case 71:
               RecoverType5a6858e.recoverMethod_a_4877eda2().recoverMethod_b_f0554df3(var1);
               break;
            case 72:
               if (var6 != null && var6.length == 4 && var6[0] instanceof String && var6[1] instanceof Integer && var6[2] instanceof Integer) {
                  recoverMethod_a_d0b52086(var1, (String)var6[0], (Integer)var6[1], (Integer)var6[2], (Integer)var6[3]);
               }
               break;
            case 73:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Boolean) {
                  Bundle var36 = new Bundle();
                  var36.putBoolean("enable", (Boolean)var6[0]);
                  recoverMethod_a_e26f8bed(var1, "JCore", "set_wake_enable", var36);
               }
            case 74:
               if (var5 != null) {
                  RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, "waked", var5);
               }
               break;
            case 75:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Runnable) {
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("MAJOR_TASK", (Runnable)var6[0]);
               }
               break;
            case 76:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Runnable) {
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", (Runnable)var6[0]);
               }
               break;
            case 77:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Runnable) {
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("NORMAL_TASK", (Runnable)var6[0]);
               }
               break;
            case 78:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Runnable) {
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", (Runnable)var6[0]);
               }
               break;
            case 79:
               if (var6 != null && var6.length > 1 && var6[0] instanceof String && var6[1] instanceof String) {
                  return RecoverTypeaf2b07c9.recoverMethod_b_e925fa0d((String)var6[0], (String)var6[1]);
               }
               break;
            case 80:
               recoverMethod_e_d19840b7(var1, var2, var5);
               break;
            case 81:
               recoverMethod_d_d19840b7(var1, var2, var5);
               break;
            case 82:
               return false;
            case 83:
               if (var6 != null && var6.length > 1 && var6[0] instanceof Runnable && var6[1] instanceof Integer) {
                  RecoverTypeaf2b0403.recoverMethod_a_63b8aba6("DELAY_TASK", (Runnable)var6[0], (Integer)var6[1]);
               }
               break;
            case 84:
               return "";
            case 85:
               return "";
            case 86:
               return RecoverTypeaf2a9724.recoverMethod_a_f23f0d9f(var1);
            case 88:
               if (var6 != null && var6.length > 0 && JConstants.SDK_VERSION_INT > 284) {
                  RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var1, 20002, 0, (String)var6[0]);
               }
               break;
            case 89:
            case 90:
               this.recoverMethod_c_d19840b7(var1, var4, var5);
               return null;
            case 92:
               return RecoverType5a681cd.recoverField_b;
            case 93:
               if (var6 != null && var6.length >= 2 && var6[0] instanceof Thread && var6[1] instanceof Throwable) {
                  Thread var51 = (Thread)var6[0];
                  Throwable var58 = (Throwable)var6[1];
                  RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_a_77d15a4f(var51, var58);
               }
               break;
            case 95:
               return RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_o_f0554df7(var1);
            case 96:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Boolean) {
                  boolean var50 = (Boolean)var6[0];
                  RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreHelper", "saveAuth：" + var50);
                  RecoverTypeaf2b07c2.recoverMethod_a_25decb5(var50, true);
                  recoverMethod_a_1a552341(var1, var50);
               }
               break;
            case 97:
               ArrayList var13 = new ArrayList();
               RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, "get_installed_pkg_list", var13);
               return var13;
            case 98:
               return RecoverTypeaf2b07c2.recoverMethod_a_9b79();
            case 99:
               return recoverMethod_a_9daef18f(var1, var6);
            case 100:
               return null;
            case 101:
               return RecoverTypeaf2b07c5.recoverMethod_a_16ce4761(var1);
            case 102:
               return RecoverTypeaf2b07c8.recoverField_a;
            case 103:
               Object var14 = null;
               if (null != var6 && var6.length > 0) {
                  var14 = var6[0];
               }

               return "";
            case 105:
               recoverMethod_a_e26f8bed(var1, "JCore", "open_log", var5);
               break;
            case 106:
               try {
                  if (null == var5) {
                     return null;
                  }

                  var5.putString("save_exception_data", var4);
                  recoverMethod_a_e26f8bed(var1, "JCore", "save_exception_data", var5);
               } catch (Throwable var22) {
               }
               break;
            case 107:
               return new RecoverTypeaf2ac7f2().recoverMethod_c_e501eb0d(var1);
            case 108:
               if (null != var5) {
                  recoverMethod_a_d19840b7(var1, "a7", var5);
               }
               break;
            case 109:
               if (null != var6 && var6.length > 0 && var6[0] instanceof Boolean) {
                  RecoverTypeaf2b07c2.recoverMethod_a_1a552341(var1, (Boolean)var6[0]);
               }
               break;
            case 110:
               if (!RecoverTypeaf2b07c2.recoverMethod_b_f0554df7(var1)) {
                  RecoverType5a681cd.recoverMethod_c_f0554df3(var1);
               }
            case 115:
               if (null != var6 && var6.length > 0 && var6[0] instanceof Boolean) {
                  RecoverTypeaf2b07c2.recoverMethod_c_1a552341(var1, (Boolean)var6[0]);
               }
               break;
            case 111:
               try {
                  Bundle var57 = RecoverTypeaf2aa629.recoverMethod_a_c694849d().recoverMethod_a_99967dbb(var1, var2, var5);
                  if (var57 != null && !var57.isEmpty()) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "start send CMD_EVENT_SET_DATA_CONTROL");
                     recoverMethod_a_d19840b7(var1, "a7", var57);
                  }
               } catch (Throwable var21) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreHelper", "CMD_EVENT_SET_COLLECT_CONFIG throwable:" + var21);
               }
               break;
            case 112:
               RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var1, 35, 0, "start to get");
               break;
            case 116:
               int var11 = var5.getInt("configID");
               boolean var12 = false;
               if (var5.containsKey("isFromJCore")) {
                  var12 = var5.getBoolean("isFromJCore");
               }

               return RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_b_256326a(var11, var12);
            case 117:
               recoverMethod_a_d19840b7(var1, "a10", var5);
               break;
            case 118:
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new Runnable() {
                  @Override
                  public void run() {
                     JDynamicLoadBase.getInstance().deletePlugin(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), 1);
                  }
               });
               break;
            case 119:
               if (null != var6 && var6.length > 0 && var6[0] instanceof Boolean) {
                  RecoverTypeaf2b07c2.recoverMethod_g_1a552341(var1, (Boolean)var6[0]);
               }
               break;
            case 120:
               if (null != var6 && var6.length > 0 && var6[0] instanceof Boolean) {
                  RecoverTypeaf2b07c2.recoverMethod_b_1a552341(var1, (Boolean)var6[0]);
               }
               break;
            case 121:
               return RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(var1);
            case 122:
               RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_79e5e33f(var5);
               break;
            case 123:
               return RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_f0554df7(var1);
            case 124:
               RecoverTypeaf2a9725.recoverMethod_a_c692b321();
               return RecoverTypeaf2a9725.recoverMethod_c_9b79();
            case 125:
               return RecoverTypeaf2b07c2.recoverMethod_b_f0554df7(var1);
            case 126:
               return true;
            case 127:
               RecoverTypeaf2a9725.recoverMethod_a_c692b321();
               return RecoverTypeaf2a9725.recoverMethod_e_9b79();
            case 128:
               this.recoverMethod_b_5ffc00fd(var1, var4);
               break;
            case 129:
               recoverMethod_a_d19840b7(var1, "tcp_a26", var5);
               break;
            case 130:
               if (null != var5) {
                  boolean var56 = var5.getBoolean("enable", false);
                  boolean var61 = var5.getBoolean("upload", false);
                  RecoverType5a690d3.recoverMethod_a_1a552341(var1, var56);
                  recoverMethod_a_d19840b7(var1, "a13", var5);
               }
               break;
            case 131:
               return RecoverTypeaf2a9ae4.recoverMethod_a_e501eb0d(var1);
            case 132:
               recoverMethod_a_d19840b7(var1, "tcp_a28", var5);
               break;
            case 133:
               if (var6 != null && var6.length > 0) {
                  JSONObject var55 = new JSONObject();
                  var55.put("data", var6[0]);
                  RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), var55, "xlog");
               }
               break;
            case 134:
               if (var5 != null) {
                  byte[] var54 = var5.getByteArray("b");
                  String var60 = var5.getString("m");
                  String var17 = var5.getString("u");
                  String var18 = var5.getString("h");
                  Map var19 = null;
                  if (!TextUtils.isEmpty(var18)) {
                     var19 = RecoverTypeaf2b07d5.recoverMethod_a_b00aa7de(new JSONObject(var18));
                  }

                  return RecoverTypeaf2af8c0.recoverMethod_a_88a363d0(var60, var17, var19, var54);
               }

               return null;
            case 135:
               if (var6 != null && var6.length > 0 && var6[0] instanceof String) {
                  String var53 = (String)var6[0];
                  if (TextUtils.isEmpty(var53)) {
                     return null;
                  }

                  try {
                     JSONObject var16 = new JSONObject(var53);
                     RecoverType5a6aed9.recoverMethod_a_3c4029ab(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), var16);
                  } catch (Throwable var20) {
                  }
               }
               break;
            case 136:
               if (var6 != null && var6.length > 0 && var6[0] instanceof Integer) {
                  int var15 = (Integer)var6[0];
                  RecoverType5a6aed9.recoverMethod_a_1a54e370(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), 86);
               }
         }
      } catch (Throwable var24) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreHelper", "onEvent:" + var24);
      }

      return null;
   }

   private void recoverMethod_c_d19840b7(Context var1, String var2, Bundle var3) {
      try {
         JSONObject var4 = new JSONObject();
         var4.put("bundle", RecoverTypeaf2b07d5.recoverMethod_a_fcab3ead(var3));
         var4.put("action", var2);
         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var1, 2010, 0, var4.toString());
      } catch (Throwable var5) {
      }
   }

   private static PackageInfo recoverMethod_a_9daef18f(Context var0, Object... var1) {
      if (var0 != null && var1 != null) {
         int var2 = 0;
         String var3 = "";
         if (var1.length == 1) {
            if (var1[0] instanceof String) {
               var3 = (String)var1[0];
            } else if (var1[0] instanceof Integer) {
               var2 = (Integer)var1[0];
            }
         } else if (var1.length > 1 && var1[0] instanceof String && var1[1] instanceof Integer) {
            var3 = (String)var1[0];
            var2 = (Integer)var1[1];
         }

         return !TextUtils.isEmpty(var3)
            ? RecoverTypeaf2b07cf.recoverMethod_a_afbf9147(var0, var3, var2)
            : RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, var2);
      } else {
         return null;
      }
   }

   public static void recoverMethod_a_d0b52086(Context var0, String var1, int var2, int var3, int var4) {
      Bundle var5 = new Bundle();
      var5.putString("name", var1);
      var5.putInt("custom", var2);
      var5.putInt("dynamic", var3);
      var5.putInt("sdk_v", var4);
      RecoverType5a6aed9.recoverMethod_a_b00c10b9(var0, "set_sdktype_info", var5);
   }

   private static void recoverMethod_a_69c77401(Context var0, Bundle var1) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "setSDKConfigs");

            try {
               int var2 = var1.getInt("heartbeat_interval", JConstants.DEFAULT_HEARTBEAT_INTERVAL);
               if (var2 >= RecoverTypeaf2ab531.recoverField_b && var2 <= RecoverTypeaf2ab531.recoverField_a) {
                  JConstants.DEFAULT_HEARTBEAT_INTERVAL = var2;
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "set heartbeat interval=" + var2);
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
                     "JCoreHelper",
                     "Invalid heartbeat interval: "
                        + var2
                        + ". Valid range is ["
                        + RecoverTypeaf2ab531.recoverField_b
                        + ", "
                        + RecoverTypeaf2ab531.recoverField_a
                        + "] seconds"
                  );
               }
            } catch (Throwable var8) {
            }

            int var10 = 86400;

            try {
               int var3 = var1.getInt("alarm_delay", JConstants.DEFAULT_ALARM_DELAY);
               if (var3 > 86400) {
                  var3 = 86400;
               }

               if (var3 < JConstants.DEFAULT_HEARTBEAT_INTERVAL + 5) {
                  var3 = JConstants.DEFAULT_HEARTBEAT_INTERVAL + 5;
               }

               JConstants.DEFAULT_ALARM_DELAY = var3;
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "set alarm delay=" + var3);
            } catch (Throwable var7) {
            }

            byte var11 = (byte)var1.getInt("tcp_algorithm", -1);
            if (var11 >= 0) {
               JConstants.tcpAlgorithm = var11;
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "set tcp algorithm=" + var11);
            }

            if (var1.containsKey("tcp_report")) {
               RecoverTypeaf2aa26b.recoverField_d = var1.getBoolean("tcp_report");
            }

            if (var1.containsKey("plugin_report_switch")) {
               boolean var4 = var1.getBoolean("plugin_report_switch");
               String var5 = "cn.jiguang.prefs";
               SharedPreferences var6 = var0.getSharedPreferences(var5, 0);
               var6.edit().putBoolean("plugin_report_switch", var4).apply();
            }

            if (var1.containsKey("plugin_multi_switch")) {
               boolean var12 = var1.getBoolean("plugin_multi_switch");
               String var14 = "cn.jiguang.prefs";
               SharedPreferences var15 = var0.getSharedPreferences(var14, 0);
               var15.edit().putBoolean("plugin_multi_switch", var12).apply();
            }

            int var13 = var1.getInt("ipv_config", -1);
            RecoverTypeaf2ab8ee.recoverMethod_a_13462e(var13);
            if (var13 == 2 || var13 == 3) {
               RecoverTypeaf2aa26b.recoverField_i = false;
            } else if (var13 == 0 || var13 == 1) {
               RecoverTypeaf2aa26b.recoverField_i = true;
            }
         } catch (Throwable var9) {
         }
      }
   }

   private void recoverMethod_b_5ffc00fd(Context var1, String var2) {
      try {
         Bundle var3 = new Bundle();
         var3.putString("set_country", var2);
         var3.putString("sdk_type", "JCore");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "setCountry action:" + var2 + " countrybundle=" + var3);
         RecoverType5a681cd.recoverMethod_a_d19840b7(var1, "tcp_set_country", var3);
      } catch (Throwable var4) {
      }
   }

   private static void recoverMethod_c_5ffc00fd(Context var0, String var1) {
      Bundle var2 = new Bundle();
      var2.putString("sdk_type", var1);
      RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "tcp_a8", var2);
   }

   private static void recoverMethod_d_5ffc00fd(Context var0, String var1) {
      Bundle var2 = new Bundle();
      var2.putString("sdk_type", var1);
      RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "tcp_a9", var2);
   }

   public static void recoverMethod_a_d19840b7(Context var0, String var1, Bundle var2) {
      RecoverType5a681cd.recoverMethod_a_d19840b7(var0, var1, var2);
   }

   public void recoverMethod_b_d19840b7(Context var1, String var2, Bundle var3) {
      RecoverType5a681cd.recoverMethod_b_d19840b7(var1, var2, var3);
   }

   @Override
   public Bundle directHandle(Context var1, String var2, String var3, Bundle var4) {
      return RecoverTypeaf2a9727.recoverMethod_a_c692b35f().recoverMethod_b_f41a771(var1, var2, var3, var4);
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      RecoverTypeaf2a9723.recoverMethod_a_5ffc00fd(var0, var1);
   }

   public static void recoverMethod_a_e26f8bed(Context var0, String var1, String var2, Bundle var3) {
      if (var3 == null) {
         var3 = new Bundle();
      }

      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreHelper", "runActionWithService action:" + var2);
      var3.putString("sdk_type", var1);
      var3.putString("internal_action", var2);
      RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "a3", var3);
   }

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      try {
         Bundle var2 = new Bundle();
         var2.putBoolean("is_auth", var1);
         recoverMethod_a_d19840b7(var0, "set_auth", var2);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreHelper", "sendSaveAuth error:" + var3);
      }
   }

   public static void recoverMethod_a_bb994ac1(Context var0, String var1, Bundle var2, String var3) {
      if (var2 != null) {
         var2.putString("sdk_type", var1);
         RecoverType5a681cd.recoverMethod_a_d19840b7(var0, var3, var2);
      }
   }

   private static void recoverMethod_a_473e1ef3(String var0, String var1, boolean var2, int var3, String var4, Throwable var5) {
      RecoverTypeaf2a9ae6.recoverMethod_a_473e1ef3(var0, var1, var2, var3, var4, var5);
   }

   private static void recoverMethod_d_d19840b7(Context var0, String var1, Bundle var2) {
      try {
         if (var2 != null) {
            boolean var3 = var2.getBoolean("sdk_banned");
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "[handleSdkBannedStatusChanged] sdk banned: " + var3);
            if (var3) {
               recoverMethod_c_5ffc00fd(var0, var1);
               RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(8000);
            } else {
               recoverMethod_d_5ffc00fd(var0, var1);
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreHelper", "network change handler error, " + var4.getMessage());
      }
   }

   private static void recoverMethod_e_d19840b7(Context var0, String var1, Bundle var2) {
      try {
         if (var2 != null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "[handleConnectionChanged] connected: " + var2.getBoolean("connected"));
         }

         RecoverTypeaf2b07d6.recoverMethod_a_9b75();
         Intent var3 = new Intent();
         var3.setAction("android.net.conn.CONNECTIVITY_CHANGE");
         if (null != var0) {
            ConnectivityManager var4 = (ConnectivityManager)var0.getSystemService("connectivity");
            if (null != var4) {
               NetworkInfo var5 = var4.getActiveNetworkInfo();
               if (null != var5) {
                  var3.putExtra("networkInfo", var5);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "networkInfo, available: " + var5.isAvailable() + ", type: " + var5.getType());
               }
            }
         }

         ActionManager.getInstance().handleMessage(var0, var1, var3);
         boolean var7 = JConstants.isCallInit.get();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "networkInfo isCallInit: " + var7);
         if (!var7) {
            return;
         }

         RecoverType5a6aed9.recoverMethod_a_b00c10b9(var0, "action_net_changed", var2);
         RecoverType5a681cd.recoverMethod_a_d19840b7(var0, "tcp_a15", var2);
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreHelper", "network change handler error, " + var6.getMessage());
      }
   }

   private void recoverMethod_a_c4d2ecb0(Context var1, Intent var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "[handleReceiverIntent]:" + var2.getAction());
      RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, "get_receiver", var2);
      String var3 = var2.getAction();
      if (var3 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreHelper", "onReceive empty action");
      } else {
         String var4 = "android.os.action.DEVICE_IDLE_MODE_CHANGED";
         String var5 = "android.os.action.POWER_SAVE_MODE_CHANGED";
         if (!var3.equals("android.intent.action.USER_PRESENT")) {
            if (var3.equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
               NetworkInfo var6 = (NetworkInfo)var2.getParcelableExtra("networkInfo");
               if (null == var6) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreHelper", "Not found networkInfo");
                  return;
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "Connection state changed to - " + var6.toString());
               if (2 == var6.getType() || 3 == var6.getType()) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "MMS or SUPL network state change, to do nothing!");
                  return;
               }

               boolean var7 = var2.getBooleanExtra("noConnectivity", false);
               Bundle var8 = var2.getExtras();
               if (var8 == null) {
                  var8 = new Bundle();
               }

               if (var7) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "No any network is connected");
                  var8.putBoolean("connected", false);
               } else {
                  try {
                     if (State.CONNECTED == var6.getState()) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "Network is connected.");
                        var8.putBoolean("connected", true);
                     } else if (State.DISCONNECTED == var6.getState()) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "Network is disconnected.");
                        var8.putBoolean("connected", false);
                     } else {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "other network state - " + var6.getState() + ". Do nothing.");
                     }
                  } catch (Throwable var14) {
                     boolean var10 = RecoverTypeaf2b07ce.recoverMethod_a_9b79();
                     var8.putBoolean("connected", var10);
                  }
               }

               RecoverType5a681cd.recoverMethod_a_d19840b7(var1, "tcp_a15", var8);
            } else if (var3.equals(var4) || var3.equals(var5)) {
               PowerManager var18 = (PowerManager)var1.getSystemService("power");
               boolean var22 = true;
               if (var18 != null) {
                  try {
                     Class var24 = Class.forName("android.os.PowerManager");
                     if (var24 != null) {
                        if (var3.equals(var4)) {
                           Method var25 = var24.getDeclaredMethod("isDeviceIdleMode");
                           if (var25 != null) {
                              var22 = (Boolean)var25.invoke(var18);
                           }
                        } else if (var3.equals(var5)) {
                           Method var26 = var24.getDeclaredMethod("isPowerSaveMode");
                           if (var26 != null) {
                              var22 = (Boolean)var26.invoke(var18);
                           }
                        }

                        if (!var22) {
                           RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "doze or powersave mode exit.");
                           RecoverType5a681cd.recoverMethod_a_304fbba3(var1, true, 0L);
                        }
                     }
                  } catch (Throwable var13) {
                     RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreHelper", "handle DEVICE_IDLE_MODE_CHANGED or POWER_SAVE_MODE_CHANGED fail:" + var13);
                  }
               }
            } else if (var3.equals("noti_open_proxy")) {
               if (var2.getBooleanExtra("debug_notification", false)) {
                  String var15 = var2.getStringExtra("toastText");
                  if (!TextUtils.isEmpty(var15)) {
                     Toast var19 = Toast.makeText(var1, var15, 0);

                     try {
                        View var23 = var19.getView();
                        if (var23 instanceof LinearLayout) {
                           LinearLayout var9 = (LinearLayout)var23;
                           View var27 = var9.getChildAt(0);
                           if (var27 instanceof TextView) {
                              TextView var11 = (TextView)var27;
                              if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var15)) {
                                 var11.setText(var15);
                              }

                              var11.setTextSize(13.0F);
                           }
                        }
                     } catch (Exception var12) {
                     }

                     var19.show();
                     return;
                  }
               }
            } else if (RecoverTypeaf2ae5f9.recoverField_e.equals(var3)) {
               Bundle var16 = var2.getExtras();
               if (var16 != null) {
                  String var20 = var16.getString("data");
                  RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_b_aad3b1ff(var20);
               }
            } else if (RecoverTypeaf2a972d.recoverField_a.equals(var3)) {
               Bundle var17 = var2.getExtras();
               if (var17 != null) {
                  String var21 = var17.getString("data");
                  RecoverTypeaf2a972d.recoverMethod_a_5ffc00fd(var1, var21);
               }
            }
         }
      }
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      Pair var1 = RecoverTypeaf2a9723.recoverMethod_d_49a4c97d(var0);
      return var1 != null && !RecoverTypeaf2b07d5.recoverMethod_a_aad3b203((String)var1.first) ? (String)var1.first : "";
   }

   public static void recoverMethod_a_79e5e33f(Bundle var0) {
   }

   private static Bundle recoverMethod_a_9c9f3728(Context var0, int var1, Bundle var2) {
      try {
         Bundle var3 = new Bundle();
         switch (var1) {
            case 4096:
               if (var2 != null) {
                  String var4 = RecoverTypeaf2a9723.recoverMethod_a_7fee7477(var2, "arg1");
                  recoverMethod_a_5ffc00fd(var0, var4);
               }

               return var3;
            case 4098:
               JCoreManager.init(var0);
               return var3;
            case 36864:
               recoverMethod_a_79e5e33f(var2);
               return null;
            case 36865:
               if (JCoreManager.isInternal()) {
                  JConstants.testAndroidQ();
               }

               return null;
            default:
               return null;
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreHelper", "si e:" + var5);
         return null;
      }
   }

   public void recoverMethod_a_304fb7e2(Context var1, boolean var2, int var3) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreHelper", "changeForegroudStat:" + var2 + ", userActiveType:" + var3);
      RecoverTypeaf2aa628.recoverField_b.set(var2);
      Bundle var4 = new Bundle();
      var4.putBoolean("foreground", var2);
      var4.putInt("user_type", var3);
      RecoverType5a681cd.recoverMethod_a_d19840b7(var1, "a4", var4);
   }

   @Override
   public Object onCommonMethod(Context var1, String var2, Object var3) {
      return RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, var2, var3);
   }

   static {
      recoverField_a.add(18);
      recoverField_a.add(13);
      recoverField_a.add(52);
      recoverField_a.add(98);
      recoverField_a.add(7);
      recoverField_a.add(111);
      recoverField_a.add(99);
      recoverField_a.add(104);
      recoverField_a.add(119);
      recoverField_a.add(120);
      recoverField_a.add(121);
      recoverField_a.add(123);
      recoverField_a.add(100);
      recoverField_a.add(76);
      recoverField_a.add(42);
      recoverField_a.add(89);
      recoverField_a.add(90);
      recoverField_a.add(92);
      recoverField_a.add(95);
      recoverField_a.add(102);
      recoverField_a.add(108);
      recoverField_a.add(109);
      recoverField_a.add(111);
      recoverField_a.add(116);
      recoverField_a.add(122);
      recoverField_a.add(73);
      recoverField_a.add(128);
      recoverField_a.add(64);
      recoverField_a.add(25);
      recoverField_a.add(66);
      recoverField_a.add(75);
      recoverField_a.add(76);
      recoverField_a.add(77);
      recoverField_a.add(78);
      recoverField_a.add(83);
      recoverField_a.add(105);
      recoverField_a.add(130);
      recoverField_a.add(131);
   }

   class Member36297a0d extends RecoverTypeaf2b0401 {
      private Context recoverField_b;
      private Intent recoverField_c;
      private String recoverField_d;

      public Member36297a0d(Context var2, Intent var3, String var4) {
         this.recoverField_b = var2;
         this.recoverField_d = var4;
         this.recoverField_c = var3;
         this.recoverField_h = "JCoreHelper#PushReceiverAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            ActionManager.getInstance().handleMessage(this.recoverField_b, this.recoverField_d, this.recoverField_c);
            RecoverType5a68950.this.recoverMethod_a_c4d2ecb0(this.recoverField_b, this.recoverField_c);
         } catch (Throwable var2) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreHelper", "[PushReceiverAction failed:]" + var2.getMessage());
         }
      }
   }
}
