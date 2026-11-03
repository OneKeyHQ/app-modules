package cn.jiguang.ah;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.ai.RecoverTypeaf2a8be0;
import cn.jiguang.ai.RecoverTypeaf2a8be1;
import cn.jiguang.ai.RecoverTypeaf2a8be2;
import cn.jiguang.ai.RecoverTypeaf2a8be3;
import cn.jiguang.ak.RecoverTypeaf2a9362;
import cn.jiguang.ak.RecoverTypeaf2a9363;
import cn.jiguang.ak.RecoverTypeaf2a9364;
import cn.jiguang.ak.RecoverTypeaf2a9366;
import cn.jiguang.ak.RecoverTypeaf2a9367;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.i.RecoverType5a69fd6;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.json.JSONObject;

public class RecoverTypeaf2a881f extends RecoverTypeaf2b0401 {
   private Context recoverField_a;
   private RecoverTypeaf2a8be0 recoverField_b;
   private String recoverField_c;
   private final HashMap recoverField_d = new HashMap();
   private static long recoverField_e;

   public RecoverTypeaf2a881f(Context var1, String var2) {
      this.recoverField_a = var1;
      this.recoverField_c = var2;
      this.recoverField_h = "JWake";
   }

   @Override
   public void recoverMethod_a_9b75() {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake with:" + this.recoverField_c);
         boolean var1 = RecoverType5a68d11.recoverMethod_a_f0554df7(this.recoverField_a);
         if (!var1) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "can't w because not r yet");
            return;
         }

         this.recoverField_b = RecoverTypeaf2a9363.recoverMethod_b_5edd4c44(this.recoverField_a);
         this.recoverMethod_d_9b75();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use config:" + this.recoverField_b);
         recoverMethod_a_f93da84e(this.recoverField_a, this.recoverField_b);
         if (!this.recoverMethod_c_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake is disabled by user");
            return;
         }

         if (!this.recoverField_b.recoverField_a || !this.recoverField_b.recoverField_c) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake is disabled by server");
            return;
         }

         if (this.recoverField_b.recoverField_y == 7) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "all wakeup type is unsupported of app, not wakeup any package");
            return;
         }

         if (this.recoverField_b.recoverField_i && this.recoverField_c.equals("start")) {
            if (!this.recoverMethod_a_61fb9e6a(this.recoverField_b.recoverField_z)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake is disabled by unsafe package");
               return;
            }

            List var10 = this.recoverMethod_e_a9863e32();
            RecoverTypeaf2a9367.recoverMethod_a_c6923f1f().recoverMethod_a_979dd93f(this.recoverField_a, this.recoverField_b, var10);
            return;
         }

         if (!this.recoverField_b.recoverField_h) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "time disabled");
            return;
         }

         long var2 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWake");
         long var4 = this.recoverField_b.recoverField_g;
         long var6 = System.currentTimeMillis();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "[wakeUp]currentTimeMillis:" + var6 + ",lastBusinessTime:" + var2 + ",wakeInterval:" + var4);
         if (var6 - var2 >= var4) {
            if (!this.recoverMethod_a_61fb9e6a(this.recoverField_b.recoverField_z)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake is disabled by unsafe package");
               return;
            }

            List var8 = this.recoverMethod_e_a9863e32();
            RecoverTypeaf2a9367.recoverMethod_a_c6923f1f().recoverMethod_a_979dd93f(this.recoverField_a, this.recoverField_b, var8);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "need not wake up");
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "wake failed:" + var9.getMessage());
      }
   }

   private boolean recoverMethod_a_61fb9e6a(List var1) {
      try {
         if (var1 != null && var1.size() > 0) {
            for (Object recoveryElement_126 : var1) {
               String var3 = (String)recoveryElement_126;
               if (this.recoverField_d.containsKey(var3)) {
                  Boolean var6 = (Boolean)this.recoverField_d.get(var3);
                  if (null != var6 && var6) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "checkSafeStatus blackPkgName = " + var3);
                     return false;
                  }

                  return true;
               }

               boolean var4 = RecoverTypeaf2b07c1.recoverMethod_e_5ffc0101(this.recoverField_a, var3);
               this.recoverField_d.put(var3, var4);
               if (var4) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "checkSafeStatus blackPkgName = " + var3);
                  return false;
               }
            }
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "checkSafeStatus error:" + var5);
      }

      return true;
   }

   private boolean recoverMethod_a_aad3b203(String var1) {
      if (this.recoverField_b.recoverField_p != null && this.recoverField_b.recoverField_p.contains(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", var1 + " is in black list");
         return true;
      }

      if (!TextUtils.isEmpty(this.recoverField_b.recoverField_n)) {
         if (this.recoverField_b.recoverField_n.equals("exclude")) {
            if (this.recoverField_b.recoverField_o.contains(var1)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", var1 + " is in excloude list");
               return true;
            }
         } else if (this.recoverField_b.recoverField_n.equals("include") && !this.recoverField_b.recoverField_o.contains(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", var1 + " is not in include list");
            return true;
         }
      }

      return false;
   }

   private boolean recoverMethod_c_9b79() {
      boolean var1 = RecoverType5a69fd6.recoverMethod_c_f0554df7(this.recoverField_a);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "isActionUserEnable :" + var1);
      return var1;
   }

   private void recoverMethod_d_9b75() {
      long var1 = this.recoverField_b.recoverField_l;
      recoverField_e = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWakeConfigHelper");
      long var3 = System.currentTimeMillis();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JWake", "[refeshWakeConfig] currentTimeMillis:" + var3 + ",lastBusinessTime:" + recoverField_e + ",wakeConfigInterval:" + var1
      );
      if (var3 - recoverField_e >= var1) {
         RecoverTypeaf2a8be0 var5 = recoverMethod_e_5edd4c44(this.recoverField_a);
         if (var5 != null) {
            this.recoverField_b = var5;
         }

         RecoverTypeaf2a8821.recoverMethod_a_f0554df3(this.recoverField_a);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "need not get wake config");
      }
   }

   private static void recoverMethod_a_f93da84e(Context var0, RecoverTypeaf2a8be0 var1) {
      boolean var2 = var1.recoverField_b && var1.recoverField_d;
      boolean var3 = RecoverType5a69fd6.recoverMethod_c_f0554df7(var0);
      var2 = var2 && var3;
      RecoverTypeaf2a9362.recoverMethod_a_1a552341(var0, var2);
      RecoverTypeaf2a8821.recoverMethod_a_1a54e731(var0, var1.recoverField_l);
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      try {
         if (RecoverTypeaf2a9725.recoverMethod_e_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "is only JOperate sdk reConfig");
            return;
         }

         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverTypeaf2a881f.Member8ea9011c(var0));
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "[requestConfig failed] " + var2.getMessage());
      }
   }

   public static Integer recoverMethod_b_bd10275c() {
      return 1;
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      recoverMethod_a_f0554df3(var0);
   }

   private static RecoverTypeaf2a8be0 recoverMethod_e_5edd4c44(Context var0) {
      try {
         JSONObject var1 = RecoverTypeaf2a9363.recoverMethod_a_16ce4761(var0);
         if (var1 != null) {
            RecoverTypeaf2a9363.recoverMethod_a_5ffc00fd(var0, RecoverTypeaf2b07d7.recoverMethod_b_e96ea081(var1.toString()));
            return RecoverTypeaf2a9363.recoverMethod_a_e1c9b7fc(var0, var1);
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "[requestConfigNow] failed:" + var3.getMessage());
      }

      return null;
   }

   public static Object recoverMethod_c_f23f0d9f(Context var0) {
      boolean var1 = RecoverType5a69fd6.recoverMethod_c_f0554df7(var0);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "isActionUserEnable :" + var1);
      return var1;
   }

   public static Object recoverMethod_a_328f7003(Context var0, Object var1) {
      if (var1 instanceof List) {
         List var2 = (List)var1;
         RecoverTypeaf2a8be0 var3 = RecoverTypeaf2a9363.recoverMethod_b_5edd4c44(var0);
         return RecoverTypeaf2a9364.recoverMethod_a_181df4aa(var3, var2);
      } else {
         return var1;
      }
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      try {
         if (RecoverTypeaf2a9725.recoverMethod_e_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "[doAction ] " + var1 + " : is only JOperate sdk");
            return;
         }

         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverTypeaf2a881f(var0, var1));
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "[doAction failed] " + var1 + " :" + var3.getMessage());
      }
   }

   public static List recoverMethod_a_3407328(Context var0, RecoverTypeaf2a8be0 var1, List var2) {
      if (RecoverTypeaf2a9725.recoverMethod_e_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "is only JOperate sdk cmd");
         return new ArrayList();
      }

      if (null == var2) {
         return new ArrayList();
      }

      RecoverTypeaf2a881f var3 = new RecoverTypeaf2a881f(var0, "start");
      var3.recoverField_b = var1;
      HashMap var4 = new HashMap();

      for (int var5 = 0; var5 < var2.size(); var5++) {
         RecoverTypeaf2a8be2 var6 = (RecoverTypeaf2a8be2)var2.get(var5);
         if (null != var6) {
            var4.put(var6.recoverField_a, var6);
         }
      }

      List var11 = var3.recoverMethod_a_6066f3c8(var4, null, true);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "cmdPrepareWakeTargets:" + var11);
      if (null == var11) {
         var11 = new ArrayList();
      }

      for (int var12 = 0; var12 < var2.size(); var12++) {
         RecoverTypeaf2a8be2 var7 = (RecoverTypeaf2a8be2)var2.get(var12);
         if (null != var7) {
            boolean var8 = false;

            for (int var9 = 0; var9 < var11.size(); var9++) {
               RecoverTypeaf2a8be2 var10 = (RecoverTypeaf2a8be2)var11.get(var9);
               var10.recoverField_g |= 4;
               if (var7.recoverField_a == var10.recoverField_a) {
                  var8 = true;
                  break;
               }
            }

            if (!var8) {
               var7.recoverField_g = 4;
               var11.add(var7);
            }
         }
      }

      return var11;
   }

   public static List recoverMethod_a_c98d58e3(Context var0, List var1) {
      if (RecoverTypeaf2a9725.recoverMethod_e_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "is only JOperate sdk wake");
         return null;
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "[wake] wakeTargets:" + var1);
      RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWake");
      RecoverType5a69fd6.recoverMethod_b_f0554df3(var0);
      if (var1 != null && var1.size() > 0) {
         HashMap var2 = new HashMap();
         String var3 = var0.getPackageName();
         var2.put("from_package", var3);
         JSONObject var4 = new JSONObject();

         try {
            var4.put("di", RecoverTypeaf2b07c8.recoverMethod_b_e501eb0d(var0));
         } catch (Throwable var24) {
         }

         ArrayList var5 = new ArrayList();

         for (Object recoveryElement_343 : var1) {
            RecoverTypeaf2a8be2 var7 = (RecoverTypeaf2a8be2)recoveryElement_343;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "start wake target:" + var7);
            if (var7 == null) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "wakeTarget is null, continue another wake");
            } else if (var7.recoverField_c == 2) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                  "JWake", "command this app is not allow to wake package:" + var7.recoverField_a + ",service:" + var7.recoverField_b
               );
            } else {
               try {
                  if (!TextUtils.isEmpty(var7.recoverField_l)) {
                     var4.put("u_extra", var7.recoverField_l);
                  }

                  var2.put("jg_extras", var4.toString());
               } catch (Throwable var23) {
               }

               boolean var8 = false;
               RecoverTypeaf2a8be1 var9 = new RecoverTypeaf2a8be1();
               var9.recoverField_a = var7.recoverField_a;
               if ((var7.recoverField_g & 1) != 0 && var7.recoverField_e != null && !RecoverTypeaf2a9362.recoverMethod_a_f0554df7(var0)) {
                  try {
                     Bundle var10 = RecoverTypeaf2a9364.recoverMethod_a_b000e162(var2);
                     var10.putInt("type", 8);
                     Intent var11 = var7.recoverField_e;
                     boolean var12 = recoverMethod_a_ee5748c2(var0, var10, var11);
                     if (!var12 && var7.recoverField_f != null) {
                        var11 = var7.recoverField_f;
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "will wake backupActivity:" + var11);
                        var12 = recoverMethod_a_ee5748c2(var0, var10, var11);
                     }

                     if (var12) {
                        var8 = true;
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use ACTIVITY wake up ,start activity:" + var7.recoverField_a + "," + var11);
                        var9.recoverField_b.put(8, 1);
                     } else {
                        var9.recoverField_b.put(8, 101);
                     }
                  } catch (Throwable var22) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "Fail to start activity caused by:" + var22);
                     var9.recoverField_b.put(8, 101);
                  }
               }

               if ((var7.recoverField_g & 2) != 0 && var7.recoverField_h != null) {
                  try {
                     AccountManager var31 = (AccountManager)var0.getSystemService("account");
                     if (var31 != null) {
                        boolean var34 = false;
                        AuthenticatorDescription[] var38 = var31.getAuthenticatorTypes();

                        for (int var13 = 0; var13 < var38.length; var13++) {
                           AuthenticatorDescription var14 = var38[var13];
                           if (var14.type.equals(var7.recoverField_h)) {
                              Bundle var15 = RecoverTypeaf2a9364.recoverMethod_a_b000e162(var2);
                              var15.putInt("type", 16);
                              var31.addAccount(var14.type, "cn.jiguang.sdk.wake", null, var15, null, null, null);
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use ACCOUNT wake up ,start add account to:" + var14.type);
                              var34 = true;
                              var8 = true;
                              RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWake_account");
                              break;
                           }
                        }

                        if (!var34) {
                           RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "start add account failed ,not found the target type :" + var7.recoverField_h);
                           var9.recoverField_b.put(16, 103);
                        } else {
                           var9.recoverField_b.put(16, 1);
                        }
                     } else {
                        var9.recoverField_b.put(16, 102);
                     }
                  } catch (Throwable var30) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "Fail to add account  caused by:" + var30);
                     var9.recoverField_b.put(16, 101);
                  }
               }

               if (var8) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake success, continue wake other target");
                  var5.add(var9);
               } else {
                  if ((var7.recoverField_g & 8) != 0 && !TextUtils.isEmpty(var7.recoverField_i)) {
                     try {
                        String[] var32 = var7.recoverField_i.split("&");

                        for (String var45 : var32) {
                           try {
                              Bundle var48 = RecoverTypeaf2a9364.recoverMethod_a_b000e162(var2);
                              var48.putInt("type", 64);
                              var8 = var0.startInstrumentation(new ComponentName(var7.recoverField_a, var45), null, var48);
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                                 "JWake", "use INSTRUMENTATION wake up ,start instrumentation: " + var7.recoverField_a + ", " + var45 + ", state: " + var8
                              );
                              if (var8) {
                                 var9.recoverField_b.put(64, 1);
                                 RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWakeinstrument");
                                 break;
                              }

                              var9.recoverField_b.put(64, 102);
                           } catch (Throwable var28) {
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "fail to start instrumentation caused by: " + var28);
                              var9.recoverField_b.put(64, 101);
                           }
                        }
                     } catch (Throwable var29) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "fail to start instrumentation caused by: " + var29);
                        var9.recoverField_b.put(64, 101);
                     }
                  }

                  if (var8) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "instrumentation wake success, continue wake other target");
                     var5.add(var9);
                  } else if ((var7.recoverField_g & 4) == 0) {
                     var5.add(var9);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wake end, no service or provider wake, wakeType: " + var7.recoverField_g);
                  } else {
                     int var33 = 0;
                     if (!TextUtils.isEmpty(var7.recoverField_b)) {
                        var33 = 2;
                        if (VERSION.SDK_INT < 26 || var7.recoverField_c < 26) {
                           var33 |= 1;
                        }
                     }

                     if (!TextUtils.isEmpty(var7.recoverField_d)) {
                        var33 |= 4;
                     }

                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use OLD wake up ,wake type is :" + var33);
                     if ((var33 & 2) != 0 || (var33 & 1) != 0) {
                        String[] var36 = var7.recoverField_b.split("&");

                        for (String var49 : var36) {
                           ComponentName var16 = new ComponentName(var7.recoverField_a, var49);
                           Intent var17 = new Intent();
                           var17.setComponent(var16);
                           if (VERSION.SDK_INT >= 12) {
                              var17.setFlags(32);
                           }

                           Bundle var18 = RecoverTypeaf2a9364.recoverMethod_a_b000e162(var2);
                           var18.putInt("type", 2);
                           if (var18 != null) {
                              var17.putExtras(var18);
                           }

                           boolean var19 = !TextUtils.equals(var49, "cn.jpush.android.service.DaemonService");
                           if ((var33 & 2) != 0) {
                              try {
                                 RecoverTypeaf2a881f.Member8ea9011d var20 = new RecoverTypeaf2a881f.Member8ea9011d(var0);
                                 boolean var21 = var0.getApplicationContext().bindService(var17, var20, 1);
                                 if (var21 && var19) {
                                    RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWakedservice");
                                 }

                                 var9.recoverField_b.put(2, var21 ? 1 : 102);
                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use bindService wake up, state: " + var21 + ", is d config: " + var19);
                                 if (var21) {
                                    break;
                                 }
                              } catch (Throwable var27) {
                                 RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "bindService throwable:" + var27.getMessage());
                                 var9.recoverField_b.put(2, 101);
                              }
                           }

                           if ((var33 & 1) != 0) {
                              boolean var55 = false;

                              try {
                                 ComponentName var58 = var0.startService(var17);
                                 var55 = var58 != null;
                                 if (var55 && var19) {
                                    RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWakedservice");
                                 }

                                 var9.recoverField_b.put(1, var55 ? 1 : 102);
                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use startService wake up, state: " + var55 + ", is d config: " + var19);
                                 if (var55) {
                                    break;
                                 }
                              } catch (Throwable var26) {
                                 RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "startService throwable:" + var26.getMessage());
                                 var9.recoverField_b.put(1, 101);
                              }
                           }
                        }
                     }

                     if ((var33 & 4) != 0) {
                        String[] var37 = var7.recoverField_d.split("&");

                        for (String var50 : var37) {
                           try {
                              boolean var51 = !TextUtils.equals(var50, var7.recoverField_a + ".DownloadProvider");
                              if (!TextUtils.isEmpty(var50)) {
                                 ContentResolver var52 = var0.getApplicationContext().getContentResolver();
                                 String var53 = var50;
                                 if (!var53.startsWith("content://")) {
                                    var53 = "content://" + var53;
                                 }

                                 var2.put("type", "4");
                                 String var54 = RecoverTypeaf2a9364.recoverMethod_b_cfe96442(var2);
                                 if (!TextUtils.isEmpty(var54)) {
                                    var53 = var53 + var54;
                                 }

                                 Cursor var57 = var52.query(Uri.parse(var53), null, null, null, null);
                                 if (var57 != null && !var57.isClosed()) {
                                    var57.close();
                                 }

                                 var9.recoverField_b.put(4, 1);
                                 if (var51) {
                                    RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWakedprovider");
                                 }

                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "use provider wake up, state: true, is d config: " + var51);
                                 break;
                              }
                           } catch (Throwable var25) {
                              RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "getContentResolver throwable:" + var25.getMessage());
                              var9.recoverField_b.put(4, 101);
                           }
                        }
                     }

                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "wakeResult:" + var9.toString());
                     var5.add(var9);
                  }
               }
            }
         }

         return var5;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "there are no wakeTarget");
         return null;
      }
   }

   private static boolean recoverMethod_a_ee5748c2(Context var0, Bundle var1, Intent var2) {
      try {
         Intent var3 = var2;
         if (var3 != null) {
            var3.addFlags(276824064);
            var3.putExtras(var1);
            var0.startActivity(var3);
            if (var3.getComponent() != null) {
               String var4 = var3.getComponent().getClassName();
               if (!"cn.jpush.android.service.DActivity".equals(var4) && !"cn.jpush.android.service.BActivity".equals(var4)) {
                  RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWake_activity");
               } else {
                  RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWake_dactivity");
               }
            }

            return true;
         }

         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "Fail to start activity ,activityIntent is null !!");
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "Fail to start activity caused by:" + var5);
      }

      return false;
   }

   @TargetApi(15)
   private List recoverMethod_e_a9863e32() {
      HashMap var1 = RecoverTypeaf2a9364.recoverMethod_a_2ad20c96(this.recoverField_a);
      HashMap var2 = RecoverTypeaf2a9364.recoverMethod_a_68955e43(this.recoverField_a, var1);
      return this.recoverMethod_a_6066f3c8(var1, var2, false);
   }

   private List recoverMethod_a_6066f3c8(HashMap var1, HashMap var2, boolean var3) {
      ArrayList var4 = new ArrayList();
      HashMap var5 = (HashMap)this.recoverField_b.recoverField_k;
      long var6 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWake_account");
      long var8 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWake_activity");
      long var10 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWake_dactivity");
      long var12 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWakedservice");
      long var14 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWakedprovider");
      long var16 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWakeinstrument");
      long var18 = System.currentTimeMillis();
      boolean var20 = var18 - var6 >= this.recoverField_b.recoverField_q;
      boolean var21 = var18 - var8 >= this.recoverField_b.recoverField_s;
      boolean var22 = var18 - var12 >= this.recoverField_b.recoverField_t;
      boolean var23 = var18 - var14 >= this.recoverField_b.recoverField_u;
      boolean var24 = var18 - var16 >= this.recoverField_b.recoverField_v;
      boolean var25 = var18 - var10 >= this.recoverField_b.recoverField_r || this.recoverField_c.equals("start");
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JWake",
         "currentTimeMillis:"
            + var18
            + ",lastAccountTime:"
            + var6
            + ",lastActivityTime:"
            + var8
            + ",lastDActivityTime:"
            + var10
            + ",lastDServiceTime:"
            + var12
            + ",lastDProviderTime: "
            + var14
            + ", lastInstrumentTime: "
            + var16
      );
      if (var5 != null) {
         Set var26 = var5.entrySet();
         Iterator var27 = var26.iterator();

         while (var27.hasNext() && var4.size() < this.recoverField_b.recoverField_f) {
            Entry var28 = (Entry)var27.next();
            String var29 = (String)var28.getKey();
            if (!this.recoverMethod_a_aad3b203(var29)) {
               if (RecoverTypeaf2a9366.recoverMethod_a_5ffc0101(this.recoverField_a, var29)) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "[" + var29 + "] is alive now.");
               } else {
                  RecoverTypeaf2a8be3 var30 = (RecoverTypeaf2a8be3)var28.getValue();
                  if (var30.recoverField_a == 0) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", var29 + " not need any method to wake, type is 0");
                  } else if (recoverMethod_b_5ffc0101(this.recoverField_a, var30.recoverField_g)) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", var29 + " is alive");
                  } else {
                     RecoverTypeaf2a8be2 var31 = null;
                     if (var1 != null && var1.containsKey(var29)) {
                        var31 = (RecoverTypeaf2a8be2)var1.get(var29);
                     } else if (var30.recoverField_d) {
                        var31 = RecoverTypeaf2a9364.recoverMethod_a_af60ab8c(this.recoverField_a, var29);
                     }

                     if (var31 == null && var2 != null && var2.containsKey(var29)) {
                        var31 = (RecoverTypeaf2a8be2)var2.get(var29);
                     }

                     if (var31 == null) {
                        RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", var29 + " not found targetInfo");
                     } else {
                        var31.recoverField_l = var30.recoverField_h;
                        boolean var32 = false;
                        var31.recoverField_g = var30.recoverField_a;
                        if (!var3) {
                           var31.recoverField_k = var30.recoverField_i;
                        }

                        if ((var31.recoverField_g & 2) != 0 && var20) {
                           var31.recoverField_h = var30.recoverField_e;
                           var32 = true;
                        }

                        if (!var20) {
                           RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not account time");
                        }

                        try {
                           if ((var31.recoverField_g & 1) != 0) {
                              boolean var33 = false;
                              Intent var34 = null;
                              Intent var35 = null;
                              if (TextUtils.isEmpty(var30.recoverField_f)) {
                                 if (var25) {
                                    var34 = new Intent();
                                    var34.setClassName(var29, "cn.jpush.android.service.DActivity");
                                    var35 = new Intent();
                                    var35.setClassName(var29, "cn.jpush.android.service.BActivity");
                                    var33 = this.recoverMethod_a_e7e5ccdc(var34, var29, false) | this.recoverMethod_a_e7e5ccdc(var35, var29, false);
                                 } else {
                                    RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not DActivity time");
                                 }
                              } else if (var21) {
                                 var34 = recoverMethod_a_2e1cfbaf(var30.recoverField_f, 0);
                                 var33 = this.recoverMethod_a_e7e5ccdc(var34, var29, true);
                              } else {
                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not custom Activity time");
                              }

                              if (var33 && (var34 != null || var35 != null)) {
                                 if (var34 != null) {
                                    var31.recoverField_e = var34;
                                    var31.recoverField_f = var35;
                                 } else {
                                    var31.recoverField_e = var35;
                                 }

                                 var32 = true;
                              } else {
                                 RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                                    "JWake", "do not use Activity wake , activityEnable = " + var33 + ",  intent=" + var34
                                 );
                              }
                           }
                        } catch (URISyntaxException var36) {
                           RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JWake", "parse package:" + var29 + " activity intent error:" + var36);
                        }

                        if ((var31.recoverField_g & 4) != 0) {
                           if (!TextUtils.isEmpty(var30.recoverField_j)) {
                              if (var22) {
                                 var31.recoverField_b = var30.recoverField_j;
                                 var32 = true;
                              } else {
                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not Service time");
                              }
                           } else {
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "Service name is empty");
                           }

                           if (!TextUtils.isEmpty(var30.recoverField_k)) {
                              if (var23) {
                                 var31.recoverField_d = var30.recoverField_k;
                                 var32 = true;
                              } else {
                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not Provider time");
                              }
                           } else {
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "Provider authority is empty");
                           }
                        }

                        if ((var31.recoverField_g & 8) != 0) {
                           if (!TextUtils.isEmpty(var30.recoverField_l)) {
                              if (var24) {
                                 if (RecoverTypeaf2a9366.recoverMethod_b_5ffc0101(this.recoverField_a, var29)) {
                                    var31.recoverField_i = var30.recoverField_l;
                                    var32 = true;
                                 } else {
                                    RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not instrumentation for forbidding");
                                 }
                              } else {
                                 RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "not instrumentation time");
                              }
                           } else {
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "Instrumentation name is empty");
                           }
                        }

                        if (var32) {
                           var4.add(var31);
                        }
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   private static boolean recoverMethod_b_5ffc0101(Context var0, String var1) {
      try {
         if (TextUtils.isEmpty(var1)) {
            return false;
         }

         String var2 = var1;
         if (!var2.startsWith("content://")) {
            var2 = "content://" + var2;
         }

         Uri var3 = Uri.parse(var2);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", " provider: " + var2 + ", uri: " + var3);
         Uri var4 = var0.getApplicationContext().getContentResolver().insert(var3, new ContentValues());
         if (var4 != null) {
            return true;
         }
      } catch (Throwable var5) {
      }

      return false;
   }

   private boolean recoverMethod_a_e7e5ccdc(Intent var1, String var2, boolean var3) {
      if (RecoverTypeaf2a9362.recoverMethod_a_3441485e(this.recoverField_a, var2, var1, var3)) {
         return true;
      }

      Object var4 = null;
      return false;
   }

   private static Intent recoverMethod_a_2e1cfbaf(String var0, int var1) throws URISyntaxException {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      Intent var2 = Intent.parseUri(var0, var1);
      Intent var3 = new Intent(var2);
      var2.addCategory("android.intent.category.BROWSABLE");
      var2.setComponent(null);
      if (VERSION.SDK_INT >= 15) {
         var2.setSelector(null);
      }

      return var3;
   }

   private static class Member8ea9011c extends RecoverTypeaf2b0401 {
      private Context recoverField_a;

      public Member8ea9011c(Context var1) {
         this.recoverField_a = var1;
         this.recoverField_h = "JWake#RequestConfigAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         RecoverTypeaf2a881f.recoverMethod_e_5edd4c44(this.recoverField_a);
      }
   }

   private static class Member8ea9011d implements ServiceConnection {
      private Context recoverField_a;

      public Member8ea9011d(Context var1) {
         this.recoverField_a = var1;
      }

      @Override
      public void onServiceConnected(ComponentName var1, IBinder var2) {
         try {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWake", "unbind wake ServiceConnection");
            this.recoverField_a.getApplicationContext().unbindService(this);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWake", "onServiceConnected throwable" + var4.getMessage());
         }
      }

      @Override
      public void onServiceDisconnected(ComponentName var1) {
      }
   }
}
