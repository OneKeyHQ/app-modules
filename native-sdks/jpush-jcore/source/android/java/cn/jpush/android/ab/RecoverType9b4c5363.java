package cn.jpush.android.ab;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5aec;
import cn.jpush.android.api.CallBackParams;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b45582f;
import cn.jpush.android.helper.RecoverType8b455834;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.z.RecoverType2e4d20be;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType9b4c5363 {
   private static ConcurrentLinkedQueue recoverField_a = new ConcurrentLinkedQueue();

   public static void recoverMethod_a_53928bd0(Context var0, String var1, Set var2, CallBackParams var3) {
      long var4 = RecoverType8b455834.recoverMethod_a_9b69();
      if (null != var3) {
         RecoverType9b4c5365.recoverMethod_a_d8c382a1().recoverMethod_a_8791f9c0(var0, var4, var3);
      }

      if (!(var0 instanceof Application)) {
         var0 = var0.getApplicationContext();
      }

      if (var3 != null && var3.protoType == 0) {
         RecoverType9b4c5365.recoverMethod_a_d8c382a1().recoverMethod_a_f0554df3(var0);
      }

      Bundle var6 = new Bundle();
      var6.putString("alias", var1);
      ArrayList var7 = null;
      if (var2 != null) {
         var7 = new ArrayList(var2);
      }

      var6.putStringArrayList("tags", var7);
      if (var3 != null && (var3.protoType == 3 && var3.pros != null || var3.protoType == 4)) {
         var6.putSerializable("callback", var3);
      }

      var6.putLong("seq", var4);
      var6.putString("proto_type", (var3 != null ? var3.protoType : 0) + "");
      var6.putString("action_type", (var3 != null ? var3.action : 0) + "");
      Logger.recoverMethod_d_e925fa09("TagAliasHelper", "setAliasAndTags bundle=" + var6);
      JCoreHelper.runActionWithService(var0, "JPUSH", "tagalis", var6);
   }

   public static Set recoverMethod_a_40b97519(Set var0) {
      if (null == var0) {
         return null;
      }

      if (var0.isEmpty()) {
         return var0;
      }

      LinkedHashSet var1 = new LinkedHashSet();
      int var2 = 0;

      for (Object recoveryElement_78 : var0) {
         String var4 = (String)recoveryElement_78;
         if (!TextUtils.isEmpty(var4) && RecoverType9b4c5aec.recoverMethod_a_aad3b203(var4)) {
            var1.add(var4);
            if (++var2 >= 1000) {
               Logger.recoverMethod_ww_e925fa09("TagAliasHelper", "The lenght of tags maybe more than 1000.");
               return var1;
            }
         } else {
            Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "Invalid tag : " + var4);
         }
      }

      return var1;
   }

   public static String recoverMethod_b_b555d336(Set var0) {
      if (null == var0) {
         return null;
      }

      if (var0.isEmpty()) {
         return "";
      }

      String var1 = null;
      int var2 = 0;

      for (Object recoveryElement_105 : var0) {
         String var4 = (String)recoveryElement_105;
         if (!TextUtils.isEmpty(var4) && RecoverType9b4c5aec.recoverMethod_a_aad3b203(var4)) {
            if (null == var1) {
               var1 = var4;
            } else {
               var1 = var1 + "," + var4;
            }

            if (++var2 >= 1000) {
               return var1;
            }
         } else {
            Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "Invalid tag: " + var4);
         }
      }

      return var1;
   }

   private static boolean recoverMethod_a_afa340b9(String var0, boolean var1) {
      int var2 = 0;
      if (!TextUtils.isEmpty(var0)) {
         var2 += var0.getBytes().length;
      }

      Logger.recoverMethod_v_e925fa09("TagAliasHelper", "tags length:" + var2);
      return var1 ? var2 <= 5000 : var2 <= 7000;
   }

   private static int recoverMethod_a_1349e2(long var0) {
      if (recoverField_a.size() < 10) {
         recoverField_a.offer(var0);
         return 0;
      }

      long var2 = (Long)recoverField_a.element();
      long var4 = var0 - var2;
      if (var4 < 0L) {
         recoverField_a.clear();
         return 2;
      }

      if (var4 <= 10000L) {
         return 1;
      }

      while (recoverField_a.size() >= 10) {
         recoverField_a.poll();
      }

      recoverField_a.offer(var0);
      return 0;
   }

   public static void recoverMethod_a_69c77401(Context var0, Bundle var1) {
      String var2 = var1.getString("alias");
      ArrayList var3 = var1.getStringArrayList("tags");
      CallBackParams var4 = (CallBackParams)var1.getSerializable("callback");
      long var5 = var1.getLong("seq", 0L);
      int var7 = 0;
      int var8 = 0;

      try {
         var7 = Integer.parseInt(var1.getString("proto_type"));
      } catch (Throwable var12) {
         Logger.recoverMethod_w_e925fa09("TagAliasHelper", "load tag/alias proto type failed - error:" + var12);
         recoverMethod_a_d8b82b91(var0, var7, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var5);
      }

      try {
         var8 = Integer.parseInt(var1.getString("action_type"));
      } catch (Throwable var11) {
         Logger.recoverMethod_w_e925fa09("TagAliasHelper", "load tag/alias action type failed - error:" + var11);
         recoverMethod_a_d8b82b91(var0, var7, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var5);
      }

      if (var7 == 4 && RecoverTypea2146d45.recoverMethod_b_1a54e374(var0, 0)) {
         RecoverType9b4c5362.recoverMethod_a_3047fd93(var0, 1, var8);
      } else if (RecoverTypea2146d45.recoverMethod_e_f0554df7(var0)) {
         recoverMethod_a_3e4db992(var0, var7, JPushInterface.Member6357d7ea.PUSH_STOPED, var5, var8);
      } else {
         int var9 = recoverMethod_a_1349e2(System.currentTimeMillis());
         if (var9 != 0) {
            if (var9 == 1) {
               Logger.recoverMethod_w_e925fa09("TagAliasHelper", "set tags/alias too soon,over 10 times in 10s");
            } else {
               Logger.recoverMethod_w_e925fa09("TagAliasHelper", "set tags/alias failed,time shaft error，please try again");
            }

            recoverMethod_a_3e4db992(
               var0, var7, var9 == 1 ? JPushInterface.Member6357d7ea.INVOKE_TOO_SOON : JPushInterface.Member6357d7ea.INCORRECT_TIME, var5, var8
            );
         } else if (RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_d8b89c33(var0, var7, var5, var8)) {
            recoverMethod_a_3e4db992(var0, var7, JPushInterface.Member6357d7ea.ERROR_CODE_SERVER_UNAVAILABLE, var5, var8);
         } else {
            String var10 = null;
            if (var7 == 0) {
               Logger.recoverMethod_d_e925fa09("TagAliasHelper", "old tag/alias proto");
               var10 = recoverMethod_a_ff495c8a(var0, var3, var2, var5);
            } else if (var7 == 1) {
               var10 = recoverMethod_a_c9fb1ebd(var0, var3, var5, var8, -1, -1);
            } else if (var7 == 2) {
               var10 = recoverMethod_a_507a31a4(var0, var2, var5, var8);
            } else if (var7 == 3) {
               var10 = recoverMethod_a_f10c0b4f(var0, var4.pros, var5, var8);
            } else if (var7 == 4) {
            }

            Logger.recoverMethod_d_e925fa09("TagAliasHelper", "onPushServiceTagAliasAction request=" + var10);
            if (var10 != null && (var7 == 1 || var7 == 2)) {
               if (!RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_134632(var7)) {
                  Logger.recoverMethod_w_e925fa09("TagAliasHelper", (var7 == 1 ? "tag" : "alias") + " is operatoring, please wait last operator complete");
                  recoverMethod_a_d8b82b91(
                     var0,
                     var7,
                     var7 == 1 ? JPushInterface.Member6357d7ea.ERROR_CODE_TAG_OPERATORING : JPushInterface.Member6357d7ea.ERROR_CODE_ALIAS_OPERATORING,
                     var5
                  );
                  return;
               }

               RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_3daeada0(var7, var8, var5, var3, var2, null);
            }

            if (var10 != null && var7 == 3) {
               if (!RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_134632(var7)) {
                  Logger.recoverMethod_w_e925fa09("TagAliasHelper", "pros is operatoring, please wait last operator complete");
                  recoverMethod_a_d8b82b91(var0, var7, JPushInterface.Member6357d7ea.ERROR_CODE_PROPERTIES_OPERATORING, var5);
                  return;
               }

               RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_3daeada0(var7, var8, var5, var3, var2, var4.pros);
            }

            if (var7 == 4) {
               if (!RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_134632(var7)) {
                  Logger.recoverMethod_w_e925fa09("TagAliasHelper", "push status is operatoring, please wait last operator complete");
                  recoverMethod_a_3e4db992(var0, var7, JPushInterface.Member6357d7ea.ERROR_CODE_PROPERTIES_OPERATORING, var5, var8);
                  return;
               }

               RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_3daeada0(var7, var8, var5, null, null, null);
            }

            if (var7 == 3) {
               recoverMethod_a_317643a8(var0, var5, var10, var4.action);
            } else if (var7 == 4) {
               recoverMethod_a_304871f2(var0, var5, var4.action);
            } else {
               recoverMethod_a_5756fe1e(var0, var7, var5, var10);
            }
         }
      }
   }

   protected static void recoverMethod_a_304871f2(Context var0, long var1, int var3) {
      Logger.recoverMethod_dd_e925fa09("TagAliasHelper", " protoType=" + var3);
      byte[] var4 = RecoverType2e4d20be.recoverMethod_a_255806f(var3);
      JCoreHelper.sendRequest(var0, "JPUSH", 40, 1, var1, 20000L, var4);
   }

   protected static void recoverMethod_a_317643a8(Context var0, long var1, String var3, int var4) {
      Logger.recoverMethod_dd_e925fa09("TagAliasHelper", "pros:" + var3 + " protoType=" + var4);
      if (var3 != null) {
         byte[] var5 = RecoverType2e4d20be.recoverMethod_a_9e9f8f65(var4, var3);
         JCoreHelper.sendRequest(var0, "JPUSH", 38, 0, var1, 20000L, var5);
      } else {
         Logger.recoverMethod_w_e925fa09("TagAliasHelper", "prosRequest was null");
      }
   }

   protected static void recoverMethod_a_5756fe1e(Context var0, int var1, long var2, String var4) {
      Logger.recoverMethod_dd_e925fa09("TagAliasHelper", "tagalias:" + var4);
      if (var4 != null) {
         int var5 = var1 == 0 ? 10 : (var1 == 1 ? 28 : 29);
         int var6 = var1 == 0 ? 4 : (var1 == 1 ? 1 : 2);
         byte[] var7 = RecoverType2e4d20be.recoverMethod_a_917ffcef(JCoreHelper.getAppKey(var0), var5, var4);
         JCoreHelper.sendRequest(var0, "JPUSH", var5, var6, var2, 20000L, var7);
      } else {
         Logger.recoverMethod_w_e925fa09("TagAliasHelper", "tagaliasRequest was null");
      }
   }

   public static void recoverMethod_a_3e4db992(Context var0, int var1, int var2, long var3, int var5) {
      try {
         if (var1 == 4) {
            RecoverType9b4c5362.recoverMethod_a_3047fd93(var0, var2, var5);
            return;
         }

         Intent var6 = new Intent();
         var6.addCategory(var0.getPackageName());
         var6.setPackage(var0.getPackageName());
         if (var1 == 0) {
            var6.setAction("cn.jpush.android.intent.TAG_ALIAS_CALLBACK");
         } else {
            var6.setAction("tag_alias_message");
            String var7 = JPushConstants.getMessageReceiverClass(var0);
            var6.setClass(var0, Class.forName(var7));
            if (var1 == 1) {
               var6.putExtra("message_type", 1);
            } else if (var1 == 3) {
               var6.putExtra("message_type", 3);
            } else {
               var6.putExtra("message_type", 2);
            }
         }

         var6.putExtra("tagalias_errorcode", var2);
         var6.putExtra("tagalias_seqid", var3);
         RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var6);
      } catch (Throwable var8) {
         Logger.recoverMethod_ww_e925fa09("TagAliasHelper", "NotifyTagAliasError:" + var8.getMessage());
      }
   }

   public static void recoverMethod_a_d8b82b91(Context var0, int var1, int var2, long var3) {
      recoverMethod_a_3e4db992(var0, var1, var2, var3, -1);
   }

   private static boolean recoverMethod_a_aaf08cce(Context var0, int var1, String var2, long var3) {
      int var5 = RecoverType9b4c5aec.recoverMethod_b_aad3b1f2(var2);
      if (var5 != 0) {
         Logger.recoverMethod_dd_e925fa09("TagAliasHelper", "Invalid alias: " + var2 + ", will not set alias this time.");
         recoverMethod_a_d8b82b91(var0, var1, var5, var3);
         return false;
      } else {
         return true;
      }
   }

   private static boolean recoverMethod_a_f2db3359(Context var0, int var1, Set var2, long var3) {
      int var5 = RecoverType9b4c5aec.recoverMethod_a_abc98a9d(var2);
      if (var5 != 0) {
         Logger.recoverMethod_dd_e925fa09("TagAliasHelper", "Invalid tags, will not set tags this time.");
         recoverMethod_a_d8b82b91(var0, var1, var5, var3);
         return false;
      } else {
         return true;
      }
   }

   private static boolean recoverMethod_b_510e8a80(Context var0, String var1, long var2, int var4) {
      if (null != var1 && !recoverMethod_a_afa340b9(var1.replaceAll(",", ""), var4 != 0)) {
         recoverMethod_a_d8b82b91(var0, var4, JPushInterface.Member6357d7ea.TOO_LONG_TAGALIAS, var2);
         Logger.recoverMethod_ww_e925fa09("TagAliasHelper", "The length of tags should be less than " + (var4 != 0 ? 5000 : 7000) + " bytes.");
         return false;
      } else {
         return true;
      }
   }

   private static String recoverMethod_a_ff495c8a(Context var0, List var1, String var2, long var3) {
      HashSet var5 = null;
      if (var1 != null) {
         var5 = new HashSet(var1);
      }

      if (null != var2 && !recoverMethod_a_aaf08cce(var0, 0, var2, var3)) {
         return null;
      }

      if (null != var5 && !recoverMethod_a_f2db3359(var0, 0, var5, var3)) {
         return null;
      }

      String var6 = recoverMethod_b_b555d336(var5);
      if (!recoverMethod_b_510e8a80(var0, var6, var3, 0)) {
         return null;
      }

      if (null == var6 && null == var2) {
         Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "NULL alias and tags. Give up action.");
         recoverMethod_a_d8b82b91(var0, 0, JPushInterface.Member6357d7ea.NULL_TAGANDALIAS, var3);
         return null;
      }

      Logger.recoverMethod_dd_e925fa09("TagAliasHelper", "action:setAliasAndTags - alias:" + var2 + ", tags:" + var6);

      try {
         JSONObject var7 = new JSONObject();
         var7.put("platform", "a");
         if (null != var2) {
            var7.put("alias", var2);
         }

         if (null != var5) {
            var7.put("tags", var6);
         }

         String var8 = var7.toString();
         Logger.recoverMethod_d_e925fa09("TagAliasHelper", "tagalias str:" + var8);
         if (!TextUtils.isEmpty(var8)) {
            return var8;
         }

         Logger.recoverMethod_d_e925fa09("TagAliasHelper", "tagalias request action was empty");
      } catch (Throwable var9) {
         Logger.recoverMethod_w_e925fa09("TagAliasHelper", "tagalias exception:" + var9);
         recoverMethod_a_d8b82b91(var0, 0, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var3);
      }

      return null;
   }

   protected static String recoverMethod_a_c9fb1ebd(Context var0, List var1, long var2, int var4, int var5, int var6) {
      if (var4 == 0) {
         Logger.recoverMethod_e_e925fa09("TagAliasHelper", "unsupport tag action type");
         recoverMethod_a_d8b82b91(var0, 1, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var2);
      } else {
         try {
            JSONObject var7 = recoverMethod_a_fb20d41c(var4);
            if (recoverMethod_a_255f299(1, var4)) {
               if (null == var1 || var1.isEmpty()) {
                  Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "tags was empty. Give up action.");
                  recoverMethod_a_d8b82b91(var0, 1, JPushInterface.Member6357d7ea.NULL_TAGANDALIAS, var2);
                  return null;
               }

               HashSet var8 = new HashSet(var1);
               if (!recoverMethod_a_f2db3359(var0, 1, var8, var2)) {
                  return null;
               }

               String var9 = recoverMethod_b_b555d336(var8);
               if (!recoverMethod_b_510e8a80(var0, var9, var2, 1)) {
                  return null;
               }

               if (var4 == 6) {
                  if (TextUtils.isEmpty(var9)) {
                     Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "stags was empty. Give up action.");
                     recoverMethod_a_d8b82b91(var0, 1, JPushInterface.Member6357d7ea.NULL_TAGANDALIAS, var2);
                     return null;
                  }

                  var7.put("tags", var9);
               } else {
                  JSONArray var10 = new JSONArray();

                  for (Object recoveryElement_445 : var1) {
                     String var12 = (String)recoveryElement_445;
                     var10.put(var12);
                  }

                  var7.put("tags", var10);
               }
            }

            if (var4 == 5) {
               var7.put("curr", var6 == -1 ? 1 : var6);
            }

            String var14 = var7.toString();
            Logger.recoverMethod_d_e925fa09("TagAliasHelper", "tag str:" + var14);
            if (!TextUtils.isEmpty(var14)) {
               return var14;
            }

            Logger.recoverMethod_d_e925fa09("TagAliasHelper", "tag request action was empty");
         } catch (Throwable var13) {
            Logger.recoverMethod_w_e925fa09("TagAliasHelper", "tag exception:" + var13);
            recoverMethod_a_d8b82b91(var0, 1, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var2);
         }
      }

      return null;
   }

   protected static String recoverMethod_a_f10c0b4f(Context var0, Map var1, long var2, int var4) {
      if (recoverMethod_d_134632(var4)) {
         try {
            JSONObject var5 = new JSONObject();
            JSONObject var6 = new JSONObject();
            if (var4 == 2) {
               return var5.toString();
            }

            if (var1.isEmpty()) {
               Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "pros was empty. Give up action.");
               recoverMethod_a_d8b82b91(var0, 3, JPushInterface.Member6357d7ea.NULL_TAGANDALIAS, var2);
               return null;
            }

            for (Object recoveryElement_488 : var1.entrySet()) {
               Entry var8 = (Entry)recoveryElement_488;
               String var9 = (String)var8.getKey();
               if (TextUtils.isEmpty(var9)) {
                  Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "pros key was empty. Give up action.");
                  recoverMethod_a_d8b82b91(var0, 3, JPushInterface.Member6357d7ea.NULL_TAGANDALIAS, var2);
                  return null;
               }

               Object var10 = var8.getValue();
               if (var10 instanceof Integer || var10 instanceof Boolean) {
                  var5.put(var9, var10 + "");
               } else if (var10 instanceof Date) {
                  var5.put(var9, ((Date)var10).getTime() + "");
               } else if (var10 instanceof String) {
                  var5.put(var9, var10);
               } else {
                  var6.put(var9, var10);
               }
            }

            String var12 = var6.toString();
            if (!TextUtils.isEmpty(var12) && !var12.equals("{}")) {
               recoverMethod_a_d0b594d9(var0, var12, 3, var2, JPushInterface.Member6357d7ea.ERROR_CODE_PROPERTY_ERROR);
               return null;
            }

            if (!TextUtils.isEmpty(var5.toString())) {
               return var5.toString();
            }

            Logger.recoverMethod_d_e925fa09("TagAliasHelper", "pros request action was empty");
         } catch (Throwable var11) {
            Logger.recoverMethod_w_e925fa09("TagAliasHelper", "pros exception:" + var11);
            recoverMethod_a_d8b82b91(var0, 3, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var2);
         }
      } else {
         Logger.recoverMethod_e_e925fa09("TagAliasHelper", "unsupport pros action type");
         recoverMethod_a_d8b82b91(var0, 3, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var2);
      }

      return null;
   }

   protected static String recoverMethod_a_507a31a4(Context var0, String var1, long var2, int var4) {
      if (recoverMethod_c_134632(var4)) {
         try {
            JSONObject var5 = recoverMethod_a_fb20d41c(var4);
            if (recoverMethod_b_255f299(2, var4)) {
               if (TextUtils.isEmpty(var1)) {
                  Logger.recoverMethod_ee_e925fa09("TagAliasHelper", "alias was empty. Give up action.");
                  recoverMethod_a_d8b82b91(var0, 2, JPushInterface.Member6357d7ea.NULL_TAGANDALIAS, var2);
                  return null;
               }

               if (!recoverMethod_a_aaf08cce(var0, 2, var1, var2)) {
                  return null;
               }

               var5.put("alias", var1);
            }

            String var6 = var5.toString();
            Logger.recoverMethod_d_e925fa09("TagAliasHelper", "alias str:" + var6);
            if (!TextUtils.isEmpty(var6)) {
               return var6;
            }

            Logger.recoverMethod_d_e925fa09("TagAliasHelper", "alias request action was empty");
         } catch (Throwable var7) {
            Logger.recoverMethod_w_e925fa09("TagAliasHelper", "alias exception:" + var7);
            recoverMethod_a_d8b82b91(var0, 2, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var2);
         }
      } else {
         Logger.recoverMethod_e_e925fa09("TagAliasHelper", "unsupport alias action type");
         recoverMethod_a_d8b82b91(var0, 2, JPushInterface.Member6357d7ea.UNKNOWN_ERROR, var2);
      }

      return null;
   }

   private static JSONObject recoverMethod_a_fb20d41c(int var0) throws org.json.JSONException {
      JSONObject var1 = new JSONObject();
      var1.put("platform", "a");
      var1.put("op", recoverMethod_b_47921032(var0));
      return var1;
   }

   private static String recoverMethod_b_47921032(int var0) {
      switch (var0) {
         case 1:
            return "add";
         case 2:
            return "set";
         case 3:
            return "del";
         case 4:
            return "clean";
         case 5:
            return "get";
         case 6:
            return "valid";
         default:
            return null;
      }
   }

   private static boolean recoverMethod_a_255f299(int var0, int var1) {
      return var0 == 1 && (var1 == 1 || var1 == 2 || var1 == 3 || var1 == 6);
   }

   private static boolean recoverMethod_b_255f299(int var0, int var1) {
      return var0 == 2 && var1 == 2;
   }

   private static boolean recoverMethod_c_134632(int var0) {
      return var0 == 2 || var0 == 3 || var0 == 5;
   }

   private static boolean recoverMethod_d_134632(int var0) {
      return var0 == 0 || var0 == 1 || var0 == 2;
   }

   public static long recoverMethod_a_d0b594d9(Context var0, String var1, int var2, long var3, int var5) {
      Logger.recoverMethod_d_e925fa09("TagAliasHelper", "action - onRecvTagAliasCallBack");

      try {
         if (var2 == 4) {
            RecoverType9b4c5362.recoverMethod_a_304871f2(var0, var3, var5);
            return var3;
         }

         JSONObject var6 = null;
         if (var2 != 3) {
            var6 = new JSONObject(var1);
            var5 = var6.optInt("code", JPushInterface.Member6357d7ea.UNKNOWN_ERROR);
            if (var2 == 0) {
               var3 = var6.optLong("sequence");
            }
         }

         Intent var7 = new Intent();
         if (var2 == 0) {
            var7.setAction("cn.jpush.android.intent.TAG_ALIAS_CALLBACK");
         } else {
            String var8 = JPushConstants.getMessageReceiverClass(var0);
            if (!TextUtils.isEmpty(var8)) {
               var7 = new Intent(var0, Class.forName(var8));
            }

            var7.setAction("tag_alias_message");
            if (var2 == 1) {
               var7.putExtra("message_type", 1);
            } else if (var2 == 3) {
               var7.putExtra("message_type", 3);
               var7.putExtra("message_des", var1);
            } else {
               var7.putExtra("message_type", 2);
            }
         }

         var7.addCategory(var0.getPackageName());
         var7.putExtra("proto_type", var2);
         var7.setPackage(var0.getPackageName());
         var7.putExtra("tagalias_errorcode", var5);
         var7.putExtra("tagalias_seqid", var3);
         Intent var10 = var2 != 3
            ? RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_9db63c2a(var0, var3, var5, var6, var7)
            : RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_c0184484(var0, var3, var5, var7);
         if (var10 != null) {
            RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var10);
         }

         return var3;
      } catch (Throwable var9) {
         Logger.recoverMethod_w_e925fa09("TagAliasHelper", "tagalias msgContent:" + var1);
         return -1L;
      }
   }
}
