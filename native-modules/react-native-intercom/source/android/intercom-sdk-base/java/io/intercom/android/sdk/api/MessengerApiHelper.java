package io.intercom.android.sdk.api;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import io.intercom.android.sdk.Injector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody.Companion;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001J#\u0010\t\u001a\u00020\u00052\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0000¢\u0006\u0002\b\u000bJ\u0013\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0000¢\u0006\u0002\b\u000f¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/api/MessengerApiHelper;",
         "",
         "<init>",
         "()V",
         "optionsMapToRequestBody",
         "Lokhttp3/RequestBody;",
         "options",
         "",
         "",
         "getDefaultRequestBody",
         "bodyParams",
         "getDefaultRequestBody$intercom_sdk_base_release",
         "getDefaultMultipartFields",
         "",
         "Lokhttp3/MultipartBody$Part;",
         "getDefaultMultipartFields$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nMessengerApi.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessengerApi.kt\nio/intercom/android/sdk/api/MessengerApiHelper\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,290:1\n216#2,2:291\n*S KotlinDebug\n*F\n+ 1 MessengerApi.kt\nio/intercom/android/sdk/api/MessengerApiHelper\n*L\n283#1:291,2\n*E\n"
)
public final class MessengerApiHelper {
   @NotNull
   public static final MessengerApiHelper INSTANCE = new MessengerApiHelper();
   public static int $stable;

   static {}

   private MessengerApiHelper() {
   }

   @JvmName(name = "optionsMapToRequestBody")
   @NotNull
   public final RequestBody optionsMapToRequestBody(@NotNull Map var1) {
      Intrinsics.checkNotNullParameter(var1, "options");
      Companion var10000 = RequestBody.Companion;
      String var10001 = new Gson().toJson(var1);
      Intrinsics.checkNotNullExpressionValue(var10001, "toJson(...)");
      return var10000.create(var10001, MediaType.Companion.get("application/json; charset=utf-8"));
   }

   @NotNull
   public final RequestBody getDefaultRequestBody$intercom_sdk_base_release(@NotNull Map var1) {
      Intrinsics.checkNotNullParameter(var1, "bodyParams");
      Injector var2 = Injector.get();
      Map var3 = var2.getUserIdentity().toMap();
      var2.getApi().addSecureHash(var3);
      var3.putAll(var1);
      Intrinsics.checkNotNull(var3);
      return this.optionsMapToRequestBody(var3);
   }

   // $VF: synthetic method
   public static RequestBody getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper var0, Map var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = MapsKt.emptyMap();
      }

      return var0.getDefaultRequestBody$intercom_sdk_base_release(var1);
   }

   @NotNull
   public final List getDefaultMultipartFields$intercom_sdk_base_release() {
      Injector var1 = Injector.get();
      Map var10000 = var1.getUserIdentity().toMap();
      Intrinsics.checkNotNullExpressionValue(var10000, "toMap(...)");
      Map var2 = MapsKt.toMutableMap(var10000);
      var1.getApi().addSecureHash(var2);
      List var3 = new ArrayList();
      Map var4 = var2;
      int var5 = 0;

      for (java.lang.Object recoveredEntryElement_0_101 : var4.entrySet()) {
         Entry var7 = (Entry) recoveredEntryElement_0_101;
         Entry var8 = var7;
         int var9 = 0;
         String var10 = (String)var8.getKey();
         Object var11 = var8.getValue();
         okhttp3.MultipartBody.Part.Companion var10001 = Part.Companion;
         Intrinsics.checkNotNull(var10);
         var3.add(var10001.createFormData(var10, var11.toString()));
      }

      return var3;
   }
}
