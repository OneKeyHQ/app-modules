package cn.jpush.android.ac;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JThirdPlatFormInterface;
import cn.jpush.android.asus.RecoverTypefa94ae12;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.data.JPushConfig;
import cn.jpush.android.data.JPushConfigEx;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b45582f;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.m.RecoverType2e4ceff0;
import cn.jpush.android.x.RecoverType2e4d193b;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;

public class RecoverType9b4c5727 {
   private static String recoverField_a = "exception_type";
   private List recoverField_b;
   private static Map recoverField_c = new HashMap();
   private static Map recoverField_d = new HashMap();
   private static Map recoverField_e = new HashMap();
   private Bundle recoverField_f;
   private boolean recoverField_g = false;
   private static final Object recoverField_h = new Object();
   private long recoverField_i = 50L;
   private boolean recoverField_j;
   private byte[] recoverField_k;
   private Boolean recoverField_l;
   private Boolean recoverField_m;
   private Boolean recoverField_n;

   private RecoverType9b4c5727() {
      this.recoverField_b = new ArrayList();
   }

   public static RecoverType9b4c5727 recoverMethod_a_d8c3f71f() {
      return RecoverType9b4c5727.Memberf9932e24.recoverField_a;
   }

   public synchronized void recoverMethod_a_f0554df3(Context var1) {
      if (!this.recoverField_g) {
         if (var1 != null) {
            Logger.recoverMethod_d_e925fa09("ThirdPushManager", "initOnMainProcess");
            this.recoverField_g = this.recoverMethod_f_f0554df7(var1);
         }
      }
   }

   private boolean recoverMethod_a_879483a4(Throwable var1) {
      return var1 instanceof RuntimeException && var1.getMessage() != null && var1.getMessage().contains("Please check") && JCoreManager.getDebugMode();
   }

