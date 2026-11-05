package io.intercom.android.sdk.utilities.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class RuntimeTypeAdapterFactory implements TypeAdapterFactory {
   private final Class baseType;
   private final String typeFieldName;
   private final Map labelToSubtype = new LinkedHashMap();
   private final Map subtypeToLabel = new LinkedHashMap();
   private final Map labelToIgnoreSubtype = new LinkedHashMap();
   private final Map subtypeToIgnoreLabel = new LinkedHashMap();
   private final boolean maintainType;

   private RuntimeTypeAdapterFactory(Class var1, String var2, boolean var3) {
      if (var2 != null && var1 != null) {
         this.baseType = var1;
         this.typeFieldName = var2;
         this.maintainType = var3;
      } else {
         throw new NullPointerException();
      }
   }

   public static RuntimeTypeAdapterFactory of(Class var0, String var1, boolean var2) {
      return new RuntimeTypeAdapterFactory(var0, var1, var2);
   }

   public static RuntimeTypeAdapterFactory of(Class var0, String var1) {
      return new RuntimeTypeAdapterFactory(var0, var1, false);
   }

   public static RuntimeTypeAdapterFactory of(Class var0) {
      return new RuntimeTypeAdapterFactory(var0, "type", false);
   }

   public RuntimeTypeAdapterFactory registerSubtype(Class var1, String var2) {
      if (var1 != null && var2 != null) {
         this.labelToSubtype.put(var2, var1);
         this.subtypeToLabel.put(var1, var2);
         return this;
      } else {
         throw new NullPointerException();
      }
   }

   public RuntimeTypeAdapterFactory registerIgnoredSubtype(Class var1, String var2) {
      if (var1 != null && var2 != null) {
         this.labelToIgnoreSubtype.put(var2, var1);
         this.subtypeToIgnoreLabel.put(var1, var2);
         return this;
      } else {
         throw new NullPointerException();
      }
   }

   public RuntimeTypeAdapterFactory registerSubtype(Class var1) {
      return this.registerSubtype(var1, var1.getSimpleName());
   }

   @Override
   public TypeAdapter create(Gson var1, TypeToken var2) {
      if (var2.getRawType() != this.baseType) {
         return null;
      }

      TypeAdapter var3 = var1.getAdapter(JsonElement.class);
      Map var4 = new LinkedHashMap();
      Map var5 = new LinkedHashMap();
      Map var6 = new LinkedHashMap();
      Map var7 = new LinkedHashMap();

      for (java.lang.Object recoveredEntryElement_0_79 : this.labelToSubtype.entrySet()) {
         Entry var9 = (Entry) recoveredEntryElement_0_79;
         TypeAdapter var10 = var1.getDelegateAdapter(this, TypeToken.get((Class)var9.getValue()));
         var4.put((String)var9.getKey(), var10);
         var5.put((Class)var9.getValue(), var10);
      }

      for (java.lang.Object recoveredEntryElement_1_85 : this.labelToIgnoreSubtype.entrySet()) {
         Entry var12 = (Entry) recoveredEntryElement_1_85;
         TypeAdapter var13 = var1.getDelegateAdapter(this, TypeToken.get((Class)var12.getValue()));
         var6.put((String)var12.getKey(), var13);
         var7.put((Class)var12.getValue(), var13);
      }

      return new RuntimeTypeAdapterFactory$1(this, var3, var4, var6, var5).nullSafe();
   }

   // $VF: synthetic method
   static boolean access$000(RuntimeTypeAdapterFactory var0) {
      return var0.maintainType;
   }

   // $VF: synthetic method
   static String access$100(RuntimeTypeAdapterFactory var0) {
      return var0.typeFieldName;
   }

   // $VF: synthetic method
   static Class access$200(RuntimeTypeAdapterFactory var0) {
      return var0.baseType;
   }

   // $VF: synthetic method
   static Map access$300(RuntimeTypeAdapterFactory var0) {
      return var0.subtypeToLabel;
   }
}
