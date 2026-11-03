package cn.jpush.android.l;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.aa.RecoverType9b4c4fa1;
import cn.jpush.android.ab.RecoverType9b4c5362;
import cn.jpush.android.ab.RecoverType9b4c5363;
import cn.jpush.android.ab.RecoverType9b4c5364;
import cn.jpush.android.ab.RecoverType9b4c5365;
import cn.jpush.android.ac.RecoverType9b4c5725;
import cn.jpush.android.ac.RecoverType9b4c5727;
import cn.jpush.android.ad.RecoverType9b4c5ae4;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aeb;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.api.CallBackParams;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.b.RecoverType2e4cc6a8;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.d.RecoverType2e4cce28;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b45582f;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.helper.RecoverType8b455832;
import cn.jpush.android.local.JPushAction;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.local.JPushResponse;
import cn.jpush.android.local.ProxyActivityAction;
import cn.jpush.android.m.RecoverType2e4ceff0;
import cn.jpush.android.n.RecoverType2e4cf3b2;
import cn.jpush.android.n.RecoverType2e4cf3b4;
import cn.jpush.android.o.RecoverType2e4cf772;
import cn.jpush.android.p.RecoverType2e4cfb34;
import cn.jpush.android.q.RecoverType2e4cfef4;
import cn.jpush.android.s.RecoverType2e4d0676;
import cn.jpush.android.service.JPushMessageReceiver;
import cn.jpush.android.ui.RecoverType9b558516;
import cn.jpush.android.ui.RecoverType9b558517;
import cn.jpush.android.ups.UPSPushHelper;
import cn.jpush.android.v.RecoverType2e4d11b9;
import cn.jpush.android.x.RecoverType2e4d193c;
import cn.jpush.android.x.RecoverType2e4d193e;
import cn.jpush.android.x.RecoverType2e4d193f;
import cn.jpush.android.x.RecoverType2e4d1940;
import cn.jpush.android.z.RecoverType2e4d20bd;
import cn.jpush.android.z.RecoverType2e4d20bf;
import cn.jpush.android.z.RecoverType2e4d20c1;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class RecoverType2e4cec2f extends JPushAction {
   private Boolean recoverField_a;
   private Boolean recoverField_b;
   private Context recoverField_c;
   private RecoverType2e4d0676 recoverField_d = new RecoverType2e4d0676();
   private static boolean recoverField_e;
   private static int recoverField_f = 1000;
   private static int recoverField_g = 1001;
   private static int recoverField_h = 1014;

   private boolean recoverMethod_b_f0554df7(Context var1) {
      if (this.recoverField_a != null) {
         return this.recoverField_a;
      } else if (var1 == null) {
         Logger.recoverMethod_ww_e925fa09("JPushActionImpl", "context is null");
         return false;
      } else {
         this.recoverField_c = var1.getApplicationContext();
         this.recoverField_a = RecoverType9b4c5ae5.recoverMethod_h_f0554df7(var1);
         return this.recoverField_a;
      }
   }

   @Override
   public void onActivityLifeCallback(Activity var1, String var2) {
      try {
         switch (var2) {
            case "started":
               RecoverType9b4c5ae4.recoverMethod_a_f9c5dd07(var1);
               this.recoverField_d.recoverMethod_b_f9c5dd07(var1);
               break;
            case "resumed":
               RecoverType9b4c5ae4.recoverMethod_b_f9c5dd07(var1);
               this.recoverField_d.recoverMethod_c_f9c5dd07(var1);
               break;
            case "paused":
               RecoverType9b4c5ae4.recoverMethod_c_f9c5dd07(var1);
               this.recoverField_d.recoverMethod_d_f9c5dd07(var1);
               break;
            case "stopped":
               RecoverType9b4c5ae4.recoverMethod_d_f9c5dd07(var1);
               this.recoverField_d.recoverMethod_e_f9c5dd07(var1);
               break;
            case "destroyed":
               RecoverType9b4c5ae4.recoverMethod_e_f9c5dd07(var1);
               this.recoverField_d.recoverMethod_f_f9c5dd07(var1);
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("JPushActionImpl", "onActivityLifeCallback failed. " + var5.getMessage());
      }
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      Logger.recoverMethod_d_e925fa09("JPushActionImpl", "serviceInit...");

      try {
         if (this.recoverField_b != null) {
            return;
         }

         this.recoverField_b = true;
         RecoverType9b4c5af2.recoverMethod_b_6263c3eb(var1);
         RecoverType2e4d193c.recoverMethod_a_30483d64(var1, 0, true);
         RecoverType9b4c5aeb.recoverMethod_a_d8c46bdb().recoverMethod_a_f0554df3(var1);
         this.recoverMethod_d_f0554df3(var1);
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "google:true");
      } catch (Throwable var3) {
      }
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      if (!recoverField_e) {
         recoverField_e = true;
         if (JPushConstants.SUPPORT_DY) {
         }

         int var1 = RecoverType2e4cec2f.Membercec2a12c.recoverMethod_a_9b68();
         int var2 = RecoverType2e4cec2f.Membercec2a12c.recoverMethod_b_9b68();
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "custom:" + var1 + ",dynamic:" + var2);
         RecoverType2e4d193f.recoverMethod_a_cd395451().recoverMethod_a_2b7802e6(var0, null);
         JCoreHelper.reportSdkType(var0, "push", var1, var2, RecoverType2e4ceff0.recoverField_a);
         if (JPushConstants.canGetLbsInBackGround(var0)) {
            RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_b_9b75();
         }

         RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_a_f0554df3(var0);
         RecoverType9b4c5362.recoverMethod_a_f0554df3(var0);
      }
   }

   @Override
   public Object getDataByCmd(Context var1, int var2) {
      if (30003 == var2) {
         return 2;
      } else if (30004 == var2) {
         int var4 = RecoverType2e4cec2f.Membercec2a12c.recoverMethod_a_9b68();
         return var4;
      } else if (30005 == var2) {
         int var3 = RecoverType2e4cec2f.Membercec2a12c.recoverMethod_b_9b68();
         return var3;
      } else {
         return 30006 == var2 ? JPushConstants.SDK_VERSION_CODE : null;
      }
   }

   private void recoverMethod_d_f0554df3(Context var1) {
      try {
         Class.forName("cn.jpush.android.dy.JPushThirdDyManager").getDeclaredMethod("update", Context.class).invoke(null, var1);
      } catch (Throwable var3) {
      }
   }

   @Override
   public String getSdkVersion(String var1) {
      return RecoverType2e4ceff0.recoverField_b;
   }

   @Override
   public Object handleAction(Context var1, String var2, Object var3) {
      Logger.recoverMethod_d_e925fa09("JPushActionImpl", "handleAction:" + var2);

      try {
         if (!this.recoverMethod_b_f0554df7(var1)) {
            Logger.recoverMethod_ke_e925fa09("JPushActionImpl", "check config failed, will drop current action:" + var2);
            return null;
         }

         if (var1 == null) {
            var1 = this.recoverField_c;
         }

         if (TextUtils.isEmpty(var2)) {
            Logger.recoverMethod_w_e925fa09("JPushActionImpl", "action is empty");
            return null;
         }

         RecoverType9b4c4fa1.recoverMethod_a_d8c30de5().recoverMethod_a_5ffc00fd(var1, var2);
         Bundle var4 = null;
         if (var3 instanceof Bundle) {
            var4 = (Bundle)var3;
         }

         if (var2.equals("init")) {
            this.recoverMethod_c_f0554df3(var1);
         } else if (var2.equals("resume")) {
            if (RecoverTypea2146d45.recoverMethod_b_1a54e374(var1, 0)) {
               RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2006, 1, "set failed");
               return null;
            }

            RecoverTypea2146d45.recoverMethod_a_1a54e370(var1, 0);
            JCoreManager.onEvent(var1, "JPUSH", 1, null, null);
         } else if (var2.equals("stop")) {
            if (RecoverTypea2146d45.recoverMethod_b_1a54e374(var1, 0)) {
               RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2007, 1, "set failed");
               return null;
            }

            Logger.recoverMethod_dd_e925fa09(
               "JPushActionImpl", "The service is stopped, it will give up all the actions until you call resumePush method to resume the service"
            );
            RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2007, 0, "set success");
            RecoverTypea2146d45.recoverMethod_a_1a54e370(var1, 1);
            JCoreManager.onEvent(var1, "JPUSH", 0, null, null);
         } else if (var2.equals("send_exception_stats")) {
            JCoreManager.onEvent(var1, "JPUSH", 106, null, var4);
         } else if (var2.equals("clear_all_notify")) {
            RecoverType2e4d193c.recoverMethod_a_f0554df3(var1);
         } else if (var2.equals("disable_push")) {
            RecoverTypea2146d45.recoverMethod_c_1a552341(var1, false);
         } else if (var2.equals("geo_interval")) {
            if (var4 != null) {
               long var38 = var4.getLong("interval", -1L);
               if (var38 != -1L) {
                  RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_1349ef(var38);
               }
            }
         } else if (var2.equals("max_num")) {
            recoverMethod_a_69c77401(var1, var4);
         } else if (var2.equals("set_mobile")) {
            RecoverType8b455832.recoverMethod_a_e76c89f4().recoverMethod_a_69c77401(var1, var4);
         } else if (var2.equals("pushtime")) {
            recoverMethod_c_69c77401(var1, var4);
         } else if (var2.equals("silenceTime")) {
            recoverMethod_b_69c77401(var1, var4);
         } else if (var2.equals("geo_fence_max_num")) {
            if (var4 != null) {
               int var37 = var4.getInt("num", -1);
               if (var37 > 0) {
                  RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_13462e(var37);
               }
            }
         } else if (var2.equals("enable_lbs")) {
            if (var4 != null) {
               boolean var36 = var4.getBoolean("enable", true);
               RecoverTypea2146d45.recoverMethod_e_1a552341(var1, var36);
               if (!var36) {
                  Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "lbs is disabled,stop geo");
                  RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_c_9b75();
               } else {
                  Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "lbs is enabled");
                  RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_b_9b75();
               }
            }
         } else if (var2.equals("enable_notification_callback")) {
            if (var4 != null) {
               boolean var35 = var4.getBoolean("enable", false);
               RecoverTypea2146d45.recoverMethod_h_1a552341(var1, var35);
            }
         } else if (!var2.equals("enable_smart_push")
            && !var2.equals("enable_link_push")
            && !var2.equals("enable_data_insights")
            && !var2.equals("enable_geo_push")) {
            if (var2.equals("third_enable")) {
               if (var4 != null) {
                  boolean var34 = var4.getBoolean("enable", true);
                  boolean var45 = RecoverType2e4ceff0.recoverMethod_a_9b79();
                  String var48 = RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var1);
                  if (!var1.getPackageName().equals(var48)) {
                     Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "third enable:" + var34 + ",last :" + var45);
                  } else {
                     Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "third enable:" + var34);
                  }

                  try {
                     JPushConstants.THIRD_ENABLE = var34;
                  } catch (Throwable var10) {
                  }

                  if (var34) {
                     RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_b_d19840b7(var1, var2, var4);
                  } else {
                     RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_d19840b7(var1, var2, var4);
                  }
               }
            } else if (var2.equals("delete_geo_fence")) {
               if (var4 != null) {
                  String var33 = var4.getString("id");
                  if (!TextUtils.isEmpty(var33)) {
                     RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_aad3b1ff(var33);
                  }
               }
            } else if (var2.equals("set_badge")) {
               if (var4 != null) {
                  int var32 = var4.getInt("num");
                  RecoverTypea0793a19.set(var1, Key.BadgeCurNum().set(var32));
                  RecoverType2e4d193c.recoverMethod_a_b04e2dcd(var1, null, var32, var32);
               }
            } else if (var2.equals("msg")) {
               if (var3 instanceof JPushResponse) {
                  recoverMethod_a_29a065f0(var1, (JPushResponse)var3);
               }
            } else if (var2.equals("msg_time_out")) {
               if (var4 != null) {
                  int var31 = var4.getInt("cmd", -1);
                  long var44 = var4.getLong("rid", -1L);
                  recoverMethod_a_30480154(var1, var31, var44);
               }
            } else if (var2.equals("tagalis")) {
               RecoverType9b4c5363.recoverMethod_a_69c77401(var1, var4);
            } else if (var2.equals("add_local_notify")) {
               if (var4 != null) {
                  JPushLocalNotification var30 = (JPushLocalNotification)var4.getSerializable("local_notify");
                  RecoverType2e4d11b9.recoverMethod_a_4af8d359(var1).recoverMethod_a_9131a9a(var1, var30);
               }
            } else if (var2.equals("rm_local_notify")) {
               if (var4 != null) {
                  long var29 = var4.getLong("local_notify_ID");
                  RecoverType2e4d11b9.recoverMethod_a_4af8d359(var1).recoverMethod_a_1a54e735(var1, var29);
               }
            } else if (var2.equals("clear_local_notify")) {
               RecoverType2e4d11b9.recoverMethod_a_4af8d359(var1).recoverMethod_b_f0554df3(var1);
            } else if (var2.equals("show_local_notify")) {
               if (RecoverTypea2146d45.recoverMethod_e_f0554df7(var1)) {
                  Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "push has stoped");
               } else if (var4 != null) {
                  String var28 = var4.getString("local_notify_msg");
                  if (!TextUtils.isEmpty(var28)) {
                     RecoverType2e4cce2a var43 = RecoverType2e4cce2a.recoverMethod_a_e5f4ac2a(var28, var1.getPackageName(), JCoreHelper.getAppKey(var1), 0L);
                     if (var43 != null) {
                        var43.recoverMethod_a_f0554df3(var1);
                        RecoverType2e4d193c.recoverMethod_a_ae3b7eb8(var1, var43);
                     }
                  } else {
                     Logger.recoverMethod_d_e925fa09("JPushActionImpl", "local message is empty");
                  }
               }
            } else if (var2.equals("set_custom_notify")) {
               if (var4 != null) {
                  int var27 = var4.getInt("buidler_id", -1);
                  String var42 = var4.getString("buidler_string");
                  Logger.recoverMethod_d_e925fa09("JPushActionImpl", "builderId:" + var27 + ",buildString:" + var42);
                  if (var27 >= 0 && !TextUtils.isEmpty(var42)) {
                     RecoverTypea2146d45.recoverMethod_a_9d23c587(var1, var27 + "", var42);
                  }
               }
            } else if ("third_push_upload_regid".equals(var2)) {
               if (var4 != null) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_c_69c77401(var1, var4);
               }
            } else if ("intent.plugin.platform.ON_MESSAGING".equals(var2)) {
               String var26 = var4.getString("appId");
               String var41 = var4.getString("senderId");
               String var47 = var4.getString("JMessageExtra");
               byte var49 = var4.getByte("platform");
               Logger.recoverMethod_d_e925fa09("JPushActionImpl", "appId:" + var26 + ",senderId:" + var41 + ",JMessageExtra:" + var47 + ", romType: " + var49);
               if (TextUtils.isEmpty(var47)) {
                  return null;
               }

               if (TextUtils.isEmpty(var26)) {
                  var26 = var1.getPackageName();
               }

               if (TextUtils.isEmpty(var41)) {
                  var41 = JCoreHelper.getAppKey(var1);
               }

               RecoverType2e4cce28.recoverMethod_a_90ee5046(var1, 2, var47, var26, var41, 0L, var49);
            } else if ("change_foreground".equals(var2)) {
               if (var4 != null) {
                  boolean var25 = var4.getBoolean("foreground");
                  if (JPushConstants.isForeGround == -1 && var25) {
                     Logger.recoverMethod_d_e925fa09("JPushActionImpl", "first in foreground");
                     RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_b_9b75();
                  }

                  JPushConstants.isForeGround = var25 ? 0 : 1;
                  Logger.recoverMethod_d_e925fa09("JPushActionImpl", "change foregroud:" + JPushConstants.isForeGround);
                  if (var25) {
                     RecoverType2e4d193c.recoverMethod_a_30483d64(var1, 1, false);
                  }
               }
            } else if (var2.equals("check_stop")) {
               boolean var24 = RecoverTypea2146d45.recoverMethod_e_f0554df7(var1);
               RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2003, var24 ? 1 : 0, var24 ? "stopped" : "not stop");
            } else if (var2.equals("get_connection")) {
               boolean var23 = JPushConstants.isTcpConnected();
               RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2004, var23 ? 0 : 1, var23 ? "connected" : "not connect");
            } else if (var2.equals("get_rid")) {
               String var22 = JCoreHelper.getRegistrationID(var1);
               RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2005, 0, var22);
            } else if (var2.equals("synce_platform")) {
               RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_69c77401(var1, var4);
            } else if ("jcore_on_event".equals(var2)) {
               try {
                  if (var4 != null) {
                     int var21 = var4.getInt("type");
                     int var40 = var4.getInt("code");
                     String var46 = var4.getString("msg");
                     Logger.recoverMethod_d_e925fa09("JPushActionImpl", "eventType:" + var21 + ",code:" + var40 + ",msg:" + var46);
                     if (var21 != -1 && var21 != 0 && var21 != 1 && var21 != 2000 && var21 != 2001 && var21 != 2002) {
                        if (var21 == 19) {
                           RecoverType2e4d11b9.recoverMethod_a_4af8d359(var1).recoverMethod_d_f0554df3(var1);
                        } else if (var21 == 35) {
                           RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, 2009, 0, "");
                        } else if (var21 == 2011) {
                           RecoverType2e4d193c.recoverMethod_a_4d98d517(var1, var21, var40, var46, null);
                        }
                     } else {
                        if (var21 == 2000 || var21 == 2001 || var21 == 0 || var21 == 2002) {
                           if (var21 == 2000) {
                              RecoverTypea0793a19.set(var1, Key.UPSRegister().set(false));
                              UPSPushHelper.upsCallBack(var1, 0, "ups.unregister", var40, var46);
                              return null;
                           }

                           RecoverTypea0793a19.set(var1, Key.UPSRegister().set(true));
                           if (var40 != 1 && var21 == 2001 || var21 == 2002) {
                              UPSPushHelper.upsCallBack(var1, 0, "ups.register", var40, var46);
                              return null;
                           }

                           if (var21 == 0) {
                              UPSPushHelper.upsCallBack(var1, 0, "ups.register", var40, var46);
                           }
                        }

                        RecoverType2e4d193c.recoverMethod_a_4abe089d(var1, var21, var40, var46);
                        if (var21 == 1) {
                           JPushConstants.setTcpConnected(true);
                           recoverMethod_a_f0554df3(var1);
                           RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_c_f0554df3(var1);
                           RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_d_9b75();
                           RecoverType9b4c4fa1.recoverMethod_a_d8c30de5().recoverMethod_b_9b75();
                           RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_f0554df3(var1);
                        } else if (var21 == -1) {
                           JPushConstants.setTcpConnected(false);
                           RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_e_9b75();
                        }
                     }
                  }
               } catch (Throwable var11) {
                  Logger.recoverMethod_w_e925fa09("JPushActionImpl", "onEvent failed:" + var11.getMessage());
               }
            } else if (!"handle_msg".equals(var2)) {
               if ("third_init".equals(var2)) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_aac1ff8b(var1, var4, var2);
               } else if ("third_resume".equals(var2)) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_5ffc00fd(var1, var2);
               } else if ("third_stop".equals(var2)) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_c_5ffc00fd(var1, var2);
               } else if ("third_all_notify".equals(var2)) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_b_5ffc00fd(var1, var2);
               } else if ("clear_by_id".equals(var2)) {
                  if (var4 != null) {
                     int var5 = var4.getInt("id");
                     RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_3f52113a(var1, var5, var2);
                  }
               } else if ("third_action".equals(var2)) {
                  if (var4 != null) {
                     String var13 = var4.getString("third_key_action");
                     RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_bb994ac1(var1, var13, var4, var2);
                  }
               } else if ("set_third_token".equals(var2)) {
                  if (null != var4) {
                     String var14 = var4.getString("third_token");
                     RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_b_9d23c587(var1, var14, var2);
                  }
               } else if ("dy_register".equals(var2)) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_9d23c587(var1, var4.getString("dev_key"), var2);
               } else if ("third_unbind".equals(var2)) {
                  RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_b_69c77401(var1, var4);
               } else if ("sync_fragment_life".equals(var2)) {
                  if (var4 != null) {
                     String var15 = var4.getString("activity_name");
                     boolean var6 = var4.getBoolean("state");

                     try {
                        Activity var7 = RecoverType2e4d0676.recoverMethod_a_2b1cd295(var1);
                        String var8 = "";
                        if (var7 != null) {
                           var8 = var7.getClass().getCanonicalName();
                        }

                        if (var6) {
                           var15 = var8 + "&" + var15;
                           RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_157a5ecb(var1, true, var15);
                        } else {
                           RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_5ffc00fd(var1, var8);
                        }
                     } catch (Throwable var9) {
                     }
                  }
               } else if ("delay_notify".equals(var2)) {
                  RecoverType2e4d193f.recoverMethod_a_cd395451().recoverMethod_a_2b7802e6(var1, null);
               } else if ("ups.turnOn".equals(var2)) {
                  if (var4 != null) {
                     int var17 = var4.getInt("ups.rid");
                     if (RecoverTypea2146d45.recoverMethod_b_1a54e374(var1, 1)) {
                        UPSPushHelper.upsCallBack(var1, var17, "ups.turnOn", 1, JCoreHelper.getRegistrationID(var1));
                        return null;
                     }

                     UPSPushHelper.upsCallBack(var1, var17, "ups.turnOn", 0, JCoreHelper.getRegistrationID(var1));
                  }

                  RecoverTypea2146d45.recoverMethod_a_1a552341(var1, true);
                  JCoreManager.onEvent(var1, "JPUSH", 1, null, null);
               } else if (!"ups.unregister".equals(var2)) {
                  if ("ups.turnOff".equals(var2)) {
                     if (var4 != null) {
                        int var18 = var4.getInt("ups.rid");
                        if (RecoverTypea2146d45.recoverMethod_b_1a54e374(var1, 1)) {
                           UPSPushHelper.upsCallBack(var1, var18, "ups.turnOff", 1, JCoreHelper.getRegistrationID(var1));
                           return null;
                        }

                        UPSPushHelper.upsCallBack(var1, var18, "ups.turnOff", 0, JCoreHelper.getRegistrationID(var1));
                     }

                     RecoverTypea2146d45.recoverMethod_a_1a552341(var1, false);
                     JCoreManager.onEvent(var1, "JPUSH", 0, null, null);
                  } else if ("handle_cache_message".equals(var2)) {
                     RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_f0554df3(var1);
                  } else if ("sync_target".equals(var2)) {
                     if (var4 != null) {
                        String var19 = var4.getString("targert");
                        RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_b_5ffc00fd(var1, var19);
                     }
                  } else if ("sync_view_show_success".equals(var2)) {
                     if (var4 != null) {
                        RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_1a552341(var1, var4.getBoolean("result"));
                     }
                  } else if ("handle_notify_inapp_message".equals(var2)) {
                     if (var4 != null) {
                        String var20 = var4.getString("content");
                        if (!TextUtils.isEmpty(var20)) {
                           RecoverType2e4cce2a var39 = RecoverType2e4cce2a.recoverMethod_b_365d807c(var20);
                           if (null != var39) {
                              RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_ae3b7eb8(var1, var39);
                           }
                        }
                     }

                     return null;
                  }
               }
            }
         } else if (var4 != null) {
            this.recoverMethod_a_aac1ff8b(var1, var4, var2);
         }
      } catch (Throwable var12) {
         Logger.recoverMethod_e_e925fa09("JPushActionImpl", "handleAction failed:" + var12.getMessage());
      }

      return null;
   }

   private void recoverMethod_a_aac1ff8b(Context var1, Bundle var2, String var3) {
      boolean var4 = var2.getBoolean("enable", true);
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      if ("enable_smart_push".equals(var3)) {
         RecoverTypea2146d45.recoverMethod_i_1a552341(var1, var4);
         boolean var7 = RecoverTypea2146d45.recoverMethod_p_f0554df7(var1);
         boolean var8 = RecoverTypea2146d45.recoverMethod_o_f0554df7(var1);
         boolean var9 = RecoverTypea2146d45.recoverMethod_q_f0554df7(var1);
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "ACTION_ENABLE_SMART_PUSH");
         if (!var4 && !var7 && !var9) {
            var6.add(recoverField_g);
         } else {
            var5.add(recoverField_g);
         }

         if (!var4 && !var8 && !var9) {
            var6.add(recoverField_f);
            var6.add(recoverField_h);
         } else {
            var5.add(recoverField_f);
            var5.add(recoverField_h);
         }
      } else if ("enable_geo_push".equals(var3)) {
         RecoverTypea2146d45.recoverMethod_e_1a552341(var1, var4);
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "ACTION_ENABLE_GEO_PUSH");
         boolean var10 = RecoverTypea2146d45.recoverMethod_q_f0554df7(var1);
         boolean var13 = RecoverTypea2146d45.recoverMethod_s_f0554df7(var1);
         if (!var4 && !var13 && !var10) {
            var6.add(recoverField_f);
            var6.add(recoverField_h);
         } else {
            var5.add(recoverField_f);
            var5.add(recoverField_h);
         }
      } else if ("enable_link_push".equals(var3)) {
         RecoverTypea2146d45.recoverMethod_f_1a552341(var1, var4);
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "ACTION_ENABLE_LINK_PUSH");
         boolean var11 = RecoverTypea2146d45.recoverMethod_q_f0554df7(var1);
         boolean var14 = RecoverTypea2146d45.recoverMethod_s_f0554df7(var1);
         if (!var4 && !var14 && !var11) {
            var6.add(recoverField_g);
         } else {
            var5.add(recoverField_g);
         }
      } else if ("enable_data_insights".equals(var3)) {
         RecoverTypea2146d45.recoverMethod_g_1a552341(var1, var4);
         boolean var12 = RecoverTypea2146d45.recoverMethod_p_f0554df7(var1);
         boolean var15 = RecoverTypea2146d45.recoverMethod_o_f0554df7(var1);
         boolean var16 = RecoverTypea2146d45.recoverMethod_s_f0554df7(var1);
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "ACTION_ENABLE_DATA_INSIGHTS ");
         if (!var4 && !var16 && !var12) {
            var6.add(recoverField_g);
         } else {
            var5.add(recoverField_g);
         }

         if (!var4 && !var16 && !var15) {
            var6.add(recoverField_f);
            var6.add(recoverField_h);
         } else {
            var5.add(recoverField_f);
            var5.add(recoverField_h);
         }
      }

      Logger.recoverMethod_d_e925fa09("JPushActionImpl", "enable=" + var5);
      Logger.recoverMethod_d_e925fa09("JPushActionImpl", "disable=" + var6);
      if (var5.size() != 0 || var6.size() != 0) {
         var2.putIntegerArrayList("enable_c_c", var5);
         var2.putIntegerArrayList("disable_c_c", var6);
         JCoreManager.onEvent(var1, "JPUSH", 111, null, var2);
      }
   }

   @Override
   public Object beforLogin(Context var1, String var2, int var3, String var4) {
      this.recoverMethod_b_f0554df7(var1);
      if (var1 == null) {
         Logger.recoverMethod_e_e925fa09("JPushActionImpl", "context was null");
         return null;
      }

      if (TextUtils.isEmpty(var4)) {
         Logger.recoverMethod_e_e925fa09("JPushActionImpl", " filed name was empty");
         return null;
      }

      if ("platformtype".equals(var4)) {
         if (var3 >= 16) {
            return RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_d_f0554ddf(var1);
         }
      } else if ("platformregid".equals(var4)) {
         return RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_b_e501eb0d(var1);
      }

      return null;
   }

   @Override
   public void handleNotificationIntent(Context var1, Intent var2) {
      if (RecoverType2e4d193c.recoverMethod_a_c4d2ecb4(var1, var2)) {
         if (RecoverType9b4c5aeb.recoverMethod_a_d8c46bdb().recoverMethod_a_c4d2ecb4(var1, var2)) {
            Logger.recoverMethod_d_e925fa09("JPushActionImpl", "handleCustomButtonClick: ok");
         } else {
            RecoverType2e4cce2a var3 = RecoverType2e4d193c.recoverMethod_b_f07c8e6b(var1, var2);
            Logger.recoverMethod_i_e925fa09("JPushActionImpl", "handleNotificationIntent:" + var3);
            if (null == var3) {
               RecoverType9b4c5aeb.recoverMethod_a_d8c46bdb().recoverMethod_c_c4d2ecb0(var1, var2);
               Logger.recoverMethod_i_e925fa09("JPushActionImpl", "handleNotificationIntent handleOpenClick");
            } else {
               if ("cn.jpush.android.intent.NOTIFICATION_OPENED".equals(var2.getAction())) {
                  RecoverType9b4c5aeb.recoverMethod_a_d8c46bdb().recoverMethod_a_78545e75(var1, var3, var2);
               } else {
                  RecoverType2e4d193c.recoverMethod_a_74ff4fff(var1, var2.getAction(), var3, var2);
               }
            }
         }
      }
   }

   @Override
   public void onJPushMessageReceive(Context var1, JPushMessageReceiver var2, Intent var3) {
      RecoverType8b45582f.recoverMethod_a_e76c8997().recoverMethod_a_79196310(var1, var2, var3);
   }

   @Override
   public void onJPushMessageReceive(Context var1, String var2, String var3, Bundle var4) {
      if (var4 != null) {
         try {
            Intent var5 = RecoverType9b4c5ae5.recoverMethod_a_ee49a2f4(var4);
            RecoverType8b45582f.recoverMethod_a_e76c8997().recoverMethod_a_79196310(var1, JPushConstants.getPushMessageReceiver(var1), var5);
         } catch (Throwable var6) {
         }
      }
   }

   @Override
   public void onMessage(Context var1, CustomMessage var2) {
      RecoverType8b45582f.recoverMethod_a_f1105770(var1, var2);
   }

   @Override
   public void onNotifyMessageOpened(Context var1, NotificationMessage var2) {
      RecoverType8b45582f.recoverMethod_a_64b14134(var1, var2, "cn.jpush.android.intent.NOTIFICATION_OPENED");
   }

   @Override
   public void onNotifyMessageArrived(Context var1, NotificationMessage var2) {
      RecoverType8b45582f.recoverMethod_a_64b14134(var1, var2, "cn.jpush.android.intent.NOTIFICATION_RECEIVED");
   }

   @Override
   public void onMultiAction(Context var1, Intent var2) {
      RecoverType8b45582f.recoverMethod_b_c4d2ecb0(var1, var2);
   }

   @Override
   public void onTagAliasResponse(Context var1, long var2, int var4, Intent var5) {
      RecoverType9b4c5365.recoverMethod_a_d8c382a1().recoverMethod_a_6269c3af(var1.getApplicationContext(), var2, var4, var5);
   }

   @Override
   public ProxyActivityAction getPopWinActivity(Context var1) {
      return new RecoverType9b558516();
   }

   @Override
   public ProxyActivityAction getPushActivity(Context var1) {
      return new RecoverType9b558517();
   }

   @Override
   public void setAliasAndTags(Context var1, String var2, Set var3, CallBackParams var4) {
      RecoverType9b4c5363.recoverMethod_a_53928bd0(var1, var2, var3, var4);
   }

   @Override
   public void onInAppMessageShow(Context var1, NotificationMessage var2) {
      RecoverType8b45582f.recoverMethod_a_64b14134(var1, var2, "cn.jpush.android.intent.IN_APP_MSG_SHOW");
   }

   @Override
   public void onInAppMessageClick(Context var1, NotificationMessage var2) {
      RecoverType8b45582f.recoverMethod_a_64b14134(var1, var2, "cn.jpush.android.intent.IN_APP_MSG_CLICK");
   }

   private static void recoverMethod_a_30480154(Context var0, int var1, long var2) {
      switch (var1) {
         case 10:
         case 28:
         case 29:
         case 38:
            int var4 = RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_1349e2(var2);
            RecoverType9b4c5363.recoverMethod_a_d8b82b91(var0, var4, JPushInterface.Member6357d7ea.TIMEOUT, var2);
            break;
         case 26:
            RecoverType8b455832.recoverMethod_a_e76c89f4().recoverMethod_a_304871f2(var0, var2, JPushInterface.Member6357d7ea.TIMEOUT);
            break;
         case 27:
            RecoverType9b4c5725.recoverMethod_a_d8c3f6e1().recoverMethod_b_1a54e731(var0, var2);
            break;
         case 37:
            RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_cb5e72f(var2, JPushInterface.Member6357d7ea.TIMEOUT, null);
      }
   }

   private static void recoverMethod_a_29a065f0(Context var0, JPushResponse var1) {
      Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "action:handleMsg:" + var1);
      if (null != var1) {
         switch (var1.getCmd()) {
            case 3:
               RecoverType2e4d20bf var4 = new RecoverType2e4d20bf(var1);
               if (RecoverTypea2146d45.recoverMethod_e_f0554df7(var0)) {
                  Logger.recoverMethod_d_e925fa09("JPushActionImpl", "JPush was stoped");
                  if (var4.recoverMethod_a_9b68() != 20) {
                     return;
                  }
               }

               RecoverType2e4cce28.recoverMethod_a_d3c5a803(var0, var4);
               break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 39:
            default:
               Logger.recoverMethod_w_e925fa09("JPushActionImpl", "Unknown command for parsing inbound. Union AD cmd: " + var1.getCmd());
               break;
            case 10:
            case 28:
            case 29:
            case 38:
            case 40:
               RecoverType2e4d20c1 var2 = new RecoverType2e4d20c1(var1);
               byte var3;
               switch (var1.getCmd()) {
                  case 28:
                     var3 = 1;
                     break;
                  case 38:
                     var3 = 3;
                     break;
                  case 40:
                     var3 = 4;
                     break;
                  default:
                     var3 = 2;
               }

               RecoverType9b4c5363.recoverMethod_a_d0b594d9(var0, var2.recoverMethod_a_73cf11cb(), var3, var1.getRquestId(), var2.recoverField_a);
               break;
            case 25:
               JSONObject var9 = recoverMethod_a_c01e8fea(var1.getBody());
               if (var9 != null) {
                  int var10 = var9.optInt("cmd");
                  switch (var10) {
                     case 56:
                        RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_be7fc169(var9);
                     case 57:
                        break;
                     case 58:
                     default:
                        Logger.recoverMethod_d_e925fa09("JPushActionImpl", "Unknown command for ctrl");
                        break;
                     case 59:
                        recoverMethod_a_3c4029ab(var0, var9);
                        break;
                     case 60:
                        RecoverType2e4d193c.recoverMethod_a_30483d64(var0, 2, true);
                  }
               }
               break;
            case 26:
               RecoverType2e4d20bd var6 = new RecoverType2e4d20bd(var1);
               RecoverType8b455832.recoverMethod_a_e76c89f4().recoverMethod_a_304871f2(var0, var6.getRquestId(), var6.recoverMethod_a_9b68());
               break;
            case 27:
               RecoverType2e4d20bd var5 = new RecoverType2e4d20bd(var1);
               if (var5.recoverMethod_a_9b68() == 0) {
                  RecoverType9b4c5725.recoverMethod_a_d8c3f6e1().recoverMethod_a_1a54e731(var0, var1.getRquestId());
               } else {
                  RecoverType9b4c5725.recoverMethod_a_d8c3f6e1().recoverMethod_a_304871f2(var0, var1.getRquestId(), var5.recoverMethod_a_9b68());
               }
               break;
            case 36:
               RecoverType2e4cf3b2 var7 = new RecoverType2e4cf3b2(var1);
               RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_84fe6c32(var7);
               break;
            case 37:
               RecoverType2e4cf3b4 var8 = new RecoverType2e4cf3b4(var1);
               RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_cb5e72f(var1.getRquestId(), 0, var8);
         }
      }
   }

   private static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      try {
         JSONObject var2 = var1.getJSONObject("content");
         String var3 = var2.optString("ids");
         if (TextUtils.isEmpty(var3)) {
            return;
         }

         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "dealCancelNotification ids=" + var3);
         String[] var4 = var3.split(",");
         LinkedList var5 = new LinkedList();

         for (String var9 : var4) {
            if (!TextUtils.isEmpty(var9)) {
               if (RecoverType2e4d193e.recoverMethod_a_5ffc0101(var0, var9)) {
                  int var10 = RecoverType2e4d193c.recoverMethod_a_afa300d7(var9, 0);
                  Logger.recoverMethod_d_e925fa09("JPushActionImpl", "message id:" + var9 + " is in local history ,try cancel notificationID : " + var10);
                  boolean var11 = RecoverType2e4d193c.recoverMethod_a_1a54e374(var0, var10);
                  if (var11) {
                     RecoverType2e4d193c.recoverMethod_d_1a54e370(var0, var10);
                     String var12 = var9;
                     if (var4.length == 2) {
                        var12 = var4[1];
                     }

                     RecoverType8b455831.recoverMethod_a_36ca9ac3(var12, 1068L, var0);
                  }
               } else if (!RecoverType2e4d193e.recoverMethod_a_7555e60(var0, var9, null, 1)) {
                  byte var14 = RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_d_5ffc00e9(var0, var9);
                  Logger.recoverMethod_d_e925fa09("JPushActionImpl", "message id:" + var9 + " may be ospush channel,try cancel by sdkType: " + var14);
                  if (var14 != -1) {
                     RecoverType8b455831.recoverMethod_a_2ef42797(var9, "", var14, 1069L, var0);
                  }
               } else {
                  Logger.recoverMethod_d_e925fa09("JPushActionImpl", "dealCancelNotification , do nothing ");
               }

               var5.add(var9);
            }
         }

         RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_5ffc00fd(var0, var3);
         RecoverType2e4d193e.recoverMethod_a_4eab7a9f(var0, var5, -1);
      } catch (Throwable var13) {
         Logger.recoverMethod_w_e925fa09("JPushActionImpl", "dealCancelNotification e:" + var13);
      }
   }

   private static JSONObject recoverMethod_a_c01e8fea(ByteBuffer var0) {
      try {
         ByteBuffer var1 = ByteBuffer.wrap(var0.array());
         var1.getLong();
         short var2 = var1.getShort();
         byte[] var3 = new byte[var2];
         var1.get(var3);
         String var4 = new String(var3, "UTF-8");
         Logger.recoverMethod_d_e925fa09("JPushActionImpl", "parseBundle2Json content: " + var4);
         return new JSONObject(var4);
      } catch (Exception var5) {
         Logger.recoverMethod_w_e925fa09("JPushActionImpl", "parseBundle2Json exception:" + var5.getMessage());
         return null;
      }
   }

   private static void recoverMethod_a_69c77401(Context var0, Bundle var1) {
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("JPushActionImpl", "[setMaxNotificationNum] bundle is bull");
      } else {
         int var2 = var1.getInt("num");
         Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "action:setMaxNotificationNum :" + var2);
         int var3 = RecoverType2e4d1940.recoverMethod_b_9b68();
         Logger.recoverMethod_v_e925fa09("JPushActionImpl", "number in queue: " + var3);
         if (var2 < var3) {
            int var4 = var3 - var2;
            Logger.recoverMethod_v_e925fa09("JPushActionImpl", "decreaseNotification:" + var4);
            RecoverType2e4d193c.recoverMethod_c_1a54e370(var0, var4);
         }

         RecoverTypea2146d45.recoverMethod_c_1a54e370(var0, var2);
      }
   }

   private static void recoverMethod_b_69c77401(Context var0, Bundle var1) {
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("JPushActionImpl", "[setSilenceTime] bundle is bull");
      } else {
         String var2 = var1.getString("time");
         Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "action:setSilenceTime pushTime:" + var2);
         RecoverTypea2146d45.recoverMethod_c_5ffc00fd(var0, var2);
      }
   }

   private static void recoverMethod_c_69c77401(Context var0, Bundle var1) {
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("JPushActionImpl", "[setPushTime] bundle is bull");
      } else {
         RecoverTypea2146d45.recoverMethod_c_1a552341(var0, true);
         String var2 = var1.getString("time");
         String var3 = "([0-9]|1[0-9]|2[0-3])\\^([0-9]|1[0-9]|2[0-3])";
         String var4 = "([0-6]{0,7})_((" + var3 + ")|(" + var3 + "-)+(" + var3 + "))";
         Pattern var5 = Pattern.compile(var4);
         Matcher var6 = var5.matcher(var2);
         if (var6.matches()) {
            String var7 = RecoverTypea2146d45.recoverMethod_h_e501eb0d(var0);
            if (var2.equals(var7)) {
               Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "Already SetPushTime, give up - " + var7);
               return;
            }

            Logger.recoverMethod_dd_e925fa09("JPushActionImpl", "action:setPushTime pushTime:" + var2);
            RecoverTypea2146d45.recoverMethod_b_5ffc00fd(var0, var2);
         } else {
            Logger.recoverMethod_ee_e925fa09("JPushActionImpl", "Invalid time format - " + var2);
         }
      }
   }

   public static class Membercec2a12c {
      public static int recoverMethod_a_9b68() {
         return 1;
      }

      public static int recoverMethod_b_9b68() {
         return RecoverType2e4ceff0.recoverField_a != JPushConstants.SDK_VERSION_CODE ? 2 : 0;
      }
   }
}
