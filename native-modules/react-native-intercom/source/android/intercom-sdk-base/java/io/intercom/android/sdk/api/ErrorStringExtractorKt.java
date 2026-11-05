package io.intercom.android.sdk.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000\u001a\u0010\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u001a\u0010\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\tH\u0002\u001a\u0010\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\fH\u0002\u001a\u0012\u0010\r\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000¨\u0006\u000e",
   d2 = {
         "extractErrorString",
         "",
         "errorObject",
         "Lio/intercom/android/sdk/api/ErrorObject;",
         "extractErrorFromElement",
         "errorElement",
         "Lcom/google/gson/JsonElement;",
         "extractErrorFromJsonArray",
         "jsonArray",
         "Lcom/google/gson/JsonArray;",
         "extractErrorFromJsonObject",
         "jsonObject",
         "Lcom/google/gson/JsonObject;",
         "extractErrorCode",
         "intercom-sdk-base_release"
   }
)
public final class ErrorStringExtractorKt {
   @NotNull
   public static final String extractErrorString(@NotNull ErrorObject var0) {
      Intrinsics.checkNotNullParameter(var0, "errorObject");
      Twig var1 = LumberMill.getLogger();
      String var10000;
      if (var0.hasErrorBody() && var0.getErrorBody() != null) {
         JsonObject var3;
         try {
            JsonObject var8 = (JsonObject)Injector.get().getGson().fromJson(var0.getErrorBody(), JsonObject.class);
            if (var8 == null) {
               return "Something went wrong";
            }

            var3 = var8;
         } catch (Exception var5) {
            var1.e(var5);
            Throwable var6 = var0.getThrowable();
            if (var6 != null) {
               var10000 = var6.getMessage();
               if (var10000 != null) {
                  return var10000;
               }
            }

            return "Something went wrong";
         }

         JsonObject var2 = var3;
         if (var2.has("error")) {
            JsonElement var9 = var2.get("error");
            Intrinsics.checkNotNullExpressionValue(var9, "get(...)");
            var10000 = extractErrorFromElement(var9);
         } else if (var2.has("errors")) {
            JsonArray var10 = var2.getAsJsonArray("errors");
            Intrinsics.checkNotNullExpressionValue(var10, "getAsJsonArray(...)");
            var10000 = extractErrorFromJsonArray(var10);
         } else {
            var10000 = "Something went wrong";
         }
      } else {
         var10000 = "Something went wrong";
      }

      return var10000;
   }

   private static final String extractErrorFromElement(JsonElement var0) {
      String var10000;
      if (var0.isJsonPrimitive()) {
         var10000 = var0.getAsString();
         Intrinsics.checkNotNullExpressionValue(var10000, "getAsString(...)");
      } else if (var0.isJsonArray()) {
         JsonArray var1 = var0.getAsJsonArray();
         Intrinsics.checkNotNullExpressionValue(var1, "getAsJsonArray(...)");
         var10000 = extractErrorFromJsonArray(var1);
      } else if (var0.isJsonObject()) {
         JsonObject var2 = var0.getAsJsonObject();
         Intrinsics.checkNotNullExpressionValue(var2, "getAsJsonObject(...)");
         var10000 = extractErrorFromJsonObject(var2);
      } else {
         var10000 = "Something went wrong";
      }

      return var10000;
   }

   private static final String extractErrorFromJsonArray(JsonArray var0) {
      return CollectionsKt.joinToString$default(var0, " - ", null, null, 0, null, ((kotlin.jvm.functions.Function1) (recoveredArgument_50_0) -> ErrorStringExtractorKt.extractErrorFromJsonArray$lambda$0((com.google.gson.JsonElement)recoveredArgument_50_0)), 30, null);
   }

   private static final String extractErrorFromJsonObject(JsonObject var0) {
      String var10000;
      if (var0.has("message")) {
         String var1 = var0.get("message").getAsString();
         Intrinsics.checkNotNull(var1);
         var10000 = var1;
      } else {
         var10000 = "Something went wrong";
      }

      return var10000;
   }

   @Nullable
   public static final String extractErrorCode(@NotNull ErrorObject var0) {
      Intrinsics.checkNotNullParameter(var0, "errorObject");
      if (var0.hasErrorBody() && var0.getErrorBody() != null) {
         String var1;
         try {
            JsonObject var4 = (JsonObject)Injector.get().getGson().fromJson(var0.getErrorBody(), JsonObject.class);
            JsonElement var2 = var4 != null ? var4.get("error") : null;
            var1 = var2 != null && var2.isJsonObject() && var2.getAsJsonObject().has("type") ? var2.getAsJsonObject().get("type").getAsString() : null;
         } catch (Exception var3) {
            LumberMill.getLogger().e(var3);
            var1 = null;
         }

         return var1;
      } else {
         return null;
      }
   }

   private static final CharSequence extractErrorFromJsonArray$lambda$0(JsonElement var0) {
      CharSequence var1;
      if (var0.isJsonPrimitive()) {
         var1 = var0.getAsString();
         Intrinsics.checkNotNullExpressionValue(var1, "getAsString(...)");
         var1 = var1;
      } else if (var0.isJsonObject()) {
         JsonObject var2 = var0.getAsJsonObject();
         Intrinsics.checkNotNullExpressionValue(var2, "getAsJsonObject(...)");
         var1 = extractErrorFromJsonObject(var2);
      } else {
         var1 = "Something went wrong";
      }

      return var1;
   }
}
