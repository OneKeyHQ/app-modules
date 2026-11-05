package io.intercom.android.sdk.api;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.MultipartBody.Part;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u0005J\u000e\u0010\u000b\u001a\u0004\u0018\u00010\f*\u00020\bH\u0002J\u000e\u0010\r\u001a\u0004\u0018\u00010\u000e*\u00020\fH\u0002J\u0016\u0010\t\u001a\u0004\u0018\u00010\u000f*\u00020\u000f2\u0006\u0010\n\u001a\u00020\u0005H\u0002J\f\u0010\u0010\u001a\u00020\u0011*\u00020\u0012H\u0002J\f\u0010\u0013\u001a\u00020\u0005*\u00020\fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/api/UserJwtRequestBody;",
         "",
         "<init>",
         "()V",
         "JWT_FIELD",
         "",
         "readJwt",
         "request",
         "Lokhttp3/Request;",
         "withJwt",
         "jwt",
         "replayableBody",
         "Lokhttp3/RequestBody;",
         "jsonObject",
         "Lcom/google/gson/JsonObject;",
         "Lokhttp3/MultipartBody;",
         "isJwtField",
         "",
         "Lokhttp3/MultipartBody$Part;",
         "readUtf8",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nUserJwtRequestBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserJwtRequestBody.kt\nio/intercom/android/sdk/api/UserJwtRequestBody\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,62:1\n295#2,2:63\n2746#2,3:66\n1869#2,2:69\n1#3:65\n*S KotlinDebug\n*F\n+ 1 UserJwtRequestBody.kt\nio/intercom/android/sdk/api/UserJwtRequestBody\n*L\n16#1:63,2\n45#1:66,3\n47#1:69,2\n*E\n"
)
public final class UserJwtRequestBody {
   @NotNull
   public static final UserJwtRequestBody INSTANCE = new UserJwtRequestBody();
   @NotNull
   private static final String JWT_FIELD = "intercom_user_jwt";
   public static int $stable;

   static {}

   private UserJwtRequestBody() {
   }

   @Nullable
   public final String readJwt(@NotNull Request var1) throws java.io.IOException {
      Intrinsics.checkNotNullParameter(var1, "request");
      RequestBody var2 = this.replayableBody(var1);
      Object var10000;
      if (var2 == null) {
         var10000 = null;
      } else if (var2 instanceof MultipartBody) {
         Iterable var3 = ((MultipartBody)var2).parts();
         int var4 = 0;
         Iterator var5 = var3.iterator();

         while (true) {
            if (!var5.hasNext()) {
               var10000 = null;
               break;
            }

            Object var6 = var5.next();
            Part var7 = (Part)var6;
            int var8 = 0;
            if (INSTANCE.isJwtField(var7)) {
               var10000 = var6;
               break;
            }
         }

         Part var13 = (Part)var10000;
         if ((Part)var10000 != null) {
            RequestBody var14 = var13.body();
            if (var14 != null) {
               return this.readUtf8(var14);
            }
         }

         var10000 = null;
      } else {
         JsonObject var15 = this.jsonObject(var2);
         if (var15 != null) {
            JsonElement var16 = var15.get("intercom_user_jwt");
            if (var16 != null) {
               JsonElement var9 = var16;
               JsonElement var10 = var9;
               int var11 = 0;
               var16 = var10.isJsonPrimitive() ? var9 : null;
               if (var16 != null) {
                  return var16.getAsString();
               }
            }
         }

         var10000 = null;
      }

      return (String)var10000;
   }

   @Nullable
   public final Request withJwt(@NotNull Request var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "request");
      Intrinsics.checkNotNullParameter(var2, "jwt");
      RequestBody var10000 = this.replayableBody(var1);
      if (var10000 == null) {
         return null;
      }

      RequestBody var3 = var10000;
      if (var3 instanceof MultipartBody) {
         var10000 = this.withJwt((MultipartBody)var3, var2);
      } else {
         label28: {
            JsonObject var5 = this.jsonObject(var3);
            if (var5 != null) {
               JsonObject var7 = var5;
               JsonObject var8 = var7;
               int var9 = 0;
               JsonObject var6 = var8.has("intercom_user_jwt") ? var7 : null;
               if (var6 != null) {
                  var8 = var6;
                  JsonObject var13 = var8;
                  int var10 = 0;
                  var13.addProperty("intercom_user_jwt", var2);
                  String var12 = var8.toString();
                  if (var12 != null) {
                     var10000 = RequestBody.Companion.create(var12, var3.contentType());
                     break label28;
                  }
               }
            }

            var10000 = null;
         }
      }

      if (var10000 == null) {
         return null;
      }

      RequestBody var4 = var10000;
      return var1.newBuilder().method(var1.method(), var4).build();
   }

   private final RequestBody replayableBody(Request var1) {
      RequestBody var10000 = var1.body();
      if (var10000 != null) {
         RequestBody var2 = var10000;
         RequestBody var3 = var2;
         int var4 = 0;
         var10000 = !var3.isOneShot() && !var3.isDuplex() ? var2 : null;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private final JsonObject jsonObject(RequestBody var1) {
      MediaType var10000 = var1.contentType();
      if (!Intrinsics.areEqual(var10000 != null ? var10000.subtype() : null, "json")) {
         return null;
      }

      JsonObject var2;
      try {
         JsonElement var3 = JsonParser.parseString(this.readUtf8(var1));
         JsonElement var4 = var3;
         int var5 = 0;
         JsonElement var7 = var4.isJsonObject() ? var3 : null;
         var2 = var7 != null ? var7.getAsJsonObject() : null;
      } catch (Exception var6) {
         var2 = null;
      }

      return var2;
   }

   private final MultipartBody withJwt(MultipartBody var1, String var2) {
      var var3 = var1.parts();
      int var4 = 0;
      boolean var10000;
      if (var3 instanceof Collection && ((Collection)var3).isEmpty()) {
         var10000 = true;
      } else {
         Iterator var5 = var3.iterator();

         while (true) {
            if (!var5.hasNext()) {
               var10000 = true;
               break;
            }

            Object var6 = var5.next();
            Part var7 = (Part)var6;
            int var8 = 0;
            if (INSTANCE.isJwtField(var7)) {
               var10000 = false;
               break;
            }
         }
      }

      if (var10000) {
         return null;
      }

      Builder var10 = new Builder(var1.boundary()).setType(var1.type());
      Iterable var11 = var1.parts();
      int var12 = 0;

      for (Object var14 : var11) {
         Part var15 = (Part)var14;
         int var9 = 0;
         if (INSTANCE.isJwtField(var15)) {
            var10.addPart(var15.headers(), RequestBody.Companion.create(var2, var15.body().contentType()));
         } else {
            var10.addPart(var15);
         }
      }

      return var10.build();
   }

   private final boolean isJwtField(Part var1) {
      Headers var10000 = var1.headers();
      if (var10000 != null) {
         String var2 = var10000.get("Content-Disposition");
         if (var2 != null) {
            return StringsKt.contains$default(var2, "name=\"intercom_user_jwt\"", false, 2, null);
         }
      }

      return false;
   }

   private final String readUtf8(RequestBody var1) throws java.io.IOException {
      Buffer var2 = new Buffer();
      Buffer var3 = var2;
      int var4 = 0;
      var1.writeTo(var3);
      return var2.readUtf8();
   }
}
