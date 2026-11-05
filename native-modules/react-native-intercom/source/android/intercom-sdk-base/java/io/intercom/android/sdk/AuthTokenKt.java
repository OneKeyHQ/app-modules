package io.intercom.android.sdk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005",
   d2 = {"toMap", "", "", "", "Lio/intercom/android/sdk/AuthToken;", "intercom-sdk-base_release"}
)
@SourceDebugExtension(
   "SMAP\nAuthToken.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AuthToken.kt\nio/intercom/android/sdk/AuthTokenKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,9:1\n1193#2,2:10\n1267#2,4:12\n*S KotlinDebug\n*F\n+ 1 AuthToken.kt\nio/intercom/android/sdk/AuthTokenKt\n*L\n8#1:10,2\n8#1:12,4\n*E\n"
)
public final class AuthTokenKt {
   @NotNull
   public static final Map toMap(@NotNull List var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Iterable var1 = var0;
      int var2 = 0;
      int var3 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(var1, 10)), 16);
      Iterable var4 = var1;
      Map var5 = new LinkedHashMap(var3);
      int var6 = 0;

      for (Object var8 : var4) {
         Map var9 = var5;
         AuthToken var10 = (AuthToken)var8;
         int var11 = 0;
         Pair var12 = TuplesKt.to(var10.getName(), var10.getToken());
         var9.put(var12.getFirst(), var12.getSecond());
      }

      return var5;
   }
}
