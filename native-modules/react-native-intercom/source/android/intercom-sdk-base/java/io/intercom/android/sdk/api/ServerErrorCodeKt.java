package io.intercom.android.sdk.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u0014\u0010\u0002\u001a\u0004\u0018\u00010\u00012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0001H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0004",
   d2 = {"USER_JWT_EXPIRED_ERROR_CODE", "", "parseServerErrorCode", "errorBody", "intercom-sdk-base_release"}
)
@SourceDebugExtension(
   "SMAP\nServerErrorCode.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ServerErrorCode.kt\nio/intercom/android/sdk/api/ServerErrorCodeKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,20:1\n1#2:21\n*E\n"
)
public final class ServerErrorCodeKt {
   @NotNull
   public static final String USER_JWT_EXPIRED_ERROR_CODE = "intercom_user_jwt_expired";

   @Nullable
   public static final String parseServerErrorCode(@Nullable String var0) {
      CharSequence var1 = var0;
      if (var1 == null || StringsKt.isBlank(var1)) {
         return null;
      }

      String var9;
      try {
         JsonObject var10000;
         label47: {
            JsonArray var2 = JsonParser.parseString(var0).getAsJsonObject().getAsJsonArray("errors");
            if (var2 != null) {
               JsonElement var3 = (JsonElement)CollectionsKt.firstOrNull(var2);
               if (var3 != null) {
                  JsonElement var5 = var3;
                  JsonElement var6 = var5;
                  int var7 = 0;
                  JsonElement var4 = var6.isJsonObject() ? var5 : null;
                  if (var4 != null) {
                     var10000 = var4.getAsJsonObject();
                     break label47;
                  }
               }
            }

            var10000 = null;
         }

         String var16;
         label40: {
            JsonObject var10 = var10000;
            if (var10 != null) {
               JsonElement var14 = var10.get("code");
               if (var14 != null) {
                  JsonElement var11 = var14;
                  JsonElement var12 = var11;
                  int var13 = 0;
                  JsonElement var15 = var12.isJsonPrimitive() ? var11 : null;
                  if (var15 != null) {
                     var16 = var15.getAsString();
                     break label40;
                  }
               }
            }

            var16 = null;
         }

         var9 = var16;
      } catch (Exception var8) {
         var9 = null;
      }

      return var9;
   }
}
