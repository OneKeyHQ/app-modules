package cn.jpush.android.api;

import cn.jpush.android.helper.RecoverType8b455834;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class CallBackParams implements Serializable {
   private long sendTime;
   public String alias;
   public Set tags;
   public Map pros;
   public TagAliasCallback tagAliasCallBack;
   public int sequence;
   public int protoType = 0;
   public int action = 0;

   public CallBackParams(String var1, Set var2, TagAliasCallback var3, long var4, int var6, int var7) {
      this.alias = var1;
      this.tags = var2;
      this.tagAliasCallBack = var3;
      this.sendTime = var4;
      this.protoType = var6;
      this.action = var7;
      this.sequence = (int)RecoverType8b455834.recoverMethod_a_9b69();
   }

   public CallBackParams(int var1, Set var2, long var3, int var5, int var6) {
      this.sequence = var1;
      this.tags = var2;
      this.sendTime = var3;
      this.protoType = var5;
      this.action = var6;
   }

   public CallBackParams(int var1, Map var2, long var3, int var5, int var6) {
      this.sequence = var1;
      this.pros = var2;
      this.sendTime = var3;
      this.protoType = var5;
      this.action = var6;
   }

   public CallBackParams(int var1, String var2, long var3, int var5, int var6) {
      this.sequence = var1;
      this.alias = var2;
      this.sendTime = var3;
      this.protoType = var5;
      this.action = var6;
   }

   public CallBackParams(int var1, long var2, int var4, int var5) {
      this.sequence = var1;
      this.sendTime = var2;
      this.protoType = var4;
      this.action = var5;
   }

   public boolean isTimeOut(long var1) {
      return this.protoType == 0 ? System.currentTimeMillis() - this.sendTime > var1 + 10000L : false;
   }

   @Override
   public String toString() {
      return "CallBackParams{sendTime="
         + this.sendTime
         + ", alias='"
         + this.alias
         + '\''
         + ", pros='"
         + this.pros
         + '\''
         + ", tags="
         + this.tags
         + ", tagAliasCallBack="
         + this.tagAliasCallBack
         + ", sequence="
         + this.sequence
         + ", protoType="
         + this.protoType
         + ", action="
         + this.action
         + '}';
   }
}
