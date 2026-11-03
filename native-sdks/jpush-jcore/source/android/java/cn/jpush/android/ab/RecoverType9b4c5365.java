package cn.jpush.android.ab;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5aee;
import cn.jpush.android.api.CallBackParams;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.service.TagAliasReceiver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public class RecoverType9b4c5365 {
   private static volatile RecoverType9b4c5365 recoverField_a;
   private TagAliasReceiver recoverField_b;
   private ConcurrentHashMap recoverField_c = new ConcurrentHashMap();
   private static final Object recoverField_d = new Object();
   private AtomicBoolean recoverField_e = new AtomicBoolean(false);

   public static RecoverType9b4c5365 recoverMethod_a_d8c382a1() {
      if (recoverField_a == null) {
         synchronized (recoverField_d) {
            if (recoverField_a == null) {
               recoverField_a = new RecoverType9b4c5365();
            }
         }
      }

      return recoverField_a;
   }

   private RecoverType9b4c5365() {
   }

   public void recoverMethod_a_8791f9c0(Context var1, Long var2, CallBackParams var3) {
      this.recoverMethod_c_f0554df3(var1);
      this.recoverField_c.put(var2, var3);
   }

   public CallBackParams recoverMethod_a_82f0b351(long var1) {
      return (CallBackParams)this.recoverField_c.get(var1);
   }

   public ConcurrentHashMap recoverMethod_b_ea240ad1() {
      return this.recoverField_c;
   }

   public void recoverMethod_b_1349ef(long var1) {
      this.recoverField_c.remove(var1);
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      if (this.recoverField_c != null && !this.recoverField_c.isEmpty()) {
         ArrayList var2 = new ArrayList();

         for (Object recoveryElement_65 : this.recoverField_c.entrySet()) {
            Entry var4 = (Entry)recoveryElement_65;
            CallBackParams var5 = (CallBackParams)var4.getValue();
            if (var5.isTimeOut(20000L)) {
               var2.add((Long)var4.getKey());
            }
         }

         for (Object recoveryElement_72 : var2) {
            Long var7 = (Long)recoveryElement_72;
            Logger.recoverMethod_w_e925fa09("TagAliasOperator", "cleanTimeOutCallback timeout rid:" + var7);
            this.recoverMethod_a_30480154(var1, JPushInterface.Member6357d7ea.TIMEOUT, var7);
         }
      }
   }

   public synchronized void recoverMethod_a_f0554df3(Context var1) {
      if (!this.recoverField_e.get()) {
         try {
            IntentFilter var2 = new IntentFilter();
            var2.addCategory(var1.getPackageName());
            var2.addAction("cn.jpush.android.intent.TAG_ALIAS_TIMEOUT");
            var2.addAction("cn.jpush.android.intent.TAG_ALIAS_CALLBACK");
            if (this.recoverField_b == null) {
               this.recoverField_b = new TagAliasReceiver();
            }

            RecoverType9b4c5aee.recoverMethod_a_b0740a13(var1, this.recoverField_b, var2, var1.getPackageName() + ".permission.JPUSH_MESSAGE", null);
            this.recoverField_e.set(true);
         } catch (Exception var3) {
            Logger.recoverMethod_e_e925fa09("TagAliasOperator", "setTagAndAlias e:" + var3.getMessage());
         }
      } else {
         Logger.recoverMethod_d_e925fa09("TagAliasOperator", "tag alias callback register is called");
      }
   }

   public synchronized void recoverMethod_b_f0554df3(Context var1) {
      this.recoverMethod_c_f0554df3(var1);
      if (this.recoverField_e.get() && null != this.recoverField_c && this.recoverField_c.isEmpty()) {
         try {
            if (this.recoverField_b != null) {
               var1.unregisterReceiver(this.recoverField_b);
               this.recoverField_b = null;
            }
         } catch (IllegalArgumentException var3) {
            Logger.recoverMethod_ww_1765c98c("TagAliasOperator", "Receiver not registered, cannot call unregisterReceiver", var3);
         } catch (Exception var4) {
            Logger.recoverMethod_ww_1765c98c("TagAliasOperator", "other exception", var4);
         }

         this.recoverField_e.set(false);
         Logger.recoverMethod_v_e925fa09("TagAliasOperator", "unRegister tag alias callback");
      } else {
         Logger.recoverMethod_v_e925fa09("TagAliasOperator", "tagAliasCallbacks is not empty");
      }
   }

   public void recoverMethod_a_6269c3af(Context var1, long var2, int var4, Intent var5) {
      Logger.recoverMethod_v_e925fa09(
         "TagAliasOperator", "action - onTagAliasResponse rid:" + var2 + " tagAliasCallbacks :" + recoverMethod_a_d8c382a1().recoverMethod_b_ea240ad1()
      );
      if ("cn.jpush.android.intent.TAG_ALIAS_TIMEOUT".equals(var5.getAction())) {
         this.recoverMethod_a_30480154(var1, var4, var2);
      } else {
         this.recoverMethod_a_aae9cd11(var1, var4, var2, var5);
      }

      this.recoverMethod_b_f0554df3(var1);
   }

   public JPushMessage recoverMethod_a_ec80629a(Context var1, Intent var2) {
      long var3 = var2.getLongExtra("tagalias_seqid", -1L);
      int var5 = var2.getIntExtra("tagalias_errorcode", 0);
      Logger.recoverMethod_v_e925fa09("TagAliasOperator", "parseTagAliasResponse2JPushMessage, errorCode:" + var5 + " rid:" + var3);
      CallBackParams var6 = this.recoverMethod_a_82f0b351(var3);
      if (var6 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasOperator", "tagalias callback is null; rid=" + var3);
         return null;
      }

      recoverMethod_a_d8c382a1().recoverMethod_b_1349ef(var3);
      boolean var7 = false;

      try {
         if (var5 == 0) {
            if (var6.action == 5) {
               if (var6.protoType == 1) {
                  ArrayList var8 = var2.getStringArrayListExtra("tags");
                  if (var8 != null) {
                     var6.tags = new HashSet(var8);
                     Logger.recoverMethod_d_e925fa09("TagAliasOperator", "all tags was loaded, value:" + var6.tags);
                  }
               } else if (var6.protoType == 2) {
                  var6.alias = var2.getStringExtra("alias");
                  Logger.recoverMethod_d_e925fa09("TagAliasOperator", "alias was loaded, value:" + var6.tags);
               }
            } else if (var6.action == 6) {
               var7 = var2.getBooleanExtra("validated", false);
            }
         }
      } catch (Throwable var14) {
         Logger.recoverMethod_d_e925fa09("TagAliasOperator", "get tag or alias failed - error:" + var14);
      }

      JPushMessage var16 = new JPushMessage();
      var16.setProtoType(var6.protoType);
      var16.setAction(var6.action);
      var16.setErrorCode(var5);
      var16.setSequence(var6.sequence);
      if (var6.protoType == 1) {
         if (var6.action == 6) {
            var16.setCheckTag(this.recoverMethod_a_6e2a1de3(var6));
            var16.setTagCheckStateResult(var7);
            var16.setTagCheckOperator(true);
         } else {
            var16.setTags(var6.tags);
         }
      } else if (var6.protoType == 3) {
         try {
            String var9 = var2.getStringExtra("message_des");
            if (!TextUtils.isEmpty(var9)) {
               HashMap var10 = new HashMap();
               JSONObject var11 = new JSONObject(var9);
               Iterator var12 = var11.keys();

               while (var12.hasNext()) {
                  String var13 = (String)var12.next();
                  var10.put(var13, var11.get(var13));
               }

               var16.setPros(var10);
            } else {
               var16.setPros(var6.pros);
            }
         } catch (Throwable var15) {
            Logger.recoverMethod_d_e925fa09("TagAliasOperator", "pros des  failed - error:" + var15);
            var16.setPros(var6.pros);
         }
      } else {
         var16.setAlias(var6.alias);
      }

      return var16;
   }

   private void recoverMethod_a_30480154(Context var1, int var2, long var3) {
      if (var2 == JPushInterface.Member6357d7ea.TIMEOUT) {
         Logger.recoverMethod_v_e925fa09("TagAliasOperator", "action - onTimeout rid:" + var3);
      } else {
         Logger.recoverMethod_v_e925fa09("TagAliasOperator", "action - on send data over limit");
      }

      CallBackParams var5 = this.recoverMethod_a_82f0b351(var3);
      if (var5 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasOperator", "tagalias callback is null; rid=" + var3);
      } else {
         this.recoverMethod_b_1349ef(var3);
         this.recoverMethod_a_741b1510(var1, var5, var2, false);
      }
   }

   private void recoverMethod_a_aae9cd11(Context var1, int var2, long var3, Intent var5) {
      Logger.recoverMethod_v_e925fa09("TagAliasOperator", "SetAliasAndTags finish : errorCode:" + var2 + " rid:" + var3);
      CallBackParams var6 = this.recoverMethod_a_82f0b351(var3);
      if (var6 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasOperator", "tagalias callback is null; rid=" + var3);
      } else {
         recoverMethod_a_d8c382a1().recoverMethod_b_1349ef(var3);
         boolean var7 = false;

         try {
            if (var5 != null) {
               if (var6.action == 5) {
                  if (var6.protoType == 1) {
                     ArrayList var8 = var5.getStringArrayListExtra("tags");
                     if (var8 != null) {
                        var6.tags = new HashSet(var8);
                        Logger.recoverMethod_d_e925fa09("TagAliasOperator", "all tags was loaded, value:" + var6.tags);
                     }
                  } else if (var6.protoType == 2) {
                     var6.alias = var5.getStringExtra("alias");
                     Logger.recoverMethod_d_e925fa09("TagAliasOperator", "alias was loaded, value:" + var6.tags);
                  }
               } else if (var6.action == 6) {
                  var7 = var5.getBooleanExtra("validated", false);
               }
            }
         } catch (Throwable var9) {
            Logger.recoverMethod_d_e925fa09("TagAliasOperator", "get tag or alias failed - error:" + var9);
         }

         this.recoverMethod_a_741b1510(var1, var6, var2, var7);
      }
   }

   private void recoverMethod_a_741b1510(Context var1, CallBackParams var2, int var3, boolean var4) {
      Logger.recoverMethod_d_e925fa09("TagAliasOperator", "action - invokeUserCallback, errorCode:" + var3 + ",callBack:" + var2);
      if (var2.protoType == 0) {
         if (var2.tagAliasCallBack != null) {
            var2.tagAliasCallBack.gotResult(var3, var2.alias, var2.tags);
         }
      } else {
         Logger.recoverMethod_w_e925fa09("TagAliasOperator", "new proto type do not call user callback");
      }
   }

   private String recoverMethod_a_6e2a1de3(CallBackParams var1) {
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasOperator", "callBack was null");
         return null;
      }

      try {
         if (var1.tags != null && var1.tags.size() > 0) {
            return (String)var1.tags.toArray()[0];
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_d_e925fa09("TagAliasOperator", "get origin check tag failed, error:" + var3);
      }

      return null;
   }
}