   private boolean recoverMethod_f_f0554df7(Context var1) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "[loadThirdPush]");
      if (!JCoreHelper.isAuth(var1)) {
         Logger.recoverMethod_w_e925fa09("ThirdPushManager", "user don't auth, so return init");
         return false;
      }

      for (Object recoveryElement_82 : recoverField_c.entrySet()) {
         Entry var3 = (Entry)recoveryElement_82;
         try {
            Class var4 = null;
            if (null == var4) {
               var4 = Class.forName((String)var3.getKey(), false, var1.getClassLoader());
            }

            if (var4 != null) {
               Object var5 = var4.newInstance();
               if (var5 != null && var5 instanceof JThirdPlatFormInterface) {
                  ((JThirdPlatFormInterface)var5).init(var1);
                  if (((JThirdPlatFormInterface)var5).isSupport(var1)) {
                     this.recoverField_b.add((JThirdPlatFormInterface)var5);
                  }
               }
            }
         } catch (Throwable var6) {
            if (this.recoverMethod_a_879483a4(var6)) {
               if (RecoverType2e4ceff0.recoverField_c >= 238) {
                  JCoreHelper.futureExecutor(var1, new RecoverType9b4c5aea("ThirdPushManager#loadThirdPush") {
                     @Override
                     public void recoverMethod_a_9b75() {
                        throw new RuntimeException(var6);
                     }
                  });
               } else {
                  new Thread(new Runnable() {
                     @Override
                     public void run() {
                        throw new RuntimeException(var6);
                     }
                  }).start();
               }
            }

            Logger.recoverMethod_w_e925fa09("ThirdPushManager", "loadThirdPush error:" + var6.getMessage());
         }
      }

      if (VERSION.SDK_INT >= 26 && !this.recoverField_b.isEmpty()) {
         RecoverType2e4d193b.recoverMethod_a_f0554df3(var1);
      }

      this.recoverMethod_a_fa4794e4(var1, this.recoverField_b);
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "third init success, add " + this.recoverField_b.size() + " third push channel");
      return true;
   }

   private void recoverMethod_a_fa4794e4(Context var1, List var2) {
      try {
         Bundle var3 = new Bundle();
         byte[] var4 = new byte[var2.size()];

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4[var5] = ((JThirdPlatFormInterface)var2.get(var5)).getRomType(var1);
         }

         var3.putByteArray("platform", var4);
         JCoreHelper.runActionWithService(var1, "JPUSH", "synce_platform", var3);
      } catch (Throwable var6) {
      }
   }

   public void recoverMethod_a_9d23c587(Context var1, String var2, String var3) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "registAndConfigureSDKOnMainProcess");
      final String var4 = RecoverType9b4c5ae5.recoverMethod_d_e96ea081(var2);
      final String var5 = RecoverTypea2146d45.recoverMethod_g_a4ea6d43(var1, var4);
      if (!TextUtils.isEmpty(var5)) {
         RecoverTypea2146d45.recoverMethod_f_1a54e370(var1, 1);
         Logger.recoverMethod_d_e925fa09("ThirdPushManager", "use cache config register");
         this.recoverMethod_a_db49031b(
            var1,
            var5,
            RecoverTypea2146d45.recoverMethod_h_a4ea6d43(var1, var4),
            RecoverTypea2146d45.recoverMethod_i_a4ea6d43(var1, var4),
            RecoverTypea2146d45.recoverMethod_j_a4ea6d43(var1, var4)
         );
      }

      RecoverType9b4c5724.recoverMethod_a_14f3cf61(var1, var2, new RecoverType9b4c5723() {
         @Override
         public void recoverMethod_a_5ffc00fd(Context var1, String var2x) {
            if (TextUtils.isEmpty(var2x)) {
               Logger.recoverMethod_w_e925fa09("ThirdPushManager", "plugin response config is empty");
            } else {
               try {
                  JSONObject var3x = new JSONObject(var2x);
                  String var4x = var3x.optString("j_key");
                  String var5x = var3x.optString("m_key");
                  String var6 = var3x.optString("m_id");
                  String var7 = var3x.optString("m_scrt");
                  RecoverTypea2146d45.recoverMethod_b_9d23c587(var1, var4, var4x);
                  RecoverTypea2146d45.recoverMethod_c_9d23c587(var1, var4, var5x);
                  RecoverTypea2146d45.recoverMethod_d_9d23c587(var1, var4, var6);
                  RecoverTypea2146d45.recoverMethod_e_9d23c587(var1, var4, var7);
                  if (TextUtils.isEmpty(var5)) {
                     Logger.recoverMethod_d_e925fa09("ThirdPushManager", "start register sdk, use appKey: " + var4x);
                     RecoverTypea2146d45.recoverMethod_f_1a54e370(var1, 2);
                     RecoverType9b4c5727.this.recoverMethod_a_db49031b(var1, var4x, var5x, var6, var7);
                  } else if (!TextUtils.equals(var4x, var5)) {
                     Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "push appKey change, old: " + var5 + ", new: " + var4x);
                  }
               } catch (Throwable var8) {
                  Logger.recoverMethod_w_e925fa09("ThirdPushManager", "parsing plugin config failed, " + var8.getMessage());
               }
            }
         }

         @Override
         public void recoverMethod_a_9b75() {
            Logger.recoverMethod_w_e925fa09("ThirdPushManager", "plugin config request failed");
         }
      });
   }

   private void recoverMethod_a_db49031b(Context var1, String var2, String var3, String var4, String var5) {
      JPushConfigEx var6 = new JPushConfigEx();
      var6.setjAppKey(var2);
      byte var7 = this.recoverMethod_e_f0554ddf(var1);
      switch (var7) {
         case 0:
         case 6:
         case 8:
         default:
            break;
         case 1:
            var6.setXmAppKey(var3);
            var6.setXmAppId(var4);
            break;
         case 2:
            var6.setHwAppId(var4);
            break;
         case 3:
            var6.setMzAppKey(var3);
            var6.setMzAppId(var4);
            break;
         case 4:
            var6.setOppoAppKey(var3);
            var6.setOppoAppId(var4);
            var6.setOppoAppSecret(var5);
            break;
         case 5:
            var6.setVivoAppKey(var3);
            var6.setVivoAppId(var4);
            break;
         case 7:
            var6.setHonorAppId(var4);
      }

      JPushInterface.init(var1, var6);
   }

   public void recoverMethod_a_aac1ff8b(Context var1, Bundle var2, String var3) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "registerOnMainProcess");
      if (RecoverType2e4ceff0.recoverMethod_a_9b79()) {
         if (!JCoreHelper.isAuth(var1)) {
            Logger.recoverMethod_e_e925fa09("ThirdPushManager", "register user don't auth, so return init");
         } else {
            this.recoverMethod_a_f0554df3(var1);
            if (!RecoverTypea2146d45.recoverMethod_e_f0554df7(var1.getApplicationContext())) {
               try {
                  if (var2 != null) {
                     Serializable var4 = var2.getSerializable("config");
                     JPushConfigEx var5 = null;
                     if (var4 instanceof JPushConfigEx) {
                        var5 = (JPushConfigEx)var4;
                     } else if (var4 instanceof JPushConfig) {
                        var5 = new JPushConfigEx((JPushConfig)var4);
                     }

                     if (null != var5) {
                        var2.putString("MEIZU_APPID", var5.getMzAppId());
                        var2.putString("MEIZU_APPKEY", var5.getMzAppKey());
                        var2.putString("OPPO_APPKEY", var5.getOppoAppKey());
                        var2.putString("OPPO_APPID", var5.getOppoAppId());
                        var2.putString("OPPO_APPSECRET", var5.getOppoAppSecret());
                        var2.putString("XIAOMI_APPID", var5.getXmAppId());
                        var2.putString("XIAOMI_APPKEY", var5.getXmAppKey());
                        var2.putString("VIVO_APPKEY", var5.getVivoAppKey());
                        var2.putString("VIVO_APPID", var5.getVivoAppId());
                        var2.putString("HW_APPID", var5.getHwAppId());
                        var2.putString("HONOR_APPID", var5.getHonorAppId());
                     }
                  }

                  this.recoverField_f = var2;
               } catch (Throwable var7) {
               }

               for (Object recoveryElement_271 : this.recoverField_b) {
                  JThirdPlatFormInterface var10 = (JThirdPlatFormInterface)recoveryElement_271;
                  try {
                     if (TextUtils.equals(RecoverType9b4c5ae5.recoverMethod_d_e501eb0d(var1), var1.getPackageName())) {
                        this.recoverMethod_a_def2981d(var1, var10);
                     }

                     if (RecoverTypea2146d45.recoverMethod_u_f0554de6(var1) == 2) {
                        this.recoverMethod_a_e7235ab3(var1, var10.getRomType(var1), var10.getToken(var1));
                     }
                  } catch (Throwable var8) {
                     if (this.recoverMethod_a_879483a4(var8)) {
                        if (RecoverType2e4ceff0.recoverField_c >= 238) {
                           JCoreHelper.futureExecutor(var1, new RecoverType9b4c5aea("ThirdPushManager#loadThirdPush") {
                              @Override
                              public void recoverMethod_a_9b75() {
                                 throw new RuntimeException(var8);
                              }
                           });
                        } else {
                           new Thread(new Runnable() {
                              @Override
                              public void run() {
                                 throw new RuntimeException(var8);
                              }
                           }).start();
                        }
                     }

                     Logger.recoverMethod_ww_1765c98c("ThirdPushManager", "Third push register failed#", var8);
                  }
               }
            } else {
               Logger.recoverMethod_d_e925fa09("ThirdPushManager", "push has close");
            }
         }
      }
   }

   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "resumePushOnMainProcess");
      if (RecoverType2e4ceff0.recoverMethod_a_9b79()) {
         this.recoverMethod_a_f0554df3(var1);

         for (Object recoveryElement_314 : this.recoverField_b) {
            JThirdPlatFormInterface var4 = (JThirdPlatFormInterface)recoveryElement_314;
            var4.resumePush(var1);
         }
      }
   }

   public void recoverMethod_a_3f52113a(Context var1, int var2, String var3) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "clearNotificationOnMainProcess");
      if (RecoverType2e4ceff0.recoverMethod_a_9b79()) {
         this.recoverMethod_a_f0554df3(var1);

         for (Object recoveryElement_325 : this.recoverField_b) {
            JThirdPlatFormInterface var5 = (JThirdPlatFormInterface)recoveryElement_325;
            var5.clearNotification(var1, var2);
         }
      }
   }

   public void recoverMethod_b_5ffc00fd(Context var1, String var2) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "clearAllNotificationOnMainProcess");
      if (RecoverType2e4ceff0.recoverMethod_a_9b79()) {
         this.recoverMethod_a_f0554df3(var1);

         for (Object recoveryElement_336 : this.recoverField_b) {
            JThirdPlatFormInterface var4 = (JThirdPlatFormInterface)recoveryElement_336;
            var4.clearAllNotification(var1);
         }
      }
   }

   public void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "stopPushOnMainProcess");
      if (RecoverType2e4ceff0.recoverMethod_a_9b79()) {
         this.recoverMethod_a_f0554df3(var1);

         for (Object recoveryElement_347 : this.recoverField_b) {
            JThirdPlatFormInterface var4 = (JThirdPlatFormInterface)recoveryElement_347;
            var4.stopPush(var1);
         }
      }
   }

   public void recoverMethod_a_bb994ac1(Context var1, String var2, Bundle var3, String var4) {
      try {
         Logger.recoverMethod_d_e925fa09(
            "ThirdPushManager", "doAction,action:" + var2 + ",bundle:" + var3 + ",enable:" + RecoverType2e4ceff0.recoverMethod_a_9b79()
         );
         if (!RecoverType2e4ceff0.recoverMethod_a_9b79()) {
            return;
         }

         this.recoverMethod_d_d19840b7(var1, var2, var3);
      } catch (Throwable var6) {
         Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "doAction failed:" + var6.getMessage());
      }
   }

   private void recoverMethod_d_d19840b7(final Context var1, final String var2, final Bundle var3) {
      try {
         JCoreHelper.majorExecutor(
            var1,
            "ThirdPushManager",
            new RecoverType9b4c5aea("ThirdPushManager#doAction") {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     if (!TextUtils.isEmpty(var2)) {
                        if (var2.equals("action_notification_arrived")) {
                           RecoverType9b4c5727.Memberf9932e25 var1x = RecoverType9b4c5727.this.recoverMethod_a_c5ff6aeb(var3);
                           if (var1x != null) {
                              RecoverType9b4c5726.recoverMethod_a_87826923(
                                 var1, var1x.recoverField_c, var1x.recoverField_a, var1x.recoverField_b, var1x.recoverField_d, 1
                              );
                           }
                        } else if (var2.equals("action_notification_clicked")) {
                           RecoverType9b4c5727.Memberf9932e25 var4 = RecoverType9b4c5727.this.recoverMethod_a_c5ff6aeb(var3);
                           if (var4 != null) {
                              RecoverType9b4c5726.recoverMethod_a_87826923(
                                 var1, var4.recoverField_c, var4.recoverField_a, var4.recoverField_b, var4.recoverField_d, 0
                              );
                           }
                        } else if (var2.equals("action_notification_custom_button_clcked")) {
                           RecoverType9b4c5727.Memberf9932e25 var5 = RecoverType9b4c5727.this.recoverMethod_a_c5ff6aeb(var3);
                           if (var5 != null) {
                              RecoverType9b4c5726.recoverMethod_b_87826923(
                                 var1, var5.recoverField_c, var5.recoverField_a, var5.recoverField_b, var5.recoverField_d, 3
                              );
                           }
                        } else if (var2.equals("action_notification_unshow")) {
                           RecoverType9b4c5727.Memberf9932e25 var6 = RecoverType9b4c5727.this.recoverMethod_a_c5ff6aeb(var3);
                           if (var6 != null) {
                              RecoverType9b4c5726.recoverMethod_a_87826923(
                                 var1, var6.recoverField_c, var6.recoverField_a, var6.recoverField_b, var6.recoverField_d, 2
                              );
                           }
                        } else if (!var2.equals("action_notification_show")) {
                           if (var2.equals("action_register_token")) {
                              if (!RecoverType9b4c5727.this.recoverField_g) {
                                 Logger.recoverMethod_d_e925fa09("ThirdPushManager", "is not init cache action:" + var2 + ",bundle:" + var3);
                                 RecoverType9b4c5727.recoverField_d.put(var2, var3);
                                 return;
                              }

                              if (var3 != null) {
                                 String var7 = var3.getString("token");
                                 byte var2x = var3.getByte("platform", (byte)-1);
                                 RecoverType9b4c5727.this.recoverMethod_a_e7235ab3(var1, var2x, var7);
                              }
                           } else if (var2.equals("action_send_exception")) {
                              if (!RecoverType9b4c5727.this.recoverField_g) {
                                 Logger.recoverMethod_d_e925fa09("ThirdPushManager", "is not init cache action:" + var2 + ",bundle:" + var3);
                                 RecoverType9b4c5727.recoverField_d.put(var2, var3);
                                 return;
                              }

                              if (var3 != null) {
                                 RecoverType9b4c5727.this.recoverMethod_d_69c77401(var1, var3);
                              }
                           }
                        }
                     }
                  } catch (Throwable var3x) {
                     Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "doAction failed internal:" + var3x.getMessage());
                  }
               }
            }
         );
      } catch (Throwable var5) {
         Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "doAction failed:" + var5.getMessage());
      }
   }

   private void recoverMethod_d_69c77401(Context var1, Bundle var2) {
      try {
         if (!RecoverType2e4ceff0.recoverMethod_a_9b79()) {
            return;
         }

         if (var1 == null) {
            var1 = JPushConstants.mApplicationContext;
         }

         if (var1 == null) {
            Logger.recoverMethod_e_e925fa09("ThirdPushManager", "context was null");
            return;
         }

         this.recoverMethod_a_f0554df3(var1);
         var2.putString(recoverField_a, "1");
         JCoreHelper.runActionWithService(var1, "JPUSH", "send_exception_stats", var2);
      } catch (Throwable var4) {
      }
   }

   public void recoverMethod_b_3f52113a(Context var1, int var2, String var3) {
      try {
         Logger.recoverMethod_dd_e925fa09("ThirdPushManager", "[callBackTokenToUser] platform:" + var2 + ",token:" + var3);
         Bundle var4 = new Bundle();
         var4.putInt("platform", var2);
         var4.putString("token", var3);
         RecoverType2e4d193c.recoverMethod_a_4d98d517(var1, 10000, 0, "", var4);
      } catch (Throwable var5) {
         Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "callBackTokenToUser failed:" + var5.getMessage());
      }
   }

   public void recoverMethod_a_d19840b7(Context var1, String var2, Bundle var3) {
      try {
         JCoreHelper.runActionWithService(var1, "JPUSH", "third_unbind", var3);
      } catch (Throwable var5) {
      }
   }

   public void recoverMethod_a_e7235ab3(Context var1, byte var2, String var3) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "uploadRegIdOnMainProcess");
      if (RecoverType2e4ceff0.recoverMethod_a_9b79()) {
         if (var1 == null) {
            var1 = JPushConstants.mApplicationContext;
         }

         if (var1 == null) {
            Logger.recoverMethod_e_e925fa09("ThirdPushManager", "context was null");
         } else {
            Logger.recoverMethod_ii_e925fa09("ThirdPushManager", "uploadRegID regid:" + var3);
            this.recoverMethod_a_f0554df3(var1);
            Bundle var4 = new Bundle();
            var4.putString("token", var3);
            var4.putByte("platform", var2);
            JCoreHelper.runActionWithService(var1, "JPUSH", "third_push_upload_regid", var4);
         }
      }
   }

   private RecoverType9b4c5727.Memberf9932e25 recoverMethod_a_c5ff6aeb(Bundle var1) {
      RecoverType9b4c5727.Memberf9932e25 var2 = null;
      if (var1 != null) {
         var2 = new RecoverType9b4c5727.Memberf9932e25();
         var2.recoverField_c = var1.getString("data");
         var2.recoverField_a = var1.getString("msg_id");
         var2.recoverField_b = var1.getInt("noti_id", 0);
         var2.recoverField_d = var1.getByte("platform", (byte)-1);
      }

      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "parse third messgae:" + var2);
      return var2;
   }

   public void recoverMethod_b_9d23c587(Context var1, String var2, String var3) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "uploadThirdTokenFromUserOnMainProcess");
      byte var4 = this.recoverMethod_e_f0554ddf(var1);
      if (var4 != 0) {
         if (TextUtils.isEmpty(var2)) {
            this.recoverMethod_a_fd48ae81(var1, var4, null, true);
         } else {
            Logger.recoverMethod_d_e925fa09("ThirdPushManager", "uploadThirdTokenFromUser platform：" + var4);
            this.recoverMethod_a_e7235ab3(var1, var4, var2);
         }
      }
   }

   public void recoverMethod_b_d19840b7(Context var1, String var2, Bundle var3) {
      Logger.recoverMethod_d_e925fa09(
         "ThirdPushManager", "triggerTokenUploadOnMainProcess bundle=" + var3 + " isThirdEnable=" + RecoverType2e4ceff0.recoverMethod_a_9b79()
      );

      try {
         if (!RecoverType2e4ceff0.recoverMethod_a_9b79()) {
            return;
         }

         Logger.recoverMethod_d_e925fa09("ThirdPushManager", "triggerTokenUploadOnMainProcess start on main");
         this.recoverMethod_a_f0554df3(var1);
         Logger.recoverMethod_d_e925fa09("ThirdPushManager", "[triggerTokenUploadOnMainProcess] , platFormInterfaceList size: " + this.recoverField_b.size());

         for (Object recoveryElement_545 : this.recoverField_b) {
            JThirdPlatFormInterface var5 = (JThirdPlatFormInterface)recoveryElement_545;
            byte var6 = var5.getRomType(var1);
            String var7 = var5.getToken(var1);
            Logger.recoverMethod_d_e925fa09("ThirdPushManager", "triggerTokenUploadOnMainProcess token=" + var7 + " romType=" + var6);
            if (!TextUtils.isEmpty(var7)) {
               this.recoverMethod_a_e7235ab3(var1, var6, var7);
            } else {
               if (!JCoreHelper.isAuth(var1)) {
                  Logger.recoverMethod_w_e925fa09("ThirdPushManager", "user don't auth, so return init");
                  return;
               }

               this.recoverMethod_a_def2981d(var1, var5);
            }
         }
      } catch (Throwable var8) {
         if (this.recoverMethod_a_879483a4(var8)) {
            if (RecoverType2e4ceff0.recoverField_c >= 238) {
               JCoreHelper.futureExecutor(var1, new RecoverType9b4c5aea("ThirdPushManager#loadThirdPush") {
                  @Override
                  public void recoverMethod_a_9b75() {
                     throw new RuntimeException(var8);
                  }
               });
            } else {
               new Thread(new Runnable() {
                  @Override
                  public void run() {
                     throw new RuntimeException(var8);
                  }
               }).start();
            }
         }

         Logger.recoverMethod_d_e925fa09("ThirdPushManager", "triggerTokenUploadOnMainProcess on main process throwable=" + var8);
      }
   }

   private void recoverMethod_a_def2981d(Context var1, JThirdPlatFormInterface var2) {
      try {
         if (var2 == null) {
            return;
         }

         if (recoverField_e.containsKey(var2)) {
            Long var3 = (Long)recoverField_e.get(var2);
            if (Math.abs(System.currentTimeMillis() - var3) > this.recoverField_i) {
               Logger.recoverMethod_d_e925fa09("ThirdPushManager", "start register > " + this.recoverField_i + " mills. platImpl=" + var2);
               var2.register(var1, this.recoverField_f);
               recoverField_e.put(var2, System.currentTimeMillis());
            } else {
               Logger.recoverMethod_d_e925fa09("ThirdPushManager", "is registering,no need to register again < " + this.recoverField_i + " mills");
            }
         } else {
            Logger.recoverMethod_d_e925fa09("ThirdPushManager", "start register platImpl=" + var2);
            var2.register(var1, this.recoverField_f);
            recoverField_e.put(var2, System.currentTimeMillis());
         }
      } catch (Throwable var4) {
      }
   }

   private void recoverMethod_e_5ffc00fd(Context var1, String var2) {
      this.recoverMethod_e_d19840b7(var1, var2, new Bundle());
   }

   private void recoverMethod_e_d19840b7(Context var1, String var2, Bundle var3) {
      try {
         if (RecoverType9b4c5ae5.recoverMethod_c_f0554df7(var1)) {
            Logger.recoverMethod_d_e925fa09("ThirdPushManager", "process is main, do dealMessage action=" + var2 + " bundle=" + var3);
            this.recoverMethod_c_d19840b7(var1, var2, var3);
         } else {
            String var4 = JPushConstants.getMessageReceiverClass(var1);
            if (!TextUtils.isEmpty(var4)) {
               Intent var5 = new Intent(var1, Class.forName(var4));
               var5.setAction(var2);
               if (var3 == null) {
                  var3 = new Bundle();
               }

               var5.putExtras(var3);
               var5.setPackage(var1.getPackageName());
               Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "send intent=" + var5);
               RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var1, var5);
            }
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "send " + var2 + " failed:" + var6);
      }
   }

   public void recoverMethod_a_c4d2ecb0(Context var1, Intent var2) {
      if (var2 != null) {
         String var3 = var2.getAction();
         Bundle var4 = var2.getExtras();
         if (var3.equals(RecoverType9b4c5ae5.recoverMethod_e_a4ea6d43(var1, "cmd_msg"))) {
            int var5 = var2.getIntExtra("code", -2);
            int var6 = var2.getIntExtra("cmd", -2);
            String var7 = var2.getStringExtra("message");
            Logger.recoverMethod_d_e925fa09("ThirdPushManager", "registerOnMainProcess :" + var5 + ",cmd:" + var6 + ",message:" + var7);
            if (var6 == 1 && !this.recoverField_j) {
               this.recoverField_j = true;
               this.recoverMethod_a_aac1ff8b(var1, var4, var3);
               Logger.recoverMethod_d_e925fa09("ThirdPushManager", "start to send cache map=" + recoverField_d);

               for (Object recoveryElement_650 : recoverField_d.entrySet()) {
                  Entry var9 = (Entry)recoveryElement_650;
                  this.recoverMethod_d_d19840b7(var1, (String)var9.getKey(), (Bundle)var9.getValue());
               }

               recoverField_d.clear();
               Logger.recoverMethod_d_e925fa09("ThirdPushManager", " cache map send finish map=" + recoverField_d);
            }
         } else {
            this.recoverMethod_c_d19840b7(var1, var3, var4);
         }
      }
   }

   public void recoverMethod_c_d19840b7(Context var1, String var2, Bundle var3) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "dealMessage action=" + var2 + " bundle=" + var3);
      if ("intent.plugin.platform.REFRESSH_REGID".equals(var2)) {
         this.recoverMethod_b_d19840b7(var1, var2, var3);
      } else if ("third_resume".equals(var2)) {
         this.recoverMethod_a_5ffc00fd(var1, var2);
      } else if ("third_stop".equals(var2)) {
         this.recoverMethod_c_5ffc00fd(var1, var2);
      } else if ("third_all_notify".equals(var2)) {
         this.recoverMethod_b_5ffc00fd(var1, var2);
      } else if ("clear_by_id".equals(var2)) {
         if (var3 != null) {
            int var4 = var3.getInt("id");
            this.recoverMethod_a_3f52113a(var1, var4, var2);
         }
      } else if ("set_third_token".equals(var2)) {
         if (null != var3) {
            String var7 = var3.getString("third_token");
            this.recoverMethod_b_9d23c587(var1, var7, var2);
         }
      } else if ("dy_register".equals(var2)) {
         this.recoverMethod_a_9d23c587(var1, var3.getString("dev_key"), var2);
      } else if ("third_enable".equals(var2)) {
         boolean var8 = var3.getBoolean("enable", true);

         try {
            JPushConstants.THIRD_ENABLE = var8;
         } catch (Throwable var6) {
         }

         if (var8) {
            this.recoverMethod_b_d19840b7(var1, var2, var3);
         }
      } else if ("action_notification_arrived".equals(var2)
         || "action_notification_clicked".equals(var2)
         || "action_notification_unshow".equals(var2)
         || "action_notification_show".equals(var2)
         || "action_register_token".equals(var2)
         || "action_send_exception".equals(var2)) {
         this.recoverMethod_a_bb994ac1(var1, var2, var3, "");
      }
   }

   public void recoverMethod_a_69c77401(Context var1, Bundle var2) {
      try {
         Logger.recoverMethod_i_e925fa09("ThirdPushManager", "syncPlatformsOnServiceProcess bundle=" + var2);
         this.recoverField_k = var2.getByteArray("platform");
      } catch (Throwable var4) {
      }
   }

   public byte recoverMethod_d_5ffc00e9(Context var1, String var2) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "clearThirdNotificaitonOnServiceProcess");
      Bundle var3 = new Bundle();
      if (this.recoverMethod_e_f0554ddf(var1) == 1) {
         var3.putInt("id", RecoverType2e4d193c.recoverMethod_a_afa300d7(var2, 1));
         this.recoverMethod_e_d19840b7(var1, "clear_by_id", var3);
         return 1;
      } else {
         return -1;
      }
   }

   public String recoverMethod_b_e501eb0d(Context var1) {
      Logger.recoverMethod_i_e925fa09("ThirdPushManager", "getThirdTokenOnServiceProcess");

      try {
         this.recoverField_k = this.recoverMethod_g_1a54714a(var1);

         for (byte var5 : this.recoverField_k) {
            if (var5 != 8) {
               return (String)RecoverTypea0793a19.get(var1, Key.ThirdPush_RegID(var5));
            }
         }
      } catch (Throwable var6) {
      }

      return "";
   }

   public void recoverMethod_a_1a54c929(Context var1, byte var2) {
      try {
         this.recoverField_k = this.recoverMethod_g_1a54714a(var1);
         if (this.recoverField_k == null) {
            this.recoverField_k = new byte[1];
            this.recoverField_k[0] = var2;
         } else {
            boolean var3 = false;

            for (byte var7 : this.recoverField_k) {
               if (var7 == var2) {
                  var3 = true;
                  break;
               }
            }

            if (!var3) {
               byte[] var9 = new byte[this.recoverField_k.length + 1];
               System.arraycopy(this.recoverField_k, 0, var9, 0, this.recoverField_k.length);
               var9[this.recoverField_k.length] = var2;
               this.recoverField_k = var9;
            }
         }
      } catch (Throwable var8) {
      }
   }

   public void recoverMethod_b_69c77401(Context var1, Bundle var2) {
      try {
         this.recoverField_k = this.recoverMethod_g_1a54714a(var1);
         Logger.recoverMethod_i_e925fa09("ThirdPushManager", "unbindOnServiceProcess platforms=" + Arrays.toString(this.recoverField_k));

         for (byte var6 : this.recoverField_k) {
            RecoverTypea0793a19.set(var1, Key.ThirdPush_RegUpload(var6).set(false));
            RecoverTypea0793a19.set(var1, Key.ThirdPush_RegID(var6).set(null));
            this.recoverMethod_a_fd48ae81(var1, var6, null, true);
         }
      } catch (Throwable var7) {
      }
   }

   public void recoverMethod_c_f0554df3(Context var1) {
      RecoverType9b4c5725.recoverMethod_a_d8c3f6e1().recoverMethod_a_f0554df3(var1);
      Logger.recoverMethod_i_e925fa09(
         "ThirdPushManager", "uploadRegIdAfterLoginOnServiceProcess, then send broadcast action=intent.plugin.platform.REFRESSH_REGID"
      );
      this.recoverMethod_e_5ffc00fd(var1, "intent.plugin.platform.REFRESSH_REGID");
   }

   public void recoverMethod_c_69c77401(Context var1, Bundle var2) {
      Logger.recoverMethod_i_e925fa09("ThirdPushManager", "uploadOnServiceProcess bundle=" + var2);
      if (var1 == null) {
         var1 = JPushConstants.mApplicationContext;
      }

      if (var1 == null) {
         Logger.recoverMethod_e_e925fa09("ThirdPushManager", "context was null");
      } else if (var2 == null) {
         Logger.recoverMethod_ww_e925fa09("ThirdPushManager", "bundle is null");
      } else {
         byte var3 = var2.getByte("platform", (byte)0);
         String var4 = var2.getString("token");
         this.recoverMethod_a_fd48ae81(var1, var3, var4, false);
      }
   }

   private void recoverMethod_a_fd48ae81(Context var1, byte var2, String var3, boolean var4) {
      Logger.recoverMethod_d_e925fa09("ThirdPushManager", "sendUpdateRegIDRequest, WhichPlatform:" + var2 + ",regID:" + var3);
      RecoverType9b4c5725.recoverMethod_a_d8c3f6e1().recoverMethod_a_fd48ae81(var1, var2, var3, var4);
   }

   public byte recoverMethod_d_f0554ddf(Context var1) {
      this.recoverField_k = this.recoverMethod_g_1a54714a(var1);
      byte var2 = 0;

      try {
         if (this.recoverField_k == null) {
            Logger.recoverMethod_i_e925fa09("ThirdPushManager", " mPlatforms is null return platforms=0");
            return var2;
         }

         Logger.recoverMethod_i_e925fa09("ThirdPushManager", " romType all mPlatforms =" + this.recoverField_k);

         for (byte var6 : this.recoverField_k) {
            var2 |= var6;
            String var7 = (String)RecoverTypea0793a19.get(var1, Key.ThirdPush_RegID(var6));
            boolean var8 = (Boolean)RecoverTypea0793a19.get(var1, Key.ThirdPush_RegUpload(var6));
            Logger.recoverMethod_i_e925fa09("ThirdPushManager", " romType first =" + var6);
            Logger.recoverMethod_i_e925fa09("ThirdPushManager", " romType with 0 =" + var2);
            Logger.recoverMethod_i_e925fa09("ThirdPushManager", " token =" + var7);
            Logger.recoverMethod_i_e925fa09("ThirdPushManager", " regUploaded =" + var8);
            if (var6 == 8) {
               var2 = (byte)(var2 | 8);
               if (var8 && !TextUtils.isEmpty(var7)) {
                  var2 = (byte)(var2 | 32);
               }
            } else {
               if (var6 == 2) {
                  var2 = (byte)(var2 | 64);
               }

               if (!var8 || TextUtils.isEmpty(var7)) {
                  Logger.recoverMethod_i_e925fa09("ThirdPushManager", " romType before 0x80  =" + var2);
                  var2 = (byte)(var2 | 128);
                  Logger.recoverMethod_i_e925fa09("ThirdPushManager", " romType with 0x80  =" + var2);
               }
            }
         }
      } catch (Throwable var9) {
      }

      Logger.recoverMethod_i_e925fa09("ThirdPushManager", "getPlatformsOnServiceProcess final platforms=" + var2);
      return var2;
   }

   private byte[] recoverMethod_g_1a54714a(Context var1) {
      if (this.recoverField_k == null || this.recoverField_k.length == 0) {
         this.recoverField_k = this.recoverMethod_h_1a54714a(var1);
      }

      return this.recoverField_k;
   }

   private byte[] recoverMethod_h_1a54714a(Context var1) {
      byte[] var2 = null;

      try {
         byte var3 = this.recoverMethod_e_f0554ddf(var1);
         boolean var4 = this.recoverMethod_k_f0554df7(var1);
         if (var4) {
            if (var3 != 0) {
               var2 = new byte[]{8, var3};
            } else {
               var2 = new byte[]{8};
            }
         } else if (var3 != 0) {
            var2 = new byte[]{var3};
         }
      } catch (Throwable var5) {
      }

      return var2;
   }

   public byte recoverMethod_e_f0554ddf(Context var1) {
      try {
         String var2 = RecoverType9b4c5ae5.recoverMethod_c_73cf11cb().toLowerCase();
         if (var2.contains("xiaomi") || var2.contains("blackshark")) {
            return 1;
         }

         if (var2.contains("huawei")) {
            return 2;
         }

         if (var2.contains("meizu")) {
            return 3;
         }

         if (var2.contains("oppo") || var2.contains("realme") || var2.contains("oneplus")) {
            return 4;
         }

         if (var2.contains("vivo")) {
            return 5;
         }

         if (var2.contains("honor")) {
            if (!this.recoverMethod_i_f0554df7(var1) && this.recoverMethod_c_9b79()) {
               return 2;
            }

            return 7;
         }

         if (var2.contains("nio")) {
            return 16;
         }
      } catch (Throwable var3) {
      }

      return 0;
   }

   private boolean recoverMethod_i_f0554df7(Context var1) {
      if (this.recoverField_l == null) {
         this.recoverField_l = this.recoverMethod_j_f0554df7(var1);
      }

      return this.recoverField_l;
   }

   private boolean recoverMethod_j_f0554df7(Context var1) {
      try {
         Class var2 = Class.forName("com.hihonor.push.sdk.HonorPushClient");
         Method var3 = var2.getMethod("getInstance");
         Object var4 = var3.invoke(null);
         Class[] var5 = new Class[]{Context.class};
         Method var6 = var2.getMethod("checkSupportHonorPush", var5);
         boolean var7 = (Boolean)var6.invoke(var4, var1);
         Logger.recoverMethod_d_e925fa09("ThirdPushManager", "isSupportHonorPush =" + var7);
         return var7;
      } catch (Throwable var8) {
         Logger.recoverMethod_e_e925fa09("ThirdPushManager", " isSupportHonorPush error:" + var8);
         return false;
      }
   }

   private boolean recoverMethod_c_9b79() {
      if (this.recoverField_n == null) {
         this.recoverField_n = this.recoverMethod_d_9b79();
      }

      return this.recoverField_n;
   }

   private boolean recoverMethod_d_9b79() {
      String var1 = recoverMethod_a_e96ea081("ro.build.version.emui");
      Logger.recoverMethod_kd_e925fa09("ThirdPushManager", "isEmotionOs name:" + var1);
      if (!TextUtils.isEmpty(var1)) {
         String var2 = var1.toLowerCase();
         if (var2.contains("magicui") || var2.contains("magicos")) {
            Logger.recoverMethod_kd_e925fa09("ThirdPushManager", "isEmotionOs true");
            return true;
         }
      }

      return false;
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      try {
         Class[] var1 = new Class[]{String.class};
         Object[] var2 = new Object[]{var0};
         Class var3 = Class.forName("android.os.SystemProperties");
         Method var4 = var3.getDeclaredMethod("get", var1);
         String var5 = (String)var4.invoke(var3, var2);
         Logger.recoverMethod_d_e925fa09("ThirdPushManager", "get " + var0 + " version is:" + var5);
         return var5;
      } catch (Throwable var6) {
         Logger.recoverMethod_e_e925fa09("ThirdPushManager", " get " + var0 + "wrong error:" + var6.getMessage());
         return "";
      }
   }

   private boolean recoverMethod_k_f0554df7(Context var1) {
      if (this.recoverField_m == null) {
         this.recoverField_m = this.recoverMethod_l_f0554df7(var1);
      }

      return this.recoverField_m;
   }

   private static boolean recoverMethod_f_5ffc0101(Context var0, String var1) {
      try {
         PackageManager var2 = var0.getPackageManager();
         ComponentName var3 = new ComponentName(var0.getPackageName(), var1);
         var2.getServiceInfo(var3, 128);
         return true;
      } catch (Throwable var4) {
         return false;
      }
   }

   private boolean recoverMethod_l_f0554df7(Context var1) {
      try {
         if (recoverMethod_f_5ffc0101(var1, "cn.jpush.android.service.PluginFCMMessagingService")) {
            if (VERSION.SDK_INT < 14) {
               return false;
            }

            try {
               boolean var2 = recoverMethod_e_9b79();
               Class var3 = Class.forName("com.google.android.gms.common.GoogleApiAvailabilityLight");
               Method var4 = var3.getMethod("getInstance");
               Object var5 = var4.invoke(null);
               Method var6 = var3.getMethod("isGooglePlayServicesAvailable", Context.class);
               int var7 = (Integer)var6.invoke(var5, var1);
               if (var2 && 0 == var7) {
                  return true;
               }
            } catch (Throwable var8) {
            }
         }
      } catch (Throwable var9) {
      }

      return false;
   }

   private static boolean recoverMethod_e_9b79() throws java.lang.ClassNotFoundException {
      Class var0 = Class.forName("com.google.firebase.messaging.FirebaseMessaging");
      return true;
   }

   static {
      recoverField_c.put("cn.jpush.android.thirdpush.meizu.MeizuPushManager", (byte)3);
      recoverField_c.put("cn.jpush.android.thirdpush.xiaomi.XMPushManager", (byte)1);
      recoverField_c.put("cn.jpush.android.thirdpush.huawei.HWPushManager", (byte)2);
      recoverField_c.put("cn.jpush.android.thirdpush.fcm.FCMPushManager", (byte)8);
      recoverField_c.put("cn.jpush.android.thridpush.oppo.OPushManager", (byte)4);
      recoverField_c.put("cn.jpush.android.thirdpush.vivo.VivoPushManager", (byte)5);
      recoverField_c.put("cn.jpush.android.thirdpush.honor.HonorPushManager", (byte)7);
      recoverField_c.put("cn.jpush.android.thirdpush.nio.NioPushManager", (byte)16);
      recoverField_c.put(RecoverTypefa94ae12.class.getName(), (byte)6);
   }

   private static final class Memberf9932e24 {
      private static final RecoverType9b4c5727 recoverField_a = new RecoverType9b4c5727();
   }

   class Memberf9932e25 {
      String recoverField_a;
      int recoverField_b;
      String recoverField_c;
      byte recoverField_d;

      @Override
      public String toString() {
         return "ThirdMessage{msgid='"
            + this.recoverField_a
            + '\''
            + ", notiId="
            + this.recoverField_b
            + ", content='"
            + this.recoverField_c
            + '\''
            + ", platform="
            + this.recoverField_d
            + '}';
      }
   }
}
