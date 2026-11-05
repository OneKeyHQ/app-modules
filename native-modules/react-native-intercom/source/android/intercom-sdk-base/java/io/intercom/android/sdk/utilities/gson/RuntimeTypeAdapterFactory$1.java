package io.intercom.android.sdk.utilities.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

class RuntimeTypeAdapterFactory$1 extends TypeAdapter {
   // $VF: synthetic field
   final TypeAdapter val$jsonElementAdapter;
   // $VF: synthetic field
   final Map val$labelToDelegate;
   // $VF: synthetic field
   final Map val$labelToIgnoreDelegate;
   // $VF: synthetic field
   final Map val$subtypeToDelegate;
   // $VF: synthetic field
   final RuntimeTypeAdapterFactory this$0;

   RuntimeTypeAdapterFactory$1(RuntimeTypeAdapterFactory var1, TypeAdapter var2, Map var3, Map var4, Map var5) {
      this.this$0 = var1;
      this.val$jsonElementAdapter = var2;
      this.val$labelToDelegate = var3;
      this.val$labelToIgnoreDelegate = var4;
      this.val$subtypeToDelegate = var5;
   }

   @Override
   public Object read(JsonReader var1) throws IOException {
      JsonElement var2 = (JsonElement)this.val$jsonElementAdapter.read(var1);
      JsonElement var3;
      if (RuntimeTypeAdapterFactory.access$000(this.this$0)) {
         var3 = var2.getAsJsonObject().get(RuntimeTypeAdapterFactory.access$100(this.this$0));
      } else {
         var3 = var2.getAsJsonObject().remove(RuntimeTypeAdapterFactory.access$100(this.this$0));
      }

      if (var3 == null) {
         throw new JsonParseException(
            "cannot deserialize "
               + RuntimeTypeAdapterFactory.access$200(this.this$0)
               + " because it does not define a field named "
               + RuntimeTypeAdapterFactory.access$100(this.this$0)
         );
      }

      String var4 = var3.getAsString();
      TypeAdapter var5 = (TypeAdapter)this.val$labelToDelegate.get(var4);
      if (var5 == null) {
         var5 = (TypeAdapter)this.val$labelToIgnoreDelegate.get("UnSupported");
      }

      return var5.fromJsonTree(var2);
   }

   @Override
   public void write(JsonWriter var1, Object var2) throws IOException {
      Class var3 = var2.getClass();
      String var4 = (String)RuntimeTypeAdapterFactory.access$300(this.this$0).get(var3);
      TypeAdapter var5 = (TypeAdapter)this.val$subtypeToDelegate.get(var3);
      if (var5 == null) {
         throw new JsonParseException("cannot serialize " + var3.getName() + "; did you forget to register a subtype?");
      }

      JsonObject var6 = var5.toJsonTree(var2).getAsJsonObject();
      if (RuntimeTypeAdapterFactory.access$000(this.this$0)) {
         this.val$jsonElementAdapter.write(var1, var6);
      } else {
         JsonObject var7 = new JsonObject();
         if (var6.has(RuntimeTypeAdapterFactory.access$100(this.this$0))) {
            throw new JsonParseException(
               "cannot serialize " + var3.getName() + " because it already defines a field named " + RuntimeTypeAdapterFactory.access$100(this.this$0)
            );
         }

         var7.add(RuntimeTypeAdapterFactory.access$100(this.this$0), new JsonPrimitive(var4));

         for (java.lang.Object recoveredEntryElement_0_84 : var6.entrySet()) {
            Entry var9 = (Entry) recoveredEntryElement_0_84;
            var7.add((String)var9.getKey(), (JsonElement)var9.getValue());
         }

         this.val$jsonElementAdapter.write(var1, var7);
      }
   }
}
