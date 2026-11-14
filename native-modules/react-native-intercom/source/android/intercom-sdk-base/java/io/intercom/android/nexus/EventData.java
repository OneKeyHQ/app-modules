package io.intercom.android.nexus;

import java.util.HashMap;

public class EventData extends HashMap<String,Object> {
   public EventData() {
   }

   public EventData(int var1) {
      super(var1);
   }

   public String optString(String var1) {
      return this.optString(var1, "");
   }

   public String optString(String var1, String var2) {
      Object var3 = this.get(var1);
      return var3 instanceof String ? (String)var3 : var2;
   }

   public long optLong(String var1) {
      return this.optLong(var1, -1L);
   }

   public long optLong(String var1, long var2) {
      Object var4 = this.get(var1);
      if (var4 instanceof Integer) {
         Integer var5 = (Integer)var4;
         return Long.valueOf((long)var5.intValue());
      } else {
         return var4 instanceof Long ? (Long)var4 : var2;
      }
   }
}
