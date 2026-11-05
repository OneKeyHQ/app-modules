package io.intercom.android.sdk.helpcenter.api;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u0016\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006\u0012\u0004\u0012\u00020\u00060\u0005¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApiHelper;",
         "",
         "<init>",
         "()V",
         "addDefaultOptions",
         "",
         "",
         "kotlin.jvm.PlatformType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nHelpCenterApi.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterApi.kt\nio/intercom/android/sdk/helpcenter/api/HelpCenterApiHelper\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,59:1\n1#2:60\n465#3:61\n415#3:62\n1252#4,4:63\n*S KotlinDebug\n*F\n+ 1 HelpCenterApi.kt\nio/intercom/android/sdk/helpcenter/api/HelpCenterApiHelper\n*L\n54#1:61\n54#1:62\n54#1:63,4\n*E\n"
)
public final class HelpCenterApiHelper {
   @NotNull
   public static final HelpCenterApiHelper INSTANCE = new HelpCenterApiHelper();
   public static int $stable;

   static {}

   private HelpCenterApiHelper() {
   }

   @NotNull
   public final Map addDefaultOptions() {
      Injector var1 = Injector.get();
      int var2 = 0;
      Map var3 = var1.getUserIdentity().toMap();
      Map var4 = var3;
      int var5 = 0;
      var1.getApi().addSecureHash(var4);
      Intrinsics.checkNotNullExpressionValue(var3, "apply(...)");
      Map var6 = var3;
      int var20 = 0;
      var4 = var6;
      Map var22 = new LinkedHashMap(MapsKt.mapCapacity(var6.size()));
      int var7 = 0;
      Iterable var8 = var4.entrySet();
      int var9 = 0;

      for (Object var11 : var8) {
         Entry var12 = (Entry)var11;
         Map var13 = var22;
         int var14 = 0;
         Object var10001 = var12.getKey();
         Entry var15 = (Entry)var11;
         Object var16 = var10001;
         Map var17 = var13;
         int var18 = 0;
         String var19 = var15.getValue().toString();
         var17.put(var16, var19);
      }

      return var22;
   }
}
