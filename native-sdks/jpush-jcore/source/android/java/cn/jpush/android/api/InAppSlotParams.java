package cn.jpush.android.api;

import org.json.JSONException;
import org.json.JSONObject;

public class InAppSlotParams {
   public int recoverField_a = 0;
   public String recoverField_b = "";
   public String recoverField_c = "";

   public static InAppSlotParams.Member7771e473 builder() {
      return new InAppSlotParams.Member7771e473();
   }

   @Override
   public String toString() {
      return "InAppSlotParams{sequence=" + this.recoverField_a + ", adSlot='" + this.recoverField_b + '\'' + ", adEvent='" + this.recoverField_c + '\'' + '}';
   }

   public String toJSONString() {
      JSONObject var1 = new JSONObject();

      try {
         var1.put("sequence", this.recoverField_a);
         var1.put("slot", this.recoverField_b);
         var1.put("event", this.recoverField_c);
      } catch (JSONException var3) {
      }

      return var1.toString();
   }

   public static InAppSlotParams parseJSONString(String var0) {
      InAppSlotParams var1 = new InAppSlotParams();

      try {
         JSONObject var2 = new JSONObject(var0);
         var1.recoverField_a = var2.optInt("sequence", 0);
         var1.recoverField_b = var2.optString("slot", "");
         var1.recoverField_c = var2.optString("event", "");
      } catch (Throwable var3) {
      }

      return var1;
   }

   public static class Member7771e473 {
      InAppSlotParams recoverField_a = new InAppSlotParams();

      public InAppSlotParams.Member7771e473 setSequence(int var1) {
         this.recoverField_a.recoverField_a = var1;
         return this;
      }

      public InAppSlotParams.Member7771e473 setAdSlot(String var1) {
         this.recoverField_a.recoverField_b = var1;
         return this;
      }

      public InAppSlotParams.Member7771e473 setAdEvent(String var1) {
         this.recoverField_a.recoverField_c = var1;
         return this;
      }

      public InAppSlotParams build() {
         return this.recoverField_a;
      }
   }

   public interface Memberb62c4686 {
      String SEQ = "sequence";
      String SLOT = "slot";
      String EVENT = "event";
   }
}
