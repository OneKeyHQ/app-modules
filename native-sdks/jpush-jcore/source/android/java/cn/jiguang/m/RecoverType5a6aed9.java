package cn.jiguang.m;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.aa.RecoverTypeaf2a6dd8;
import cn.jiguang.ac.RecoverTypeaf2a755c;
import cn.jiguang.ad.RecoverTypeaf2a791b;
import cn.jiguang.ae.RecoverTypeaf2a7cdd;
import cn.jiguang.af.RecoverTypeaf2a809d;
import cn.jiguang.ag.RecoverTypeaf2a845e;
import cn.jiguang.ah.RecoverTypeaf2a881f;
import cn.jiguang.ah.RecoverTypeaf2a8820;
import cn.jiguang.ah.RecoverTypeaf2a8822;
import cn.jiguang.ak.RecoverTypeaf2a9363;
import cn.jiguang.ak.RecoverTypeaf2a9367;
import cn.jiguang.ak.RecoverTypeaf2a9368;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab533;
import cn.jiguang.bg.RecoverTypeaf2af8bf;
import cn.jiguang.bg.RecoverTypeaf2af8c0;
import cn.jiguang.bg.RecoverTypeaf2af8c1;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.j.RecoverType5a6a397;
import cn.jiguang.j.RecoverType5a6a398;
import cn.jiguang.k.RecoverType5a6a758;
import cn.jiguang.k.RecoverType5a6a759;
import cn.jiguang.k.RecoverType5a6a75a;
import cn.jiguang.k.RecoverType5a6a75b;
import cn.jiguang.k.RecoverType5a6a75c;
import cn.jiguang.k.RecoverType5a6a75d;
import cn.jiguang.l.RecoverType5a6ab18;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import org.json.JSONObject;

public class RecoverType5a6aed9 {
   private static long recoverField_b;
   private static Boolean recoverField_c;
   public static String recoverField_a = ".permission.JPUSH_MESSAGE";

   private static LinkedList recoverMethod_a_3e0e9f8f(String var0) {
      try {
         LinkedList var1 = new LinkedList();
         if (var0 != null && var0.length() > 0) {
            for (String var5 : var0.split(",")) {
               if (!TextUtils.isEmpty(var5)) {
                  var1.add(var5);
               }
            }
         }

         return var1;
      } catch (Throwable var6) {
         return null;
      }
   }

