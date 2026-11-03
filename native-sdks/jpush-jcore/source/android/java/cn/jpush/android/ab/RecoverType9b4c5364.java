package cn.jpush.android.ab;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType9b4c5364 {
   private static volatile RecoverType9b4c5364 recoverField_a;
   private static final Object recoverField_b = new Object();
   private ConcurrentHashMap recoverField_c = new ConcurrentHashMap();

   public static RecoverType9b4c5364 recoverMethod_a_d8c38282() {
      if (recoverField_a == null) {
         synchronized (recoverField_b) {
            if (recoverField_a == null) {
               recoverField_a = new RecoverType9b4c5364();
            }
         }
      }

      return recoverField_a;
   }

   public void recoverMethod_a_3daeada0(int var1, int var2, long var3, ArrayList var5, String var6, Map var7) {
      RecoverType9b4c5364.Memberf9850f21 var8 = new RecoverType9b4c5364.Memberf9850f21(var1, var2, var3, var5, var7, var6);
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - createNewCacheNode, tagAliasCacheNode:" + var8);
      this.recoverField_c.put(var3, var8);
   }

   public boolean recoverMethod_a_134632(int var1) {
      if (this.recoverField_c != null && !this.recoverField_c.isEmpty()) {
         for (Object recoveryElement_42 : this.recoverField_c.entrySet()) {
            Entry var3 = (Entry)recoveryElement_42;
            RecoverType9b4c5364.Memberf9850f21 var4 = (RecoverType9b4c5364.Memberf9850f21)var3.getValue();
            if (var4 != null && var4.recoverField_a == var1) {
               return false;
            }
         }
      }

      return true;
   }

   public Intent recoverMethod_a_9db63c2a(Context var1, long var2, int var4, JSONObject var5, Intent var6) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onTagAliasResponse, seqID:" + var2 + ",errorCode:" + var4 + ",intent:" + var6);
      RecoverType9b4c5364.Memberf9850f21 var7 = (RecoverType9b4c5364.Memberf9850f21)this.recoverField_c.get(var2);
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode:" + var7);
      this.recoverField_c.remove(var2);
      if (var5 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "responseJson was null");
         return var6;
      }

      if (var7 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode was null");
         return var6;
      }

      if (this.recoverMethod_a_a44ac94(var1, var4, var7)) {
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "retry action was sended");
         return null;
      }

      if (var4 != 0) {
         if (var4 == 100) {
            long var8 = var5.optLong("wait", -1L);
            Logger.recoverMethod_ww_e925fa09("TagAliasNewProtoRetryHelper", "set tag/alias action will freeze " + var8 + " seconds");
            if (var8 > 0L) {
               RecoverTypea2146d45.recoverMethod_a_1a54e731(var1, var8);
            }
         }

         var4 = this.recoverMethod_a_255f288(var7.recoverField_a, var4);
         var6.putExtra("tagalias_errorcode", var4);
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "mapped errorCode:" + var4);
         return var6;
      } else {
         var7.recoverField_i = 0;
         if (var7.recoverField_b == 5) {
            var7.recoverField_g = var5.optInt("total", -1);
            var7.recoverField_h = var5.optInt("curr", -1);
            this.recoverMethod_a_f9e726e7(var5, var7);
         }

         if (this.recoverMethod_a_e2e50b71(var1, var7)) {
            var7.recoverField_h++;
            Logger.recoverMethod_d_e925fa09(
               "TagAliasNewProtoRetryHelper", "load next page, currpage:" + var7.recoverField_h + ",totalPage:" + var7.recoverField_g
            );
            if (this.recoverMethod_a_d8b89c33(var1, var7.recoverField_a, var7.recoverField_c, var7.recoverField_b)) {
               return null;
            }

            if (this.recoverMethod_b_e2e50b71(var1, var7)) {
               Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "get next page request was sended");
               return null;
            }
         }

         if (var7.recoverField_b == 5) {
            if (var7.recoverField_a == 1) {
               if (var7.recoverField_d.size() > 0) {
                  var6.putStringArrayListExtra("tags", var7.recoverField_d);
               }
            } else if (var7.recoverField_a == 2 && var7.recoverField_f != null) {
               var6.putExtra("alias", var7.recoverField_f);
            }
         } else if (var7.recoverField_b == 6) {
            if (var7.recoverField_a == 1) {
               var6.putExtra("validated", var5.optBoolean("validated", false));
            } else {
               Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "unsupport  proto type");
            }
         }

         return var6;
      }
   }

   public Intent recoverMethod_a_c0184484(Context var1, long var2, int var4, Intent var5) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onProsResponse, seqID:" + var2 + ",errorCode:" + var4 + ",intent:" + var5);
      RecoverType9b4c5364.Memberf9850f21 var6 = (RecoverType9b4c5364.Memberf9850f21)this.recoverField_c.get(var2);
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode:" + var6);
      this.recoverField_c.remove(var2);
      if (var6 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode was null");
         return var5;
      } else if (this.recoverMethod_a_a44ac94(var1, var4, var6)) {
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "retry action was sended");
         return null;
      } else if (var4 != 0) {
         var4 = this.recoverMethod_a_255f288(var6.recoverField_a, var4);
         var5.putExtra("tagalias_errorcode", var4);
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "mapped errorCode:" + var4);
         return var5;
      } else {
         return var5;
      }
   }

   public RecoverType9b4c5364.Memberf9850f21 recoverMethod_a_2d280f22(Context var1, long var2, int var4) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onPushStatusResponse, seqID:" + var2 + ",errorCode:" + var4);
      RecoverType9b4c5364.Memberf9850f21 var5 = (RecoverType9b4c5364.Memberf9850f21)this.recoverField_c.get(var2);
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode:" + var5);
      this.recoverField_c.remove(var2);
      if (var5 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode was null");
         return var5;
      } else if (this.recoverMethod_a_a44ac94(var1, var4, var5)) {
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "retry action was sended");
         return null;
      } else {
         return var5;
      }
   }

   private RecoverType9b4c5364.Memberf9850f21 recoverMethod_a_f9e726e7(JSONObject var1, RecoverType9b4c5364.Memberf9850f21 var2) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onUpdateCacheNode,responseJson:" + var1 + ",tagAliasCacheNode:" + var2);
      if (var2 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "tagAliasCacheNode was null");
         return null;
      }

      String var3 = var1.optString("op");
      if (TextUtils.equals(var3, "get")) {
         if (var2.recoverField_a == 1) {
            try {
               JSONArray var4 = var1.optJSONArray("tags");
               if (var4 != null && var4.length() != 0) {
                  ArrayList var5 = new ArrayList();

                  for (int var6 = 0; var6 < var4.length(); var6++) {
                     var5.add(var4.getString(var6));
                  }

                  if (var5.size() > 0) {
                     var2.recoverField_d.addAll(var5);
                  }
               }
            } catch (Throwable var7) {
               Logger.recoverMethod_e_e925fa09("TagAliasNewProtoRetryHelper", "parse tag list failed - error:" + var7);
            }
         } else {
            String var8 = var1.optString("alias");
            if (var8 != null) {
               var2.recoverField_f = var8;
            }
         }
      }

      return var2;
   }

   public int recoverMethod_a_1349e2(long var1) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onTagAliasTimeOut :" + var1);
      RecoverType9b4c5364.Memberf9850f21 var3 = (RecoverType9b4c5364.Memberf9850f21)this.recoverField_c.remove(var1);
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "onTagAliasTimeOut,removed cachenode:" + var3);
      return var3 != null ? var3.recoverField_a : 0;
   }

   private boolean recoverMethod_a_e2e50b71(Context var1, RecoverType9b4c5364.Memberf9850f21 var2) {
      if (var2 == null) {
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "tagAlias cache was null");
         return false;
      } else {
         Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onTagAliasResponse, tagAliasCacheNode:" + var2);
         if (var2.recoverField_h >= var2.recoverField_g) {
            Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "all tags info was loaded");
            return false;
         } else {
            return true;
         }
      }
   }

   private boolean recoverMethod_a_a44ac94(Context var1, int var2, RecoverType9b4c5364.Memberf9850f21 var3) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - CheckAndSendAgain, errorCode:" + var2 + ",tagAliasCacheNode:" + var3);
      if (var2 == 1 && var3.recoverField_i == 0) {
         var3.recoverField_i++;
         return this.recoverMethod_a_d8b89c33(var1, var3.recoverField_a, var3.recoverField_c, var3.recoverField_b)
            ? true
            : this.recoverMethod_b_e2e50b71(var1, var3);
      } else {
         return false;
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      try {
         if (this.recoverField_c != null && this.recoverField_c.size() > 0) {
            for (Object recoveryElement_240 : this.recoverField_c.entrySet()) {
               Entry var3 = (Entry)recoveryElement_240;
               RecoverType9b4c5364.Memberf9850f21 var4 = (RecoverType9b4c5364.Memberf9850f21)var3.getValue();
               this.recoverMethod_b_e2e50b71(var1, var4);
            }
         }
      } catch (Throwable var5) {
      }
   }

   private boolean recoverMethod_b_e2e50b71(Context var1, RecoverType9b4c5364.Memberf9850f21 var2) {
      Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "action - onSendAgain, tagAliasCacheNode:" + var2);
      if (var2 == null) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "onSendAgain - tagAliasCacheNode was null");
         return false;
      }

      String var3 = null;
      if (var2.recoverField_a == 1) {
         var3 = RecoverType9b4c5363.recoverMethod_a_c9fb1ebd(
            var1, var2.recoverField_d, var2.recoverField_c, var2.recoverField_b, var2.recoverField_g, var2.recoverField_h
         );
      } else if (var2.recoverField_a == 2) {
         var3 = RecoverType9b4c5363.recoverMethod_a_507a31a4(var1, var2.recoverField_f, var2.recoverField_c, var2.recoverField_a);
      } else if (var2.recoverField_a == 3) {
         var3 = RecoverType9b4c5363.recoverMethod_a_f10c0b4f(var1, null, var2.recoverField_c, var2.recoverField_b);
      } else {
         if (var2.recoverField_a != 4) {
            Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "unsupport proto type");
            return false;
         }

         var3 = "PROTO_TYPE_PUSH_STATUS";
      }

      if (var3 != null) {
         if (var2.recoverField_i > 200) {
            this.recoverField_c.remove(var2.recoverField_c);
            RecoverType9b4c5363.recoverMethod_a_3e4db992(
               var1, var2.recoverField_a, JPushInterface.Member6357d7ea.ERROR_CODE_TOO_BUSY, var2.recoverField_c, var2.recoverField_b
            );
            Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "same tag/alias request times greate than 200");
         } else {
            if (var2.recoverField_a == 3) {
               RecoverType9b4c5363.recoverMethod_a_317643a8(var1, var2.recoverField_c, var3, var2.recoverField_b);
            } else if (var2.recoverField_a == 4) {
               RecoverType9b4c5363.recoverMethod_a_304871f2(var1, var2.recoverField_c, var2.recoverField_b);
            } else {
               RecoverType9b4c5363.recoverMethod_a_5756fe1e(var1, var2.recoverField_a, var2.recoverField_c, var3);
            }

            var2.recoverField_i++;
            this.recoverField_c.put(var2.recoverField_c, var2);
            Logger.recoverMethod_d_e925fa09("TagAliasNewProtoRetryHelper", "send request success");
         }

         return true;
      } else {
         return false;
      }
   }

   public int recoverMethod_a_255f288(int var1, int var2) {
      if (var1 == 0) {
         return var2;
      }

      switch (var2) {
         case 0:
            return var2;
         case 1:
         case 2:
            return JPushInterface.Member6357d7ea.ERROR_CODE_TOO_BUSY;
         case 3:
            return JPushInterface.Member6357d7ea.ERROR_CODE_BLACKLIST;
         case 4:
            return JPushInterface.Member6357d7ea.ERROR_CODE_INVALIDUSER;
         case 5:
            return JPushInterface.Member6357d7ea.ERROR_CODE_INVALIDREQ;
         case 6:
            return JPushInterface.Member6357d7ea.ERROR_CODE_INTERNEL_SERVER_ERROR;
         case 7:
         case 8:
            return JPushInterface.Member6357d7ea.ERROR_CODE_GET_FAILED;
         case 9:
            return JPushInterface.Member6357d7ea.ERROR_CODE_TOO_MANY_TAGS;
         case 17:
            try {
               return JPushInterface.Member6357d7ea.ERROR_CODE_ALIAS_LIMIT;
            } catch (Throwable var4) {
               return JPushInterface.Member6357d7ea.ERROR_CODE_INVALIDREQ;
            }
         case 100:
            return JPushInterface.Member6357d7ea.ERROR_CODE_SERVER_UNAVAILABLE;
         case 1013:
            return JPushInterface.Member6357d7ea.ERROR_CODE_APPKEY_NOT_AUTH_TO_SET_PROPERTY;
         case 1014:
            return JPushInterface.Member6357d7ea.ERROR_CODE_PROPERTY_ERROR;
         default:
            return var2;
      }
   }

   protected boolean recoverMethod_a_d8b89c33(Context var1, int var2, long var3, int var5) {
      if ((var2 == 1 || var2 == 2 || var2 == 3 || var2 == 4) && RecoverTypea2146d45.recoverMethod_k_f0554df7(var1)) {
         Logger.recoverMethod_w_e925fa09("TagAliasNewProtoRetryHelper", "tag/alias action was freezed");
         RecoverType9b4c5363.recoverMethod_a_3e4db992(var1, var2, JPushInterface.Member6357d7ea.ERROR_CODE_SERVER_UNAVAILABLE, var3, var5);
         return true;
      } else {
         return false;
      }
   }

   public class Memberf9850f21 {
      public int recoverField_a;
      public int recoverField_b;
      public long recoverField_c;
      public ArrayList recoverField_d;
      public Map recoverField_e;
      public String recoverField_f;
      public int recoverField_g = -1;
      public int recoverField_h = -1;
      public int recoverField_i = 0;
      public int recoverField_j = 0;

      public Memberf9850f21(int var2, int var3, long var4, ArrayList var6, Map var7, String var8) {
         this.recoverField_a = var2;
         this.recoverField_b = var3;
         this.recoverField_c = var4;
         this.recoverField_d = var6;
         if (var2 == 1 && this.recoverField_d == null) {
            this.recoverField_d = new ArrayList();
         }

         this.recoverField_e = var7;
         if (var2 == 3 && this.recoverField_e == null) {
            this.recoverField_e = new HashMap();
         }

         this.recoverField_f = var8;
         this.recoverField_j = 1;
      }

      @Override
      public String toString() {
         return "TagAliasCacheBean{protoType="
            + this.recoverField_a
            + ", actionType="
            + this.recoverField_b
            + ", seqID="
            + this.recoverField_c
            + ", tags="
            + this.recoverField_d
            + ", pros="
            + this.recoverField_e
            + ", alias='"
            + this.recoverField_f
            + '\''
            + ", totalPage="
            + this.recoverField_g
            + ", currPage="
            + this.recoverField_h
            + ", retryCount="
            + this.recoverField_i
            + '}';
      }
   }
}