   public static Object recoverMethod_a_b00c10b9(final Context var0, String var1, final Object var2) {
      try {
         if (var0 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonActionHelper", "context is null,give up continue");
            return null;
         }

         if (TextUtils.isEmpty(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonActionHelper", "action is null,give up continue");
            return null;
         }

         if (TextUtils.isEmpty(RecoverType5a69fd7.Member367e0ad4.recoverField_b)) {
            RecoverType5a69fd7.Member367e0ad4.recoverField_b = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var0);
         }

         switch (var1) {
            case "periodtask":
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "action period task..");
               RecoverType5a6a398.recoverMethod_b_487b90d8().recoverMethod_a_b13261ab(var0, new RecoverType5a6a397() {
                  @Override
                  public void recoverMethod_a_9b75() {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "data config is prepare finished");
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_9b75();
                     RecoverType5a6aed9.recoverMethod_b_127ac70f(var0, var2);
                  }
               });
               RecoverTypeaf2a845e.recoverMethod_a_c6906d08().recoverMethod_b_f0554df3(var0);
               break;
            case "get_loc_info":
               return RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_b_3df933f7(var0);
            case "getwakeenable":
               return RecoverTypeaf2a881f.recoverMethod_c_f23f0d9f(var0);
            case "filter_pkg_list":
               return RecoverTypeaf2a881f.recoverMethod_a_328f7003(var0, var2);
            case "deviceinfo":
               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1000)) {
                  return RecoverType5a6a758.recoverMethod_a_487c0518().recoverMethod_f_f23f0d9f(var0);
               }

               return null;
            case "on_register":
               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1000)) {
                  RecoverType5a6a758.recoverMethod_a_487c0518()
                     .recoverMethod_b_1a54e370(var0, RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_b_134621(1000));
               }

               RecoverTypeaf2a845e.recoverMethod_a_c6906d08().recoverMethod_a_f0554df3(var0);
               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1800)) {
                  RecoverTypeaf2a791b.recoverMethod_a_1a54e370(var0, 1);
               }
               break;
            case "on_logged":
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "action on logged...");
               recoverMethod_a_f0554df3(var0);
               break;
            case "tcp_a1":
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "action service  create..");
               RecoverType5a6a398.recoverMethod_b_487b90d8().recoverMethod_a_1a54e370(var0, 5);
               break;
            case "get_imei":
               RecoverType5a6a398.recoverMethod_b_487b90d8().recoverMethod_a_1a54e370(var0, 1);
               return "";
            case "get_installed_pkg_list":
            case "uid_switch":
         }

         Bundle var11 = var2 instanceof Bundle ? (Bundle)var2 : null;
         if (var11 != null) {
            switch (var1) {
               case "init_local_ctrl":
               case "lbsforenry":
               default:
                  break;
               case "lbsenable":
                  if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1500)) {
                     boolean var16 = var11.getBoolean("enable");
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "[setLBSEnable] " + var16);
                     RecoverType5a69fd6.recoverMethod_a_9f84d177(var0, "JLocation", var16);
                  }
                  break;
               case "set_ctrl_url":
                  RecoverTypeaf2a9363.recoverMethod_a_53d4850d(recoverMethod_a_3e0e9f8f(var11.getString("test_wake_controll_url")));
                  RecoverType5a6a398.recoverMethod_a_53d4850d(recoverMethod_a_3e0e9f8f(var11.getString("test_all_cfg_url")));
                  RecoverTypeaf2a755c.recoverMethod_a_53d4850d(recoverMethod_a_3e0e9f8f(var11.getString("test_lv2_url")));
                  RecoverTypeaf2a845e.recoverField_a = var11.getString("test_uaid_url");
                  RecoverType5a6a75b.recoverField_a = var11.getString("test_ipv6_url");
                  RecoverType5a69853.recoverMethod_a_79e5e33f(var11);
                  break;
               case "cmd":
                  recoverMethod_a_69c77401(var0, var11);
                  break;
               case "waked":
                  int var15 = var11.getInt("type", -1);
                  String var21 = var11.getString("from_package");
                  if (TextUtils.isEmpty(var21)) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonActionHelper", "[waked]empty packageName waked from ");
                  } else if (var15 == -1) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonActionHelper", "[waked]wrong waked type");
                  } else {
                     RecoverTypeaf2a9368.recoverMethod_a_cf277f22(var0, var11, var15);
                  }
                  break;
               case "set_sdktype_info":
                  RecoverTypeaf2a809d var6 = new RecoverTypeaf2a809d();
                  var6.recoverMethod_a_69c77401(var0, var11);
                  break;
               case "notification_state":
                  if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1800)) {
                     int var20 = var11.getInt("scence");
                     RecoverTypeaf2a791b.recoverMethod_a_1a54e370(var0, var20);
                  }
                  break;
               case "set_wake_enable":
                  boolean var19 = var11.getBoolean("enable");
                  RecoverType5a69fd6.recoverMethod_a_1a552341(var0, var19);
                  break;
               case "open_log":
                  if (var11 != null) {
                     String var18 = var11.getString("aes");
                     String var8 = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var18, RecoverType5a69fd7.Member367e0ad4.recoverField_j);
                     JSONObject var9 = new JSONObject(var8);
                     RecoverType5a68d10.recoverMethod_a_4bc5be89(var0, var9, true);
                  }
                  break;
               case "foreground_state_change":
                  boolean var7 = var11.getBoolean("foreground");
                  RecoverTypeaf2a9367.recoverMethod_a_c6923f1f().recoverMethod_a_1a552341(var0, var7);
                  break;
               case "GET_ANDROID_ID":
                  if (var11 != null && var11.getInt("s_a_id") == 2000) {
                     RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_c_5e799b7f(var0, true);
                  }
                  break;
               case "action_net_changed":
                  if (var11 != null && var11.getBoolean("connected")) {
                     RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_a_5e799b7f(var0, true);
                     RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_b_5e799b7f(var0, true);
                  }
            }
         }

         Intent var13 = var2 instanceof Intent ? (Intent)var2 : null;
         if (var13 != null) {
            switch (var1) {
               case "get_receiver":
                  String var22 = var13.getAction();
                  if (var22 != null) {
                  }
            }
         }
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonActionHelper", "onEvent throwable:" + var10.getMessage());
      }

      return null;
   }

   private static void recoverMethod_b_127ac70f(final Context var0, final Object var1) {
      RecoverType5a69853 var2 = RecoverType5a69853.recoverMethod_a_487a337d();
      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_f_5d527811(2072);
      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_g_5d527811(2072);
      if (var2.recoverMethod_a_134632(2700)) {
         RecoverType5a6a75c.recoverMethod_a_487c0594().recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(2700) + 3000);
      }

      if (var2.recoverMethod_a_134632(1500)) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2082);
         int var3 = RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_b_1385f2(false);
         RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_a_1a54e370(var0, var3);
      }

      if (var2.recoverMethod_a_134632(1800)) {
         RecoverTypeaf2aa628.recoverMethod_a_fa8b67f0(new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               RecoverTypeaf2a791b.recoverMethod_a_1a54e370(var0, RecoverType5a6aed9.recoverMethod_b_5d527804(var1));
            }
         }, var2.recoverMethod_b_134621(1800));
      }

      if (var2.recoverMethod_a_134632(1000)) {
         RecoverType5a6a758.recoverMethod_a_487c0518().recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(1000));
      }

      if (var2.recoverMethod_d_134632(1200)) {
         RecoverType5a6a759.recoverMethod_a_487c0537().recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(1200));
      }

      if (var2.recoverMethod_d_134632(1900)) {
         RecoverType5a6a75a.recoverMethod_a_487c0556().recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(1900));
      }

      RecoverTypeaf2a881f.recoverMethod_a_5ffc00fd(var0, "heartbeat");
      RecoverTypeaf2a8820.recoverMethod_a_3c4029ab(var0, null);
      RecoverTypeaf2a8822.recoverMethod_a_c690e1c4().recoverMethod_c_f0554df3(var0);
      if (var2.recoverMethod_d_134632(2600)) {
         RecoverTypeaf2a7cdd var4 = new RecoverTypeaf2a7cdd();
         var4.recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(2600));
      }

      if (var2.recoverMethod_d_134632(2800)) {
         RecoverType5a6a75b.recoverMethod_a_487c0575().recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(2800));
      }

      if (var2.recoverMethod_d_134632(2900)) {
         RecoverType5a6a75d.recoverMethod_a_487c05b3().recoverMethod_a_1a54e370(var0, var2.recoverMethod_b_134621(2900));
      }
   }

   private static void recoverMethod_a_f0554df3(Context var0) {
      try {
         if (recoverField_c != null) {
            return;
         }

         recoverField_c = true;
      } catch (Throwable var2) {
      }
   }

   private static void recoverMethod_a_69c77401(Context var0, Bundle var1) {
      JSONObject var2 = recoverMethod_a_fcab3ead(var1);
      recoverMethod_a_3c4029ab(var0, var2);
   }

   public static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      if (var1 != null) {
         int var2 = var1.optInt("cmd");
         recoverMethod_a_1a54e370(var0, var2);
         switch (var2) {
            case 4:
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_f_5d527811(2072);
               break;
            case 5:
            case 45:
               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_f_134632(1502)) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2081);
                  RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_a_1385ff(true);
                  RecoverTypeaf2a6dd8.recoverMethod_a_c68db2ce().recoverMethod_a_3c4029ab(var0, var1);
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2004);
               }
            case 9:
            case 44:
            case 54:
            case 74:
            case 79:
            case 81:
            default:
               break;
            case 50:
               RecoverTypeaf2a8820.recoverMethod_a_3c4029ab(var0, var1);
               break;
            case 52:
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_g_5d527811(2072);
               break;
            case 70:
               RecoverTypeaf2a881f.recoverMethod_a_f0554df3(var0);
               break;
            case 82:
               RecoverType5a68d10.recoverMethod_a_4bc5be89(var0, var1, true);
               break;
            case 83:
               RecoverType5a6a75b.recoverMethod_a_487c0575().recoverMethod_a_1385ff(true);
               RecoverType5a6a75b.recoverMethod_a_487c0575().recoverMethod_d_f0554df3(var0);
               break;
            case 86:
               recoverMethod_a_be7fc169(var1);
         }
      }
   }

   public static void recoverMethod_a_be7fc169(JSONObject var0) {
      try {
         JSONObject var1 = var0.getJSONObject("content");
         if (var1 == null) {
            return;
         }

         String var2 = var1.getString("s_c");
         int var3 = var1.optInt("code");
         if (160 == var3) {
            int var4 = var1.optInt("e_n", 0);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "update enable: " + var4);
            RecoverTypeaf2af8c0.recoverMethod_b_1385ff(var4 == 1);
            recoverMethod_b_aad3b1ff(var2);
         } else if (163 == var3) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "update url: " + var2);
            RecoverTypeaf2af8c0.recoverMethod_d_aad3b1ff(RecoverTypeaf2af8c0.recoverMethod_a_e96ea081(var2));
         } else if (159 == var3) {
            int var6 = var1.optInt("e_n", 0);
            RecoverTypeaf2af8c0.recoverMethod_a_1385ff(var6 == 1);
         } else {
            RecoverTypeaf2af8c1.recoverMethod_a_b7d3a948(
               RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d()),
               RecoverType5a68d10.recoverMethod_f_f0554de7(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d()),
               var3,
               var2
            );
         }
      } catch (Throwable var5) {
      }
   }

   private static void recoverMethod_b_aad3b1ff(String var0) {
      String var1 = RecoverTypeaf2af8c0.recoverMethod_a_73cf11cb();
      if (var0 != null && !var0.trim().isEmpty() && (var1 == null || !var0.equalsIgnoreCase(var1))) {
         RecoverTypeaf2af8bf.recoverMethod_b_9b75();
      }
   }

   private static int recoverMethod_b_5d527804(Object var0) {
      byte var1 = 0;
      if (var0 instanceof Bundle) {
         boolean var2 = ((Bundle)var0).getBoolean("login");
         if (var2) {
            var1 = 2;
         }
      }

      return var1;
   }

   private static JSONObject recoverMethod_a_fcab3ead(Bundle var0) {
      try {
         byte[] var1 = var0.getByteArray("RESPONSE_BODY");
         ByteBuffer var2 = ByteBuffer.wrap(var1);
         recoverField_b = var2.getLong();
         short var3 = var2.getShort();
         byte[] var4 = new byte[var3];
         var2.get(var4);
         String var5 = new String(var4, "UTF-8");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonActionHelper", "parseBundle2Json content: " + var5);
         return new JSONObject(var5);
      } catch (Exception var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonActionHelper", "parseBundle2Json exception:" + var6.getMessage());
         return null;
      }
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      long var2 = RecoverType5a6aeda.recoverMethod_a_9b69();
      String var4 = RecoverType5a6ab18.recoverMethod_a_fcba5f30(var0, var1);
      byte[] var5 = RecoverType5a6aeda.recoverMethod_a_25254a04(recoverField_b, var4);
      RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_57af07a2(var0, var2, 25, 1, var5, "JCOMMON");
   }
}
